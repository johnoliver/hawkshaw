package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.TimedCycle;
import hawkshaw.throttles.LongTime;
import hawkshaw.throttles.Sin;
import hawkshaw.throttles.TimedNumberProducer;
import hawkshaw.throttles.WhiteDist;

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

    private void neutralThrottle() {
        managers.add( new NThreadedManagedCache(    new WhiteDist(seed++, 0, 100),
                                                    new WhiteDist(seed++, 0, 100), 
                                                    new WhiteDist(333, 1, 1, MBYTE),
                                                    1));
    }

    private void sin() {
        managers.add( new NThreadedManagedCache(new Sin(seed++, 0, 1000, 1000) ,
                                                new Sin(seed++, Math.PI, 1000, 1000) , 
                                                new WhiteDist(seed++,  5, 10, MBYTE),
                                                2));
    }

    private void steadyState() {
        TimedNumberProducer warmup = new TimedNumberProducer(new WhiteDist(seed++, 0, 100), 100000);
        TimedNumberProducer steady = new TimedNumberProducer(new LongTime(), Long.MAX_VALUE);

        managers.add( new NThreadedManagedCache(    new LongTime(),
                                                    new TimedCycle(warmup, steady),
                                                    new WhiteDist(seed++, 50, 100, KBYTE),
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

    public LowThroughput() {
    }

    public LowThroughput(long timeLimit) {
        super(timeLimit);
    }
}
