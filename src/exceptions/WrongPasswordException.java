package exceptions;

/**
 * @author Nicola Landro
 * @author Luca Landa
 */
public class WrongPasswordException extends Exception {

	private static final long serialVersionUID = 1;

	/**
	 * Constructs a WrongPasswordException with no detail message. 
	 */
	public WrongPasswordException() {

	}

	/**
	 * Constructs a WrongPasswordException with the specified detail message.
	 * @param message the message witch you want display
	 */
	public WrongPasswordException(String message) {
		super(message);
	}

}
