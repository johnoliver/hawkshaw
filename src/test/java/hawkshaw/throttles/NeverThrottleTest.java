package hawkshaw.throttles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NeverThrottleTest {

    public final static int OVER_TWO_BILLION_MILLISECONDS = Integer.MAX_VALUE;
    
    @SuppressWarnings("static-method")
    @Test
    public void throttlingWillEffectivelyNeverOccur() {
        NumberProducer neverThrottle = new NeverThrottle();
        assertEquals(OVER_TWO_BILLION_MILLISECONDS, neverThrottle.next());
    }

}
