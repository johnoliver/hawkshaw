package hawkshaw.throttles;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

/**
 * Produces numbers with a uniform distribution
 *
 */
public final class WhiteDist implements NumberProducer {

	private static final int SCALING_FACTOR = 1000;
	private final MersenneTwister mt;

	private final Uniform uniform;
	private double scale = SCALING_FACTOR;


	public WhiteDist(int mtSeed, double min, double max, double scale) {
		mt = new MersenneTwister(mtSeed);
		uniform = new Uniform(min, max, mt);
		this.scale = scale;
	}

	
	public WhiteDist(int mtSeed, double min, double max) {
		mt = new MersenneTwister(mtSeed);
		uniform = new Uniform(min, max, mt);
	}

	@Override
	public int next() {
		return (int) (scale * uniform.nextDouble());
	}

}
