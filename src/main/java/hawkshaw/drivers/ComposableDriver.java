package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.Bursty;
import hawkshaw.throttles.WhiteDist;

import java.util.ArrayList;
import java.util.List;

public abstract class ComposableDriver implements Runnable {

    protected final List<NThreadedManagedCache> managers = new ArrayList<NThreadedManagedCache>();
    
    protected int seed = 123;
    
    protected static final int KBYTE = 1024;
    protected static final int MBYTE = 1024 * 1024;
    
    private long timeLimit = Long.MAX_VALUE;

    public ComposableDriver() {
    }

    public ComposableDriver(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public void run() {
        createDrivers();

        for (NThreadedManagedCache manager : managers) {
            manager.startAllocation();
        }

        try {
            Thread.sleep(timeLimit);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected void noise() {
        managers.add( new NThreadedManagedCache(new Bursty(seed++, 100, 1000), 
                                                new WhiteDist(seed++, 10, 100), 
                                                new WhiteDist(seed++, 1, 2, MBYTE),
                                                2));
    }

    protected abstract void createDrivers();

}
