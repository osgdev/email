package uk.gov.dvla.osg.email;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DevNotifyEmail {

    private String _subjectLine;
    private String _msgText;
    private String _from;
    private EmailConfig data;

    /**
     * Creates builder to build {@link DevNotifyEmail}.
     * 
     * @return created builder
     * @throws DevEmailException 
     */
    public static DevNotifyEmail builder(File configFile) throws DevEmailException {
        return new DevNotifyEmail(configFile);
    }

    /**
     * Instantiates a new dev notify email.
     *
     * @param builder the builder
     * @throws DevEmailException 
     */
    private DevNotifyEmail(File configFile) throws DevEmailException {
        try {
            this.data = new ObjectMapper(new YAMLFactory()).readValue(configFile, EmailConfig.class);
        } catch (IOException ex) {
            throw new DevEmailException(String.format("Unable to load email config file [%s] : %s", configFile.getAbsolutePath(), ex.getMessage()));
        }
    }

    /**
     * Email Subject line.
     * 
     * @param subjectLine the subject line
     * @return the builder
     */
    public DevNotifyEmail subjectLine(String subjectLine) {
        this._subjectLine = subjectLine;
        return this;
    }

    /**
     * Msg text for the email body. This should be a single line of text to which
     * the date and time will be pre-pended.
     * 
     * @param msgText the msg text
     * @return the builder
     */
    public DevNotifyEmail msgText(String msgText) {
        this._msgText = msgText;
        return this;
    }

    /**
     * The application sending the email. This is used in the email signature.
     * 
     * @param from the from line
     * @return the builder
     */
    public DevNotifyEmail from(String from) {
        this._from = from;
        return this;
    }

    /**
     * Sends the email with the specified options.
     */
    public void send() throws DevEmailException {
        try {
            // Salutation, Body and Signature lines
            String greeting = "Hello,";
            String msgBody = DateUtils.timeStamp("dd/MM/yyyy HH:mm") + " - " + _msgText;
            String signature = "Please investigate ASAP\n\nThanks,\n" + _from;
            String bodyContent = StringUtils.join(Arrays.asList(greeting, msgBody, signature), "\n\n");

            // Setup mail server
            Properties properties = new Properties();
            properties.put("mail.smtp.host", data.getHost());
            properties.put("mail.smtp.port", data.getPort());
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtp.starttls.ename", "false");

            // Setup authentication
            EmailAuthenticator authenticator = new EmailAuthenticator(data.getUsername(), data.getPassword());

            // Authenticate with email server
            Session emailSession = Session.getInstance(properties, authenticator);

            // Construct the email
            MimeMessage message = EmailMessage.builder(emailSession)
                                              .from(data.getFrom())
                                              .recipients(data.getContacts())
                                              .subjectLine(_subjectLine)
                                              .message(bodyContent)
                                              .build();

            // Send the email
            Transport.send(message);
        } catch (MessagingException ex) {
            throw new DevEmailException(String.format("Unable to send email: %s", ex.getMessage()));
        }
    }
}
