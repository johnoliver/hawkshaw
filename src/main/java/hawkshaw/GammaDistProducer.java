package hawkshaw;

import cern.jet.random.Gamma;
import cern.jet.random.engine.MersenneTwister;

/**
 * Produces pseudo-random number as per the Gamma random number distribution
 */
public class GammaDistProducer implements RandomProducer {

    private final MersenneTwister mt;

    // p(x) = k * x^(alpha-1) * e^(-x/beta)
    private final Gamma gpdf;

    private GammaDistProducer(int mtSeed, double a, double b) {
        mt = new MersenneTwister(mtSeed);
        gpdf = new Gamma(a, b, mt);
    }

    public static GammaDistProducer of(int mtSeed, double a, double b) {
        return new GammaDistProducer(mtSeed, a, b);
    }

    @Override
    public double millisToLive() {
        return gpdf.nextDouble();
    }

}
