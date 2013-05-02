package hawkshaw.throttles;

public class ConstantInitialPauseThrottle implements NumberProducer {

	private int initialPause;
	private int rest;
	private boolean isFirst = true;
	private long start;

	public ConstantInitialPauseThrottle(int initialPause, int rest) {
		this.initialPause = initialPause;
		this.rest = rest;
	}

	@Override
	public int next() {
		if (isFirst) {
			this.start = System.currentTimeMillis();
			return 100000;
		} else if (System.currentTimeMillis() > start + initialPause) {
			return 100000;
		}
		return rest;
	}
}
