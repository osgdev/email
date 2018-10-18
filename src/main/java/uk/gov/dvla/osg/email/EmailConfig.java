package uk.gov.dvla.osg.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.annotation.JsonProperty;

class EmailConfig {

    @JsonProperty("password") 
    private String password;
    @JsonProperty("host") 
    private String host;
    @JsonProperty("port") 
    private String port;
    @JsonProperty("from") 
    private String from;
    @JsonProperty("username") 
    private String username;
    @JsonProperty("to") 
    private List<String> contacts = new ArrayList<>();

    /**
     * Gets the email account username.
     * 
     * @return the username
     */
    String getUsername() {
        return username;
    }

    /**
     * Gets the email account password.
     * 
     * @return the password
     */
    String getPassword() {
        return password;
    }

    /**
     * Gets the email account host IP address.
     * 
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port for the email account.
     * 
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Gets the email address to send from.
     * 
     * @return the from
     * @throws AddressException
     */
    public InternetAddress getFrom() {
        try {
            return new InternetAddress(from);
        } catch (AddressException ex) {
            return new InternetAddress();
        }
    }

    /**
     * Gets the email addresses for Dev Team members.
     * 
     * @return the Dev Team contacts
     */
    public Address[] getContacts() {
        return contacts.stream()
                       .map(str -> {
                                try {
                                    return new InternetAddress(str);
                                } catch (AddressException ex) {
                                    return new InternetAddress();
                                }})
                       .toArray(Address[]::new);
    }

}
