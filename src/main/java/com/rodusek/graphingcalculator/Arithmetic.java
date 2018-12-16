package com.rodusek.graphingcalculator;

/**
 * The Arithmetic interface contains all the basic mathematical operations required to be included in
 * arithmetic classes.
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-11-27
 */
public interface Arithmetic<E>{
	
	/**
	 * Adds two objects together and returns the sum
	 * @param other the other object to add to this
	 * @return the sum of the two objects
	 */
	public E add(final E other);
	
	/**
	 * Subtracts two objects and returns the difference
	 * @param other the other object to subtract from this
	 * @return the difference of the two objects
	 */
	public E sub(final E other);
	
	/**
	 * Multiplies two objects together and returns the product
	 * @param other the other object to multiply to this
	 * @return the product of the two objects
	 */
	public E mul(final E other);
	
	/**
	 * divides two objects and returns the quotient
	 * @param other the other object that this divides
	 * @return the quotient of the two objects
	 */
	public E div(final E other);
	
	/**
	 * calculates <i>this<sup>other</sup></i> and returns that value
	 * @param other the value to raise to the power of
	 * @return the calculated value
	 */
	public E exp(final E other);
	
	/**
	 * Returns an object whose value is <code>(this mod other)</code>
	 * 
	 * @param other the value to find the modulo of
	 * @return the calculated value
	 */
	public E mod(final E other);
	
}
