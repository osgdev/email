package uk.gov.dvla.osg.mailmark.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevNotifyEmail {

    private static final Logger LOG = LogManager.getLogger();
    private final DevNotifyEmailData data;

    public static DevNotifyEmail getInstance() {
        return new DevNotifyEmail();
    }

    private DevNotifyEmail() {
        data = new DevNotifyEmailData();
    }

    /**
     * Constructs email from settings in the email Credentials file 
     * and sends to Dev Team members.
     * 
     * @param subjectLine Subject line of the email
     * @param msgText Email text body
     * @param recipients comma separated list of email addresses
     */
    public void send(String subjectLine, String msgText, String from) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String timeStamp = dateFormat.format(new Date());
        String msgBody = timeStamp + " - " + msgText;

        try {
            // Email salutation and signature lines
            String bodyHead = "Hello,\n\n";
            String bodyFoot = "\n\nPlease investigate ASAP\n\nThanks\n" + from;

            // Setup mail server
            Properties properties = new Properties();
            properties.put("mail.smtp.host", data.credentials.getHost());
            properties.put("mail.smtp.port", data.credentials.getPort());
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtp.starttls.ename", "false");

            // Setup authentication, get session
            Session emailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(data.credentials.getUsername(), data.credentials.getPassword());
                }
            });

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(emailSession);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(data.credentials.getFrom()));
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, data.contacts);
            // Set Subject: header field
            message.setSubject(subjectLine);
            // Now set the actual message body
            message.setText(bodyHead + msgBody + bodyFoot);
            // Send message
            Transport.send(message);
        } catch (AddressException ex) {
            LOG.error("Contacts file contains an invalid email address: {}", data.contactsFile);
        } catch (MessagingException mex) {
            LOG.error("Unable to create email: ", mex.getMessage());
        }
    }

}
