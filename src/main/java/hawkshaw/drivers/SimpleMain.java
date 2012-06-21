package hawkshaw.drivers;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.Throttle;

public class SimpleMain {

    private void run() throws InterruptedException {
        //        Throttle createAt = GammaDistThrottle.of(1234567, 2.0, 2.0);
        Throttle createAt = new ConstantThrottle(0);
        Throttle deleteAt = GammaDistThrottle.of(1234567, 5.0, 2.0);
        System.out.println("Starting LCM");
        ManagedCache manager = new ManagedCache(createAt, deleteAt);
        manager.startAllocation(1_500_000);
        System.out.println("All enqueued");
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleMain sm = new SimpleMain();
        sm.run();
    }

}
