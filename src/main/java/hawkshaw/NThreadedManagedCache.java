package hawkshaw;

import hawkshaw.throttles.Throttle;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A managed cache that is harder to control the allocation rate but does not produce a significant number of scheduled Runnables for allocating/deallocating
 * 
 * For this cache the throttles control the time between each allocation/deallocation
 */
public class NThreadedManagedCache {

    final Map<String, byte[]> cache;
    private final Throttle productionThrottle;
    private final Throttle collectionThrottle;
    private ScheduledExecutorService executors;

    private volatile boolean running = false;
    private Throttle sizeThrottle;
	private int numThreads;

    public NThreadedManagedCache(Throttle collectionThrottle, Throttle productionThrottle, Throttle sizeThrottle, int numThreads) {
        this.collectionThrottle = collectionThrottle;
        this.productionThrottle = productionThrottle;
        this.sizeThrottle = sizeThrottle;
        this.numThreads = numThreads;
        cache = new ConcurrentHashMap<String, byte[]>();
    }

	public void startAllocation(long numObjects) {
		setRunning(true);

		executors = Executors.newScheduledThreadPool(4);
		for (int i = 0; i < numThreads; i++) {
			executors.execute(new ProduceKey(productionThrottle, numObjects));
		}

	}

	public void startAllocation() {
		startAllocation(Long.MAX_VALUE);
	}

    void scheduleBy(Callable<?> task, Throttle throttle) {
        int ttl = throttle.millisTillEvent();
        executors.schedule(task, ttl, TimeUnit.NANOSECONDS);
    }

	private class ProduceKey implements Runnable {
		private final Throttle productionThrottle;
		private long numObjects;

		public ProduceKey(Throttle productionThrottle, long numObjects) {
			this.productionThrottle = productionThrottle;
			this.numObjects = numObjects;
		}

		@Override
		public void run() {
			while (isRunning() && numObjects > 0 ) {
				String uuid = UUID.randomUUID().toString();
				cache.put(uuid, new byte[sizeThrottle.millisTillEvent()]);
				scheduleBy(new RemoveKey(uuid), collectionThrottle);
				numObjects--;
				performWait(productionThrottle, this);
			}
		}
	}

    private class RemoveKey implements Callable<Void> {
		private String key;

        public RemoveKey(String key) {
            this.key = key;
        }

		@Override
		public Void call() throws Exception {
			cache.remove(key);
			return null;
		}
    }

    private void performWait(Throttle throttle, Object lock) {
        try {
            int waitTime = throttle.millisTillEvent();
            if (waitTime > 0) {
                synchronized (lock) {
                    lock.wait(0, waitTime);
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

    public void terminate() throws InterruptedException {
        setRunning(false);

        executors.shutdown();
        executors.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
