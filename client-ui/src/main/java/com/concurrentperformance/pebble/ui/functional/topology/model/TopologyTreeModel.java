package com.concurrentperformance.pebble.ui.functional.topology.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceS2C;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientApplicationDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientHubDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMachineDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMountDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;
import com.concurrentperformance.pebble.ui.common.tree.SkelitalTreeModel;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class TopologyTreeModel extends SkelitalTreeModel<TopologyNode> implements TreeModel, ClientTopologyServiceS2C {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final ClientTopologyServiceC2S clientTopologyService;
	
	private final ConcurrentMap<Long, TopologyNode> allNodesById  = new ConcurrentHashMap<Long, TopologyNode>();

	private final TopologyNode applicationTopologyNode = new ApplicationTopologyNode();;

	public TopologyTreeModel(ClientTopologyServiceC2S clientTopologyService) {
		this.clientTopologyService = clientTopologyService;
		clientTopologyService.register(this);
	}

	@Override
	public Object getRoot() {
		return applicationTopologyNode;
	}

	@Override
	public Object getChild(Object parent, int index) {
		TopologyNode node = (TopologyNode)parent;
		return node.getChild(index);
	}

	@Override
	public int getChildCount(Object parent) {
		TopologyNode node = (TopologyNode)parent;
		return node.getChildCount();
	}

	@Override
	public boolean isLeaf(Object node) {
		return (node instanceof MountTopologyNode);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// SJL Auto-generated method stub
		
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		TopologyNode parentNode = (TopologyNode)parent; //TODO defer to TopologyNode
		for (int i=0;i<parentNode.getChildCount();i++) {
			TopologyNode childNode = parentNode.getChild(i);
			if (childNode == child) {
				return i;
			}
		}
		return -1;
	}

	private TopologyNode findTopologyNode(long topologyId) { 
		return allNodesById.get(topologyId);
	}

	private void removeChild(TopologyNode parent, long id) {
		if (parent != null) {
			int childIndex = parent.getChildIndex(id);
			TopologyNode child = parent.removeChild(id);
			allNodesById.remove(id);
			fireTreeNodesRemoved(parent, child, childIndex);
		}
	}

	private void addChild(TopologyNode parent, ClientTopologyDetails clientTopologyDetails) {
		if (parent != null) {
			TopologyNode child = parent.addChild(clientTopologyDetails);
			allNodesById.put(clientTopologyDetails.getId(), child);
			fireTreeNodesInserted(parent, child);
		}
	}

	@Override
	public void clientTopologyService_applicationCreated(ClientApplicationDetails clientApplicationDetails) {
		log.info("Application created notification [" + clientApplicationDetails + "]");
		applicationTopologyNode.setClientTopologyDetails(clientApplicationDetails);
		fireTreeNodeChanged(applicationTopologyNode);
	}

	@Override
	public void clientTopologyService_machineCreated(ClientMachineDetails clientMachineDetails) {
		log.info("Machine created notification [" + clientMachineDetails + "]");
		addChild(applicationTopologyNode, clientMachineDetails);
	}
	
	@Override
	public void clientTopologyService_machineUpdated(ClientMachineDetails clientMachineDetails) {
		TopologyNode machine = findTopologyNode(clientMachineDetails.getId());	
		machine.setClientTopologyDetails(clientMachineDetails);
		fireTreeNodeChanged(machine);
	}

	@Override
	public void clientTopologyService_machineDeleted(ClientMachineDetails clientMachineDetails) {
		log.info("Machine deleted notification [" + clientMachineDetails + "]");
		removeChild(applicationTopologyNode, clientMachineDetails.getId());
	}

	@Override
	public void clientTopologyService_hubCreated(ClientHubDetails clientHubDetails) {
		log.info("Hub created notification [" + clientHubDetails + "]");
		TopologyNode parent = findTopologyNode(clientHubDetails.getParentId());	
		addChild(parent, clientHubDetails);
	}
	
	@Override
	public void clientTopologyService_hubUpdated(ClientHubDetails clientHubDetails) {
		log.info("Hub updated notification [" + clientHubDetails + "]");
		TopologyNode hub = findTopologyNode(clientHubDetails.getId());	
		hub.setClientTopologyDetails(clientHubDetails);
		fireTreeNodeChanged(hub);
	}

	@Override
	public void clientTopologyService_hubDeleted(ClientHubDetails clientHubDetails) {
		log.info("Hub deleted notification [" + clientHubDetails + "]");
		TopologyNode hub = findTopologyNode(clientHubDetails.getId());	
		removeChild(hub.getParent(), clientHubDetails.getId());
	}

	@Override
	public void clientTopologyService_containerCreated(ClientContainerDetails clientContainerDetails) {
		log.info("Container created notification [" + clientContainerDetails + "]");
		TopologyNode hub = findTopologyNode(clientContainerDetails.getParentId());
		addChild(hub, clientContainerDetails); 
	}
	
	@Override
	public void clientTopologyService_containerUpdated(ClientContainerDetails clientContainerDetails) {
		log.info("Container updated notification [" + clientContainerDetails + "]");
		TopologyNode container = findTopologyNode(clientContainerDetails.getId());	
		container.setClientTopologyDetails(clientContainerDetails);
		fireTreeNodeChanged(container);
	}


	@Override
	public void clientTopologyService_containerDeleted(ClientContainerDetails clientContainerDetails) {
		log.info("Container deleted notification [" + clientContainerDetails + "]");
		TopologyNode container = findTopologyNode(clientContainerDetails.getId());	
		removeChild(container.getParent(), clientContainerDetails.getId()); 
	}
	
	@Override
	public void clientTopologyService_mountCreated(ClientMountDetails clientMountDetails) {
		log.info("Mount created notification [" + clientMountDetails + "]");
		TopologyNode container = findTopologyNode(clientMountDetails.getParentId());
		if (container != null) {
			addChild(container, clientMountDetails); 
		}
	}

	@Override
	public void clientTopologyService_mountHosted(ClientMountDetails clientMountDetails) {
		log.info("Mount hosted notification [" + clientMountDetails + "]");
		clientTopologyService_mountCreated(clientMountDetails);
	}

	@Override
	public void clientTopologyService_mountDehosted(ClientMountDetails clientMountDetails) {
		log.info("Mount dehosted notification [" + clientMountDetails + "]");
		clientTopologyService_mountDeleted(clientMountDetails);
	}

	@Override
	public void clientTopologyService_mountDeleted(ClientMountDetails clientMountDetails) {
		log.info("Mount deleted notification [" + clientMountDetails + "]");
		TopologyNode mount = findTopologyNode(clientMountDetails.getId());	
		if (mount.getParent() != null) {
			removeChild(mount.getParent(), clientMountDetails.getId());
		}
	}
	
	@Override
	public void clientTopologyService_updateTopologyState(long topologyId, ClientTopologyState clientTopologyState) {
		log.info("Status [" + topologyId+ ", " + clientTopologyState + "]");
		TopologyNode topologyNode = findTopologyNode(topologyId);	
		if (topologyNode != null ) {
			topologyNode.setClientTopologyState(clientTopologyState);
			fireTreeNodeChanged(topologyNode);
		}
	}

	@Override
	public void connectionSupport_connectionStarted() {
		clientTopologyService.populate();
	}

	@Override
	public void connectionSupport_connectionStopped() {
		log.info("Connection stopped, clearing Topology Model");
		applicationTopologyNode.removeAllChildren();
		fireTreeStructureChanged(applicationTopologyNode);		
	}

}