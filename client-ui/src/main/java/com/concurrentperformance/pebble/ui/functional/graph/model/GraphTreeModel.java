package com.concurrentperformance.pebble.ui.functional.graph.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.graph.ClientGraphServiceC2S;
import com.concurrentperformance.pebble.clientapi.controller.graph.ClientGraphServiceS2C;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.ui.common.tree.SkelitalTreeModel;

public class GraphTreeModel extends SkelitalTreeModel<GraphNode> 
		implements TreeModel, ClientGraphServiceS2C {

	private final Log log = LogFactory.getLog(this.getClass());

	private final GraphNode rootNode = new GraphNode(null, "<ROOT>");
	private Map<Long, GraphNode> mouontNodes = new HashMap<Long, GraphNode>();
	private Map<Long, GraphNode> calculationNodes = new HashMap<Long, GraphNode>(); //TODO threading
	private Map<String, GraphNode> eventInterestNodes = new HashMap<String, GraphNode>(); //TODO threading

	private final ClientGraphServiceC2S clientGraphService;
	
	public GraphTreeModel(ClientGraphServiceC2S clientGraphService) {
		this.clientGraphService = clientGraphService;
    	
		clientGraphService.register(this); 
	}
	
	@Override
	public Object getRoot() {
		return rootNode;
	}

	@Override
	public Object getChild(Object parent, int index) {
		GraphNode node = (GraphNode)parent;
		return node.getChild(index);
	}

	@Override
	public int getChildCount(Object parent) {
		GraphNode parentNode = (GraphNode)parent;
		return parentNode.getChildCount();
	}

	@Override
	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void clientGraphService_mountCreated(long mountId, String path) {
		log.info("Mount item created [" + mountId + "], path [" + path + "]");
		GraphNode node = getOrCreateNode(path);
		node.setMountType(mountId);
		mouontNodes.put(mountId, node);
	
	}

	@Override
	public void clientGraphService_mountHosted(long mountId, long containerId) {
		GraphNode node = mouontNodes.get(mountId);
		if (node == null) {
			log.warn("Cant find mount id [" + mountId + "]");
		}
		else {
			node.setMountContainer(containerId);
			fireTreeNodeChanged(node);
		}
		
		//TODO decouple with executor service
		try {
			clientGraphService.registerForUpdatesFrom(mountId);
		} catch (ClientException e) {
			log.error("Registration of updates from mount [" + mountId + "] failed", e);
		}
	}

	@Override
	public void clientGraphService_mountDehosted(long mountId) {
		clientGraphService_mountHosted(mountId, -1);
	}
	 
	@Override
	public void clientGraphService_graphItemCreated(long graphItemId, String path, String calculationType, String outputEventId) {
		log.info("Graph item created [" + graphItemId + "], path [" + path + "], type [" + calculationType + "], output event id [" + outputEventId + "]");
		GraphNode node = getOrCreateNode(path);
		
		calculationNodes.put(graphItemId, node);
		node.setCalculationType(graphItemId);

		eventInterestNodes.put(outputEventId, node);
		node.setCalculationOutputEventId(outputEventId);
	}

	@Override
	public void clientGraphService_eventValueUpdated(String eventId, int value) {
		if (log.isTraceEnabled()) {
			log.info("Event value updated [" + eventId + "], value [" +  value + "]");
		}

		GraphNode node = eventInterestNodes.get(eventId);
		if (node != null) {
			node.setCalculationValue(value);
			fireTreeNodeChanged(node);
		}		
	}

	private GraphNode getOrCreateNode(String path) {
		String[] pathParts = path.split("\\.");
		GraphNode curNode = rootNode;
		for (String part : pathParts) {
			GraphNode child = curNode.getChild(part);

			if (child == null) {
				child = curNode.addChild(part);
				fireTreeNodesInserted(curNode, child);
			}
			curNode = child;
		}
		
		return curNode;
	}
	
	@Override
	public void connectionSupport_connectionStarted() {
		clientGraphService.populateAllMounts();
	}

	@Override
	public void connectionSupport_connectionStopped() {
		log.info("Connection stopped, clearing Graph Model");
		rootNode.removeAllChildren();
		fireTreeStructureChanged(rootNode);		
	}

}