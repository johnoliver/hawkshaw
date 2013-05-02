package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.WhiteThrottle;

/**
 * -XX:+PrintGCDetails -Xloggc:gc.log -XX:+PrintTenuringDistribution -Xmx5G -Xms5G
 *
 */
public class LongPaws extends ComposableDriver {

    private void constantVolume() {
        WhiteThrottle enqueue = new WhiteThrottle(seed++, 0, 1, 1);

        managers.add( new NThreadedManagedCache(new ConstantThrottle(Integer.MAX_VALUE), 
                                                enqueue,
                                                new WhiteThrottle(seed++,  1, 10, 1024),
                                                2));
    }

    public static void main(String[] args) throws InterruptedException {
        Thread driver = new Thread(new LongPaws());
        driver.start();
        driver.join();
    }

    @Override
    protected void createDrivers() {
        noise();
        constantVolume();
    }
}
