package com.concurrentperformance.pebble.ui.functional.topology.model;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientHubDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;



/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
//TODO synchronise
public class HubTopologyNode extends SkelitalTopologyNode implements TopologyNode {
		
	HubTopologyNode(MachineTopologyNode parent, ClientHubDetails clientHubDetails) {
		super(parent, clientHubDetails);
	}
			
	@Override
	protected TopologyNode buildChild(ClientTopologyDetails clientTopologyDetails) {
		return new ContainerTopologyNode(this, (ClientContainerDetails)clientTopologyDetails);
	}
	

	@Override
	void checkType(ClientTopologyDetails clientTopologyDetails) {
		if (!(clientTopologyDetails instanceof ClientHubDetails)) {
			throw new IllegalArgumentException("childDetails [" + clientTopologyDetails + "] is not an instance of ClientHubDetails");
		}
	}

	@Override
	public String toString() {
		return getName() + "(" + ((ClientHubDetails) getClientTopologyDetails()).getListenPort() + ")";
	}
}