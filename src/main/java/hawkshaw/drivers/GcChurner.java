package hawkshaw.drivers;

import hawkshaw.DualThreadedManagedCache;
import hawkshaw.throttles.ConstantInitialPauseThrottle;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.Throttle;

public class GcChurner {

    private static final int TIME_BEFORE_COLLECTION_STARTS_IN_MS = 5000;
    private static final int PRODUCTION_PERIOD_IN_MS = 20;
    private static final int LEAK_SIZE_IN_BYTES = 1024 * 1024;

    public static void run() {
        Throttle createAt = new ConstantThrottle(PRODUCTION_PERIOD_IN_MS);
        Throttle deleteAt = new ConstantInitialPauseThrottle(TIME_BEFORE_COLLECTION_STARTS_IN_MS, PRODUCTION_PERIOD_IN_MS);
        DualThreadedManagedCache manager = new DualThreadedManagedCache(deleteAt, createAt, LEAK_SIZE_IN_BYTES);

        manager.startAllocation(Long.MAX_VALUE);
    }

    public static void main(String[] args) {
        run();
    }

}
