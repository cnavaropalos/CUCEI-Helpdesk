package mx.udg.helpdesk.errorHandlers;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * This class is the exception factory used by the exception handler
 *
 * @author Carlos Navapa
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

    private final ExceptionHandlerFactory parent;

    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /**
     * @see ExceptionHandlerFactory
     * @return ExceptionHandler object.
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = new CustomExceptionHandler(parent.getExceptionHandler());
        return result;
    }

}
