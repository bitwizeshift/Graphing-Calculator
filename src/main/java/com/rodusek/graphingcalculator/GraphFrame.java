package com.rodusek.graphingcalculator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Creates the Frame for the GraphPanel.
 * <p>This class is mainly used to initializes the JMenu</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-27
 */
@SuppressWarnings("serial")
public class GraphFrame extends JFrame{
	
	private final CalculatorModel   model;
	
	private final static JMenuBar 	menuBar 		= new JMenuBar();
	private final JMenu			    viewMenu		= new JMenu("View");
    private final JCheckBoxMenuItem	viewMenuFunc1	= new JCheckBoxMenuItem("F( x )", true);
    private final JCheckBoxMenuItem	viewMenuFunc2	= new JCheckBoxMenuItem("F'( x )", true);
    private final JCheckBoxMenuItem	viewMenuFunc3	= new JCheckBoxMenuItem("F''( x )", true);
    private final JMenu				helpMenu		= new JMenu("Help");
    private final JMenuItem			helpMenuHelp	= new JMenuItem("Command Help");
	
    private final String	helpTitle = "Command Help";
    private final String	helpString = "This graphing calculator contains full support for both real AND complex rational numbers.\n" +
			 "In order to graph complex numbers, simply add values that have 'i' to the equation. \n\n" +
			 "Example: (x+3*i)^3\n\n" +
			 "Both imaginary and real numbers will appear on the same cartesian plane, but represent their absolute values.\n" +
			 "The darker lines indicate the imaginary portion (only if present), and the lighter lines are the real portion.\n\n"+
			 "Valid Function Operators:\n" +
			 "^ : exponentiation (exponent must be constant integer value)\n" +
			 "+ : addition\n" +
			 "- : subtraction\n" +
			 "* : multiplication\n" +
			 "/ : division (divisor must be constant real or complex value)\n\n" +
			 "To view this help menu again at any point, go to 'Help->Command Help' on the Graph Window";
    // ---------------------------------------------------------------------------------
	
    /**
     * Listener for enabling and disabling graphs
     */
 	private class ViewListener implements ItemListener{
 		final int key;
 		public ViewListener(int key) {
 			this.key = key;
 		}
 		
 		/*
 		 * (non-Javadoc)
 		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
 		 */
 		@Override
 		public void itemStateChanged(ItemEvent evt) {
 			model.setVisibility(key, evt.getStateChange()==ItemEvent.SELECTED);
 			model.calculateYBounds();
 		}
 	}
 	
 	// ---------------------------------------------------------------------------------
    
 	/**
 	 * Constructor for the graph frame.
 	 * @param title the title of the window
 	 * @param model the persistent model for this swing application
 	 */
	public GraphFrame(String title, CalculatorModel model) {
		super(title);
		JOptionPane.showMessageDialog(null,helpString,helpTitle,JOptionPane.PLAIN_MESSAGE);
		this.model = model;
		
		viewMenu.setMnemonic(KeyEvent.VK_V);
		viewMenuFunc1.addItemListener(new ViewListener(CalculatorModel.KEY_POLYNOMIAL));
		viewMenuFunc2.addItemListener(new ViewListener(CalculatorModel.KEY_FIRST_DERIVATIVE));
		viewMenuFunc3.addItemListener(new ViewListener(CalculatorModel.KEY_SECOND_DERIVATIVE));
		
		// Create the help dialog
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenuHelp.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											JOptionPane.showMessageDialog(null,helpString,helpTitle,JOptionPane.PLAIN_MESSAGE);
										}});
		viewMenu.add(viewMenuFunc1);
		viewMenu.add(viewMenuFunc2);
		viewMenu.add(viewMenuFunc3);
		helpMenu.add(helpMenuHelp);
		
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		
		
		this.setJMenuBar(menuBar);
		SwingUtilities.updateComponentTreeUI(menuBar);
	}
}
