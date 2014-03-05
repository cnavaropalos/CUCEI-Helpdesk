package mx.udg.helpdesk.errorHandlers.exceptions;

/**
 * This exception is thrown when the session expired. Almost in all the cases
 * when this exception is thrown, the Exception handler will redirect you to
 * login.xhtml.
 *
 * @author Carlos Navapa
 */
public class NotUserLogedException extends Exception {

    public NotUserLogedException() {
    }

    public NotUserLogedException(String msg) {
        super(msg);
    }
}
