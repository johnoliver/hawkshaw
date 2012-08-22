package hawkshaw.throttles;

public class ConstantThrottle implements Throttle {

    private final int value;

    public ConstantThrottle(int value) {
        this.value = value;
    }

    @Override
    public int millisTillEvent() {
        return value;
    }

}
