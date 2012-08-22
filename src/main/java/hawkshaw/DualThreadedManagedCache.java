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

    private final Map<String, Object> cache;
    private final Throttle productionThrottle;
    private final Throttle collectionThrottle;
    private Thread producer;
    private Thread collector;

    public DualThreadedManagedCache(Throttle collectionThrottle, Throttle productionThrottle) {
        this.collectionThrottle = collectionThrottle;
        this.productionThrottle = productionThrottle;
        cache = new Hashtable<String, Object>();
    }

    public void startAllocation(int numberOfObjects) {
        producer = new ProduceKey(numberOfObjects, productionThrottle);
        collector = new RemoveKey(collectionThrottle);

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
                cache.put(uuid, uuid);
                try {
                    Thread.sleep(productionThrottle.millisTillEvent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

                if (cache.size() == 0) {
                    if (!producer.isAlive()) {
                        return;
                    }
                }

                try {
                    Thread.sleep(removeThrottle.millisTillEvent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
