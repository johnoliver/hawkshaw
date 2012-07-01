package hawkshaw;

import hawkshaw.throttles.Throttle;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagedCache {

    private final Map<String, Object> cache;
    private final ScheduledExecutorService executor;
    private final Throttle productionThrottle;
    private final Throttle collectionThrottle;

    private AtomicInteger toRemove;

    public ManagedCache(Throttle collectionThrottle, Throttle productionThrottle) {
        this.collectionThrottle = collectionThrottle;
        this.productionThrottle = productionThrottle;
        executor = Executors.newScheduledThreadPool(4);
        cache = new Hashtable<>();
    }

    public void startAllocation(int numberOfObjects) {
        toRemove = new AtomicInteger(numberOfObjects);
        for (int i = 0; i < numberOfObjects; i++) {
            scheduleBy(new ProduceKey(), productionThrottle);
        }
    }

    private void scheduleBy(Callable<?> task, Throttle throttle) {
        int ttl = throttle.millisTillEvent();
        executor.schedule(task, ttl, TimeUnit.MILLISECONDS);
    }

    private class ProduceKey implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            // TODO: stop using uuids
            String uuid = UUID.randomUUID().toString();
            cache.put(uuid, uuid);
            scheduleBy(new RemoveKey(uuid), collectionThrottle);
            return null;
        }
    }

    private class RemoveKey implements Callable<Void> {
        private final String key;

        public RemoveKey(String s) {
            key = s;
        }

        @Override
        public Void call() throws Exception {
            cache.remove(key);

            int remainingRemovals = toRemove.decrementAndGet();

            if (remainingRemovals == 0) {
                executor.shutdown();
                ManagedCache.this.notifyAll();
            }

            return null;
        }
    }

    public synchronized void join() {
        while (!executor.isTerminated()) {
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
