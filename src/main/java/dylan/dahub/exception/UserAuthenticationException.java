package dylan.dahub.exception;

public class UserAuthenticationException extends Exception{

    public UserAuthenticationException(String reason) {
        super(reason);
    }
}
