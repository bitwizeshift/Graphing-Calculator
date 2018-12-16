package com.rodusek.graphingcalculator;

/**
 * The <code>RationalException</code> class handles all exceptions dealing with
 * incorrect use of Rational class, including using invalid parameters on method calls.
 * 
 * <p>This exception prints default error messages based upon the value of the
 * error passed from the <code>Flags</code> parameter.</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-27
 * @see Rational
 */
@SuppressWarnings("serial")
public class RationalException extends RuntimeException{
    
	/**
     * The enumerated error flags and matching strings.
     */
	public enum Flags{
		DIVISION_BY_ZERO("Denominator can't be zero."),
		INVALID_EXPONENT("Exponent must be integer value."),
		BAD_INPUT("Rational input can't be parsed.");
		
		private final String message;
		
		// Constructor assigns message strings to flags.
		Flags(String message) {
		    this.message = message;
		}
	
		// Returns the message string that matches the current error flag.
		public String getMessage() {
		    return this.message;
		}
		
		
	}

    // ---------------------------------------------------------------
	
	/**
     * Constructs the error from the specified flag
     * @param flag <code>Flags</code> constant signaling specific exception.
     */
	public RationalException(Flags flag) {
		super(flag.getMessage());
	}
}
