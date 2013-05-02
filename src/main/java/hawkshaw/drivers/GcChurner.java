package hawkshaw.drivers;

import hawkshaw.DualThreadedManagedCache;
import hawkshaw.throttles.ConstantInitialPauseThrottle;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.NumberProducer;

public class GcChurner {

    private static final int TIME_BEFORE_COLLECTION_STARTS_IN_MS = 5000;
    private static final int PRODUCTION_PERIOD_IN_MS = 20;
    private static final int LEAK_SIZE_IN_BYTES = 1024 * 1024;

    public static void run(long numObjectsToallocate, int timeTillCollectionStarts, int allocationSize, int productionPeriod) {
        NumberProducer createAt = new ConstantThrottle(PRODUCTION_PERIOD_IN_MS);
        NumberProducer deleteAt = new ConstantInitialPauseThrottle(timeTillCollectionStarts, PRODUCTION_PERIOD_IN_MS);
        DualThreadedManagedCache manager = new DualThreadedManagedCache(deleteAt, createAt, allocationSize);

        manager.startAllocation(numObjectsToallocate);
        manager.join();
    }

    public static void main(String[] args) {
        run(Long.MAX_VALUE, TIME_BEFORE_COLLECTION_STARTS_IN_MS, LEAK_SIZE_IN_BYTES, PRODUCTION_PERIOD_IN_MS);
    }

}
