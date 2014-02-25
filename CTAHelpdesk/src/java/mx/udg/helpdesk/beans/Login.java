package mx.udg.helpdesk.beans;

/**
 * This class is the implementation of the model from login.jsf
 *
 * @author Carlos Navapa
 */
public class Login {

    private String username;
    private String password;

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

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
