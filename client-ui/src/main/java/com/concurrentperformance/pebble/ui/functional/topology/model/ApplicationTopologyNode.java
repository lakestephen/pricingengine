package com.concurrentperformance.pebble.ui.functional.topology.model;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientApplicationDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMachineDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;



/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
//TODO synchronise
public class ApplicationTopologyNode extends SkelitalTopologyNode implements TopologyNode {

	/**
	 * @param applicationDescriptor
	 */
	ApplicationTopologyNode() {
		super(null, new ClientApplicationDetails(-1, -2));
	}
	
	@Override
	protected TopologyNode buildChild(ClientTopologyDetails childDetails) {
		return new MachineTopologyNode(this, (ClientMachineDetails)childDetails);
	}

	@Override
	void checkType(ClientTopologyDetails clientTopologyDetails) {
		if (!(clientTopologyDetails instanceof ClientApplicationDetails)) {
			throw new IllegalArgumentException("childDetails [" + clientTopologyDetails + "] is not an instance of ClientApplicationDetails");
		}
	}

}
