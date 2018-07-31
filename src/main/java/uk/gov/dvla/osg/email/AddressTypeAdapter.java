package uk.gov.dvla.osg.email;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AddressTypeAdapter extends XmlAdapter<String, InternetAddress> {

    @Override
    public String marshal(InternetAddress arg0) throws Exception {
        return arg0.toString();
    }

    @Override
    public InternetAddress unmarshal(String str) throws Exception {
        return new InternetAddress(str);
    }

}
