package com.rodusek.graphingcalculator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class is a persistent model used for interprocess communication between multiple JFrames. 
 * It contains an instances of a <code>PropertyChangeSupport</code> so that changes can be notified
 * between frames. 
 * <p>This is where the bulk of the calculations are done for the y-axis values, as well as determining
 * the maximum values.</p>
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-11-27
 */
public class CalculatorModel {
	
	public static final String POLY_CHANGE  = "Polynomial Changed";
	public static final String START_CHANGE = "Plot Start Changed";
	public static final String END_CHANGE   = "Plot End Changed";
	public static final String VISIBILITY_CHANGE = "Visibility changed";
	
	// Static keys used for values
	public static final int KEY_POLYNOMIAL = 0;
	public static final int KEY_FIRST_DERIVATIVE = 1;
	public static final int KEY_SECOND_DERIVATIVE = 2;
	public static final int KEY_COMPLEX_VISIBLE = 3;
	public static final int KEY_REAL = 0;
	public static final int KEY_IMAGINARY = 1;
	
	// Polynomial information
	private Poly[] 	 polynomial = new Poly[3];
	private Rational start      = new Rational("-4");
	private Rational end        = new Rational("4");
	private double   y1[][]		= null; // 2D arrays holding real/imaginary values 
	private double   y2[][]		= null; // index 0 is real
	private double   y3[][]		= null; // index 1 is imaginary
	
	// Boundaries
	private double   yMax		= 0;
	private double   yMin		= 0;
	private double   xMax		=  4.0;
	private double	 xMin		= -4.0;
	
	private int		 n			= 460;
	
	// Boolean for the 4 display possibilities
	private boolean display[]	= {true,true,true,true}; // f(x), f'(x), f''(x), imaginary
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Attaches listeners to the model.
	 * 
	 * @param listener
	 *            The listener to attach to the model.
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * Attaches listeners to the model for a particular property.
	 * 
	 * @param propertyName The name of the property to listen for.
	 * @param listener The listener to attach to the model.
	 */
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * this constructor initializes the default polynomial/graph information to represent the polynomial
	 * "x^4-4*x^3+8*x", with a range of between -2 and 4.
	 */
	public CalculatorModel() {
		// Initialize the model with the first polynomial
		this.polynomial[KEY_POLYNOMIAL] 	   = new Poly("(x+3*i)^3", "x");
		this.polynomial[KEY_FIRST_DERIVATIVE]  = polynomial[KEY_POLYNOMIAL].diff();
		this.polynomial[KEY_SECOND_DERIVATIVE] = polynomial[KEY_FIRST_DERIVATIVE].diff();
		
		this.y1 = this.getPolynomialCoordinates(this.polynomial[KEY_POLYNOMIAL]);
		this.y2 = this.getPolynomialCoordinates(this.polynomial[KEY_FIRST_DERIVATIVE]);
		this.y3 = this.getPolynomialCoordinates(this.polynomial[KEY_SECOND_DERIVATIVE]);
		
		this.xMax = end.eval().doubleValue();
		this.xMin = start.eval().doubleValue();
		this.calculateYBounds();
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Sets the polynomial to be evaluated
	 * @param polynomial the new polynomial to plot
	 */
	public void setPoly(final Poly polynomial) {
		this.polynomial[KEY_POLYNOMIAL] 	   = polynomial;
		this.polynomial[KEY_FIRST_DERIVATIVE]  = this.polynomial[KEY_POLYNOMIAL].diff();
		this.polynomial[KEY_SECOND_DERIVATIVE] = this.polynomial[KEY_FIRST_DERIVATIVE].diff();
		// Inform listeners the model is updated.
		this.pcs.firePropertyChange(POLY_CHANGE, null, polynomial);
	}
	
	/**
	 * Sets the starting evaluation point of the Polynomial
	 * 
	 * @param start the new starting value of the evaluation
	 */
	public void setStart(final Rational start) {
		this.start = start;
		this.xMin  = start.eval().doubleValue();
		// Inform listeners the model is updated.
		this.pcs.firePropertyChange(START_CHANGE, null, start);
	}
	
	/**
	 * Sets the ending evaluation point of the Polynomial
	 * 
	 * @param end the new ending value of the evaluation
	 */
	public void setEnd(final Rational end) {
		this.end  = end;
		this.xMax = end.eval().doubleValue();
		// Inform listeners the model is updated.
		this.pcs.firePropertyChange(END_CHANGE, null, end);
	}
	
	
	
	/**
	 * Sets the interval value 
	 * @param n the interval
	 */
	public void setInterval(int n) {
		this.n = n;
	}
	
	/**
	 * Sets the visibility of the desired function plot. It fires a property change 
	 * to allow the GraphModel to recognize the change.
	 * 
	 * @param key the integer key value between 0-3
	 * @param value the boolean value to set it to
	 */
	public void setVisibility(int key, boolean value) {
		this.display[key] = value;
		this.pcs.firePropertyChange(VISIBILITY_CHANGE, null, value);
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Calculates the new polynomial Y-Coordinates and assigns it to the 3 y arrays
	 */
	public void calculatePolynomialCoordinates() {
		this.y1 = this.getPolynomialCoordinates(polynomial[KEY_POLYNOMIAL]);
		this.y2 = this.getPolynomialCoordinates(polynomial[KEY_FIRST_DERIVATIVE]);
		this.y3 = this.getPolynomialCoordinates(polynomial[KEY_SECOND_DERIVATIVE]);
	}
	
	/**
	 * Calculates the Y-boundaries based on which functions are visible, and assigns it
	 * to the models yMax and yMin
	 */
	public void calculateYBounds() {
		double yMax = -Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;
		// If display imaginary
		if(display[KEY_COMPLEX_VISIBLE]) {
			if(display[KEY_POLYNOMIAL]) {
				yMax = Math.max(yMax, Tools.max(y1[KEY_REAL],y1[KEY_IMAGINARY]));
				yMin = Math.min(yMin, Tools.min(y1[KEY_REAL],y1[KEY_IMAGINARY]));
			}
			if(display[KEY_FIRST_DERIVATIVE]) {
				yMax = Math.max(yMax, Tools.max(y2[KEY_REAL],y2[KEY_IMAGINARY]));
				yMin = Math.min(yMin, Tools.min(y2[KEY_REAL],y2[KEY_IMAGINARY]));
			}
			if(display[KEY_SECOND_DERIVATIVE]) {
				yMax = Math.max(yMax, Tools.max(y3[KEY_REAL],y3[KEY_IMAGINARY]));
				yMin = Math.min(yMin, Tools.min(y3[KEY_REAL],y3[KEY_IMAGINARY]));
			}
		// Otherwise only calculate with reals
		}else {
			if(display[KEY_POLYNOMIAL]) {
				yMax = Math.max(yMax, Tools.max(y1[KEY_REAL]));
				yMin = Math.min(yMin, Tools.min(y1[KEY_REAL]));
			}
			if(display[KEY_FIRST_DERIVATIVE]) {
				yMax = Math.max(yMax, Tools.max(y2[KEY_REAL]));
				yMin = Math.min(yMin, Tools.min(y2[KEY_REAL]));
			}
			if(display[KEY_SECOND_DERIVATIVE]) {
				yMax = Math.max(yMax, Tools.max(y3[KEY_REAL]));
				yMin = Math.min(yMin, Tools.min(y3[KEY_REAL]));
			}
		}
		this.yMax = yMax;
		this.yMin = yMin;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Grabs and returns the polynomial that's plotted
	 * 
	 * @return the polynomial
	 */
	public Poly getPoly(int n) {
		return polynomial[n];
	}
	
	/**
	 * Grabs and returns the starting evaluation point
	 * 
	 * @return the starting evaluation point
	 */
	public Rational getStart() {
		return this.start;
	}
	
	/**
	 * Grabs and returns the ending evaluation point
	 * 
	 * @return the ending evaluation point
	 */
	public Rational getEnd() {
		return this.end;
	}
	
	/**
	 * Grabs and returns the maximum x value
	 * 
	 * @return the maximum x value
	 */
	public double getXMax() {return this.xMax;}
	
	/**
	 * Grabs and returns the minimum x value
	 * 
	 * @return the minimum x value
	 */
	public double getXMin() {return this.xMin;}
	
	/**
	 * Grabs and returns the maximum y value
	 * 
	 * @return the maximum y value
	 */
	public double getYMax() {return this.yMax;}
	
	/**
	 * Grabs and returns the minimum y value
	 * 
	 * @return the minimum y value
	 */
	public double getYMin() {return this.yMin;}
	
	/**
	 * Gets the visibility of the desired key
	 * 
	 * @param key the integer key of the visibility to be found
	 * @return the display property
	 */
	public boolean getVisibility(int key) {
		return this.display[key];
	}
	
	/**
	 * Returns the Y-Coordinates of the specified function key
	 * 
	 * @param key the function key to get the Y coordinates of
	 * @return 2-dimensional array of Y-coordinates
	 */
	public double[][] getYCoordinates(int key){
		switch(key) {
			case 0: return y1;
			case 1: return y2;
			case 2: return y3;
			default:return null;
		}
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Calculates the polynomial at <code>n</code> points between values <code>start</code> and
	 * <code>end</code>, storing both the real and complex portions into a 2 dimensional array.
	 * 
	 * @return a 2 dimensional array containing y-coordinates for real and imaginary values
	 */
	private double[][] getPolynomialCoordinates(Poly p) {
		double[][] array = new double[2][n+1];
		
		Rational h = end.sub(start).div(new Rational(n)); 
		Rational x = new Rational(start); // Copy s
		Complex  y = null;
		for(int i=0;i<n+1;i++) {
			y = p.evalAt(new Complex(x));
			array[0][i] = y.getReal().eval().doubleValue();
			array[1][i] = y.getImaginary().eval().doubleValue();
			x = x.add(h);
		}
		
		return array;
	}
	
	
}
