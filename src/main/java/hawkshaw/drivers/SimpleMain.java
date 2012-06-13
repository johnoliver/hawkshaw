/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hawkshaw.drivers;

import hawkshaw.GammaDistProducer;
import hawkshaw.LifecycleManager;
import hawkshaw.RandomProducer;

import java.util.UUID;

/**
 *
 * @author boxcat
 */
public class SimpleMain {
    
    private LifecycleManager<String> lcm;
    
    private void run() throws InterruptedException {
        RandomProducer rp = GammaDistProducer.of(1234567, 5.0, 2.0);
        System.out.println("Starting LCM");
        lcm = new LifecycleManager<>(rp);
        for (int i=0; i<1_000_000; i++) {
            lcm.cacheForRandomTime(UUID.randomUUID().toString());
        }
        System.out.println("All enqueued");
        Thread.sleep(10_000);
        System.out.println("Done");        

    }
    
    public static void main(String[] args) throws InterruptedException {
        SimpleMain sm = new SimpleMain();
        sm.run();
    }
    
}
