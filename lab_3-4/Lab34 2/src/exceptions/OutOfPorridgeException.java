package exceptions;

public class OutOfPorridgeException extends Exception {
    public OutOfPorridgeException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "OutOfPorridgeException: " + super.getMessage();
    }
}
