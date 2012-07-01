package hawkshaw.throttles;

public class NeverThrottle implements Throttle {

    public NeverThrottle() {
    }

    @Override
    public int millisTillEvent() {
        return Integer.MAX_VALUE;
    }

}
