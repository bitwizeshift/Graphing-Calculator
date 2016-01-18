package rodu4140;
/**
 * The <code>ComplexException</code> class handles all exceptions dealing with
 * incorrect use of Complex class, including using invalid parameters on method calls.
 * 
 * <p>This exception prints default error messages based upon the value of the
 * error passed from the <code>Flags</code> parameter.</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-27
 * @see Complex
 */
@SuppressWarnings("serial")
public class ComplexException extends RuntimeException{

	/**
     * The enumerated error flags and matching strings.
     */
	public static enum Flags{
		
		COMPLEX_EXCEPTION(""),
		INVALID_EXPONENT("Exponent must be real integer."),
		INVALID_TOKEN("Token is invalid value."),
		DIVISION_BY_ZERO("Denominator can't be zero.");
		
		private final String message;
		
		Flags(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
	
	/**
     * Constructs the error from the specified flag
     * @param flag <code>Flags</code> constant signaling specific exception.
     */
	public ComplexException(Flags flag) {
		super(flag.getMessage());
	}
	
}
