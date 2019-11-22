package uk.gov.dvla.osg.email;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AddressTypeAdapter extends XmlAdapter<String, InternetAddress> {

    @Override
    public String marshal(InternetAddress emailAddress) throws AddressException {
        return emailAddress.toString();
    }

    @Override
    public InternetAddress unmarshal(String emailAddress) throws AddressException {
        return new InternetAddress(emailAddress);
    }

}
