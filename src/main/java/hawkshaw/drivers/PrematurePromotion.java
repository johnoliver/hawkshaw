package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.WhiteDist;


/**
 * 
 * 
 * -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -Xloggc:gc.log -verbose:gc -Xmx1024m -XX:+PrintTenuringDistribution
 * 
 * 
 * For PP:
 *  -XX:NewRatio=2
 * For ok:
 *  -XX:NewRatio=1
 * 
 * 
 * 
 * 
 * -XX:MaxTenuringThreshold=15
 * 
 *
 */
public class PrematurePromotion extends ComposableDriver {

    private void constantVolume() {
        WhiteDist remove = new WhiteDist(seed++, 1, 2, 1000000);
        WhiteDist enqueue = new WhiteDist(seed++, 1, 2, 1);

        managers.add( new NThreadedManagedCache(remove, 
                                                enqueue,
                                                new WhiteDist(seed++,  320, 400, KBYTE),
                                                2));
    }

    public static void main(String[] args) throws InterruptedException {
        Thread driver = new Thread(new PrematurePromotion());
        driver.start();
        driver.join();
    }

    @Override
    protected void createDrivers() {
        noise();
        constantVolume();
    }

}
