package exceptions;

public class PlateOverflowException extends RuntimeException {
    public PlateOverflowException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "PlateOverflowException: " + super.getMessage();
    }
}
