package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.Constant;
import hawkshaw.throttles.WhiteDist;

/**
 * -XX:+PrintGCDetails -Xloggc:gc.log -XX:+PrintTenuringDistribution -Xmx5G -Xms5G
 *
 */
public class LongPaws extends ComposableDriver {

    private void constantVolume() {
        WhiteDist enqueue = new WhiteDist(seed++, 0, 1, 1);

        managers.add( new NThreadedManagedCache(new Constant(Integer.MAX_VALUE), 
                                                enqueue,
                                                new WhiteDist(seed++,  1, 10, KBYTE),
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
