/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hawkshaw;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author boxcat
 */
public class LifecycleManager<T> {
    
    private final RandomProducer rp;
    
    private final Map<String, T> cache = new HashMap<>();
    
    private final ScheduledExecutorService stpe = Executors.newScheduledThreadPool(4);
    
    private static final int SCALING_FACTOR = 1000;
    
    public <T> LifecycleManager(RandomProducer rp_) {
        rp = rp_;
    }

    public void cacheForRandomTime(T t) {
        Callable<Void> clb = cacheWithRandomKey(t);
        
        int ttl = (int)(SCALING_FACTOR * rp.millisToLive());
        stpe.schedule(clb, ttl, TimeUnit.MILLISECONDS);
    }
    
    
    /**
     * This class is used to remove the key from the cache at some point in
     * the future, thus marking the value as eligible for GC
     */
    private class CacheCleaner implements Callable<Void> {
        private final String key;
        
        public CacheCleaner(String s) {
            key = s;
        }
        
        @Override
        public Void call() throws Exception {
            cache.remove(key);
            return null;
        }
    }
    
    /**
     * Caches the given object into the main cache.
     * 
     * Returns a handle which will remove the object again (which may be useful
     * for scheduling for future execution).
     * 
     * @param t
     * @return 
     */
    private Callable<Void> cacheWithRandomKey(T t) {
        // Generate a UUID String
        // This will generate garbage - 1 UUID object each time we put an 
        // object into the cache
        String uuid = UUID.randomUUID().toString();
        
        // Put in the cache
        cache.put(uuid, t);
     
        // Generate a cache cleaner (to be externally scheduled)
        return this.new CacheCleaner(uuid);
    }
    
}
