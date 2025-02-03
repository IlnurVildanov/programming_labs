package exceptions;

public class StrangeBehaviorException extends RuntimeException {
    public StrangeBehaviorException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "StrangeBehaviorException: " + super.getMessage();
    }
}
