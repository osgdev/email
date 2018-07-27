package uk.gov.dvla.osg.mailmark.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DevNotifyEmailData {
    private static final Logger LOG = LogManager.getLogger();
    //private static final String OS = System.getProperty("os.name").toLowerCase();
    public final String credentialsFile, contactsFile;
    public final Credentials credentials;
    public final Address[] contacts;

    public DevNotifyEmailData() {
        // Windows locations set for dev environment otherwise uses Unix filepath
        if (SystemUtils.IS_OS_WINDOWS) {
            credentialsFile = "Z:/osg/resources/config/email.xml";
            contactsFile = "Z:/osg/resources/config/contacts.xml";
        } else {
            credentialsFile = "//aiw//osg//resources//config//email.xml";
            contactsFile = "//aiw//osg//resources//config//contacts.xml";
        }
        // load SMTP configuration from config file
        credentials = loadCredentials();
        // load dev team contact email addresses
        contacts = getContacts();
    }
    
    /**
     * Converts the XML in the SMTP config file into a credetials object
     * 
     * @return SMTP configuration information.
     * @throws JAXBException file does not contain valid XML.
     */
    private Credentials loadCredentials() {
        try {
            File file = new File(credentialsFile);
            JAXBContext jc = JAXBContext.newInstance(Credentials.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (Credentials) unmarshaller.unmarshal(file);
        } catch (JAXBException ex) {
            LOG.error("Unable to load data from email config file: {}", credentialsFile);
            return null;
        }
    }

    /**
     * Gets the list of dev team members' email addresses from the config file.
     * 
     * @return dev team email addresses.
     * @throws IOException config file cannot be located.
     * @throws AddressException file contains an invalid email address.
     */
    private Address[] getContacts() {
        try {
            List<String> list = Files.readAllLines(Paths.get(contactsFile));
            Address[] addresses = new Address[list.size()];
            for (int i = 0; i < list.size(); i++) {
                addresses[i] = new InternetAddress(list.get(i));
            }
            return addresses;
        } catch (IOException ex) {
            LOG.error("Unable to load contacts file: {}", contactsFile);
        } catch (AddressException ex) {
            LOG.error("Contacts file contains an invalid email address: {}", contactsFile);
        }
        return null;
    }
}