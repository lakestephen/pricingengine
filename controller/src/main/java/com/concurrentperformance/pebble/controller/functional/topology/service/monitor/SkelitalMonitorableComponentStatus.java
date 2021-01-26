package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMaintainenceService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;

public abstract class SkelitalMonitorableComponentStatus implements MonitorableComponentStatus  {

	private final Log log = LogFactory.getLog(this.getClass());

	protected final TopologyMaintainenceService topologyMaintainenceService;
	private final AsynchServiceListenerSupport<TopologyMonitorServiceListener> listenerSupport;

	protected final TopologyDescriptor topologyDescriptor;
	protected final AtommicTopologyStateHolder state;
	protected final String nameForLogging; 
	protected RpcConnection connection;



	protected SkelitalMonitorableComponentStatus(TopologyDescriptor topologyDescriptor, 
			AsynchServiceListenerSupport<TopologyMonitorServiceListener> listenerSupport, 
			TopologyMaintainenceService topologyMaintainenceService) {
		this.listenerSupport = listenerSupport;
		this.topologyDescriptor = topologyDescriptor;
		this.nameForLogging = topologyDescriptor.getType() + ":" + topologyDescriptor.getName();
		this.state = new AtommicTopologyStateHolder(nameForLogging);
		this.topologyMaintainenceService = topologyMaintainenceService;
	}

	/**
	 * Call at regular intervals to perform monitoring. A good  
	 * starting point would be calling this method a couple of 
	 * times a second. 
	 *
	 */  
	@Override
	public void monitor() { 
		boolean compareAndSetSuccessful = false;
		
		/* Will only exit when a successful (or no) compareAndSet
		 * operation has taken place, otherwise keep retrying. 
		 * The individual methods should only return false if 
		 * a compare and set operation has failed. 
		 */
		while (!compareAndSetSuccessful) {
		
			switch (state.get()) { 
			case AWAITING_CONSTRUCTION:
				compareAndSetSuccessful = monitorStateAWAITING_CONSTRUCTION();
				break;
			case AWAITING_REGISTRATION:
				compareAndSetSuccessful = monitorStateAWAITING_REGISTRATION();
				break;
			case RUNNING:
				compareAndSetSuccessful = monitorStateRUNNING();
				break;
			case MISSED_HEARTBEAT:
				compareAndSetSuccessful = monitorStateMISSED_HEARTBEAT();
				break;
			case DECLARED_DEAD:
				compareAndSetSuccessful = monitorStateDECLARED_DEAD();
				break;
			case AWAITING_DELETION:
				compareAndSetSuccessful = monitorStateAWAITING_DELETION();
				break;
			case DELETED:
				compareAndSetSuccessful = monitorStateDELETED();
				break;
			default:
				log.error("State [" + state + "] unhandled for [" + topologyDescriptor + "]");
				compareAndSetSuccessful = true;
			}
		}
	}
	
	protected abstract boolean monitorStateDELETED();
	protected abstract boolean monitorStateAWAITING_DELETION();
	protected abstract boolean monitorStateDECLARED_DEAD();
	protected abstract boolean monitorStateMISSED_HEARTBEAT();
	protected abstract boolean monitorStateRUNNING();
	protected abstract boolean monitorStateAWAITING_REGISTRATION();
	protected abstract boolean monitorStateAWAITING_CONSTRUCTION();

	@Override
	public TopologyMonitorState getTopologyState() {
		return state.get();
	}
	
	@Override
	public Long getId() {
		return topologyDescriptor.getId();
	}
	
	protected void notifyStateChange() { //TODO should we call this from the State container - would make this class cleaner 
		TopologyMonitorState stateNow = state.get();
		for (TopologyMonitorServiceListener listener :listenerSupport.getListeners()) {
			listener.topologyMonitorService_updateTopologyStatus(topologyDescriptor, stateNow);
		}		
	}

	protected void notifyConnectionRegistration() {
		for (TopologyMonitorServiceListener listener :listenerSupport.getListeners()) {
			listener.topologyMonitorService_connectionRegistration(topologyDescriptor, connection);
		}		
	}

	protected void notifyConnectionDeregistration() {
		for (TopologyMonitorServiceListener listener :listenerSupport.getListeners()) {
			listener.topologyMonitorService_connectionDeregistration(topologyDescriptor);
		}		
	}

	@Override
	public boolean shouldRemove() {
		return state.get().equals(TopologyMonitorState.DELETED);
	}
	
	@Override
	public String toString() {
		return "SpawnedComponentStatus [nameForLogging=" + nameForLogging + "], [" + state +"]";
	}
}