package hawkshaw.throttles;

public class LongTime implements NumberProducer {

    @Override
    public int next() {
        return Integer.MAX_VALUE;
    }

}
