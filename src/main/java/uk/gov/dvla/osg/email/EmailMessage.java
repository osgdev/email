package uk.gov.dvla.osg.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailMessage {

    private EmailMessage() { }
    
    /**
     * Creates builder to build {@link EmailMessage}.
     * @param emailSession 
     * @return created builder
     */
    public static Builder builder(Session emailSession) {
        return new Builder(emailSession);
    }
    
    /**
     * Builder to build {@link EmailMessage}.
     */
    public static final class Builder {
        private Session session;
        private InternetAddress from;
        private Address[] _contacts;
        private String subjectLine;
        private String messageBody;

        private Builder(Session emailSession) {
            this.session = emailSession;
        }

        /**
        * Builder method for from parameter.
        * @param internetAddress field to set
        * @return builder
        */
        public Builder from(InternetAddress internetAddress) {
            this.from = internetAddress;
            return this;
        }

        /**
        * Builder method for contacts parameter.
        * @param contacts field to set
        * @return builder
        */
        public Builder recipients(Address[] contacts) {
            this._contacts = contacts;
            return this;
        }

        /**
        * Builder method for subjectLine parameter.
        * @param subjectLine field to set
        * @return builder
        */
        public Builder subjectLine(String subject) {
            this.subjectLine = subject;
            return this;
        }

        /**
        * Builder method for message parameter.
        * @param message field to set
        * @return builder
        */
        public Builder message(String message) {
            this.messageBody = message;
            return this;
        }

        /**
        * Builder method of the builder.
        * @return built class
         * @throws MessagingException 
        */
        public MimeMessage build() throws MessagingException {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, _contacts);
            message.setSubject(subjectLine);
            message.setText(messageBody);
            return message;
        }
    }
    

}
