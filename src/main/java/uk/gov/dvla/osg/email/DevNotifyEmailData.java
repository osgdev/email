package uk.gov.dvla.osg.email;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
class DevNotifyEmailData {

    @XmlElement(name="password")
    private String password;
    @XmlElement(name="host")
    private String host;
    @XmlElement(name="port")
    private String port;
    @XmlElement(name="from")
    @XmlJavaTypeAdapter(AddressTypeAdapter.class)
    private InternetAddress from;
	@XmlElement(name="username")
	private String username;
	@XmlElementWrapper(name="addresses")
	@XmlElement(name="address")
	@XmlJavaTypeAdapter(AddressTypeAdapter.class)
	private InternetAddress[] contacts;
	
	/**
	 * Gets the email account username.
	 * @return the username
	 */
	String getUsername() {
		return username;
	}
	
	/**
	 * Gets the email account password.
	 * @return the password
	 */
	String getPassword() {
		return password;
	}

	/**
	 * Gets the email account host IP address.
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Gets the port for the email account.
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Gets the email address to send from.
	 * @return the from
	 */
	public InternetAddress getFrom() {
		return from;
	}
	
	/**
	 * Gets the email addresses for Dev Team members.
	 * @return the Dev Team contacts
	 */
	public Address[] getContacts() {
        return contacts;
	}
	
}
