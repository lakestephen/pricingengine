package com.concurrentperformance.pebble.ui.functional.topology;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;
import com.concurrentperformance.pebble.ui.functional.topology.model.ApplicationTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.ContainerTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.HubTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.MachineTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.MountTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.TopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.TopologyTreeModel;

public class TopologyComponent extends JTree {
	
	private static final long serialVersionUID = -6833813332015291630L;

	private final Log log = LogFactory.getLog(this.getClass());

	private ClientTopologyServiceC2S clientTopologyService;
	
	public void start() {
		createMouseListener();
		createTreeModel();
		setCellRenderer(new TopologyTreeCellRenderer());
	}

	@Override
    public Insets getInsets() {
		return new Insets(5,5,5,5); 
    }

	private class TopologyTreeCellRenderer extends DefaultTreeCellRenderer { 
	
			private static final long serialVersionUID = 1L;

		private Icon application;
		private Icon machine;
		private Icon hub;
		private Icon container;
		private Icon mount;

		TopologyTreeCellRenderer() {
			application = new ImageIcon( getClass().getResource("/images/application.png"));
			machine = new ImageIcon( getClass().getResource("/images/machine.png"));
			hub= new ImageIcon( getClass().getResource("/images/hub.png" ));
			container = new ImageIcon( getClass().getResource("/images/container.png" ));
			mount = new ImageIcon( getClass().getResource("/images/hostedMount.png" ));
		}		
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			
			TopologyNode topologyNode  = (TopologyNode )value;
			if ((topologyNode instanceof MachineTopologyNode || 
				 topologyNode instanceof ContainerTopologyNode || 
				 topologyNode instanceof HubTopologyNode) &&
				(topologyNode.getClientTopologyState() == ClientTopologyState.STOPPED)) {
				super.setBackgroundNonSelectionColor(Color.PINK);
				super.setBackgroundSelectionColor(Color.PINK);	
			}
			else {
				super.setBackgroundNonSelectionColor(SystemColor.window);
				super.setBackgroundSelectionColor(SystemColor.textHighlight);	
			}
			
			if (topologyNode instanceof ApplicationTopologyNode) {
				setIcon(application);
			}
			if (topologyNode instanceof MachineTopologyNode) {
				setIcon(machine);
			}
			else if (topologyNode instanceof HubTopologyNode) {
				setIcon(hub);
			}
			else if (topologyNode instanceof ContainerTopologyNode) {
				setIcon(container);
			}
			else if (topologyNode instanceof MountTopologyNode) {
				setIcon(mount);
			}

			
			return this;
		}
		
	}
	
	private void createMouseListener() {
		addMouseListener(new TopologyMouseListener(this, clientTopologyService));
	}

    private void createTreeModel() {
    	TopologyTreeModel topologyModel = new TopologyTreeModel(clientTopologyService);
		setModel(topologyModel);
    }
    
	public void setClientTopologyService(ClientTopologyServiceC2S clientTopologyService) { 
		this.clientTopologyService = clientTopologyService;
	}

}