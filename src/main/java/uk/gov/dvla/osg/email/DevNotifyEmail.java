package uk.gov.dvla.osg.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevNotifyEmail {

    private static final Logger LOG = LogManager.getLogger();
    private static final String NEWLINE = "\n\n";
    
    private final String subjectLine, msgText, from;
    private final String smtpHost, smtpPort, username, password;
    private final Address[] contacts;
    private InternetAddress fromAddress;

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
        
        DevNotifyEmailData data = DevNotifyEmailDataFactory.newInstance();
        this.smtpHost = data.getHost();
        this.smtpPort = data.getPort();
        this.username = data.getUsername();
        this.password = data.getPassword();
        this.contacts = data.getContacts();
        this.fromAddress = data.getFrom();
    }

    /**
     * Constructs email from settings in the email Credentials file and sends to Dev
     * Team members.
     * 
     * @param nestedSubjectLine Subject line of the email
     * @param nestedMsgText Email text body
     * @param recipients comma separated list of email addresses
     */
    private void send() throws MessagingException {
        
        // Email salutation and signature lines
        String greeting = "Hello,";
        String msgBody = DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm") + " - " + msgText;
        String signature = "Please investigate ASAP" + NEWLINE +"Thanks," + NEWLINE + from;

        // Setup mail server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.smtp.starttls.ename", "false");

        // Setup session with authentication
        Session emailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(emailSession);
        message.setFrom(fromAddress);
        message.setRecipients(Message.RecipientType.TO, contacts);
        message.setSubject(subjectLine);
        message.setText(greeting + NEWLINE + msgBody + NEWLINE + signature);

        Transport.send(message);
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
                LOG.error("Unable to load email credentials file [{}]", DevNotifyEmailDataFactory.credentialsFile);
            } catch (MessagingException mex) {
                LOG.error("Unable to send email: {}", mex.getMessage());
            }
        }
    }

}
