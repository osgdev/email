package uk.gov.dvla.osg.email;

import java.io.File;
import java.lang.management.ManagementFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SystemUtils;

class DevNotifyEmailDataFactory {
    
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    
    public final static String credentialsFile;

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

    public static DevNotifyEmailData newInstance() throws JAXBException {
        File file = new File(credentialsFile);
        JAXBContext jc = JAXBContext.newInstance(DevNotifyEmailData.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (DevNotifyEmailData) unmarshaller.unmarshal(file);
    }

}
