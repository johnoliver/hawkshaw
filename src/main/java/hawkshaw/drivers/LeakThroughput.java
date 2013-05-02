package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.NeverThrottle;
import hawkshaw.throttles.WhiteThrottle;

public class LeakThroughput extends ComposableDriver {

    private void leak() {
        managers.add( new NThreadedManagedCache(    new NeverThrottle(),
                                                    new WhiteThrottle(seed++, 1, 2, 1), 
                                                    new ConstantThrottle(KBYTE),
                                                    5));
    }

    public static void main(String[] args) throws InterruptedException {
        Thread driver = new Thread(new LeakThroughput());
        driver.start();
        driver.join();
    }

    @Override
    protected void createDrivers() {
        leak();
        noise();
    }

}
