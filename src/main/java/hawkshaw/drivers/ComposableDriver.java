package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.BurstyThrottle;
import hawkshaw.throttles.WhiteThrottle;

import java.util.ArrayList;
import java.util.List;

public abstract class ComposableDriver implements Runnable {

    protected final List<NThreadedManagedCache> managers = new ArrayList<NThreadedManagedCache>();
    
    protected int seed = 123;

    @Override
    public void run() {
        createDrivers();

        for (NThreadedManagedCache manager : managers) {
            manager.startAllocation();
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected void noise() {
        managers.add( new NThreadedManagedCache(new BurstyThrottle(seed++, 100, 1000), 
                                                new WhiteThrottle(seed++, 10, 100), 
                                                new WhiteThrottle(seed++, 1, 2, 1024 * 1024),
                                                2));
    }

    protected abstract void createDrivers();
}
