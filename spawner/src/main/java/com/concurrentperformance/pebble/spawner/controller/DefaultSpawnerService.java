package com.concurrentperformance.pebble.spawner.controller;

import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.spawner.controller.impl.JvmSpawnerDescriptor;
import com.concurrentperformance.pebble.spawnercontroller.api.SpawnerServiceC2SApi;
import com.concurrentperformance.pebble.spawnercontroller.api.SpawnerServiceS2CApi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class DefaultSpawnerService extends SkelitalRpcServerS2CSupport<SpawnerServiceS2CApi>
	implements SpawnerServiceC2SApi, RpcServerS2CSupport<SpawnerServiceS2CApi> { //TODO we should not need the listener support

	private final Log log = LogFactory.getLog(this.getClass());

	private JvmSpawnerDescriptor hubSpawner;
	private JvmSpawnerDescriptor containerSpawner;
	

	@Override
	public void spawnHub(String hubName, long hubId, long hubInstance,
			String controllersHostName, int controllersHubListeningPort, 
			int hubsContainerListeningPort) {
		log.info("Spawning a HUB [" + hubName + ", " + hubId + "], " + "inst [" + hubInstance + "], " + 
				"controler[" + controllersHostName + ":" + controllersHubListeningPort + "], " +
				"listening for containers on port [" + hubsContainerListeningPort + "]" );
		
		String hubNameVmArg =        "-Dpebble.hub.name=\"" + hubName + "\"";
		String hubIdVmArg =          "-Dpebble.hub.id=\"" + hubId+ "\"";
		String hubInstVmArg =        "-Dpebble.hub.instance=\"" + hubInstance + "\"";
		String hostVmArg =           "-Dpebble.controller.host=\"" + controllersHostName + "\"";
		String controllerPortVmArg = "-Dpebble.controller.port=\"" + controllersHubListeningPort + "\"";
		String containerPortVmArg =  "-Dpebble.container.port=\"" + hubsContainerListeningPort + "\"";
		
		List<String> additionalVmArgs = new ArrayList<String>();
		additionalVmArgs.add(hubNameVmArg);
		additionalVmArgs.add(hubIdVmArg);
		additionalVmArgs.add(hubInstVmArg);
		additionalVmArgs.add(hostVmArg);
		additionalVmArgs.add(controllerPortVmArg);
		additionalVmArgs.add(containerPortVmArg);
		
		try {
			spawnJvm(hubSpawner, additionalVmArgs, "H:" + hubName);
		} catch (IOException e) {
			log.error("Error spawning a HUB [" + hubName + ", " + hubId + "]", e);
		}
	}	

	@Override
	public void spawnContainer(String containerName, long containerId, long containerInstance, 
			String controllersHostName, int controllersContainerListeningPort, 
			int hubsContainerListeningPort,
			String databaseURL, String databaseUsername, String databasePassword) {
		log.info("Spawning a CONTAINER [" + containerName + ", " + containerId + "], " + "inst [" + containerInstance + "], " + 
				"controler[" + controllersHostName + ":" + controllersContainerListeningPort + "], " +
				"connecting to hub on port [" + hubsContainerListeningPort + "]");
		
		String containerNameVmArg =    "-Dpebble.container.name=\"" + containerName + "\"";
		String containerIdVmArg =      "-Dpebble.container.id=\"" + containerId + "\"";
		String containerInstVmArg =    "-Dpebble.container.instance=\"" + containerInstance + "\"";
		String hostVmArg =             "-Dpebble.controller.host=\"" + controllersHostName + "\"";
		String portVmArg =             "-Dpebble.controller.port=\"" + controllersContainerListeningPort + "\"";
		String hubPortVmArg =          "-Dpebble.hub.port=\"" + hubsContainerListeningPort + "\"";
		String databaseUrlVmArg =      "-Dpebble.data.url=\"" + databaseURL + "\"";
		String databaseUserVmArg =     "-Dpebble.data.username=\"" + databaseUsername + "\"";
		String databasePasswordVmArg = "-Dpebble.data.password=\"" + databasePassword + "\"";

		
		List<String> additionalVmArgs = new ArrayList<String>();
		additionalVmArgs.add(containerNameVmArg);
		additionalVmArgs.add(containerIdVmArg);
		additionalVmArgs.add(containerInstVmArg);
		additionalVmArgs.add(hostVmArg);
		additionalVmArgs.add(portVmArg);
		additionalVmArgs.add(hubPortVmArg);
		additionalVmArgs.add(databaseUrlVmArg);
		additionalVmArgs.add(databaseUserVmArg);
		additionalVmArgs.add(databasePasswordVmArg);
		
		try {
			spawnJvm(containerSpawner, additionalVmArgs, "C:" + containerName);
		} catch (IOException e) {
			log.error("Error spawning a CONTAINER [" + containerName + ", " + containerId + "]", e);
		}
	}

	private void spawnJvm(JvmSpawnerDescriptor jvmSpawnerDescriptor, List<String> additionalVmArgs, String nameForLogging) throws IOException {
		log.debug("Spawning a JVM for [" + jvmSpawnerDescriptor + "] with additionalVmArgs [" + additionalVmArgs + "]");
		
        String pathToJavaExe = getPathToJavaExe();
        String classpath = jvmSpawnerDescriptor.getClassPath();
        String canonicalClassName = jvmSpawnerDescriptor.getCanonicalClassName();
        List<String> spawnerVmArgs = hubSpawner.getVmArgs();
        
        // build the command
        List<String> command = new ArrayList<String>();
        command.add(pathToJavaExe);
        if (spawnerVmArgs != null) {
        	command.addAll(spawnerVmArgs);
        }
        if (additionalVmArgs != null) {
        	command.addAll(additionalVmArgs);
        }
        command.add("-cp");
        command.add(classpath);
        command.add(canonicalClassName);

        log.debug("Creating ProcessBuilder with [" + command + "]");
 
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(jvmSpawnerDescriptor.getDirectory()));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        
        new Thread(new SpawnedInputStreamDumpTask(process), "SpawnedStreamDump:" + nameForLogging ).start();
		
	}

	private String getPathToJavaExe() {
		String separator = System.getProperty("file.separator"); //TODO put in separate helper class
        
        String path = System.getProperty("java.home") + separator + "bin" + separator + "javaw";
		return path;
	}
	
	
	/**
	 * Task to read and dump the input stream. this has to be done otherwise 
	 * the spawned process will freeze. 
	 * 
	 * TODO: This is crap! Need to find a better way of spawning a process - a C++ based service perhaps?
	 *
	 * @author Stephen Lake
	 *
	 */
	private class SpawnedInputStreamDumpTask implements Runnable {

		private final Log log = LogFactory.getLog(this.getClass());
		
		private final Process process;
		
		public SpawnedInputStreamDumpTask(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is)); //TODO tidy and close resources
			String line = null;
			try {
				while((line = reader.readLine()) != null) {
					log.error(line);
				}
			} catch (IOException e) {
				// SJL handle properly
				log.error("", e);
			}
			log.info(Thread.currentThread().getName() + " closing" );
		}		
	}
	
	public void setHubSpawner(JvmSpawnerDescriptor hubSpawner) {
		this.hubSpawner = hubSpawner;
	}

	public void setContainerSpawner(JvmSpawnerDescriptor containerSpawner) {
		this.containerSpawner = containerSpawner;
	}

}