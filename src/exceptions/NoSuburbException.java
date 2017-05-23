package exceptions;

/**
 * The <code> NoSuburbException </code> is thrown
 * when an attempt to determine a suburb with other location data failes
 * @author Nicola Landro
 * @author Luca Landa
 */
public class NoSuburbException extends Exception {

	private static final long serialVersionUID = 1L;

	
	
	/**
	 * Constructs a NoSuburbException with no detail message.
	 */
	public NoSuburbException() {

	}

	/**
	 * Constructs a NoSuburbException with the specified detail message.
	 * @param message the detail message
	 */
	public NoSuburbException(String message) {
		super(message);
	}

}
