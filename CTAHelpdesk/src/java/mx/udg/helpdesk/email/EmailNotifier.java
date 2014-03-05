package mx.udg.helpdesk.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This class sends emails using smtp server.
 *
 * @author Carlos Navapa
 */
public class EmailNotifier {

    /**
     * Using the parameters, this method sends an email to the desired
     * destinataries using smtp server.
     *
     * @param toList
     * @param subject
     * @param message
     * @return true if the email was successfully send.
     */
    public static boolean sendEmail(ArrayList<String> toList, String subject, String message) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        final String from = facesContext.getExternalContext().getInitParameter("EmailServer.ACCOUNT");
        final String password = facesContext.getExternalContext().getInitParameter("EmailServer.PASSWORD");
        final String host = facesContext.getExternalContext().getInitParameter("EmailServer.HOST");
        final String port = facesContext.getExternalContext().getInitParameter("EmailServer.PORT");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));

            InternetAddress[] address = new InternetAddress[toList.size()];
            for (int i = 0; i < toList.size(); i++) {
                address[i] = new InternetAddress(toList.get(i));
            }

            msg.setRecipients(Message.RecipientType.TO, address);

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            msg.setText(message.toString());

            Transport.send(msg);
        } catch (MessagingException ex) {
            return false;
        }
        return true;
    }

}
