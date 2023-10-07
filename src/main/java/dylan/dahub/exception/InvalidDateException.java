package dylan.dahub.exception;

public class InvalidDateException extends Exception {
    public InvalidDateException(String reason) {
        super(reason);
    }

}