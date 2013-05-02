package hawkshaw.throttles;


public final class TimedNumberProducer implements NumberProducer {

	private long time;
	private NumberProducer throttle;

	public TimedNumberProducer(NumberProducer throttle, long time) {
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
