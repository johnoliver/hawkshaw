package hawkshaw.throttles;


public final class SinThrottle implements NumberProducer {

	private double scale;

	private double step;

	private double current;

	public SinThrottle(int mtSeed, double offset, int period, double scale) {
		this.step = 2.0 * Math.PI / (double) period;
		this.scale = scale;
		this.current = offset;
	}

	@Override
	public int next() {
		current += step;
		if (current > Math.PI * 2)
			current = 0;
		return (int) (scale * Math.cos(current));
	}

}
