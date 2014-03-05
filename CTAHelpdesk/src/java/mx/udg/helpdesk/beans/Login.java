package mx.udg.helpdesk.beans;

import java.io.Serializable;
import mx.udg.helpdesk.views.FacesMapping;

/**
 * This class is the implementation of the model from login.jsf
 *
 * @author Carlos Navapa
 */
public class Login implements Serializable {

    private String username;
    private String password;

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    /**
     * Return the URL for login.xhtml
     *
     * @return URL
     */
    public String gotoLogin() {
        return FacesMapping.getMapping("anywhere-login");
    }

    /**
     * Return the URL for signUp.xhtml
     *
     * @return URL
     */
    public String gotoSignUp() {
        return FacesMapping.getMapping("anywhere-signUp");
    }

    /*
     Getters and Setters
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
