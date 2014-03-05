package mx.udg.helpdesk.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import mx.udg.helpdesk.email.notifiers.JoinRequest;
import mx.udg.helpdesk.lang.LanguajeManager;
import mx.udg.helpdesk.ldap.ActiveDirectoryConnection;
import mx.udg.helpdesk.views.FacesMapping;

/**
 *
 * @author Carlos Navapa
 */
public class SignUp implements Serializable {

    private String userID;
    private String reason;

    private String name;
    private String lastname;
    private String email;

    public SignUp() {
    }

    /**
     * First, retrieves the user s data from the active directory, next, an
     * email is send to the administrators with the reason and the user data.
     */
    public void sendSignUpRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HashMap<String, String> employeeData = ActiveDirectoryConnection.queryEmployeeData(Integer.parseInt(userID));
        if (!employeeData.isEmpty()) {

            name = employeeData.get("name");
            lastname = employeeData.get("lastname");
            email = employeeData.get("email");

            if (name == null || lastname == null || email == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, LanguajeManager.getProperty("activeDirectoryError"), null));
            }
            else {

                JoinRequest joinRequest = new JoinRequest();
                ArrayList<String> userData = new ArrayList<>();

                userData.add(name);
                userData.add(lastname);
                userData.add(email);

                if (joinRequest.sendNotification(userData, reason)) {

                    NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                    navigationHandler.handleNavigation(facesContext, null, FacesMapping.getMapping("anywhere-login"));
                    facesContext.renderResponse();
                    facesContext.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, LanguajeManager.getProperty("requestSend"), null));
                }
                else {
                    facesContext.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, LanguajeManager.getProperty("sendingRequestError"), null));
                }
            }
        }
        else {
            facesContext.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, LanguajeManager.getProperty("invalidUserID"), null));
        }

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
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
