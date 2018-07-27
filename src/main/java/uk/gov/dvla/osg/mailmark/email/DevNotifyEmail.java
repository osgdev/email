package uk.gov.dvla.osg.mailmark.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevNotifyEmail {

    private static final Logger LOG = LogManager.getLogger();
    private final DevNotifyEmailData data;
    private String subjectLine, msgText, from;

    private DevNotifyEmail(Builder builder) {
        this.subjectLine = builder.nestedSubjectLine;
        this.msgText = builder.nestedMsgText;
        this.from = builder.nestedFrom;
        data = new DevNotifyEmailData();
    }

    /**
     * Constructs email from settings in the email Credentials file 
     * and sends to Dev Team members.
     * 
     * @param nestedSubjectLine Subject line of the email
     * @param nestedMsgText Email text body
     * @param recipients comma separated list of email addresses
     */
    public void send() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String timeStamp = dateFormat.format(new Date());
        String msgBody = timeStamp + " - " + msgText;
        // Email salutation and signature lines
        String bodyHead = "Hello,\n\n";
        String bodyFoot = "\n\nPlease investigate ASAP\n\nThanks\n" + from;
        
        try {
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
        } catch (MessagingException mex) {
            LOG.error("Unable to create email: ", mex.getMessage());
        }
    }

    /**
     * Creates builder to build {@link DevNotifyEmail}.
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link DevNotifyEmail}.
     */
    public static final class Builder {
        private String nestedSubjectLine;
        private String nestedMsgText;
        private String nestedFrom;

        private Builder() {
        }

        public Builder subjectLine(String subjectLine) {
            this.nestedSubjectLine = subjectLine;
            return this;
        }

        public Builder msgText(String msgText) {
            this.nestedMsgText = msgText;
            return this;
        }

        public Builder from(String from) {
            this.nestedFrom = from;
            return this;
        }

        public void send() {
            DevNotifyEmail devNotifyEmail = new DevNotifyEmail(this);
            devNotifyEmail.send();
        }
    }

}
