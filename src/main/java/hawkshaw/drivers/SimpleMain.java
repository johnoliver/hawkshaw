package hawkshaw.drivers;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.Throttle;

public class SimpleMain {

    private static final int LEAK_SIZE_IN_BYTES = 4;

    private void run() throws InterruptedException {
        Throttle createAt = new ConstantThrottle(0);
        Throttle deleteAt = GammaDistThrottle.of(1234567, 5.0, 2.0);
        System.out.println("Starting LCM");
        ManagedCache manager = new ManagedCache(createAt, deleteAt, LEAK_SIZE_IN_BYTES);
        manager.startAllocation(1500000);
        System.out.println("All enqueued");
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleMain sm = new SimpleMain();
        sm.run();
    }

}
