package com.concurrentperformance.pebble.ui.application.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class CalculationUiFrame extends JFrame {
	
	private static final long serialVersionUID = -6902374962835593119L;

	private JComponent mainTab;
	private JComponent graphWindow;
	private JComponent logonWindow;

	/**
	 * @param defaultConfiguration
	 */
	public CalculationUiFrame() {
	    try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// SJL Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {

        setTitle("Calculation Manager");
        setBackground(Color.lightGray);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(mainTab, BorderLayout.LINE_START);
        getContentPane().add(graphWindow, BorderLayout.CENTER);

        setGlassPane(logonWindow);
        logonWindow.setVisible(true);
        
        //TODO setJMenuBar 
        pack();
        setLocation(10,10);
        setSize(500, 600);

        
        setVisible(true);
    }
	
	public void setWindowListener(List<WindowListener> listeners) {
		for (WindowListener windowListener : listeners) {
			addWindowListener(windowListener);
		}
    }

	public final void setMainTab(JComponent mainTab) {
		this.mainTab = mainTab;
	}

	public final void setGraphWindow(JComponent graphWindow) {
		this.graphWindow = graphWindow;
	}

	public final void setLogonWindow(JComponent logonWindow) {
		this.logonWindow = logonWindow;
	}
}
