package com.concurrentperformance.pebble.ui.functional.graph;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.ui.functional.graph.action.CreateMountAction;
import com.concurrentperformance.pebble.ui.functional.graph.action.DehostMountAction;
import com.concurrentperformance.pebble.ui.functional.graph.action.HostMountAction;
import com.concurrentperformance.pebble.ui.functional.graph.model.GraphNode;

public class GraphMouseListener extends MouseAdapter {

	private final Log log = LogFactory.getLog(this.getClass());

	private final GraphComponent parent;
	private final ClientTopologyServiceC2S topologyService;
	
	public GraphMouseListener(GraphComponent parent, ClientTopologyServiceC2S clientTopologyService) {
		this.parent = parent;
		this.topologyService = clientTopologyService;
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		doMouseClicked(me);
	}
	
	protected void doMouseClicked(MouseEvent me) {
		
		if (me.getButton() == MouseEvent.BUTTON3) {
			
			TreePath tp = parent.getPathForLocation(me.getX(), me.getY());
			if (tp == null) {
				return;
			}
			
			GraphNode node = (GraphNode)tp.getLastPathComponent();
			if (node == null) {
				return;
			}
			
			log.debug("Right Click on [" + tp + "], [" + node + "]");
			
			JPopupMenu menu = null;
			if (node.isMountType()) {
				menu = buildMountItemMenu(node);
			}
			else {
				menu = buildGraphItemMenu();
			}
				
			menu.show((Component) me.getSource(), me.getX(), me.getY());
		}		
	}

	private JPopupMenu buildMountItemMenu(GraphNode node) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CreateMountAction(topologyService));
		if (node.isMountHostedInContainer()) {
			menu.add(new DehostMountAction(node, topologyService));
		}
		else {
			menu.add(new HostMountAction(node, topologyService));
		}
		return menu;
	}
	
	private JPopupMenu buildGraphItemMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CreateMountAction(topologyService));
		return menu;
	}

}
