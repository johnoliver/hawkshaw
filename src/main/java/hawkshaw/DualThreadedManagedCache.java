package hawkshaw;

import hawkshaw.throttles.Throttle;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

/**
 * A managed cache that is harder to control the allocation rate but does not produce a significant number of scheduled runables for allocating/deallocating
 *
 * For this cache the throttles control the time between each allocation/deallocation
 */
public class DualThreadedManagedCache {

    private final Map<String, byte[]> cache;
    private final Throttle productionThrottle;
    private final Throttle collectionThrottle;
    private Thread producer;
    private Thread collector;

    private boolean running = false;
    private int entryVolume;

    public DualThreadedManagedCache(Throttle collectionThrottle, Throttle productionThrottle, int entryVolume) {
        this.collectionThrottle = collectionThrottle;
        this.productionThrottle = productionThrottle;
        this.entryVolume = entryVolume;
        cache = new Hashtable<String, byte[]>();
    }

    public void startAllocation(int numberOfObjects) {
        producer = new ProduceKey(numberOfObjects, productionThrottle);
        collector = new RemoveKey(collectionThrottle);

        setRunning(true);

        producer.start();
        collector.start();
    }

    private class ProduceKey extends Thread {
        private final int numToProduce;
        private final Throttle productionThrottle;

        public ProduceKey(int numToProduce, Throttle productionThrottle) {
            this.numToProduce = numToProduce;
            this.productionThrottle = productionThrottle;
        }

        @Override
        public void run() {
            for (int i = 0; i < numToProduce; i++) {
                String uuid = UUID.randomUUID().toString();
                cache.put(uuid, new byte[entryVolume]);
                try {
                    synchronized (this) {
                        wait(productionThrottle.millisTillEvent());
                    }
                } catch (InterruptedException e) {
                }

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
                // remove a random one
                Object[] keys = cache.keySet().toArray();
                if (keys.length > 0) {
                    int toRemove = (int) (Math.random() * keys.length);
                    cache.remove(keys[toRemove]);
                }

                if (!isRunning()) {
                    return;
                }

                if (cache.size() == 0) {
                    if (!producer.isAlive()) {
                        return;
                    }
                }

                try {
                    synchronized (this) {
                        wait(removeThrottle.millisTillEvent());
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public void terminate() {
        setRunning(false);
        try {
            synchronized (producer) {
                producer.notifyAll();
            }
            synchronized (collector) {
                collector.notifyAll();
            }
            producer.join();
            collector.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
