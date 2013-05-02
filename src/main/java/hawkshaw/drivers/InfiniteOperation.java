package hawkshaw.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.ConstantThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.NumberProducer;

public class InfiniteOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteOperation.class);
    
    private static final int DEFAULT_ALLOCATION_SIZE = 500000;
    private static final int PERIOD_IN_MILLISECONDS = 2000;
    private static final int LEAK_SIZE_IN_BYTES = 4;

    private static void run() throws InterruptedException {
        NumberProducer createAt = new ConstantThrottle(0);

        // Remove at a constant rate
        NumberProducer deleteAt = GammaDistThrottle.of(1234567, 2.0, 2.0);
        LOGGER.debug("Starting LCM");
        while (true) {
            ManagedCache manager = new ManagedCache(deleteAt, createAt, LEAK_SIZE_IN_BYTES);
            manager.startAllocation(DEFAULT_ALLOCATION_SIZE);
            LOGGER.debug("All enqueued");
            Thread.sleep(PERIOD_IN_MILLISECONDS);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        run();
    }

    private InfiniteOperation() {}

}
