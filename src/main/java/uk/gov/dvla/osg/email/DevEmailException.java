package uk.gov.dvla.osg.email;

/**
 * The Class DevEmailException notifies the caller that an error occurred in the Dev Email application.
 */
public class DevEmailException extends Exception {

    /**
     * Instantiates a new dev email exception.
     *
     * @param message the message
     */
    public DevEmailException(String message) {
        super(message);
    }

}
