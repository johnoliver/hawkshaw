package hawkshaw.drivers;

import hawkshaw.GammaDistProducer;
import hawkshaw.LifecycleManager;
import hawkshaw.RandomProducer;

public class SimpleMain {

    private void run() throws InterruptedException {
        RandomProducer rp = GammaDistProducer.of(1234567, 5.0, 2.0);
        System.out.println("Starting LCM");
        LifecycleManager manager = new LifecycleManager(rp);
        for (int i = 0; i < 1_000_000; i++) {
            manager.cacheForRandomTime(Integer.toString(i));
        }
        manager.shutdown();
        System.out.println("All enqueued");
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleMain sm = new SimpleMain();
        sm.run();
    }

}
