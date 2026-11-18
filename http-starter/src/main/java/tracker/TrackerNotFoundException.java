package tracker;

public class TrackerNotFoundException extends RuntimeException {
    public TrackerNotFoundException(String message) {
        super(message);
    }
}
