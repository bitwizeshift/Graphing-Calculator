package rodu4140;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * This class initializes the Swing application by creating the JFrame windows and 
 * setting their default size and names.
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 2013-11-27
 */
public class CalculatorMain {

	/*
	 * 1.0.0   : Full Release
	 * 1.0.1-5 : Performance Improvements 
	 * 1.1.0   : Created Help menu
	 * 1.1.2   : Help menu changed to dialog
	 */
    private static final String VERSION					= "1.1.2";
	private static final String EXPRESSION_WINDOW_TITLE = "Expression Input [" + VERSION + "]";
	private static final String GRAPH_WINDOW_TITLE      = "Graph Window [" + VERSION + "]";
	
	private static final Dimension GRAPH_MINIMUM_SIZE	= new Dimension(500,500);
	private static final Dimension EXPRESSION_MINIMUM_SIZE = new Dimension(500,90);
	
	
	
	public static final void main(String...args) {
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString()); // Helps resizing
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Makes the system use native graphics
		} catch (Exception e) {
			
		}
		final CalculatorModel model = new CalculatorModel();

		// Create expression window
		final JFrame expressionFrame = new JFrame(EXPRESSION_WINDOW_TITLE);
		expressionFrame.setContentPane(new ExpressionPanel(model));
		expressionFrame.setSize(EXPRESSION_MINIMUM_SIZE);
		expressionFrame.setResizable(false);
		expressionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		expressionFrame.setVisible(true);
		expressionFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("/frame_icon.png"));
		expressionFrame.setLocationRelativeTo( null );
		expressionFrame.setLocation(expressionFrame.getX(), 100);
		
		// Create graph window
		final GraphFrame graphingFrame = new GraphFrame(GRAPH_WINDOW_TITLE, model);
		graphingFrame.setContentPane(new GraphPanel(model));
		graphingFrame.setSize(GRAPH_MINIMUM_SIZE);
		graphingFrame.setMinimumSize(GRAPH_MINIMUM_SIZE);
		graphingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphingFrame.setLocation(expressionFrame.getX(), expressionFrame.getY() + expressionFrame.getHeight());
		graphingFrame.setVisible(true);
		graphingFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("/frame_icon.png"));

	}
}
