package hawkshaw.drivers;

import java.math.BigInteger;

import hawkshaw.DualThreadedManagedCache;
import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.NeverThrottle;
import hawkshaw.throttles.Throttle;

public class Leaker {

    private long runTime;
    DualThreadedManagedCache leaker;
    private int leakRate;

    public Leaker(long runTime, int leakRate) {
        this.runTime = runTime;
        this.leakRate = leakRate;// in bytes per second
    }

    // A thread that sits in the background leaking
    private void createLeaker() {
        int leakPeriod = 50;// in ms

        int leakVolume = leakRate / (1000 / leakPeriod);// in bytes per ms

        // create at a constant rate
        Throttle createAt = new ConstantThrottle(leakPeriod);

        // never remove
        Throttle deleteAt = new NeverThrottle();
        System.out.println("Starting Leaker");

        // use DualThreadedManagedCache so as not to create an enormous number of runnables
        leaker = new DualThreadedManagedCache(deleteAt, createAt, leakVolume);

        // leak a large number of objects
        leaker.startAllocation(Integer.MAX_VALUE);
    }

    // creates non-leaking churn of objects
    private void executeChurner() {

        BigInteger now = new BigInteger(Long.toString(System.currentTimeMillis()));
        BigInteger end = now.add(new BigInteger(Long.toString(runTime)));
        while (now.compareTo(end) < 0) {
            Throttle createAt = GammaDistThrottle.of((int) System.currentTimeMillis(), 2.0, 2.0);
            Throttle deleteAt = GammaDistThrottle.of((int) System.currentTimeMillis(), 1.0, 2.0);

            ManagedCache manager = new ManagedCache(deleteAt, createAt, 1024);
            manager.startAllocation(50000);
            manager.join();
            now = new BigInteger(Long.toString(System.currentTimeMillis()));
        }
    }

    private void run() throws InterruptedException {
        // make a process that leaks
        createLeaker();
        executeChurner();

        leaker.terminate();

    }

    public static void main(String[] args) throws InterruptedException {
        long runTime = Long.MAX_VALUE;
        int leakRate = 1*1024*1024;// default to 4 kb a second

        if (args.length != 0) {
            runTime = Long.parseLong(args[0])*1000;
            leakRate = Integer.parseInt(args[1]);
        }
        Leaker sm = new Leaker(runTime, leakRate);
        sm.run();
    }

}
