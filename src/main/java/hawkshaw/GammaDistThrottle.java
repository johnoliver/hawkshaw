package hawkshaw;

import cern.jet.random.Gamma;
import cern.jet.random.engine.MersenneTwister;

/**
 * Produces pseudo-random number as per the Gamma random number distribution
 */
public class GammaDistThrottle implements Throttle {

    private final MersenneTwister mt;

    // p(x) = k * x^(alpha-1) * e^(-x/beta)
    private final Gamma gpdf;

    private GammaDistThrottle(int mtSeed, double alpha, double beta) {
        mt = new MersenneTwister(mtSeed);
        gpdf = new Gamma(alpha, beta, mt);
    }

    public static GammaDistThrottle of(int mtSeed, double alpha, double beta) {
        return new GammaDistThrottle(mtSeed, alpha, beta);
    }

    @Override
    public double millisTillEvent() {
        return gpdf.nextDouble();
    }

}
