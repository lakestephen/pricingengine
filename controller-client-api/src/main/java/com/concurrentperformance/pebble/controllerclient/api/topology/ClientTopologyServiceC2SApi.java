package com.concurrentperformance.pebble.controllerclient.api.topology;

import java.util.Set;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;

/**
 * API that defines the methods a client can call on a controller. 
 *
 * @author Stephen Lake
 */
public interface ClientTopologyServiceC2SApi { 

	public void populate();

	public long createMachine(String machineName) throws ClientException;
	public void deleteMachine(long machineId) throws ClientException;
	
	public long createHub(long parentMachineHostId, String hubName) throws ClientException;
	public void setHubListenPort(long hubId, int hubListenPort) throws ClientException;
	public void deleteHub(long hubId) throws ClientException;
	
	public long createContainer(long parentHubId, String containerName) throws ClientException;
	public void deleteContainer(long containerId)throws ClientException;
	public Set<ClientContainerDetails> getAllContainers();
	
	public long createMount(String mountName) throws ClientException;
	public void deleteMount(long mountId) throws ClientException;
	public void hostMountInContainer(long mountId, long hostContainerId) throws ClientException;
	public void dehostMount(long mountId) throws ClientException;

}
