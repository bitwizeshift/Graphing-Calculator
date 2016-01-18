package rodu4140;

import java.util.StringTokenizer;

/**
 * The <code>Complex</code> class represents complex numbers of the form a+bi.
 * The basic algebra operations are added, as well as a comparison operator to test whether two <code>Complex</code>s are identical.
 * <p>It can be constructed without parameters, at which case it is considered 0, otherwise it takes two parameters or a string</p>
 * <p>The class also contains static constants for commonly used Complex values (1, 0, -1)</p>
 * <p>All real and imaginary coefficients use the <code>Rational</code> library</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-27
 * @see Rational
 */
public class Complex implements Arithmetic<Complex>, Comparable<Complex>{

	public static final Complex ZERO = new Complex();
	public static final Complex ONE  = new Complex(Rational.ONE);
	public static final Complex NEG_ONE = new Complex(Rational.NEG_ONE);
	
	private Rational re = Rational.ZERO;
	private Rational im = Rational.ZERO;
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Initializes a newly created <code>Complex</code> so that it represents the number 0.
	 */
	public Complex() {
		this.re = Rational.ZERO;
		this.im = Rational.ZERO;
	}
	
	/**
	 * Constructs a new <code>Complex</code> with a real part <code>re</code> and
	 * imaginary part of 0.
	 * 
	 * @param re the real part
	 */
	public Complex(Rational re) {
		this.re = re;
	}
	
	/**
	 * Constructs a new <code>Complex</code> with a real part <code>re</code> and
	 * imaginary part <code>im</code>.
	 * 
	 * @param re the real part
	 * @param im the imaginary part
	 */
	public Complex(Rational re, Rational im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Initializes a newly created <code>Complex</code> so that it represents the same
	 * value as the argument; in other words, the newly created Complex is a copy of the argument Complex.
	 * 
	 * @param other another Complex object
	 */
	public Complex(Complex other) {
		this.re = new Rational(other.re);
		this.im = new Rational(other.im);
	}
	
	/**
	 * Constructs a new <code>Complex</code> by parsing a string. It assumes
	 * that the imaginary part is 0 if it is not specifically given
	 * 
	 * @param line string in the form of either "a|b" or "a"
	 * @throws ComplexException if string is not in proper form
	 */
	public Complex(String line) throws ComplexException{
		StringTokenizer ST= new StringTokenizer(line,"| ");
		String s;
		s = ST.nextToken();
		this.re = new Rational(s);
		if (ST.hasMoreTokens()){
			s = ST.nextToken();
		    this.im = new Rational(s);
		}
		if(ST.hasMoreTokens()) throw new ComplexException(ComplexException.Flags.INVALID_TOKEN);
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Adds two <code>Complex</code>s together and returns the sum
	 * 
	 * @param other the value to be added
	 * @return the value of the two Complex added
	 */
	@Override
	public Complex add(Complex other) {
		Complex res = new Complex();
		res.re = this.re.add(other.re);
		res.im = this.im.add(other.im);
		return res;
	}

	/**
	 * Subtracts two <code>Complex</code>s and returns the difference 
	 * 
	 * @param other the value to be subtracted
	 * @return the difference of the two Complex
	 */
	@Override
	public Complex sub(Complex other) {
		Complex res = new Complex();
		res.re = this.re.sub(other.re);
		res.im = this.im.sub(other.im);
		return res;
	}

	/**
	 * Multiplies two <code>Complex</code>s and returns the product
	 * 
	 * @param other the value to be multiplied
	 * @return the sum of the two Complex
	 */
	@Override
	public Complex mul(Complex other) {
		Complex res = new Complex();
		res.re = this.re.mul(other.re).sub(this.im.mul(other.im));
		res.im = this.im.mul(other.re).add(this.re.mul(other.im));
		return res;
	}

	
	
	/**
	 * Divides two <code>Complex</code>s and returns the quotient
	 * 
	 * @param other the value to be divided
	 * @return the quotient of the two Complex
	 * @throws ComplexException if other is zero
	 */
	@Override
	public Complex div(Complex other) throws ComplexException{
		if(other.equals(Complex.ZERO))throw new ComplexException(ComplexException.Flags.DIVISION_BY_ZERO);
		Complex res = new Complex();
		Complex con = other.conjugate();
		Rational divisor = new Rational(other.re.mul(other.re).add(other.im.mul(other.im)));
		
		res.re = this.re.mul(other.re).sub(this.im.mul(con.im)).div(divisor);
		res.im = this.im.mul(other.re).add(this.re.mul(con.im)).div(divisor);
		return res;
	}
	
	/**
	 * Evaluates this<sup>other</sup> and returns the result
	 * 
	 * @param other the value to be exponentiated (must be whole number)
	 * @return the calculated value
	 * @throws ComplexException if other is negative or non-integer
	 */
	@Override
	public Complex exp(Complex other) throws ComplexException{
		if(!other.im.equals(Rational.ZERO)) throw new ComplexException(ComplexException.Flags.INVALID_EXPONENT);
		if(!other.re.getDenominator().equals(Rational.ONE))throw new ComplexException(ComplexException.Flags.INVALID_EXPONENT);
		
		Complex res = new Complex(this);
		for(int i = 0; i<other.re.getNumerator().intValue()-1; i++) {
			res = res.mul(this);
		}
		return res;
	}
	
	/**
	 * Returns a <code>Complex</code> whose value is <code>(this mod other)</code>
	 * 
	 * @param other the value to find the modulo of
	 * @return the calculated value
	 */
	@Override
	public Complex mod(Complex other) {
		Complex res = new Complex();
		if(other.im.equals(Complex.ZERO)) {
			res.re = this.re.mod(other.re);
			res.im = this.im.mod(other.re);
		}else {
			res.re = this.re.mod(other.re).mul(Rational.NEG_ONE);
			res.im = this.im.mod(other.im).mul(Rational.NEG_ONE);
		}
		return res;
	}

	/**
	 * Calculates the conjugate of this complex value.
	 * The conjugate is this complex with the sign of the
	 * imaginary portion negated.
	 * @return the conjugate of this Complex value
	 */
	private Complex conjugate() {
		return new Complex(this.re, this.im.negate());
	}
	
	/**
	 * Rounds and returns a value of this <code>Complex</code> rounded up
	 * to the nearest whole number
	 * @return the rounded number
	 */
	public Complex ceil() {
		Complex res = new Complex(this);
		res.re = res.re.ceil();
		res.im = res.im.ceil();
		return res;
	}
	
	/**
	 * Rounds and returns a value of this <code>Complex</code> rounded down
	 * to the nearest whole number
	 * @return the rounded number

	 */
	public Complex floor() {
		Complex res = new Complex(this);
		res.re = res.re.floor();
		res.im = res.im.floor();
		return res;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Returns the real portion of this <code>Complex</code>
	 * @return the real part of this Complex
	 */
	public Rational getReal() {
		return this.re;
	}
	
	/**
	 * Returns the imaginary portion of this <code>Complex</code>
	 * @return the imaginary part of this Complex
	 */
	public Rational getImaginary() {
		return this.im;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Checks if this Complex has only a real part
	 * @return true if real, false otherwise
	 */
	public boolean isReal() {
		return (this.im.equals(Rational.ZERO));
	}
	
	/**
	 * Checks if this Complex has only a complex part
	 * @return true if imaginary, false otherwise
	 */
	public boolean isImaginary() {
		return (this.re.equals(Rational.ZERO) && !this.im.equals(Rational.ZERO));
	}
	
	/**
	 * Checks whether this value is complex
	 * @return true if complex, false otherwise
	 */
	public boolean isComplex() {
		return (!this.re.equals(Rational.ZERO) && !this.im.equals(Rational.ZERO));
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	   Compares the contents of two <code>Complex</code> objects and returns
	 * the equality
	 * 
	 * @param other the other Complex to be compared
	 * @return true if the same, false otherwise
	 */
	@Override 
	public boolean equals(Object other) {
		if(other.getClass()!=this.getClass()) return false;
		else {
    		Complex otherComplex = (Complex) other;
    		return this.re.equals(otherComplex.re) && this.im.equals(otherComplex.im);
    	}
	}
	
	
	/**
	 * Returns a string object representing the specified <code>Complex</code>.
	 * 
	 * <p>The string has three possible outcomes:</p>
	 * <ul>
	 * <li>The value is only real: it displays only the real portion of this complex</li>
	 * <li>The value is only imaginary: It displays only the imaginary portion of this complex</li>
	 * <li>The value is complex: It displays the string in the form of a+bi</li>
	 * </ul>
	 * 
	 * @return a String representation of the Complex
	 */
	@Override
	public String toString() {
		// If the value is complex
		if(this.isComplex()) {
			// If the imaginary part is 1, print a+i
			if(this.im.equals(Rational.ONE))
				return this.re + "+i";
			// If the imaginary part is -1, print a-i
			else if(this.im.equals(Rational.NEG_ONE))
				return this.re + "-i";
			// If it's positive, print a+bi
			else if(this.im.isPositive())
				return this.re + "+" + this.im +"i";
			// If it's negative, print a-bi
			else
				return this.re + "" + this.im +"i";
		}
		// If the value is just imaginary
		else if(this.isImaginary()) {
			if(this.im.equals(Rational.ONE))
				return "i";
			else if(this.im.equals(Rational.NEG_ONE)) 
				return "-i";
			else
				return this.im + "i";
		}
		// The value must be real, so print it
		else{
			return this.re + "";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Complex other) {
		int r = this.re.mul(this.re).add(this.im.mul(this.im))
				.compareTo(other.re.mul(other.re).add(other.im.mul(other.im)));
		return r;
	}
	
}
