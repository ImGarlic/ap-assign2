package dylan.dahub.exception;

public class InvalidPostException extends Exception {
    public InvalidPostException(String reason) {
        super(reason);
    }

}
