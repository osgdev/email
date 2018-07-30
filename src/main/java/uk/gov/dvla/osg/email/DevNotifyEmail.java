package uk.gov.dvla.osg.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevNotifyEmail {

    private static final Logger LOG = LogManager.getLogger();

    private final DevNotifyEmailData data;
    private String subjectLine, msgText, from;

    /**
     * Instantiates a new dev notify email.
     *
     * @param builder the builder
     * @throws JAXBException the JAXB exception
     */
    private DevNotifyEmail(Builder builder) throws JAXBException {
        this.subjectLine = StringUtils.defaultString(builder.nestedSubjectLine);
        this.msgText = StringUtils.defaultString(builder.nestedMsgText);
        this.from = StringUtils.defaultString(builder.nestedFrom);
        this.data = new DevNotifyEmailData();
    }

    /**
     * Constructs email from settings in the email Credentials file and sends to Dev
     * Team members.
     * 
     * @param nestedSubjectLine Subject line of the email
     * @param nestedMsgText Email text body
     * @param recipients comma separated list of email addresses
     */
    private void send() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String timeStamp = dateFormat.format(new Date());
        String msgBody = timeStamp + " - " + msgText;
        // Email salutation and signature lines
        String msgHead = "Hello,\n\n";
        String msgFoot = "\n\nPlease investigate ASAP\n\nThanks\n" + from;

        try {
            // Setup mail server
            Properties properties = new Properties();
            properties.put("mail.smtp.host", data.getHost());
            properties.put("mail.smtp.port", data.getPort());
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtp.starttls.ename", "false");

            // Setup session with authentication
            Session emailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(data.getUsername(), data.getPassword());
                }
            });
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(emailSession);
            // Set From: header field of the header.
            message.setFrom(data.getFromAddress());
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, data.getContacts());
            // Set Subject: header field
            message.setSubject(subjectLine);
            // Now set the actual message body
            message.setText(msgHead + msgBody + msgFoot);
            // Send message
            Transport.send(message);
        } catch (AddressException ex) {
            LOG.error("Email credentials file contains an invalid email address: {}", DevNotifyEmailData.credentialsFile);
        } catch (MessagingException mex) {
            LOG.error("Unable to send email: {}", mex.getMessage());
        }
    }

    /**
     * Creates builder to build {@link DevNotifyEmail}.
     * 
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

        /**
         * Email Subject line.
         * 
         * @param subjectLine the subject line
         * @return the builder
         */
        public Builder subjectLine(String subjectLine) {
            this.nestedSubjectLine = subjectLine;
            return this;
        }

        /**
         * Msg text for the email body. This should be a single line of text to which
         * the date and time will be pre-pended.
         * 
         * @param msgText the msg text
         * @return the builder
         */
        public Builder msgText(String msgText) {
            this.nestedMsgText = msgText;
            return this;
        }

        /**
         * The application sending the email. This is used in the email signature.
         * 
         * @param from the from line
         * @return the builder
         */
        public Builder from(String from) {
            this.nestedFrom = from;
            return this;
        }

        /**
         * Sends the email with the specified options.
         */
        public void send() {
            try {
                DevNotifyEmail devNotifyEmail = new DevNotifyEmail(this);
                devNotifyEmail.send();
            } catch (JAXBException ex) {
                LOG.error("Unable to load email credentials file [{}]", DevNotifyEmailData.credentialsFile);
            }
        }
    }

}
