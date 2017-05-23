package exceptions;

/**
 * The <code> WrongDataException </code> is thrown when
 * the client perform an operation on server giving wrong data
 * @author Nicola Landro
 * @author Luca Landa
 */
public class WrongDataException extends Exception {

	private static final long serialVersionUID = 1;

	
	
	/**
	 * Constructs a WrongDataException with no detail message.
	 */
	public WrongDataException() {

	}

	/**
	 * Constructs a WrongDataException with the specified detail message.
	 * @param message the specified message
	 */
	public WrongDataException(String message) {
		super(message);
	}

}
