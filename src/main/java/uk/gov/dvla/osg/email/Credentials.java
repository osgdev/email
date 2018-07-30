package uk.gov.dvla.osg.email;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class Credentials {

    @XmlElement(name="password")
    private String password;
    @XmlElement(name="host")
    private String host;
    @XmlElement(name="port")
    private String port;
    @XmlElement(name="from")
    private String from;
	@XmlElement(name="username")
	private String username;
	@XmlElementWrapper(name="addresses")
	@XmlElement(name="address")
	private List<String> contacts;
	
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
	public String getFrom() {
		return from;
	}
	
	/**
	 * Gets the email addresses for Dev Team members.
	 * @return the contacts
	 */
	public List<String> getContacts() {
        return contacts;
	}
	
}
