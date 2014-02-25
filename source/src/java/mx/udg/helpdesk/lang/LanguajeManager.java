package mx.udg.helpdesk.lang;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class retrieves the strings from the properties file in order to fill
 * the messages programatically.
 *
 * @author Carlos Navapa
 */
public class LanguajeManager {

    private static final LanguajeManager languajeManager = new LanguajeManager();
    private static ResourceBundle resourceBundle;

    private LanguajeManager() {

        resourceBundle = ResourceBundle.getBundle("mx.udg.helpdesk.lang.strings", new Locale("es"));

    }

    public static String getProperty(String name) {

        return resourceBundle.getString(name);
    }

}
