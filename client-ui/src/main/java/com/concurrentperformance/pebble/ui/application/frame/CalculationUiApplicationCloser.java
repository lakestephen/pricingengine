package com.concurrentperformance.pebble.ui.application.frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class CalculationUiApplicationCloser extends WindowAdapter {

	private final Log log = LogFactory.getLog(this.getClass());

	private Connection connectionToController;

    @Override
    public void windowClosing(WindowEvent event) {
    	stopConnectionToController();
    	
        System.exit(0);
    }

	private void stopConnectionToController() {
		connectionToController.stop();
    	// wait for connection to stop
    	while (!connectionToController.isStopped()) {
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.warn("InterruptedException", e);
			}
    	}
	}

	public final void setConnectionToController(Connection connectionToController) {
		this.connectionToController = connectionToController;
	}
}
