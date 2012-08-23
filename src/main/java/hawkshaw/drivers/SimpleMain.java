package hawkshaw.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.Throttle;

public class SimpleMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMain.class);
    
    private static final int LEAK_SIZE_IN_BYTES = 4;

    private static void run() {
        Throttle createAt = new ConstantThrottle(0);
        Throttle deleteAt = GammaDistThrottle.of(1234567, 5.0, 2.0);
        LOGGER.debug("Starting LCM");
        ManagedCache manager = new ManagedCache(createAt, deleteAt, LEAK_SIZE_IN_BYTES);
        manager.startAllocation(1500000);
        LOGGER.debug("All enqueued");
    }

    public static void main(String[] args) {
        run();
    }

}
