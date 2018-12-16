package com.rodusek.graphingcalculator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JComponent;

/**
 * Graphs values on a Cartesian plane based on input from the ExpressionPanel being passed
 * through a persistent CalculatorModel.
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-11-27
 */
@SuppressWarnings("serial")
public class GraphPanel extends JComponent{
	
	private final CalculatorModel model;
	
	private final int X_OFFSET = 20;
	private final int Y_OFFSET = 20;
	private final int TICK_WIDTH = 5;
	private final int TICK_DISTANCE = 75;
		
	private int width  = this.getWidth()-2*X_OFFSET;
	private int height = this.getHeight()-2*Y_OFFSET;
	
	private double yScale = 1;
	private double xScale = 1;
	
	private double yMin = 0;
	private double yMax = 0;
	private double xMin = 0;
	private double xMax = 0;
	
	// Colors
	private static final Color REAL_FUNC_1 = new Color(0xFF0000);
	private static final Color REAL_FUNC_2 = new Color(0x00FF00);
	private static final Color REAL_FUNC_3 = new Color(0x0000FF);
	private static final Color IMAGINARY_FUNC_1 = new Color(0x880000);
	private static final Color IMAGINARY_FUNC_2 = new Color(0x008800);
	private static final Color IMAGINARY_FUNC_3 = new Color(0x000088);
	
	
	// ---------------------------------------------------------------------------------
	
	/**
     * Inner class that displays the current state of the model.
     */
    private class GraphListener implements PropertyChangeListener {
    	/*
    	 * (non-Javadoc)
    	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
    	 */
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
		    GraphPanel.this.repaint();
		}
    }
	
	// ---------------------------------------------------------------------------------
    
	/**
	 * Initializes the GraphPanel with a persisitent CalculatorModel, and registers the listeners
	 * @param model
	 */
	public GraphPanel(final CalculatorModel model) {
		this.model = model;
		this.registerListeners();
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Overrides the base paintComponent to create the graph on the Cartesian plane
	 */
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponents(g);
		
		final Graphics2D g2d = (Graphics2D) g;
		
		this.width  = this.getWidth()-2*X_OFFSET;
		this.height = this.getHeight()-2*Y_OFFSET;
		this.model.setInterval(this.width);
		this.model.calculatePolynomialCoordinates();
		
		final Poly[] polynomial = new Poly[3];
		polynomial[0] = model.getPoly(CalculatorModel.KEY_POLYNOMIAL);
		polynomial[1] = model.getPoly(CalculatorModel.KEY_FIRST_DERIVATIVE);
		polynomial[2] = model.getPoly(CalculatorModel.KEY_SECOND_DERIVATIVE);
		
		final double[][] y1 = model.getYCoordinates(CalculatorModel.KEY_POLYNOMIAL);
		final double[][] y2 = model.getYCoordinates(CalculatorModel.KEY_FIRST_DERIVATIVE);
		final double[][] y3 = model.getYCoordinates(CalculatorModel.KEY_SECOND_DERIVATIVE);
		
		// Real coordinates
		final double[] ry1 = y1[CalculatorModel.KEY_REAL];
		final double[] ry2 = y2[CalculatorModel.KEY_REAL];
		final double[] ry3 = y3[CalculatorModel.KEY_REAL];
		
		// Imaginary coordinates
		final double[] iy1 = y1[CalculatorModel.KEY_IMAGINARY];
		final double[] iy2 = y2[CalculatorModel.KEY_IMAGINARY];
		final double[] iy3 = y3[CalculatorModel.KEY_IMAGINARY];

		this.yMin = model.getYMin();
		this.yMax = model.getYMax();
		
		this.xMin = model.getXMin();
		this.xMax = model.getXMax();
		
		this.yScale = (this.height)/(double)(yMax - yMin);
		this.xScale = (this.width)/(double)(xMax - xMin);
		
		// ---------------------------------------------------------------------------------

		// Rendering Hints
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 	   RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Fill the frame white
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Draw axis and plot the polynomial and it's derivative
		g2d.setColor(Color.GRAY);
		this.drawAxis(g2d);
		
		// Graph function 1
		if(model.getVisibility(CalculatorModel.KEY_POLYNOMIAL)) {
			g2d.setColor(REAL_FUNC_1);
			this.plotPoly(g2d, ry1);
			g2d.drawString("f(x) = "+ polynomial[0].toString() , X_OFFSET, Y_OFFSET+height-30);
			if(model.getVisibility(CalculatorModel.KEY_COMPLEX_VISIBLE)){
				g2d.setColor(IMAGINARY_FUNC_1);
				this.plotPoly(g2d, iy1);
			}
		}
		
		// Graph function 2
		if(model.getVisibility(CalculatorModel.KEY_FIRST_DERIVATIVE)) {
			g2d.setColor(REAL_FUNC_2);
			this.plotPoly(g2d, ry2);
			g2d.drawString("f'(x) = "+ polynomial[1].toString() , X_OFFSET, Y_OFFSET+height-15);
			if(model.getVisibility(CalculatorModel.KEY_COMPLEX_VISIBLE)){
				g2d.setColor(IMAGINARY_FUNC_2);
				this.plotPoly(g2d, iy2);
			}
		}
		
		// Graph function 3
		if(model.getVisibility(CalculatorModel.KEY_SECOND_DERIVATIVE)) {
			g2d.setColor(REAL_FUNC_3);
			this.plotPoly(g2d, ry3);
			g2d.drawString("f''(x) = "+ polynomial[2].toString() , X_OFFSET, Y_OFFSET+height);
			if(model.getVisibility(CalculatorModel.KEY_COMPLEX_VISIBLE)){
				g2d.setColor(IMAGINARY_FUNC_3);
				this.plotPoly(g2d, iy3);
			}
		}
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Plots the polynomial in the given viewport
	 * @param g2d
	 * @param polynomial
	 */
	private void plotPoly(Graphics2D g2d, double[] yCoords) {
		int x1, x2, y1, y2;
		int n = this.width;
		x1 = 0;
		y1 = (int) ((yMax-yCoords[0])*yScale - this.height);
		for(int i=1; i<n; i++) {
			x2 = i;
			y2 = (int) ((yMax-yCoords[i])*yScale - this.height);
			g2d.drawLine(x1+X_OFFSET, height+y1+Y_OFFSET, 
						 x2+X_OFFSET, height+y2+Y_OFFSET);
			
			y1 = y2;
			x1 = x2;
		}
	}

	
	/**
	 * Draws the axis of the graph based on the selected viewport
	 * @param g2d
	 */
	private void drawAxis(Graphics2D g2d) {
		Rational n = new Rational(this.width, TICK_DISTANCE);
		Rational s = this.model.getStart();
		Rational f = this.model.getEnd();
		Rational h = f.sub(s).div(n); 
		Rational x = null;
		
		double	 y = 0;
		
		int 	 text_offset = 0;
		int		 text_width  = 0;
		int 	 tick_start  = 0;
		
		int 	 xLoc = 0;
		int 	 yLoc = 0;
		
		NumberFormat df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		
		// ---------------------------------------------------------------------------------
		
		// Calculate where the Y-Axis is
		if(xMin <= 0 && xMax >= 0) {
			yLoc = (int) (-xMin*xScale)+Y_OFFSET;
		}else if(xMax < 0) {
			yLoc = width + X_OFFSET + (X_OFFSET/2);
		}else if(xMin > 0) {
			yLoc = (X_OFFSET/2);
		}
		
		// Calculate where the X-Axis is
		if(yMin <= 0 && yMax >= 0) {
			xLoc = height-(int) (-yMin*yScale)+X_OFFSET;
		}else if(yMax < 0) {
			xLoc = (Y_OFFSET/2);
		}else if(yMin > 0) {
			xLoc = height+Y_OFFSET+(Y_OFFSET/2);
		}
		
		// If no graphs are showing, the max/min values will be extreme
		if(yMin == Double.MAX_VALUE && yMax == -Double.MAX_VALUE) {
			xLoc = height/2 + X_OFFSET;
			yLoc = width/2 + Y_OFFSET;
			yMin = 0; yMax = 0;
		}
		
		// Draw X-Axis
		g2d.drawLine(X_OFFSET, xLoc, width+X_OFFSET, xLoc);
		// Draw Y-Axis
		g2d.drawLine(yLoc,Y_OFFSET,yLoc,height+Y_OFFSET);		
		
		// Draw arrows for the Y-Axis
		g2d.fillPolygon(new int[] {yLoc+TICK_WIDTH, yLoc, 				 		yLoc-TICK_WIDTH},
						new int[] {Y_OFFSET, 		Y_OFFSET-TICK_WIDTH, 		Y_OFFSET},3);
		g2d.fillPolygon(new int[] {yLoc+TICK_WIDTH, yLoc, 				 		yLoc-TICK_WIDTH},
						new int[] {height+Y_OFFSET, height+Y_OFFSET+TICK_WIDTH, height+Y_OFFSET},3);
		
		// Draw arrows for the X-Axis
		g2d.fillPolygon(new int[] {X_OFFSET,		X_OFFSET-TICK_WIDTH,		X_OFFSET},
						new int[] {xLoc+TICK_WIDTH, xLoc,						xLoc-TICK_WIDTH}, 3);
		g2d.fillPolygon(new int[] {width+X_OFFSET,	width+X_OFFSET+TICK_WIDTH,  width+X_OFFSET},
						new int[] {xLoc+TICK_WIDTH, xLoc, 						xLoc-TICK_WIDTH}, 3);
		
		// Draw Tick marks on the Positive x-axis
		x = (xMin <= 0 && xMax >= 0 ? Rational.ZERO : s);
		tick_start = (yLoc < X_OFFSET ? X_OFFSET : yLoc);
		for(int i=tick_start + TICK_DISTANCE; i<width+X_OFFSET ;i+=TICK_DISTANCE) {
			x = x.add(h);
			String out = df.format(x.eval());
			text_offset = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getWidth()/2);
			g2d.drawLine(i, xLoc-TICK_WIDTH/2, i, xLoc+TICK_WIDTH/2);
			if(yMin > 0)
				g2d.drawString(out, i-text_offset, xLoc-16);
			else
				g2d.drawString(out, i-text_offset, xLoc+16);
		}
		
		// Draw tick marks on the negative x-axis
		x = (xMin <= 0 && xMax >= 0 ? Rational.ZERO : f);
		tick_start = (yLoc > X_OFFSET + width? X_OFFSET + width : yLoc);
		for(int i=tick_start-TICK_DISTANCE; i>X_OFFSET ;i-=TICK_DISTANCE) {
			x = x.sub(h);
			String out = df.format(x.eval());
			text_offset = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getWidth()/2);
			g2d.drawLine(i, xLoc-TICK_WIDTH/2, i, xLoc+TICK_WIDTH/2);
			if(yMin > 0) 
				g2d.drawString(out, i-text_offset, xLoc-16);
			else
				g2d.drawString(out, i-text_offset, xLoc+16);
		}
		
		// Draw tick marks on the positive y-axis
		y = (yMin <= 0 && yMax >= 0 ? 0 : yMin);
		tick_start = (xLoc < Y_OFFSET ? Y_OFFSET : xLoc);
		for(int i=tick_start-TICK_DISTANCE; i>Y_OFFSET ;i-=TICK_DISTANCE) {
			y += TICK_DISTANCE * (yMax - yMin)/(double) (height); 
			String out = df.format(y);
			text_offset = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getHeight()/2);
			text_width  = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getWidth());
			g2d.drawLine(yLoc-TICK_WIDTH/2, i, yLoc+TICK_WIDTH/2, i);
			if(yLoc + text_offset > width)
				g2d.drawString(out, yLoc-text_width-8, i+text_offset);
			else
				g2d.drawString(out, yLoc+8, i+text_offset);
		}
		
		// Draw tick marks on the negative y-axis
		y = (yMin <= 0 && yMax >= 0 ? 0 : yMax);
		tick_start = (xLoc > Y_OFFSET + height ? height + Y_OFFSET : xLoc);
		for(int i=tick_start+TICK_DISTANCE; i<height+Y_OFFSET ;i+=TICK_DISTANCE) {
			y -= TICK_DISTANCE * (yMax - yMin)/(double) height; 
			String out = df.format(y);
			text_offset = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getHeight()/2);
			text_width  = (int) (g2d.getFontMetrics().getStringBounds(out, g2d).getWidth());
			g2d.drawLine(yLoc-TICK_WIDTH/2, i, yLoc+TICK_WIDTH/2, i);
			if(yLoc + text_offset > width)
				g2d.drawString(out, yLoc-text_width-8, i+text_offset);
			else
				g2d.drawString(out, yLoc+8, i+text_offset);
		}
		
	}
	
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Registers and assigns the property change listener.
	 */
	private void registerListeners() {
		// Add property listeners.
		this.model.addPropertyChangeListener(new GraphListener());
	}

}
