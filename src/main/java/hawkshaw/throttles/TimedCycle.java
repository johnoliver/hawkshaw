package hawkshaw.throttles;


/**
 * 
 * Cycles through other throttles at a fixed time period
 *
 */
public final class TimedCycle implements NumberProducer {

    private TimedNumberProducer[] throttles;
    private int currentThrottle;
    private long timeLastThrottleStarted;

    public TimedCycle(TimedNumberProducer... throttles) {
        this.throttles = throttles;
        this.currentThrottle = 0;
        this.timeLastThrottleStarted = System.currentTimeMillis();

    }

    @Override
    public int next() {
        if ((System.currentTimeMillis() - timeLastThrottleStarted) > throttles[currentThrottle].getTime()) {
            currentThrottle++;
            if (currentThrottle == throttles.length) {
                currentThrottle = 0;
            }
            timeLastThrottleStarted = System.currentTimeMillis();
        }
        return throttles[currentThrottle].next();
    }
}
