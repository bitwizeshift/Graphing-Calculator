package com.rodusek.graphingcalculator;
/**
 * The <code>OperatorException</code> class handles all exceptions dealing with
 * incorrect use of operators, including wrong operators or missing operators
 * which cause errors in calculating arithmetic.
 * 
 * <p>This exception prints default error messages based upon the value of the
 * error passed from the <code>Flags</code> parameter.</p>
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-10-29
 * @see Operator
 */
@SuppressWarnings("serial")
public class OperatorException extends RuntimeException {
    
    /**
     * The enumerated error flags and matching strings.
     */
    public static enum Flags {
        DIVISION_BY_ZERO("Cannot perform division by zero."), 
        NOT_AN_OPERATOR("Not an operator."), 
        CANNOT_PERFORM("Cannot execute this operator."), 
        NO_RIGHT_PARENTHESIS("Missing right parenthesis."),
        NO_LEFT_PARENTHESIS("Missing left parenthesis");
    
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
    public OperatorException(Flags flag) {
        super(flag.getMessage());
    }

}
