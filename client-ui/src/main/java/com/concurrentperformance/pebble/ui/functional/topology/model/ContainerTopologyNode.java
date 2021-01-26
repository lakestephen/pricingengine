package com.concurrentperformance.pebble.ui.functional.topology.model;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMountDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
//TODO synchronise
public class ContainerTopologyNode extends SkelitalTopologyNode implements TopologyNode {
	
	ContainerTopologyNode(HubTopologyNode parent, ClientContainerDetails clientContainerDetails) {
		super(parent, clientContainerDetails);
	}

	@Override
	protected TopologyNode buildChild(ClientTopologyDetails clientTopologyDetails) {
		return new MountTopologyNode(this, (ClientMountDetails)clientTopologyDetails);
	}
	
	@Override
	void checkType(ClientTopologyDetails clientTopologyDetails) {
		if (!(clientTopologyDetails instanceof ClientContainerDetails)) {
			throw new IllegalArgumentException("clientTopologyDetails [" + clientTopologyDetails + "] is not an instance of ClientHubDetails");
		}
	}
}
