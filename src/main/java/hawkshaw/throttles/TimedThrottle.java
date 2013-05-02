package hawkshaw.throttles;


public final class TimedThrottle implements NumberProducer {

	private long time;
	private NumberProducer throttle;

	public TimedThrottle(NumberProducer throttle, long time) {
		this.throttle = throttle;
		this.time = time;

	}

	public long getTime() {
		return time;
	}

	@Override
	public int next() {
		return throttle.next();
	}
}
