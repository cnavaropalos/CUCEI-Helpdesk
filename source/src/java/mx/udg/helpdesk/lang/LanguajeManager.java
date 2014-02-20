package mx.udg.helpdesk.lang;

import java.util.Locale;
import java.util.ResourceBundle;

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
