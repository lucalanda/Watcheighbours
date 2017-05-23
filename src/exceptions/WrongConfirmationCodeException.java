package exceptions;

/**
 * The <code> WrongConfirmationCodeException </code> is thrown
 * when the client tries to confirm an account registration with a 
 * wrong confirmation code
 * @author Nicola Landro
 * @author Luca Landa
 */
public class WrongConfirmationCodeException extends Exception{

	private static final long serialVersionUID = 1;


	/**
	 * Constructs a WrongConfirmationCodeException with no detail message.
	 */
	public WrongConfirmationCodeException() {

	}

	/**
	 * Constructs a WrongConfirmationCodeException with the specified datail message.
	 * @param message the message witch you want display
	 */
	public WrongConfirmationCodeException(String message) {
		super(message);
	}


}
