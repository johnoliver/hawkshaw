package hawkshaw.throttles;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

public final class BurstyThrottle implements NumberProducer {

	private static final int SCALING_FACTOR = 1000;
	private final MersenneTwister mt;

	private final Uniform uniform;
	private final Uniform burstProbability;
	private final Uniform burstLengthProbability;
	private int burstCount = 0;

	public BurstyThrottle(int mtSeed, double min, double max) {
		mt = new MersenneTwister(mtSeed);
		uniform = new Uniform(min, max, mt);
		burstProbability = new Uniform(0, 100, mt);
		burstLengthProbability = new Uniform(0, 1000, mt);
	}

	@Override
	public int next() {
		int scaling = SCALING_FACTOR;
		if (burstCount > 0) {
			burstCount--;
			scaling = scaling / 1000;

		} else {
			if (burstProbability.nextDouble() > 95) {
				System.out.println("burst");
				burstCount = burstLengthProbability.nextInt();
			}
		}
		return (int) (scaling * uniform.nextDouble());
	}

}
