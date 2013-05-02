package hawkshaw.throttles;

public class NeverThrottle implements NumberProducer {

    public NeverThrottle() {
    }

    @Override
    public int next() {
        return Integer.MAX_VALUE;
    }

}
