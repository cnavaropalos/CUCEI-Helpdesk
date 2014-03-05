package mx.udg.helpdesk.email.notifiers;

import java.util.ArrayList;
import mx.udg.helpdesk.email.EmailNotifier;

/**
 * This class build a message with the email data from the database for asking
 * permission for using the tool.
 *
 * @author carlosed
 */
public class JoinRequest {

    public JoinRequest() {
    }

    /**
     * This method parse the data from the database which contains the message
     * arquitechture and send it.
     *
     * @param userData
     * @param reason
     * @return true if the notification was send.
     */
    public boolean sendNotification(ArrayList<String> userData, String reason) {

        String subject = "test";
        String message = "test";
        ArrayList<String> toList = new ArrayList<>();

        toList.add("carlosnavapa@gmail.com");

        if (!EmailNotifier.sendEmail(toList, subject, message.toString())) {
            return false;
        }
        return true;

    }

}
