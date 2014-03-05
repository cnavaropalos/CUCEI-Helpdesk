package mx.udg.helpdesk.views;

import java.util.HashMap;

/**
 * This class contains all the URLS and redirections for the entire system.
 *
 * @author Carlos Navapa
 */
public class FacesMapping {

    public static HashMap<String, String> facesMapping = null;

    private FacesMapping() {
    }

    /**
     * Initializes all redirections for all pages in this tool.
     */
    private static void initializeFacesMapping() {
        facesMapping = new HashMap<>();
        facesMapping.put("anywhere-login", "login.jsf");
        facesMapping.put("anywhere-error", "error.jsf");
        facesMapping.put("anywhere-signUp", "signUp.jsf");

    }

    /**
     * Return the redirection URL according to face parameter.
     *
     * @param face The "short-word" for redirection
     * @return Redirection URL
     */
    public static String getMapping(String face) {
        if (facesMapping == null) {
            initializeFacesMapping();
        }

        String mapping = facesMapping.get(face);

        return mapping;
    }

}
