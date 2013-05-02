package hawkshaw.throttles;

public class Constant implements NumberProducer {

    private final int value;

    public Constant(int value) {
        this.value = value;
    }

    @Override
    public int next() {
        return value;
    }

}
