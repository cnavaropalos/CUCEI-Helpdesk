package mx.udg.helpdesk.beans;

import mx.udg.helpdesk.lang.LanguajeManager;
import mx.udg.helpdesk.views.FacesMapping;

/**
 *
 * @author Carlos Navapa
 */
public class Error {

    private String exceptionTitle;

    /**
     * Creates a new instance of Error
     */
    public Error() {
        exceptionTitle = LanguajeManager.getProperty("exceptionTitle");
    }

    /**
     * Return the URL for login.xhtml
     *
     * @return URL
     */
    public String gotoLogin() {
        return FacesMapping.getMapping("anywhere-login");
    }

    /*
     Getters and Setters
     */
    public String getExceptionTitle() {
        return exceptionTitle;
    }

    public void setExceptionTitle(String exceptionTitle) {
        this.exceptionTitle = exceptionTitle;
    }

}
