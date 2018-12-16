package com.rodusek.graphingcalculator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.StringTokenizer;

/**
 * The <code>Rational</code> class represents fractional numbers in the form of a/b, where a and b are both integers.
 * The basic algebra operations are added, as well as a comparison operator to test whether two <code>Rational</code>s are identical.
 * <p>It can be constructed without parameters, at which case it is considered 0, otherwise it takes two parameters or a string</p>
 * <p>The class also contains static constants for commonly used Rational values (1/1, 0/1, -1/0)</p>
 * <p>As of version 2.0, it supports BigInteger values so that the rational can be any size</p>
 * 
 * @author Matthew Rodusek
 * @version 2.5, 2013-11-27
 * @since 1.0
 *
 */
public class Rational implements Comparable<Rational>, Arithmetic<Rational>{

	// Constant values
	public static final Rational ONE 		= new Rational(1);
	public static final Rational ZERO 		= new Rational(0);
	public static final Rational NEG_ONE 	= new Rational(-1);
	
	private BigInteger num = BigInteger.ZERO; // Numerator
	private BigInteger den = BigInteger.ONE;  // Denominator
	

	// ---------------------------------------------------------------------------------
	
	/**
	 * Initializes a newly created <code>Rational</code> so that it represents the number 0.
	 * 
	 */
	public Rational(){
		this.num = BigInteger.ZERO; this.den = BigInteger.ONE;
	}
	
	/**
	 * Constructs a new <code>Rational</code> with a numerator of <code>num</code> and
	 * denominator of 1. This does not require the Rational to be normalized.
	 * 
	 * @param num the value in the numerator
	 */
	public Rational(final long num){
		this.num = BigInteger.valueOf(num); this.den = BigInteger.ONE;
	}
	
	/**
	 * Constructs a new <code>Rational</code> with a numerator of <code>num</code> and
	 * denominator of <code>den</code>.
	 * 
	 * @param num the value in the numerator
	 * @param den the value in the denominator, it cannot be 0
	 * @throws IllegalArgumentException if b = 0
	 */
	public Rational(final long num, final long den) throws IllegalArgumentException{
		if(den==0) throw new IllegalArgumentException("Denominator can't be zero");
		this.num = BigInteger.valueOf(num);
		this.den = BigInteger.valueOf(den);
		this.normalize();
	}
	
	/**
	 * Constructs a new <code>Rational</code> with a numerator of <code>num</code> and
	 * denominator of <code>den</code>.
	 * 
	 * @param num the value in the numerator
	 * @param den the value in the denominator, it cannot be 0
	 * @throws IllegalArgumentException if b = 0
	 */
	public Rational(final BigInteger num, final BigInteger den) throws IllegalArgumentException{
		if(den.equals(BigInteger.ZERO)) throw new IllegalArgumentException("Denominator can't be zero");
		this.num = num;
		this.den = den;
		this.normalize();
	}
	
	/**
	 * Initializes a newly created <code>Rational</code> so that it represents the same
	 * value as the argument; in other words, the newly created Rational is a copy of the argument Rational.
	 * 
	 * @param other another Rational object
	 * @since 1.5
	 */
	public Rational(final Rational other) {
		this.num = other.num;
		this.den = other.den;
	}
	
	/**
	 * Constructs a new <code>Rational</code> by parsing a string. It assumes
	 * that the denominator is 1 if it is not specifically given
	 * 
	 * @param line string in the form of either "a/b" or "a"
	 * @throws RationalException if string is not in proper form
	 */
	public Rational(final String line) throws RationalException{
		StringTokenizer ST = new StringTokenizer(line,"/ ");
		if(ST.countTokens()>2 || ST.countTokens()<1) throw new RationalException(RationalException.Flags.BAD_INPUT);
		String s = ST.nextToken();
		this.num = new BigInteger(s);
		if(ST.hasMoreTokens()){
			s = ST.nextToken();
			this.den = new BigInteger(s);
			if(this.den.equals(BigInteger.ZERO)) throw new RationalException(RationalException.Flags.DIVISION_BY_ZERO);
		}
		this.normalize();
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Adds two <code>Rational</code>s together and returns the sum
	 * 
	 * @param other the value to be added
	 * @return the value of the two Rationals added
	 */
	@Override
	public Rational add(final Rational other){
		Rational res = new Rational();
		res.num = this.num.multiply(other.den).add(this.den.multiply(other.num));
		res.den = this.den.multiply(other.den);
		res.normalize();
		return res;
	}
	
	/**
	 * Subtracts two <code>Rational</code>s and returns the difference 
	 * 
	 * @param other the value to be subtracted
	 * @return the difference of the two Rationals
	 */
	@Override
	public Rational sub(final Rational other){
		Rational res = new Rational();
		res.num = this.num.multiply(other.den).subtract(this.den.multiply(other.num));
		res.den = this.den.multiply(other.den);
		res.normalize();
		return res;
	}
	
	/**
	 * Multiplies two <code>Rational</code>s and returns the product
	 * 
	 * @param other the value to be multiplied
	 * @return the sum of the two Rationals
	 */
	@Override
	public Rational mul(final Rational other){
		Rational res = new Rational();
		res.num = this.num.multiply(other.num);
		res.den = this.den.multiply(other.den);
		res.normalize();
		return res;
	}
	
	/**
	 * Divides two <code>Rational</code>s and returns the quotient
	 * 
	 * @param other the value to be divided
	 * @return the quotient of the two Rationals
	 * @throws RationalException if other is zero
	 */
	@Override
	public Rational div(final Rational other) throws RationalException {
		if(other.num.equals(BigInteger.ZERO)) throw new RationalException(RationalException.Flags.DIVISION_BY_ZERO);
		Rational res = new Rational();
		res.num = this.num.multiply(other.den);
		res.den = this.den.multiply(other.num);
		res.normalize();
		return res;
	}
	/**
	 * Evaluates this<sup>other</sup> and returns the result
	 * 
	 * @param other the value to be exponentiated (must be whole number)
	 * @return the calculated value
	 * @throws RationalException if other is negative or non-integer
	 */
	@Override
	public Rational exp(final Rational other) throws RationalException {
		if(!other.den.equals(BigInteger.ONE)) throw new RationalException(RationalException.Flags.INVALID_EXPONENT);
		
		Rational res = new Rational();
		res.num = this.num.pow(this.num.intValue());
		res.den = this.den;
		res.normalize();
		return res;
	}
	
	/**
	 * Returns a <code>Rational</code> whose value is <code>(this mod other)</code>
	 * 
	 * @param other the value to find the modulo of
	 * @return the calculated value
	 */
	@Override
	public Rational mod(Rational other) {
		Rational res = new Rational(this);
		res = res.sub(other.mul(res.div(other).floor()));
		return res;
	}
	
	/**
	 * Negates and returns a <code>Rational</code> whose sign is the opposite of
	 * this.
	 * 
	 * @return the negated Rational
	 */
	public Rational negate() {
		Rational res = new Rational(this);
		res.num = this.num.negate();
		return res;
	}
	
	/**
	 * Rounds and returns a value of this <code>Rational</code> rounded up
	 * to the nearest whole number
	 * @return the rounded number
	 */
	public Rational ceil() {
		Rational res = new Rational(this);
		if(!res.den.equals(BigInteger.ONE)) {
			res.num = res.num.divide(res.den).add(BigInteger.ONE);
			res.den = BigInteger.ONE;
		}
		return res;
	}
	
	/**
	 * Rounds and returns a value of this <code>Rational</code> rounded down
	 * to the nearest whole number
	 * @return the rounded number
	 */
	public Rational floor() {
		Rational res = new Rational(this);
		if(!res.den.equals(BigInteger.ONE)) {
			res.num = res.num.divide(res.den);
			res.den = BigInteger.ONE;
		}
		return res;
	}
	// ---------------------------------------------------------------------------------
	
	/**
	 * Returns the numerator of the <code>Rational</code>
	 * 
	 * @return the numerator value (num)
	 */
	public BigInteger getNumerator() {
		return num;
	}
	
	/**
	 * Returns the denominator of the <code>Rational</code>
	 * 
	 * @return the denominator value (den)
	 */
	public BigInteger getDenominator() {
		return den;
	}
	
	/**
	 * Evaluates the Rational and returns the value
	 * 
	 * @return the evaluated polynomial
	 */
	public BigDecimal eval() {
		
		return new BigDecimal(this.num).divide(new BigDecimal(this.den),10, RoundingMode.HALF_UP);
	}

	// ---------------------------------------------------------------------------------
	
	/**
	 * Checks whether the <code>Rational</code> is positive and returns a boolean
	 * 
	 * @return true if the rational is positive, false if negative
	 */
	public boolean isPositive() {
		return this.num.compareTo(BigInteger.ZERO) > 0;
	}
	
	/**
	 * Checks whether the <code>Rational</code> is negative and returns a boolean
	 * 
	 * @return true if the rational is negative, false if positive
	 */
	public boolean isNegative() {
		return this.num.compareTo(BigInteger.ZERO) < 0;
	}
	
	/**
	 * Compares the contents of two <code>Rational</code> objects and returns
	 * the equality
	 * 
	 * @param other the other Rational to be compared
	 * @return true if the same, false otherwise
	 */
	@Override
	public boolean equals(Object other){
		// If comparing against self
		if(this == other) return true;
		// If comparing against other Rational
		if(this.getClass()==other.getClass()) {
			Rational otherRational = (Rational) other;
			return (this.num.equals(otherRational.num) && this.den.equals(otherRational.den));
		}
		// Otherwise false
		else return false;
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
	public String toString(){
		// If the denominator is 1, display only the numerator. Otherwise display in the form of a/b
		return this.den.equals(BigInteger.ONE) ? this.num + "": this.num + "/" + this.den;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Normalizes the numerator and denominator to be in proper format
	 * 
	 */
	private void normalize(){
		BigInteger denom = this.num.gcd(this.den);
		this.num = this.num.divide(denom);
		this.den = this.den.divide(denom);
		if(this.den.compareTo(BigInteger.ZERO)<0){
			this.den=this.den.negate(); 
			this.num=this.num.negate();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Rational val) {
		int r = this.num.divide(this.den).compareTo(val.num.divide(val.den));
		return r;
	}

	
}
