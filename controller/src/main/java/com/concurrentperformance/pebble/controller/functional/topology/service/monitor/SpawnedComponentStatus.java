package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.SpawanableComponentDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMaintainenceService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.SpawnerServiceUnavailable;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceS2CApi;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;

/**
 * Class that represents the status of a SpawnedComponent, and aligns with a 
 * SpawanableComponent. </br> 
 * </br>
 * Once constructed, call the monitor() method at regular intervals. 
 * The shorter the interval, the more responsive the monitoring. A good 
 * starting point would be calling a couple of times a second. Other 
 * threads can call the methods such as heartbeat() to provide asynchronous 
 * information for monitoring </br>
 * </br>
 * This class is thread safe, by virtue of all mutable fields being either 
 * volatile, or modified via an AtomicReference. Particular note should be 
 * taken of the comments in the monitor() method.   
 * 
 * @author Stephen Lake
 * 
 */  

public class SpawnedComponentStatus extends SkelitalMonitorableComponentStatus 
		implements MonitorableComponentStatus {  

	// TODO these need to be a global, and passed out to the spawned component, or
	// possibly on a per spawned component basis, from the SpawanableComponent.
	private static final long HEARTBEAT_INTERVAL_MS = 
			TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
	private static final long HEARTBEAT_INTERVAL_SECOND_CHANCE_MS = 
			TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
	private static final long HEARTBEAT_TOLERANCE_MS = 
			TimeUnit.MILLISECONDS.convert(200, TimeUnit.MILLISECONDS);
	private static final long STARTUP_TOLERANCE_MS = 
		TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);

	private final Log log = LogFactory.getLog(this.getClass());

	private volatile long lastHeartbeatTimestamp = 0;

	
	private volatile long instanceId = -1;
	
	SpawnedComponentStatus(SpawanableComponentDescriptor spawanableComponentDescriptor, AsynchServiceListenerSupport<TopologyMonitorServiceListener> listenerSupport, TopologyMaintainenceService topologyMaintainenceService) {
		super(spawanableComponentDescriptor, 
				listenerSupport, 
				topologyMaintainenceService);
	}

	@Override
	protected boolean monitorStateAWAITING_CONSTRUCTION() {
		boolean compareAndSetSuccessful = false;
		instanceId = SpawnedComponentInstanceIdFactory.getNextId();
		try {
			topologyMaintainenceService.createSpawanableComponent((SpawanableComponentDescriptor)topologyDescriptor, instanceId);
			
			if (state.compareAndSet(TopologyMonitorState.AWAITING_CONSTRUCTION, TopologyMonitorState.AWAITING_REGISTRATION)) {
				compareAndSetSuccessful = true;
				notifyStateChange();
			}
		} catch (SpawnerServiceUnavailable e) {
			log.info(e.getMessage());
			// as this is generally a normal condition on startup, allow a retry next monitor cycle.
			// TODO  put a time limit on it.
			compareAndSetSuccessful = true;
		}

		return compareAndSetSuccessful;
	}

	/**
	 * Call when a hub first registers its presence.
	 * @param serviceListener 
	 */
	public void componentRegistration(RpcConnection connection, long instanceId) throws SpawnedComponentControlException {
		if (this.instanceId != instanceId) {
			String msg = "Registration of [" + this.nameForLogging + "] failed. Wrong instance. Expected [" + this.instanceId + "] got [" + instanceId + "], please shut yourself down";
			log.error(msg);
			throw new SpawnedComponentControlException(msg);
		}
		
		this.connection = connection;
		triggerHeartbeat(instanceId);  // TODO need to think about re-ordering / happens-before here - should be ok as triggerHeartbeat is modifying a volatile. 
		
		boolean compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.AWAITING_REGISTRATION, TopologyMonitorState.RUNNING);
		if (compareAndSetSuccessful) {
			notifyStateChange();
			notifyConnectionRegistration();
		}
		else {
			log.warn("[" + nameForLogging + "] registration failed as state is [" + state + "]. Expected  [" + TopologyMonitorState.AWAITING_REGISTRATION + "]. Did Component startup take too long?");
			//TODO should we throw here??
		}
	}

	@Override
	public void triggerHeartbeat(long instanceId) throws SpawnedComponentControlException {
		if (this.instanceId != instanceId) {
			String msg = "Heartbeat failed. Wrong instance. Expected [" + this.instanceId + "] got [" + instanceId + "], please shut yourself down" ;
			log.error(msg);
			throw new SpawnedComponentControlException(msg);
		}

		lastHeartbeatTimestamp = System.currentTimeMillis();
	}

	@Override
	protected boolean monitorStateAWAITING_REGISTRATION() {
		boolean compareAndSetSuccessful = true;

		if (state.getElapsedTimeSinceLastStatusChangeMs() > STARTUP_TOLERANCE_MS) {
			log.warn("Startup of [" + nameForLogging + "] has exceeded tolerace of [" + STARTUP_TOLERANCE_MS + "]ms");
			// As we exceeded startup tolerance, we mark as dead
			compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.AWAITING_REGISTRATION, TopologyMonitorState.DECLARED_DEAD);
			if (compareAndSetSuccessful) {
				notifyStateChange();
				notifyConnectionDeregistration();
			}
		}
		return compareAndSetSuccessful;
	}
	
	@Override
	protected boolean monitorStateRUNNING() {
		boolean compareAndSetSuccessful = true;
		boolean missedHeartbeat = isMissedHeartbeat();
		if (missedHeartbeat) {
			log.warn("[" + state + "] [" + nameForLogging + "] missed [" + HEARTBEAT_INTERVAL_MS
					+ "ms] heartbeat.");
			compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.RUNNING, TopologyMonitorState.MISSED_HEARTBEAT);
			if (compareAndSetSuccessful) {
				notifyStateChange();
			}
		}
		return compareAndSetSuccessful;
	}

	@Override
	protected boolean monitorStateMISSED_HEARTBEAT() {
		boolean compareAndSetSuccessful = true;
		boolean missedHeartbeat = isMissedHeartbeat();
		if (!missedHeartbeat) {
			log.warn("[" + state + "] [" + nameForLogging + "] has recovered from missed heartbeat.");
			compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.MISSED_HEARTBEAT, TopologyMonitorState.RUNNING);
			if (compareAndSetSuccessful) {
				notifyStateChange();
			}
		}
		else if (state.getElapsedTimeSinceLastStatusChangeMs() > HEARTBEAT_INTERVAL_SECOND_CHANCE_MS) {
			log.warn("[" + state + "] [" + nameForLogging + "] missed 2nd chance [" + HEARTBEAT_INTERVAL_SECOND_CHANCE_MS
					+ "ms] heartbeat.");
			compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.MISSED_HEARTBEAT, TopologyMonitorState.DECLARED_DEAD);
				// TODO switch off the hub.
				// /TODO increment the hub instance id.
			if (compareAndSetSuccessful) {
				notifyStateChange();
				notifyConnectionDeregistration();
			}
		}
		return compareAndSetSuccessful;
	}
		
	@Override
	protected boolean monitorStateDECLARED_DEAD() {
		boolean compareAndSetSuccessful = true;

		compareAndSetSuccessful = state.compareAndSet(TopologyMonitorState.DECLARED_DEAD, TopologyMonitorState.AWAITING_CONSTRUCTION);
		if (compareAndSetSuccessful) {
			notifyStateChange();
		}

		return compareAndSetSuccessful;
	}

	/**
	 * Call when a spawned component is no longer required.
	 */
	public void componentDeletedNotification() {
		state.set(TopologyMonitorState.AWAITING_DELETION);
		notifyStateChange();
	}
	
	@Override
	protected boolean monitorStateAWAITING_DELETION() {
		boolean compareAndSetSuccessful = true;
		
		SpawnedComponentControlServiceS2CApi spawnedComponentControlServiceListener = null;
		if (connection != null) {
			spawnedComponentControlServiceListener = connection.getService(SpawnedComponentControlServiceS2CApi.class);
		
			if ( spawnedComponentControlServiceListener != null) {
				spawnedComponentControlServiceListener.spawnedComponentControlServiceListener_shutdown();
			}
			else {
				log.error("spawnedComponentControlServiceListener is null while shutting down spawned component [" + nameForLogging + "], may not have registered yet.");
			}
		
		}
		else {
			log.error("connection is null while shutting down spawned component [" + nameForLogging + "], may not have registered yet.");
		}
		
		
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
	
	private boolean isMissedHeartbeat() {
		long currentTime = System.currentTimeMillis();
		long millisecondsOverdue = currentTime
				- (lastHeartbeatTimestamp + HEARTBEAT_INTERVAL_MS + HEARTBEAT_TOLERANCE_MS);
		boolean missedHeartbeat = millisecondsOverdue > 0;
		return missedHeartbeat;
	}
	
}