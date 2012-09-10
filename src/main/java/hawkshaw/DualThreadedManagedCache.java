package hawkshaw;

import hawkshaw.throttles.Throttle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A managed cache that is harder to control the allocation rate but does not produce a significant number of scheduled Runnables for allocating/deallocating
 * 
 * For this cache the throttles control the time between each allocation/deallocation
 */
public class DualThreadedManagedCache {

    final List<byte[]> cache;
    private final Throttle productionThrottle;
    private final Throttle collectionThrottle;
    Thread producer;
    private Thread collector;

    private volatile boolean running = false;
    int entryVolume;

    public DualThreadedManagedCache(Throttle collectionThrottle, Throttle productionThrottle, int entryVolume) {
        this.collectionThrottle = collectionThrottle;
        this.productionThrottle = productionThrottle;
        this.entryVolume = entryVolume;
        cache = Collections.synchronizedList(new LinkedList<byte[]>());
    }

    public void startAllocation(long numberOfObjects) {
        producer = new ProduceKey(numberOfObjects, productionThrottle);
        collector = new RemoveKey(collectionThrottle);

        setRunning(true);

        producer.start();
        collector.start();
    }

    private class ProduceKey extends Thread {
        private final long numToProduce;
        private final Throttle productionThrottle;

        public ProduceKey(long numToProduce, Throttle productionThrottle) {
            this.numToProduce = numToProduce;
            this.productionThrottle = productionThrottle;
        }

        @Override
        public void run() {
            for (long i = 0; i < numToProduce; i++) {
                cache.add(new byte[entryVolume]);
                performWait(productionThrottle);

                if (!isRunning()) {
                    return;
                }
            }
        }
    }

    private class RemoveKey extends Thread {
        private final Throttle removeThrottle;

        public RemoveKey(Throttle removeThrottle) {
            this.removeThrottle = removeThrottle;
        }

        @Override
        public void run() {
            while (true) {
                // remove random one
                if (cache.size() > 0) {
                    int index = (int) (Math.random()*cache.size());
                    cache.remove(index);
                }

                if (!isRunning()) {
                    return;
                }

                if (cache.size() == 0) {
                    if (!producer.isAlive()) {
                        return;
                    }
                }

                performWait(removeThrottle);
            }
        }
    }

    private void performWait(Throttle throttle) {
        try {
            int waitTime = throttle.millisTillEvent();
            if (waitTime > 0) {
                synchronized (this) {
                    wait(waitTime);
                }
            }
        } catch (InterruptedException e) {
            // Deliberately ignore Exception
        }
    }

    boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public void join() {
        try {
            producer.join();
            collector.join();
        } catch (InterruptedException e) {
            // Deliberately ignore Exception
        }
    }

    public void terminate() {
        setRunning(false);
        synchronized (this) {
            this.notifyAll();
        }
        join();
    }
}
