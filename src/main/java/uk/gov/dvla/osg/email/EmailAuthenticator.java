package uk.gov.dvla.osg.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {

    private PasswordAuthentication authentication;
    
    public EmailAuthenticator(String user, String password) {
        authentication = new PasswordAuthentication(user, password);
    }
    
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
