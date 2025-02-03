package exceptions;

public class NoCleanPlatesException extends Exception {
    public NoCleanPlatesException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "NoCleanPlatesException: " + super.getMessage();
    }
}
