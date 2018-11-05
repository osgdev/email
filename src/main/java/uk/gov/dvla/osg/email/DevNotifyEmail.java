package uk.gov.dvla.osg.email;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DevNotifyEmail {

    private static final Logger LOG = LogManager.getLogger();
    private static final String NEWLINE = "\n\n";
    
    private final String subjectLine, msgText, from;
    EmailConfig data;
    
    /**
     * Instantiates a new dev notify email.
     *
     * @param builder the builder
     * @throws IOException 
     */
    private DevNotifyEmail(EmailBuilder builder) throws IOException {
        this.subjectLine = StringUtils.defaultString(builder.nestedSubjectLine);
        this.msgText = StringUtils.defaultString(builder.nestedMsgText);
        this.from = StringUtils.defaultString(builder.nestedFrom);
        this.data = new ObjectMapper(new YAMLFactory()).readValue(builder.emailConfig, EmailConfig.class);

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
        String signature = "Please investigate ASAP" + NEWLINE + "Thanks,\n" + from;

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

        MimeMessage message = new MimeMessage(emailSession);
        message.setFrom(data.getFrom());
        message.setRecipients(Message.RecipientType.TO, data.getContacts());
        message.setSubject(subjectLine);
        message.setText(greeting + NEWLINE + msgBody + NEWLINE + signature);

        Transport.send(message);
    }

    /**
     * Creates builder to build {@link DevNotifyEmail}.
     * 
     * @return created builder
     */
    public static EmailBuilder builder(File configFile) {
        return new EmailBuilder(configFile);
    }

    /**
     * Builder to build {@link DevNotifyEmail}.
     */
    public static class EmailBuilder {
        private String nestedSubjectLine;
        private String nestedMsgText;
        private String nestedFrom;
        private File emailConfig;

        private EmailBuilder(File configFile) {
            this.emailConfig = configFile;
        }

        /**
         * Email Subject line.
         * 
         * @param subjectLine the subject line
         * @return the builder
         */
        public EmailBuilder subjectLine(String subjectLine) {
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
        public EmailBuilder msgText(String msgText) {
            this.nestedMsgText = msgText;
            return this;
        }

        /**
         * The application sending the email. This is used in the email signature.
         * 
         * @param from the from line
         * @return the builder
         */
        public EmailBuilder from(String from) {
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
            } catch (IOException ex) {
                LOG.error("Unable to load email credentials file [{}] : {}", emailConfig.getAbsolutePath(), ex.getMessage());
            } catch (MessagingException mex) {
                LOG.error("Unable to send email: {}", mex.getMessage());
            }
        }
    }

}
