package com.concurrentperformance.pebble.ui.functional.topology.model;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMountDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
//TODO synchronise
public class MountTopologyNode extends SkelitalTopologyNode implements TopologyNode {
	
	MountTopologyNode(ContainerTopologyNode parent, ClientMountDetails clientMountDetails) {
		super(parent, clientMountDetails);
	}

	@Override
	protected TopologyNode buildChild(ClientTopologyDetails clientTopologyDetails) {
		throw new IllegalStateException("Mounts do not have children");
	}
	
	@Override
	void checkType(ClientTopologyDetails clientTopologyDetails) {
		if (!(clientTopologyDetails instanceof ClientMountDetails)) {
			throw new IllegalArgumentException("clientTopologyDetails [" + clientTopologyDetails + "] is not an instance of ClientMountDetails");
		}
	}
}
