package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.Constant;
import hawkshaw.throttles.LongTime;
import hawkshaw.throttles.WhiteDist;

public class LeakThroughput extends ComposableDriver {

    private void leak() {
        managers.add( new NThreadedManagedCache(new LongTime(),
                                                new WhiteDist(seed++, 1, 2, 1), 
                                                new Constant(KBYTE),
                                                5));
    }

    public LeakThroughput(long timeLimit) {
        super(timeLimit);
    }

    public LeakThroughput() {
        super();
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
