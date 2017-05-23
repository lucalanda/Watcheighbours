package exceptions;

/**
 * The <code> UserNotConfirmedException </code> is thrown when
 * the client tries to log in with an user account that has not been
 * confirmed yet
 * @author Nicola Landro
 * @author Luca Landa
 */
public class UserNotConfirmedException extends Exception {

	private static final long serialVersionUID = 1;


	
	/**
	 * Constructs a UserNotConfirmedException with no detail message.
	 */
	public UserNotConfirmedException() {

	}

	/**
	 * Constructs a UserNotConfirmedException with the specified detail message.
	 * @param message the detail message
	 */
	public UserNotConfirmedException(String message) {
		super(message);
	}

}
