package hawkshaw.throttles;

public class ConstantThrottle implements NumberProducer {

    private final int value;

    public ConstantThrottle(int value) {
        this.value = value;
    }

    @Override
    public int next() {
        return value;
    }

}
