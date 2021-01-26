package com.concurrentperformance.pebble.util.shutdown.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;
import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;
import com.concurrentperformance.pebble.util.shutdown.TerminatorService;
import com.concurrentperformance.pebble.util.shutdown.TerminatorServiceListener;


public class DefaultTerminatorService extends AsynchServiceListenerSupport<TerminatorServiceListener> 
		implements TerminatorService, 
		ServiceListenerSupport<TerminatorServiceListener> {

	private final Log log = LogFactory.getLog(this.getClass());

	private volatile boolean terminating = false;
	
	@Override
	public void terminateNow(final String reason) {
		if (terminating) {
			log.info("Already terminating [" + reason + "]");
			return;
		}

		log.warn("Shutdown request recieved [" + reason + "]");
		terminating = true; 

		fireTerminateNotification();
		
		submitTask( new Runnable() {
			
			@Override
			public void run() {
				log.warn("Shutdown request recieved [" + reason + "] - Exit 0");
		    	System.exit(0);  
			}
		});
	}

	private void fireTerminateNotification() {
		for (final TerminatorServiceListener listener : getListeners()) {
			submitTask( new Runnable() {
				
				@Override
				public void run() {
					listener.terminateService_terminate();					
				}
			});			
		}
	}

	@Override
	public boolean isTerminating() {
		return terminating;
	}
}