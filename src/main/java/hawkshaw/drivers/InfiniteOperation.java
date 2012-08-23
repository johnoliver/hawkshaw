package hawkshaw.drivers;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.Throttle;

public class InfiniteOperation {

    private static final int DEFAULT_ALLOCATION_SIZE = 500000;
    private static final int PERIOD_IN_MILLISECONDS = 2000;
    private static final int LEAK_SIZE_IN_BYTES = 4;

    private static void run() throws InterruptedException {
        Throttle createAt = new ConstantThrottle(0);

        // remove at a constant rate
        Throttle deleteAt = GammaDistThrottle.of(1234567, 2.0, 2.0);
        System.out.println("Starting LCM");
        while (true) {
            ManagedCache manager = new ManagedCache(createAt, deleteAt, LEAK_SIZE_IN_BYTES);
            manager.startAllocation(DEFAULT_ALLOCATION_SIZE);
            System.out.println("All enqueued");
            Thread.sleep(PERIOD_IN_MILLISECONDS);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        run();
    }

}
