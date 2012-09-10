package hawkshaw.throttles;

public class ConstantInitialPauseThrottle implements Throttle {

    private int initialPause;
    private int rest;
    private boolean isFirst = true;

    public ConstantInitialPauseThrottle(int initialPause, int rest) {
        this.initialPause = initialPause;
        this.rest = rest;
    }

    @Override
    public int millisTillEvent() {
        if (isFirst) {
            isFirst = false;
            return initialPause;
        }
        return rest;
    }

}
