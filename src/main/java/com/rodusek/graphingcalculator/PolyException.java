package com.rodusek.graphingcalculator;
/**
 * The <code>PolyException</code> class handles all exceptions dealing with
 * incorrect use of Poly class, including using invalid parameters on method calls.
 * 
 * <p>This exception prints default error messages based upon the value of the
 * error passed from the <code>Flags</code> parameter.</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-20
 * @see Poly
 * 
 */
@SuppressWarnings("serial")
public class PolyException extends RuntimeException {

	/**
     * The enumerated error flags and matching strings.
     */
    public static enum Flags {
		NO_LEFT_PARENTHESIS("Missing left parenthesis."), 
		UNBALANCED_OPERATOR("Missing operand."), 
		UNBALANCED_OPERAND("Missing operator."), 
		INDETERMINATE_FORM("Indeterminate Form (0^0)."),
		INVALID_EXPONTENT("Exponent must be constant integer (degree 0)."),
		NEGATIVE_EXPONENT("Exponent must be positive."),
		BAD_VARIABLE("Variable must be one character."), 
		NO_INPUT("No polynomial specified."),
		INVALID_DIVISOR("Divisor must be constant value (degree 0)."),
		INVALID_TOKEN("Token is not a valid operator, variable, or operand.");
	
		private final String message;
	
		Flags(String message) {
		    this.message = message;
		}
	
		public String getMessage() {
		    return this.message;
		}
    }

    // ---------------------------------------------------------------
    
    /**
     * Constructs the error from the specified flag
     * @param flag <code>Flags</code> constant signaling specific exception.
     */
    public PolyException(Flags flag) {
		super(flag.getMessage());
    }

}