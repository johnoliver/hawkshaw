package hawkshaw.throttles;

public class LongTime implements NumberProducer {

    public LongTime() {
    }

    @Override
    public int next() {
        return Integer.MAX_VALUE;
    }

}
