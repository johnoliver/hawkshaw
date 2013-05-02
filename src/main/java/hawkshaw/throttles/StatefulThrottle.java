package hawkshaw.throttles;

import cern.jet.random.Uniform;

public final class StatefulThrottle implements NumberProducer {

	private final Uniform uniform;
	private NumberProducer[] throttles;
	private NumberProducer currentThrottle;

	public StatefulThrottle(NumberProducer... throttles) {
		uniform = new Uniform(0, 1000, 123213);
		this.throttles = throttles;
		this.currentThrottle = throttles[0];

	}

	@Override
	public int next() {
		if (uniform.nextDouble() >= 990) {
			currentThrottle = throttles[Uniform.staticNextIntFromTo(0,
					throttles.length - 1)];
		}
		return currentThrottle.next();
	}
}
