package hawkshaw.drivers;

import hawkshaw.DualThreadedManagedCache;
import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.NeverThrottle;
import hawkshaw.throttles.Throttle;

public class Leaker {

    // A thread that sits in the background leaking
    private void createLeaker() {
        // create at a constant rate
        Throttle createAt = new ConstantThrottle(1);

        // never remove
        Throttle deleteAt = new NeverThrottle();
        System.out.println("Starting Leaker");

        // use DualThreadedManagedCache so as not to create an enormous number of threads
        DualThreadedManagedCache manager = new DualThreadedManagedCache(deleteAt, createAt);

        // leak a large number of objects
        manager.startAllocation(Integer.MAX_VALUE);
    }

    // creates non-leaking churn of objects
    private void executeChurner() {
        Throttle createAt = GammaDistThrottle.of(1234567, 2.0, 2.0);

        Throttle deleteAt = GammaDistThrottle.of(1234567, 1.0, 2.0);

        while (true) {
            ManagedCache manager = new ManagedCache(deleteAt, createAt);
            manager.startAllocation(50000);
            manager.join();
        }
    }

    private void run() throws InterruptedException {
        // make a process that leaks
        createLeaker();
        executeChurner();

    }

    public static void main(String[] args) throws InterruptedException {
        Leaker sm = new Leaker();
        sm.run();
    }

}
