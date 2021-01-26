package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMaintainenceService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyMaintainanceException;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;

public class MachineStatus extends SkelitalMonitorableComponentStatus implements MonitorableComponentStatus {

	private final Log log = LogFactory.getLog(this.getClass());

	private static final long MACHINE_CHECK_INTERVAL_MS = 
			TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);

	private long lastConnectAttempt = 0;
	
	public MachineStatus(MachineDescriptor machineDescriptor, AsynchServiceListenerSupport<TopologyMonitorServiceListener> listenerSupport, TopologyMaintainenceService topologyMaintainenceService) {
		super(machineDescriptor, 
				listenerSupport, 
				topologyMaintainenceService);
	}

	@Override
	protected boolean monitorStateAWAITING_CONSTRUCTION() {
		boolean compareAndSetSuccessful = false;

		// as checking is an expensive task don't try too often - will gum up the monitor thread.. 
		long msSinceLastCheck = System.currentTimeMillis() - lastConnectAttempt;
		if (msSinceLastCheck < MACHINE_CHECK_INTERVAL_MS) {
			compareAndSetSuccessful = true;
		}
		else {
		
			// establish a connection to the spawner on that machine. 
			try {
				lastConnectAttempt = System.currentTimeMillis();
				topologyMaintainenceService.createMachine((MachineDescriptor)topologyDescriptor);
				if (state.compareAndSet(TopologyMonitorState.AWAITING_CONSTRUCTION, TopologyMonitorState.RUNNING)) {
					compareAndSetSuccessful = true;
					notifyStateChange();
				}
			} catch (TopologyMaintainanceException e) {
				log.error("Cant establish conneciton to machine. Is the spawner running? [" + topologyDescriptor + "]", e);
					
			}
		}
		return compareAndSetSuccessful;
	}

	@Override
	protected boolean monitorStateAWAITING_REGISTRATION() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean monitorStateRUNNING() {
		return true;
	}

	@Override
	protected boolean monitorStateMISSED_HEARTBEAT() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void componentDeletedNotification() {
		state.set(TopologyMonitorState.AWAITING_DELETION);
		notifyStateChange();
	}

	@Override
	protected boolean monitorStateAWAITING_DELETION() {
		boolean compareAndSetSuccessful = true;

		//TODO shut connection to spawner down.
		compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.AWAITING_DELETION, TopologyMonitorState.DELETED);
		if (compareAndSetSuccessful) {
			notifyStateChange();
		}

		return compareAndSetSuccessful;
	}

	@Override
	protected boolean monitorStateDELETED() {
		return true;
	}

	@Override
	protected boolean monitorStateDECLARED_DEAD() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void componentRegistration(RpcConnection connection, long instanceId)
			throws SpawnedComponentControlException { 
		throw new UnsupportedOperationException();
	}

	@Override
	public void triggerHeartbeat(long instanceId)
			throws SpawnedComponentControlException {
		throw new UnsupportedOperationException();
	}
}
