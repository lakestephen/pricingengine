package com.concurrentperformance.pebble.spawnedcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.client.RpcClientC2SSupport;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceS2CApi;
import com.concurrentperformance.pebble.spawnedcommon.controller.SpawnedComponentControlServiceC2S;
import com.concurrentperformance.pebble.util.shutdown.TerminatorService;
import com.concurrentperformance.pebble.util.shutdown.TerminatorServiceListener;

public class SpawnedComponentLifecycle implements SpawnedComponentControlServiceS2CApi, 
					RpcClientC2SSupport, TerminatorServiceListener {

	private final Log log = LogFactory.getLog(this.getClass());

	private String spawnedComponentType;
	private long spawnedComponentId;
	private long spawnedComponentInstanceId;
	private String spawnedComponentName;
	
	private SpawnedComponentControlServiceC2S spawnedComponentControlService;
	private RpcConnection connectionToController;
	
	private TerminatorService terminatorService;

	public void start() {
		log.info("Starting " + this );

		try {
			spawnedComponentControlService.registerSpawnedComponent(spawnedComponentId, spawnedComponentInstanceId);
			startHeartbeatThread();
		}
		catch (Exception e) {
			String msg = "Registration of " + this + " unsuccessfull!";
			log.error(msg);
			terminatorService.terminateNow(msg);		
		}
	}

	private void startHeartbeatThread() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					while (!terminatorService.isTerminating()) {
						spawnedComponentControlService.heartbeatSpawnedComponent(spawnedComponentId, spawnedComponentInstanceId);
						try {
							Thread.sleep(1000);// TODO configurable
						} catch (InterruptedException e) {
							log.info("", e);
						}
					}
				}
				catch (Exception e) {
					log.error("Exception sending heartbeat", e);					
				}
				String msg = "SpawnedHeartbeatSender terminated";
				log.info(msg);
				terminatorService.terminateNow(msg);
			}
		};
		
		Thread t = new Thread(r, "SpawnedHeartbeatSender");
		t.start();
	}

	@Override
	public void spawnedComponentControlServiceListener_shutdown() {
		terminatorService.terminateNow("Shutdown request recieved from server" + this ); 
	}

	@Override
	public void terminateService_terminate() { //TODO this is dependent on the heartbeat thread. This is bad and should be changed. 
		//UUGH  Wait to make sure the spawnedComponentControlServiceListener_shutdown has responded before we shut down the socket
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// SJL Auto-generated catch block
			e.printStackTrace();
		}// TODO configurable
		
		log.info("Stopping connection [" + connectionToController + "]");
	
		connectionToController.stop();
    	// wait for connection to stop
    	while (!connectionToController.isStopped()) {
    		log.info("waiting for connection to stop");
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.warn("InterruptedException", e);
			}
    	}
   	}	
	

	@Override
	public void connectionSupport_connectionStarted() {
		// SJL Auto-generated method stub
		
	}

	@Override
	public void connectionSupport_connectionStopped() {
		// The connection has shut off, so shut-down
		terminatorService.terminateNow("Connection terminated. [" + this + "]"); 
	}

	public void setSpawnedComponentType(String spawnedComponentType) {
		this.spawnedComponentType = spawnedComponentType;
	}

	public final void setSpawnedComponentName(String spawnedComponentName) {
		this.spawnedComponentName = spawnedComponentName;
	}

	public final void setSpawnedComponentId(long spawnedComponentId) {
		this.spawnedComponentId = spawnedComponentId;
	}

	public final void setSpawnedComponentInstanceId(long spawnedComponentInstanceId) {
		this.spawnedComponentInstanceId = spawnedComponentInstanceId;
	}

	public void setSpawnedComponentControlService(SpawnedComponentControlServiceC2S spawnedComponentControlService) {
		this.spawnedComponentControlService = spawnedComponentControlService;
		spawnedComponentControlService.register(this);
	}
	
	public final void setConnectionToController(RpcConnection connectionToController) {
		this.connectionToController = connectionToController;
	}

	public void setTerminatorService(TerminatorService terminatorService) {
		this.terminatorService = terminatorService;
		terminatorService.register(this);
	}

	@Override
	public String toString() {
		return "[" + spawnedComponentType + "," + spawnedComponentName + "," + spawnedComponentId + ", " + spawnedComponentInstanceId + "]";
	}

}
