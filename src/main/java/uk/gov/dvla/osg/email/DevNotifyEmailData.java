package uk.gov.dvla.osg.email;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SystemUtils;

class DevNotifyEmailData {
    
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    
    public final static String credentialsFile;
    private final Credentials credentials;

    static {
        // Windows locations set for dev environment otherwise uses Unix filepath
        if (DEBUG_MODE) {
            credentialsFile = "C:/temp/email.xml";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            credentialsFile = "Z:/osg/resources/config/email.xml";
        } else {
            credentialsFile = "//aiw//osg//resources//config//email.xml";
        }
    }

    /**
     * Instantiates a new dev notify email data.
     * @throws JAXBException the JAXB exception
     */
    public DevNotifyEmailData() throws JAXBException {
        // load SMTP configuration from config file
        credentials = loadCredentials();
    }

    /**
     * Converts the XML in the SMTP config file into a credetials object
     * @return SMTP configuration information.
     * @throws JAXBException file does not contain valid XML.
     */
    private Credentials loadCredentials() throws JAXBException {
        File file = new File(credentialsFile);
        JAXBContext jc = JAXBContext.newInstance(Credentials.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (Credentials) unmarshaller.unmarshal(file);
    }


    /**
     * Gets the email account username.
     * @return the username
     */
    public String getUsername() {
        return credentials.getUsername();
    }

    /**
     * Gets the email account password.
     * @return the password
     */
    public String getPassword() {
        return credentials.getPassword();
    }

    /**
     * Gets the email account host IP address.
     * @return the host
     */
    public String getHost() {
        return credentials.getHost();
    }

    /**
     * Gets the port for the email account.
     * @return the port
     */
    public String getPort() {
        return credentials.getPort();
    }

    /**
     * Gets the email address to send from.
     * @return the from address
     * @throws AddressException the address exception
     */
    public InternetAddress getFromAddress() throws AddressException {
        return new InternetAddress(credentials.getFrom());
    }
    
    /**
     * Gets the email addresses for Dev Team members.
     * @return the contacts
     * @throws AddressException the address exception
     */
    public Address[] getContacts() throws AddressException {
        List<String> contacts = credentials.getContacts();
        Address[] addresses = new Address[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            addresses[i] = new InternetAddress(contacts.get(i));
        }
        return addresses;
    }
}
