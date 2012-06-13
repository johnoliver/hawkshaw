package hawkshaw;

public class ConstantThrottle implements Throttle {

    private final double value;

    public ConstantThrottle(double value) {
        this.value = value;
    }

    @Override
    public double millisTillEvent() {
        return value;
    }

}
