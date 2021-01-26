package com.concurrentperformance.pebble.controllerclient.api.topology;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientApplicationDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientHubDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMachineDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMountDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;



public interface ClientTopologyServiceS2CApi {  
		
	void clientTopologyService_applicationCreated(ClientApplicationDetails clientApplicationDetails);  

	void clientTopologyService_machineCreated(ClientMachineDetails clientMachineDetails);  
	void clientTopologyService_machineUpdated(ClientMachineDetails clientMachineDetails);
	void clientTopologyService_machineDeleted(ClientMachineDetails clientMachineDetails);
	
	void clientTopologyService_hubCreated(ClientHubDetails clientHubDetails);  
	void clientTopologyService_hubUpdated(ClientHubDetails clientHubDetails);
	void clientTopologyService_hubDeleted(ClientHubDetails clientHubDetails);

	void clientTopologyService_containerCreated(ClientContainerDetails clientContainerDetails);  
	void clientTopologyService_containerUpdated(ClientContainerDetails clientContainerDetails);
	void clientTopologyService_containerDeleted(ClientContainerDetails clientContainerDetails);

	void clientTopologyService_mountCreated(ClientMountDetails clientMountDetails);
	void clientTopologyService_mountHosted(ClientMountDetails clientMountDetails);
	void clientTopologyService_mountDehosted(ClientMountDetails clientMountDetails);
	void clientTopologyService_mountDeleted(ClientMountDetails clientMountDetails);

	void clientTopologyService_updateTopologyState(long topologyId, ClientTopologyState clientTopologyState);



}
