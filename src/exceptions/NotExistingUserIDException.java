package exceptions;

/**
 * The <code> NotExistingUserIDException </code> is thrown 
 * when the client tries to log in with ad user ID account that has
 * not been registered yet
 * @author Nicola Landro
 * @author Luca Landa
 */
public class NotExistingUserIDException extends Exception {

	private static final long serialVersionUID = 1;


	
	/**
	 * Constructs a NotExistingUserIDException with no detail message.
	 */
	public NotExistingUserIDException() {

	}

	/**
	 * Constructs a NotExistingUserIDException with the specified detail message.
	 * @param message the specified message
	 */
	public NotExistingUserIDException(String message) {
		super(message);
	}

}
