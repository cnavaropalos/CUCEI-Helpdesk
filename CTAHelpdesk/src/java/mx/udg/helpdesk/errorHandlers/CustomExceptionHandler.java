package mx.udg.helpdesk.errorHandlers;

import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import mx.udg.helpdesk.views.FacesMapping;

/**
 * This class handles the exceptions in the systems and redirects to the correct
 * page.
 *
 * @author Carlos Navapa
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private final ExceptionHandler wrapped;

    public CustomExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * @see ExceptionHandlerWrapper
     * @return ExceptionHandler object
     */
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    /**
     * All the exceptions are catch by this method, then, the method redirect
     * you to a new page decided by the algorithm.
     *
     * @see ExceptionHandlerWrapper
     * @throws FacesException
     */
    @Override
    public void handle() throws FacesException {
        Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

        while (iterator.hasNext()) {

            ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
            ExceptionQueuedEventContext exceptionContext = (ExceptionQueuedEventContext) event.getSource();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            Throwable throwable = exceptionContext.getException();

            if (throwable instanceof ViewExpiredException) {
                NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(facesContext, null, FacesMapping.getMapping("anywhere-login"));
                facesContext.renderResponse();
            } else if (throwable instanceof TimeoutException) {
                NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(facesContext, null, FacesMapping.getMapping("anywhere-login"));
                facesContext.renderResponse();
            } else {
                try {
                    Flash flash = facesContext.getExternalContext().getFlash();
                    flash.put("errorDetails", throwable.getMessage());
                    NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                    navigationHandler.handleNavigation(facesContext, null, FacesMapping.getMapping("anywhere-error"));
                    facesContext.renderResponse();
                } finally {
                    iterator.remove();
                }
            }
        }
        getWrapped().handle();
    }
}
