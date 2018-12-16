package com.rodusek.graphingcalculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.StringTokenizer;

/**
 * The <code>Poly</code> class represents Polynomial functions which use <code>Rational</code> coefficients.
 * Basic algebra options are available, inherited from <code>Arithmetic</code> as well as a comparison operator 
 * to test whether the Polys are identical.
 * 
 * There are constructors that take variable numbers of <code>Rational</code> objects, an integer and an array, 
 * or a string parsing one that can take polynomial written in regular mathematical notation.
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 2.0, 2013-10-29
 * @since 1.0
 */
public class Poly implements Arithmetic<Poly> {
	
	// Instance Variables
	private int 		deg;			// for the degree
	private Complex [] coeffs; 		// for the array of coefficients
	private String		variable = "x";
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Initializes a newly created <code>Poly</code> so that it represents the number 0.
	 */
	public Poly() {
		this.deg 		= 0;
		this.coeffs 	= new Complex[1];
		this.coeffs[0] 	= new Complex(Rational.ZERO);
	}
		
	/**
	 * Constructs a new <code>Poly</code> from a series of <code>Rational</code> arguments 
	 * in ascending order of magnitude (the first argument is the constant value).
	 * 
	 * @param coeffs series of Rational arguments
	 */
	public Poly(final Complex ...coeffs) {
		this.deg = coeffs.length - 1;
		this.coeffs = coeffs;
		this.reducePoly();
	}
	
	/**
	 * Constructs a new <code>Poly</code> of degree <i>deg</i>, with Rational coefficients <i>coeffs</i>.
	 * 
	 * @param deg the degree of the polynomial
	 * @param coeffs the Rational coefficients of the polynomial
	 * @throws IllegalArgumentException if polynomial can't exist
	 * @since 1.0
	 */
	public Poly(final int deg, final Complex [] coeffs) throws IllegalArgumentException{
		if(deg + 1 == coeffs.length) {
			this.deg 	= deg;
			this.coeffs = coeffs;
			// Reduce the polynomial to minimal form
			this.reducePoly();
		}else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Constructs a new <code>Poly</code> by parsing a string.
	 * The string must be entered in the form of a mathematical equation,
	 * and may use the operators '+' (add), '-' (subtract), '/' (divide), 
	 * '*' (multiply), '^' (exponent), and may also include brackets.
	 * The string will parse and calculate following the BEDMAS order of operations
	 * 
	 * @param line string in the form of a mathematical equation
	 * @param variable the variable character to be used
	 * @throws PolyException if too many operands are given for the number of operands
	 * @throws OperatorException if too many operators are given for the number of operators
	 * @since 2.0
	 */
	public Poly(final String line, final String variable) throws OperatorException, PolyException{
		if(variable.length()>1)throw new PolyException(PolyException.Flags.BAD_VARIABLE);
		this.variable = variable;
		
		final Stack<Operator> operator = new Stack<Operator>();
		final Stack<Poly>     operand  = new Stack<Poly>();
		final Poly 			  result;
		boolean 			  lastVar  = false;	
		
		// The delimiters used by the tokenizer.
		final String delimiters = variable+"i+-*/^() ";
		final StringTokenizer input = new StringTokenizer(line, delimiters, true);
		String token = null;
		
		// ---------------------------------------------------------------------------------
		
		// Check to see if the first value is negative
		if (line.startsWith("-")) {
			// Read the first operand as a negative number.
			token = input.nextToken();
				
			final Poly p = new Poly(Complex.NEG_ONE);
			operand.push(p);
			operator.push(new Operator("*"));
		}
		
		// ---------------------------------------------------------------------------------
		
		// Parse the remaining input
		while (input.hasMoreTokens()) {
			token = input.nextToken();
			// Skip spaces
			if(token.equals(" ")) {
				continue;
			}
			// If the value is a variable, create a new poly equal to x
			else if(token.equals(this.variable)) {
				if(lastVar)throw new PolyException(PolyException.Flags.UNBALANCED_OPERAND);
				operand.push(new Poly(Complex.ZERO, Complex.ONE));
				lastVar = true;
			// If the value is imaginary, create a new poly equal to i
			}else if(token.equals("i")){
				if(lastVar)throw new PolyException(PolyException.Flags.UNBALANCED_OPERAND);
				operand.push(new Poly(new Complex(Rational.ZERO, Rational.ONE)));
				lastVar = true;
			}
			// If the value is an operator
			else if(Operator.isOperator(token)) {
				// Always push left brackets down
				if(token.equals("(")) {
					if(lastVar)throw new PolyException(PolyException.Flags.UNBALANCED_OPERAND);
					lastVar = false;
					operator.push(new Operator(token));
				}
				// If right bracket, pop and perform operations until left bracket is met
				else if(token.equals(")")) {
					while (!operator.isEmpty() && !operator.peek().toString().equals("(")) {
						try {
							operate(operator, operand);
						} catch (final Exception e) {
							throw new PolyException(PolyException.Flags.UNBALANCED_OPERATOR);
						}
					}
					if(operator.isEmpty()) {
						throw new OperatorException(OperatorException.Flags.NO_LEFT_PARENTHESIS);
					}
					operator.pop();
				}
				// Otherwise assume it's a new operator and push it down
				else {
					if(!lastVar)throw new PolyException(PolyException.Flags.UNBALANCED_OPERATOR);
					
					Operator op = new Operator(token);
					while(!operator.isEmpty() && op.precedes(operator.peek())) {
						operate(operator, operand);
					}
					operator.push(op);
					lastVar = false;
				}
			}
			// Assume remaining values must be numeric input
			else {
				if(lastVar)throw new PolyException(PolyException.Flags.UNBALANCED_OPERAND);
				
				operand.push(new Poly(new Complex(token)));
				lastVar = true;
			}
		}
		
		// ---------------------------------------------------------------------------------
		
		while (!operator.isEmpty()) {
		    try {
				operate(operator, operand);
		    } catch (final EmptyStackException e) {
		    	throw new PolyException(PolyException.Flags.UNBALANCED_OPERATOR);
		    }
		}
		if(operand.isEmpty()) 
			throw new PolyException(PolyException.Flags.NO_INPUT);
		result = operand.pop();
		
		if(!operand.isEmpty()) 
			throw new PolyException(PolyException.Flags.UNBALANCED_OPERATOR);
		
		this.coeffs = result.coeffs;
		this.deg    = result.deg;
	}

	/**
	 * Constructs a new <code>Poly</code> by parsing a string.
	 * The string must be entered in the form of a mathematical equation,
	 * and may use the operators '+' (add), '-' (subtract), '/' (divide), 
	 * '*' (multiply), '^' (exponent), and may also include brackets.
	 * The string will parse and calculate following the BEDMAS order of operations
	 * 
	 * @param line string in the form of a mathematical equation
	 * @throws PolyException if too many operands are given for the number of operands
	 * @throws OperatorException if too many operators are given for the number of operators
	 * @since 2.0
	 */
	public Poly(final String line) throws OperatorException, PolyException{
		this(line,"x");
	}
	
	/**
	 * Initializes a newly created <code>Poly</code> so that it represents the same
	 * value as the argument; in other words, the newly created Poly is a copy of the argument Poly.
	 * 
	 * @param other another Poly object
	 * @since 1.0
	 */
	public Poly(final Poly other){
		this.deg 	= other.deg;
		this.coeffs = new Complex[other.coeffs.length];
		for(int i = 0; i <= this.deg; i++) {
			this.coeffs[i] = new Complex(other.coeffs[i]);
		}
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Adds other to the Polynomial and returns the resultant Polynomial
	 * @param other The other polynomial to add to this
	 * @return the result of the addition
	 */
	@Override
	public Poly add(final Poly other) {
		// Variables
		Poly res = null;
		Complex [] high = this.coeffs.length >  other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] low	 = this.coeffs.length <= other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] coeffsRes = new Complex[high.length]; // Create array of Rational coefficients
		
		// Add the values of the same degree together
		for(int i = 0; i < low.length ; i++) {
			coeffsRes[i] = low[i].add(high[i]);
		}
		// Store the remaining degrees as the new array
		for(int i = low.length; i < high.length ; i++) {
			coeffsRes[i] = high[i];
		}
		// Create resultant Poly and return it
		res = new Poly(high.length-1, coeffsRes);
		
		return res;
	}
	
	/**
	 * Subtracts other from the Polynomial and returns the resultant polynomial
	 * @param other The other polynomial to subtract from this
	 * @return the result of the subtraction
	 */
	@Override
	public Poly sub(final Poly other) {
		// Variables
		Poly res = null;
		Complex [] high = this.coeffs.length >  other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] low	 = this.coeffs.length <= other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] coeffsRes = new Complex[high.length]; // Create array of Rational coefficients
		
		// If this Poly is the higher degree
		if(this.coeffs==high) {
			for(int i=0; i < low.length; i++) {
				// Subtract other from this
				coeffsRes[i] = high[i].sub(low[i]);
			}
			for(int i=low.length; i < high.length; i++) {
				// Add on the remaining values
				coeffsRes[i] = high[i];
			}
		// If this Poly is the lower degree
		}else {
			for(int i=0; i < low.length; i++) {
				// Subtract other from this
				coeffsRes[i] = low[i].sub(high[i]);
			}
			for(int i=low.length; i < high.length; i++) {
				// Subtract the remaining values from zero
				coeffsRes[i] = Complex.ZERO.sub(high[i]);
			}
		}
		// Create resultant Poly and return it
		res = new Poly(high.length-1, coeffsRes);
		
		return res;
	}
	
	/**
	 * Multiplies the Polynomial by other and returns the resultant polynomial
	 * @param other the other polynomial to multiply this by
	 * @return the result of the multiplication
	 */
	@Override
	public Poly mul(final Poly other) {
		// Variables
		Poly res = null;
		Complex [] high = this.coeffs.length >  other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] low	 = this.coeffs.length <= other.coeffs.length ? this.coeffs : other.coeffs;
		Complex [] coeffsRes = new Complex[high.length + low.length - 1]; // Create array of Rational coefficients
		// Fill the coefficient array with Rationals of value Zero
		for(int i=0; i < coeffsRes.length; i++) {
			coeffsRes[i] = new Complex(Rational.ZERO);
		}
		// Calculate the result of the multiplication
		for(int i=0; i < low.length; i++) {
			for(int j=0; j < high.length; j++) {
				coeffsRes[i+j] = coeffsRes[i+j].add(low[i].mul(high[j]));
			}
		}
		// Create resultant Poly and return it
		res = new Poly(high.length + low.length -2, coeffsRes);

		return res;
	}
	
	/**
	 * Divides the Polynomial by other and returns the resultant Polynomial
	 * other must of degree 0 for this to work, otherwise an exception is thrown
	 * @param other the other polynomial of degree 0 to divide by this
	 * @return the result of the division
	 * @throws PolyException if the divisor is invalid
	 */
	@Override
	public Poly div(final Poly other) throws PolyException{
		if(other.deg>0)throw new PolyException(PolyException.Flags.INVALID_DIVISOR);
		
		Poly 		res 	  = null;
		Complex [] coeffsRes = new Complex[this.deg+1];
		for(int i=0; i < coeffsRes.length; i++) {
			coeffsRes[i] = this.coeffs[i].div(other.coeffs[0]);
		}
		res = new Poly(this.deg, coeffsRes);
		
		return res;
	}
	
	/**
	 * Calculates <i>this<sup>other</sup></i> and returns that value.
	 * other must be a positive polynomial of degree 0.
	 * @param other the value to raise to the power of
	 * @return the calculated value
	 * @throws PolyException If exponent is invalid
	 */
	@Override
	public Poly exp(final Poly other) throws PolyException{
		if(!other.coeffs[0].getImaginary().equals(Rational.ZERO)) throw new PolyException(PolyException.Flags.INVALID_EXPONTENT);
		BigInteger e 	 = other.coeffs[0].getReal().getNumerator();
		Poly res = null;

		// Throw exceptions
		if(other.deg>0)throw new PolyException(PolyException.Flags.INVALID_EXPONTENT);		
		if(other.coeffs[0].getReal().getDenominator().compareTo(BigInteger.ONE)>0)throw new PolyException(PolyException.Flags.INVALID_EXPONTENT);
		if(other.coeffs[0].getReal().isNegative())throw new PolyException(PolyException.Flags.NEGATIVE_EXPONENT);
		if(this.coeffs[0].equals(Rational.ZERO) && e.equals(BigInteger.ZERO)) throw new PolyException(PolyException.Flags.INDETERMINATE_FORM);
		
		// If exponent is 0, Polynomial is just 1 (x^0 = 1);
		if(e.equals(BigInteger.ZERO)) res = new Poly(Complex.ONE);
		else {
			res = new Poly(this); // Copies current poly
			for(int i=0; i<e.intValue()-1; i++) {
				res = res.mul(this); // Multiply by itself e-1 times
			}
		}
		return res;
	}
	
	/**
	 * Returns a <code>Poly</code> whose value is <code>(this mod other)</code>
	 * 
	 * @return null (modulo not valid at this point in time)
	 */
	@Override
	public Poly mod(Poly other) {
		
		return null;
	}
	
	
	/**
	 * Returns a differentiated version of this <code>Poly</code>. The new
     * <code>Poly</code> has a degree one smaller than that of the source
     * <code>Poly</code>.
	 * 
	 * @return a differentiated version of this <code>Poly</code>.
	 */
	public Poly diff() {
		// Variables
		Complex [] coeffsRes 	= new Complex[this.deg];
		Poly 		res 		= null;
		
		if(this.deg>0) {
			// Calculate the new coefficients
			for(int i = this.deg; i > 0; i--) {
				coeffsRes[i-1] = this.coeffs[i].mul(new Complex(new Rational(i))); 
			}
			// Create and return new poly of 1 degree less, with new coefficients
			res = new Poly(this.deg-1, coeffsRes);
		}else {
			res = new Poly(Complex.ZERO);
		}
		res.setVariable(this.variable);
		return res;
	}
	
	/**
	 * Evaluates the polynomial at the Rational value, p
	 * 
	 * @return a Rational representation of the evaluation
	 * @see Rational
	 */
	public Complex evalAt(final Complex p) {
		// Variables
		Complex res = new Complex(Rational.ZERO); // Rational with value ZERO 
		
		// Use Horner's Method to calculate evaluation, then return value
		for (int i = this.deg; i >= 0; i--) {
			res = this.coeffs[i].add(p.mul(res));
		}
		return res;
	}
	
	/**
	 * Calculates and prints any Rational roots of the Polynomial.
	 * 
	 * <p>This method first calculates the lowest common multiple of the Poly,
	 * so that there is no denominator present. It then calculates it as a 
	 * Linear Diophantine Equation, using P as the integer constant term, and
	 * Q as the integer coefficient of the highest degree term.</p>
	 * 
	 * @see Tools#gcd(int[])
	 * @see Tools#factor(int, boolean)
	 */
	public void iRoots() {
		
		// Variables
		Poly 	 temp 	  = new Poly(this); 	// Copy of this Poly
		Complex x 		  = null;				// x to evaluate at
		Complex lcm;
		
		int[]	 denom    = new int[this.deg+1];// Array of integer denominators to find LCM of
		
		int		 offset	  = 0;					// The offset used to 'common factor' out x terms 
		
		Integer[] p_factors = null;				// Factors of the constant term
		Integer[] q_factors = null;				// Factors of the highest degree
		
		ArrayList<Rational> roots = new ArrayList<Rational>(); // the roots discovered
		String output		= "";				// The output string
		
		// Store all denominators in an array
		for(int i=0; i<=temp.deg; i++) {
			denom[i] = temp.coeffs[i].getReal().getDenominator().intValue();
		}
		// Calculate the lcm
		lcm = new Complex(new Rational(Tools.lcm(denom)));
		
		// multiply all the coefficients by the lcm to remove the denominator
		for(int i=0; i<=temp.deg; i++) {
			temp.coeffs[i] = temp.coeffs[i].mul(lcm); // Mul was overloaded to take integers
		}
		// If the constant term is zero, then 0 is a root
		if(temp.coeffs[0].equals(Complex.ZERO)) {
			roots.add(Rational.ZERO);
		}
		// Calculate how many terms are zero to common factor out x terms
		while(offset < temp.deg && temp.coeffs[offset].equals(Complex.ZERO)) {
			offset++;
		}
		// Calculate the factors of P and Q
		// Only P factors can contain negative values (this way there aren't redundant values)
		p_factors = Tools.factor(temp.coeffs[offset].getReal().getNumerator().intValue(), true);
		q_factors = Tools.factor(temp.coeffs[this.deg].getReal().getNumerator().intValue(), false);

		// Diophantine Equation
		// p is the factors of the constant term
		// q is the factors of the highest-degree coefficient
		// Evaluate the Poly at p/q and test if equal to zero
		for(int p : p_factors) {
			for(int q : q_factors) {
				x = new Complex(new Rational(p,q));
				if(temp.evalAt(x).getReal().equals(Rational.ZERO) ) {
					// if the root has not been found yet, add it to the list
					if(!roots.contains(x.getReal())) {
						roots.add(x.getReal());
					}
				}
			}
		}
		
		// If no roots, change output string
		if(roots.isEmpty()) {output = "No Rational roots!"; }
		// Otherwise list the roots
		else {
			for( int i = 0; i< roots.size(); i++ ) {
				output += roots.get(i);
				if(i!=roots.size()-1) {
					output += ", ";
				}
				
			}
		}
		System.out.println(output);

	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Compares the contents of two <code>Poly</code> objects and returns
	 * the equality based on the order of the coefficients and the degree
	 * @param obj the other Poly to be compared
	 * @return true if the same, false otherwise
	 */
	@Override
	public boolean equals(final Object obj) {
		// If comparing against self
		if(this==obj) { return true; }
		// If comparing against other Poly
		if(this.getClass()!=obj.getClass()) {
			return false;
		}else{
			Poly other = (Poly) obj;
			// Are the degrees the same?
			if(this.deg!=other.deg) {
				return false; 
			// Are all the coefficients the same?
			}else{	
				for(int i = this.deg; i >= 0; i--){
					if(!this.coeffs[i].equals(other.coeffs[i])){
						return false; 
					}
				}
			}
		}
		return true;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Returns a string object representing the specified Rational.
	 * 
	 * <p>If the denominator is equal to 1, then only the numerator is printed,
	 * however if the denominator is greater than 1, it prints it in the
	 * form of a/b, where a is the numerator, and b is the denominator</p>
	 * 
	 * @return a String representation of the Rational in base 10
	 */
	@Override
	public String toString() {
		String output = "";
		for(int i = this.deg; i>=0; i--) {
			if(!this.coeffs[i].equals(Complex.ZERO)) {
				if(i!=0 && this.coeffs[i].equals(Complex.NEG_ONE)) {
					output += "-";
				}
				else if(this.coeffs[i].isComplex()) {
					output += "(" + this.coeffs[i] + ")";
				}
				else if(i==0 || !this.coeffs[i].equals(Complex.ONE)){
					output += this.coeffs[i];
				}
				output += (i > 1 ? this.variable + "^" + i : (i == 1 ? this.variable : ""));	
			}
			if(i!=0 && (this.coeffs[i-1].getReal().isPositive()|| 
						this.coeffs[i-1].getImaginary().isPositive()||
						this.coeffs[i-1].isComplex())) {
				output += "+";
			}
		}
		if(this.deg==0) {
			output = this.coeffs[0].toString();
		}
		return output;
	}
	
	/**
	 * Changes the parameter 
	 * @param variable the letter variable to be set
	 * @throws PolyException if variable is more than 1 letter
	 */
	public void setVariable(String variable) throws PolyException {
		if(variable.length() > 1)throw new PolyException(PolyException.Flags.BAD_VARIABLE);
		this.variable = variable;
	}
	// ---------------------------------------------------------------------------------
	
	/**
	 * Performs mathematical operation popped from the operator stack, using the top
	 * two operands from the operand stack. 
	 * The result is pushed back onto the operand stack. 
	 * 
	 * @param operator pointer to a Stack of Character operators
	 * @param operand pointer to a Stack of Poly values
	 * @throws OperatorException An operator error
	 */
	private static void operate(final Stack<Operator> operator, final Stack<Poly> operand) throws OperatorException{
		final Operator op = operator.pop();
		if(op.toString()=="(")throw new OperatorException(OperatorException.Flags.NO_RIGHT_PARENTHESIS);
		final Poly q = operand.pop();
		final Poly p = operand.pop();
		operand.push((Poly) op.perform(p, q));
	}

	/**
	 * Reduces the Polynomial's degree to match the number of coefficients,
	 * also removes preceding 0 coefficients. This function is called upon creation of Poly objects
	 * @see #Poly(String)
	 */
	private void reducePoly() {
		// Variables
		int i = this.deg;
		
		// Count how many leading coefficients are 0
		while(i > 0 && this.coeffs[i].equals(Rational.ZERO)) {
			i--;
		}
		// If the degree changed
		if(this.deg - i != 0) {
			this.deg -= (this.deg - i); // Subtract the difference
			Complex [] newCoeffs = new Complex[this.deg+1];
			// Calculate new degree
			for(int j = 0; j <= this.deg; j++) {
				newCoeffs[j] = this.coeffs[j];
			}
			// Set pointer to the newly created array of coefficients
			this.coeffs = newCoeffs;
		}
	}
	
}
