package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;

/**
 * Holder for the TopologyMonitorState that provides compare and set capability, 
 * along with a timestamp for the last state change. 
 * 
 * @author Stephen
 */
public class AtommicTopologyStateHolder {
	private final Log log = LogFactory.getLog(this.getClass());

	private final String nameForLogging;

	private final AtomicReference<TopologyMonitorState> state = new AtomicReference<TopologyMonitorState>(TopologyMonitorState.AWAITING_CONSTRUCTION);
	private volatile long lastStatusChangeMs = System.currentTimeMillis();
	
	AtommicTopologyStateHolder(String nameForLogging) {
		this.nameForLogging = nameForLogging;
		log.info("[" +  nameForLogging + "] initial state set to [" + this + "].");
	}
	
	public TopologyMonitorState get() {
		return state.get();
	}

	public long getElapsedTimeSinceLastStatusChangeMs() {
		return System.currentTimeMillis() - lastStatusChangeMs;
	}
	
	public boolean compareAndSet(TopologyMonitorState expect, TopologyMonitorState update) {
		boolean casSuccess = state.compareAndSet(expect, update);
		if(casSuccess) {
			log.info("[" +  nameForLogging + "] state change to [" + update + "] (from [" + expect + "]) CAS. [" + getElapsedTimeSinceLastStatusChangeMs() + "ms] since last status change.");
			lastStatusChangeMs = System.currentTimeMillis();
		}
		else {
			log.info("[" +  nameForLogging + "] failed to change state to [" + update + "] (from [" + expect + "]) CAS");
		}
		return casSuccess;
	}

	public void set(TopologyMonitorState newValue) {
		TopologyMonitorState originalValue = state.get();
		state.set(newValue);
		lastStatusChangeMs = System.currentTimeMillis();
		log.info("[" +  nameForLogging + "] state change to [" + newValue + "] (from [" + originalValue + "]) . [" + getElapsedTimeSinceLastStatusChangeMs() + "ms] since last status change.");
	}
	
	@Override
	public String toString() {
		return state.toString();
	}
}
