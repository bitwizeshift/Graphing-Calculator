package com.rodusek.graphingcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Constructs the JPanel for the Expression Frame. It initializes the JTextField with a preset function and range
 * 
 * @author Matthew Rodusek
 * @version 1.0, 2013-11-27
 */
@SuppressWarnings("serial")
public class ExpressionPanel extends JPanel{

	private final CalculatorModel model;
	
	private final JPanel 	 gridPanel 	   = new JPanel(new GridLayout(2,4));
	private final JTextField functionField = new JTextField("(x+3*i)^3");
	private final JTextField varField      = new JTextField("x"); 
	private final JTextField startField    = new JTextField("-4");
	private final JTextField endField      = new JTextField("4");
	private final JButton    plotButton    = new JButton("Plot");
	private final JLabel	 statusLabel   = new JLabel("Status: Successfully Plotted");
	private final JLabel[]   headingLabel  = {new JLabel("Function"), new JLabel("Variable"), 
											  new JLabel("Start"),    new JLabel("End")};
	// Colors
	private final Color		SUCCESS_GREEN = new Color(0x008800);
	private final Color		FAILURE_RED	  = new Color(0xFF0000);
	
	private String var = "x";
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Private inner class that removes all values that aren't numbers or operators
	 * supported by the <code>Rational</code> class
	 *
	 */
	private class RangeFilter extends DocumentFilter{
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, text.replaceAll("[^0-9\\-\\/]", ""), attrs);
		}		
	}
	
	/**
	 * Private inner class that removes all inputs that aren't letters
	 *
	 */
	private class VarFilter extends DocumentFilter{
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if(text==null)
				return;
			//if(fb.getDocument().getLength() + text.length() <= 1)
				super.replace(fb, offset, length, text.replaceAll("[^a-hj-zA-Z]", ""), attrs);
		}
		
	}
	
	/**
	 * Private inner class that removes all inputs that aren't supported by the <code>Poly</code> object constructor.
	 *
	 */
	private class FunctionFilter extends DocumentFilter{
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, text.replaceAll("[^i" + var + "0-9\\+\\-\\/\\*\\^\\s\\(\\)]", ""), attrs);
		}
		
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Private inner class that handles calls from the Plot button to create the graph of
	 * the given polynomial. It instantiates the new Polynomial and fires a property change if
	 * successful, or displays a failure message if it fails.
	 *
	 */
	private class ButtonListener implements ActionListener{

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent evt) {
			statusLabel.setForeground(SUCCESS_GREEN);
			statusLabel.setText("Status: Successfully Plotted");
			try {
				Poly 	 p     = new Poly(functionField.getText(),varField.getText());
				Rational start = new Rational(startField.getText());
				Rational end   = new Rational(endField.getText());
				
				if(start.sub(end).isPositive()||start.sub(end).equals(Rational.ZERO)) throw new IllegalArgumentException("Incorrect boundaries");
				
				model.setPoly(p);
				model.setStart(start);
				model.setEnd(end);
				model.calculatePolynomialCoordinates();
				
				if(functionField.getText().contains("i")) 
					model.setVisibility(3, true);
				else 
					model.setVisibility(3, false);
				
				model.calculateYBounds();
				
			}catch(Exception e) {
				statusLabel.setForeground(FAILURE_RED);
				statusLabel.setText("Status: " + e.getMessage());
			}
		}
		
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Focus listener for the textfields. It selects everything when clicked,
	 * and changes the variables in the function field if the user changes the VarField.
	 */
	private class TextFieldFocusListener implements FocusListener{
		
		private JTextField instance;
		
		// ---------------------------------------------------------------------------------

		/**
		 * Constructor for the focus listener. It takes a pointer to the intance of 
		 * the calling Java Swing element for comparison
		 * @param instance the instance of the swing element
		 */
		public TextFieldFocusListener(JTextField instance) {
			this.instance = instance;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		@Override
		public void focusGained(FocusEvent evt) {
			instance.selectAll();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		@Override
		public void focusLost(FocusEvent evt) {
			if(this.instance==ExpressionPanel.this.varField) {
				String oldVar = var;
				String newVar = varField.getText();
				if(!newVar.equals("")) {
					// If the string is more than 1 character, limit it to the first one
					if(newVar.length()>1){
						varField.setText(newVar.substring(0, 1));
						newVar = varField.getText();
					}
					String functionText = functionField.getText();
				
					var = varField.getText();
					functionField.setText(functionText.replace(oldVar, newVar));
				}else {
					varField.setText(oldVar);
				}
			}

		}
		
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Constructor for the expression panel. It takes an instance of
	 * <code>CalculatorModel</code> for interprocess communication.
	 * @param model an instance of the CalculatorModel
	 */
	public ExpressionPanel(final CalculatorModel model) {
		super(new BorderLayout());
		
		this.model = model;
		
		this.registerListeners();
		this.initializeLayout();
	}
	
	// ---------------------------------------------------------------------------------

	/**
	 * Registers all the listeners and document listeners for the Expression Panel
	 */
	private void registerListeners() {
		
		// Create filters
		DocumentFilter rangeFilter = new RangeFilter();
		DocumentFilter varFilter   = new VarFilter();
		DocumentFilter funFilter   = new FunctionFilter();
		
		// Assign filters
		((AbstractDocument) this.startField   .getDocument()).setDocumentFilter(rangeFilter);
		((AbstractDocument) this.endField     .getDocument()).setDocumentFilter(rangeFilter);
		((AbstractDocument) this.varField     .getDocument()).setDocumentFilter(varFilter);
		((AbstractDocument) this.functionField.getDocument()).setDocumentFilter(funFilter);
		
		// Assign listeners
		this.startField		.addFocusListener(new TextFieldFocusListener(this.startField));
		this.endField		.addFocusListener(new TextFieldFocusListener(this.endField));
		this.varField		.addFocusListener(new TextFieldFocusListener(this.varField));
		this.functionField	.addFocusListener(new TextFieldFocusListener(this.functionField));
		this.plotButton		.addActionListener(new ButtonListener());
		
		this.statusLabel.setForeground(SUCCESS_GREEN);
	}
	
	/**
	 * Initializes the layout for the Expression Panel
	 */
	private void initializeLayout() {

		for(int i=0;i<4;i++) {
			this.gridPanel.add(headingLabel[i]);
		}
		this.gridPanel.add(functionField);
		this.gridPanel.add(varField);
		this.gridPanel.add(startField);
		this.gridPanel.add(endField);
		
		this.add(this.statusLabel, BorderLayout.NORTH);
		this.add(this.gridPanel,   BorderLayout.CENTER);
		this.add(this.plotButton,  BorderLayout.EAST);
	}
}
