package mx.udg.helpdesk.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 * This class connects to the active directory using a super user account. When
 * is already connected, an object of the active directory is returned and can
 * be used to query the active directory.
 *
 * @author Carlos Navapa
 */
public class ActiveDirectoryConnection {

    public ActiveDirectoryConnection() {
    }

    /**
     * Connects to the active directory using LDAP protocol. First, the class
     * connects to the active directory using a super user account, next, using
     * this object, the method query the active directory with the data of the
     * parameters.
     *
     * @param username
     * @param password
     * @return userID
     */
    public static Integer connectLDAP(String username, String password) {

        Integer userID = null;
        FacesContext context = FacesContext.getCurrentInstance();
        String HOST = context.getExternalContext().getInitParameter("LDAPConnection.HOST");
        Integer PORT = Integer.parseInt(context.getExternalContext().getInitParameter("LDAPConnection.PORT"));
        String BASE_DN = context.getExternalContext().getInitParameter("LDAPConnection.BASE_DN");
        String GENERIC_USER = context.getExternalContext().getInitParameter("LDAPConnection.GENERIC_USER");
        String GENERIC_PASSWORD = context.getExternalContext().getInitParameter("LDAPConnection.GENERIC_PASSWORD");

        try {
            String userDN;
            LDAPConnection connection = new LDAPConnection(HOST, PORT, GENERIC_USER, GENERIC_PASSWORD);

            SearchResult searchResults = connection.search(BASE_DN, SearchScope.SUB, "(email=" + username + ")");

            List<SearchResultEntry> entries = searchResults.getSearchEntries();

            for (SearchResultEntry entry : entries) {
                userDN = entry.getDN();
                try {
                    connection.close();
                    connection = new LDAPConnection(HOST, PORT, userDN, password);
                    userID = Integer.parseInt(entry.getAttributeValue("title"));
                }
                catch (LDAPException ex) {
                }
            }

            connection.close();
        }
        catch (LDAPException ex) {
        }
        return userID;
    }

    /**
     * Query the active directory by the userID, if exist, an hash map is built
     * with the data retrieved from the active directory using the userID
     *
     * @param userID
     * @return Employee data
     */
    public static HashMap<String, String> queryEmployeeData(int userID) {

        HashMap<String, String> employeeData = new HashMap<>();
        FacesContext context = FacesContext.getCurrentInstance();
        String HOST = context.getExternalContext().getInitParameter("LDAPConnection.HOST");
        Integer PORT = Integer.parseInt(context.getExternalContext().getInitParameter("LDAPConnection.PORT"));
        String BASE_DN = context.getExternalContext().getInitParameter("LDAPConnection.BASE_DN");
        String GENERIC_USER = context.getExternalContext().getInitParameter("LDAPConnection.GENERIC_USER");
        String GENERIC_PASSWORD = context.getExternalContext().getInitParameter("LDAPConnection.GENERIC_PASSWORD");

        try {
            LDAPConnection connection = new LDAPConnection(HOST, PORT, GENERIC_USER, GENERIC_PASSWORD);

            SearchResult searchResults = connection.search(BASE_DN, SearchScope.SUB, "(employeeID=" + userID + ")");
            List<SearchResultEntry> entries = searchResults.getSearchEntries();
            for (SearchResultEntry entry : entries) {

                employeeData.put("name", entry.getAttributeValue("givenName"));
                employeeData.put("lastname", entry.getAttributeValue("sn"));
                employeeData.put("email", entry.getAttributeValue("mail"));
            }
            connection.close();
        }
        catch (LDAPException ex) {
        }

        return employeeData;
    }
}
