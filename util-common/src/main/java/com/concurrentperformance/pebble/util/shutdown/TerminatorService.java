package com.concurrentperformance.pebble.util.shutdown;

import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;

public interface TerminatorService extends ServiceListenerSupport<TerminatorServiceListener> {

	void terminateNow(String reason);

	boolean isTerminating();
	
}
