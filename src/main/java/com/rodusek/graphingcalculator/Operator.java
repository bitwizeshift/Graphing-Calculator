package com.rodusek.graphingcalculator;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * An <code>Operator</code> object consists of a character and an arbitrary
 * precedence value for a binary integer operator. These operators are
 * <code>+ - / * % ^ ( )</code>. Given a pair of values this operator can be
 * applied to these values.
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-11-27
 */
public class Operator {
	
	private final String symbol;
	private final int 	 precedence;
	
	private final static Map<String, Integer> precedents;
	static {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("(", new Integer(0));
		map.put(")", new Integer(0));
		map.put("+", new Integer(1));
		map.put("-", new Integer(1));
		map.put("*", new Integer(2));
		map.put("/", new Integer(2));
		map.put("%", new Integer(2));
		map.put("^", new Integer(3));
		
		precedents = Collections.unmodifiableMap(map);
	}
	
	// ---------------------------------------------------------------
	
	/**
	 * Initializes the <code>Operator</code> with a string representing
	 * op.
	 * @param op the string representing the operator
	 * @throws OperatorException If the string is not an <code>Operator</code>
	 */
	public Operator(String op){
		if(!isOperator(op))throw new OperatorException(OperatorException.Flags.NOT_AN_OPERATOR);
		this.symbol = op;
		this.precedence = precedents.get(op);
	}
	
	// ---------------------------------------------------------------
	
	/**
     * Applies a binary operator to two <code>Arithmetic</code> parameters.
     * 
     * @param p the first <code>Arithmetic</code> for the binary operator
     * @param q the second <code>Arithmetic</code> for the binary operator
     * @return the result of applying the operator to x and y
     * @throws OperatorException if a left parenthesis is found, or if the operator is invalid
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
	public Arithmetic perform(final Arithmetic p, final Arithmetic q) throws OperatorException {
		Arithmetic result = null;
	
		switch(this.symbol) {
			case "+": result = (Arithmetic) p.add(q); break;
			case "-": result = (Arithmetic) p.sub(q); break;
			case "/": result = (Arithmetic) p.div(q); break;
			case "*": result = (Arithmetic) p.mul(q); break;
			case "%": result = (Arithmetic) p.mod(q); break;
			case "^": result = (Arithmetic) p.exp(q); break;
			case "(": throw new OperatorException(OperatorException.Flags.NO_RIGHT_PARENTHESIS); 
			default:  throw new OperatorException(OperatorException.Flags.CANNOT_PERFORM);
		}
		
		return result;
    }
	
    /**
     * Determines whether or not the input string is an operator.
     * 
     * @param s The string to compare against the operator pattern.
     * @return <code>true</code> if the input string is an operator,
     *         <code>false</code> otherwise.
     */
    public static boolean isOperator(final String s) {
    	return precedents.containsKey(s);
    }
	
    /**
     * Returns whether the current operator precedes another operator.
     * 
     * @param operator the operator to compare precedence against.
     * @return <code>true</code> if the current operator precedes the second
     *         operator, <code>false</code> otherwise.
     */
    public boolean precedes(final Operator operator) {
    	return this.precedence <= operator.precedence;
    }
    
    // ---------------------------------------------------------------
    
    /**
     * Compares to <code>Operator</code> objects by comparing their
     * symbolic values.
     * 
     * @return <code>true</code> if the symbols are the same
     * 		   <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object other) {
    	if(other.getClass()!=this.getClass()) return false;
    	else {
    		Operator otherOp = (Operator) other;
    		return this.symbol.equals(otherOp.symbol);
    	}
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return this.symbol;
    }
	
}
