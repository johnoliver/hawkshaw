package hawkshaw.throttles;

import cern.jet.random.Gamma;
import cern.jet.random.engine.MersenneTwister;

/**
 * Produces pseudo-random number as per the Gamma random number distribution
 */
public final class GammaDist implements NumberProducer {

    private static final int SCALING_FACTOR = 1000;
    private final MersenneTwister mt;

    // p(x) = k * x^(alpha-1) * e^(-x/beta)
    private final Gamma gpdf;
    private double scale;

    private GammaDist(int mtSeed, double alpha, double beta, double scale) {
        mt = new MersenneTwister(mtSeed);
        gpdf = new Gamma(alpha, beta, mt);
        this.scale = scale;
    }

    public static GammaDist of(int mtSeed, double alpha, double beta) {
        return new GammaDist(mtSeed, alpha, beta, SCALING_FACTOR);
    }

    public static GammaDist of(int mtSeed, double alpha, double beta, double scale) {
        return new GammaDist(mtSeed, alpha, beta, scale);
    }

    @Override
    public int next() {
        return (int) (scale * gpdf.nextDouble());
    }

}
