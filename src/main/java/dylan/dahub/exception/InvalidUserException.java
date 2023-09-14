package dylan.dahub.exception;

public class InvalidUserException extends Exception {
    public InvalidUserException(String reason) {
        super(reason);
    }
}
