package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.CycleTimedThrottle;
import hawkshaw.throttles.NeverThrottle;
import hawkshaw.throttles.SinThrottle;
import hawkshaw.throttles.TimedThrottle;
import hawkshaw.throttles.WhiteThrottle;

/**
 * For low throughput:
 * -XX:+PrintGCDetails -Xloggc:gc.log -Xmx256m -XX:+PrintTenuringDistribution  -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
 * 
 * Better throughput:
 * -XX:+PrintGCDetails -Xloggc:gc.log -Xmx1024m -XX:+PrintTenuringDistribution  -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
 * 
 * Even better:
 * -XX:+PrintGCDetails -Xloggc:gc.log -Xmx4G
 *
 */
public class LowThroughput extends ComposableDriver {

    private static int seed = 123;

    private void neutralThrottle() {
        managers.add( new NThreadedManagedCache(    new WhiteThrottle(seed++, 0, 100),
                                                    new WhiteThrottle(seed++, 0, 100), 
                                                    new WhiteThrottle(333, 1, 1, MBYTE),
                                                    1));
    }

    private void sin() {
        managers.add( new NThreadedManagedCache(new SinThrottle(seed++, 0, 1000, 1000) ,
                                                new SinThrottle(seed++, Math.PI, 1000, 1000) , 
                                                new WhiteThrottle(seed++,  5, 10, MBYTE),
                                                2));
    }

    private void steadyState() {
        TimedThrottle warmup = new TimedThrottle(new WhiteThrottle(seed++, 0, 100), 100000);
        TimedThrottle steady = new TimedThrottle(new NeverThrottle(), Long.MAX_VALUE);

        managers.add( new NThreadedManagedCache(    new NeverThrottle(),
                                                    new CycleTimedThrottle(warmup, steady),
                                                    new WhiteThrottle(seed++, 50, 100, KBYTE),
                                                    1));
    }

    public static void main(String[] args) throws InterruptedException {
        Thread driver = new Thread(new LowThroughput());
        driver.start();
        driver.join();
    }

    @Override
    protected void createDrivers() {
        neutralThrottle();
        steadyState();
        noise();
        sin();
        sin();
    }

}
