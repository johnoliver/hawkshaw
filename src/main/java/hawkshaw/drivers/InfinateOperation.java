package hawkshaw.drivers;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.Throttle;

public class InfinateOperation {

    private void run() throws InterruptedException {
        // create at a constant rate
        Throttle createAt = new ConstantThrottle(0);

        // remove at a constant rate
        Throttle deleteAt = GammaDistThrottle.of(1234567, 2.0, 2.0);
        System.out.println("Starting LCM");
        while (true) {
            ManagedCache manager = new ManagedCache(createAt, deleteAt);
            manager.startAllocation(500_000);
            System.out.println("All enqueued");
            Thread.sleep(2000);

        }
    }

    public static void main(String[] args) throws InterruptedException {
        InfinateOperation sm = new InfinateOperation();
        sm.run();
    }

}
