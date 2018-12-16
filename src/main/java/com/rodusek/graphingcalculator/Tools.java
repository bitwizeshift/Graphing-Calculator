package com.rodusek.graphingcalculator;

import java.util.ArrayList;

/**
 * The <code>Tools</code> class contains static functions that are helpers for other classes.
 * Most of the supplied methods are mathematical, supplying features like gcd and lcm calculations, 
 * or factorization of integers.
 * <p>Given that it is a static class, there is no constructor required</p>
 * <p>As of version 1.3, there is now a max() and min() function that supports a series of array
 * inputs</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.3, 11/25/13
 * @since 1.0
 */
public class Tools {
	
	/**
	 * Calculates and returns the greatest common divisor
	 * of two input values.
	 * 
	 * @param a Numerator
	 * @param b Denominator
	 * @return Greatest common divisor
	 * @since 1.0
	 */
	public static final int gcd(int a, int b){
		if(b==0) return a;
		else return gcd(b, a % b);
	}
	
	/**
	 * Calculates and returns the greatest common divisor
	 * of an array of values
	 * 
	 * @param x array of integers to calculate GCD of
	 * @return Greatest common divisor
	 * @see #gcd(int, int)
	 * @since 1.2
	 */
	public static final int gcd(int[] x) {
		if(x.length==1) { return 1; } // If there is only 1 number, the gcd is 1
		int temp = gcd(x[x.length-1],x[x.length-2]);
		for(int i=x.length-3; i>=0; i--) {
			temp = gcd(temp,x[i]);
		}
		return temp;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Calculates and returns the Least common multiple
	 * of two input values.
	 * 
	 * @param a value a
	 * @param b value b
	 * @return Least common multiple
	 * @since 1.2
	 */
	public static final int lcm(int a, int b) {
		return Math.abs(a * b) / gcd(a, b);
	}
	/**
	 * Calculates and returns the Least common multiple
	 * of an array of values
	 * 
	 * @param x array of integers to calculate lcm of
	 * @return Least common multiple
	 * @see #lcm(int, int)
	 * @since 1.2
	 */
	public static final int lcm(int[] x) {
		if(x.length==1) {return 1;} // If there is only 1 number, then the lcm is 1
		int temp = lcm(x[x.length-1], x[x.length-2]);
		for(int i=x.length-3; i>=0; i--) {
			temp = lcm(temp, x[i]);
		}
		return temp;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Calculates and returns an array of integer factors of x
	 * 
	 * @param x	the value to find the factor of
	 * @param includeNegatives boolean for whether or not negatives should be included as factors
	 * @return an array of integer factors of x
	 * @since 1.2
	 */
	public static final Integer[] factor(int x, boolean includeNegatives) {
		ArrayList<Integer> 	numArray = new ArrayList<Integer>();
		Integer[] 			factors;
		int					i;
		
		x = Math.abs(x);    		// Make x positive
		i = (int) Math.ceil(x/2); 	// start the search at half the value
		numArray.add(x);			// Add x as a factor
		if(includeNegatives && x!=0) {
			numArray.add(-x);		// Add -x as factor if searching for negatives 
		}
		
		do {
			if(i!=0 && x%i==0) {			// If i divides x, then i is a factor
				numArray.add(i);
				if(includeNegatives) {
					numArray.add(-i); // If looking for negatives, -i is also a factor
				}
				
			}
			i--;
		}while(i >= 1);
		factors = numArray.toArray(new Integer[numArray.size()]);
		return factors;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Calculates the maximum value of a series of array inputs
	 * @param args a series of arrays of double values to 
	 *  	 	   find the max value of
	 * @return the maximum value
	 */
	public static final double max(double []...args){
		double max = args[0][0];
		int j = 0;
		while(j < args.length) {
			for(double i : args[j]) {
				max = Math.max(max, i);
			}
			j++;
		}
		return max;
	}
	
	/**
	 * Calculates the maximum value of a series of array inputs
	 * @param args a series of arrays of double values to 
	 *  	 	   find the min value of
	 * @return the minimum value
	 */
	public static final double min(double []...args){
		double min = args[0][0];
		int j = 0;
		while(j < args.length) {
			for(double i : args[j]) {
				min = Math.min(min, i);
			}
			j++;
		}
		return min;
	}
}
