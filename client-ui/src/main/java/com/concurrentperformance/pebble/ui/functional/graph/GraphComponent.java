package com.concurrentperformance.pebble.ui.functional.graph;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.graph.ClientGraphServiceC2S;
import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.ui.functional.graph.model.GraphNode;
import com.concurrentperformance.pebble.ui.functional.graph.model.GraphTreeModel;

public class GraphComponent extends JTree {

	private static final long serialVersionUID = -7043925569193615669L;

	private final Log log = LogFactory.getLog(this.getClass());

	private ClientGraphServiceC2S graphService;
	private ClientTopologyServiceC2S topologyService;
	
	public void start() {
		createMouseListener();
		createTreeModel();
		setCellRenderer(new GraphTreeCellRenderer());
	}
	
    private void createTreeModel() {
    	GraphTreeModel graphModel = new GraphTreeModel(graphService);
    	setModel(graphModel);
    }
    
	private void createMouseListener() {
		addMouseListener(new GraphMouseListener(this, topologyService));
	}
	
	public void setClientGraphService(ClientGraphServiceC2S clientGraphService) { 
		this.graphService = clientGraphService;
	}
	
	public void setClientTopologyService(ClientTopologyServiceC2S topologyService) { 
		this.topologyService = topologyService;
	}

	private class GraphTreeCellRenderer extends DefaultTreeCellRenderer { 
		
		private static final long serialVersionUID = 1L;

		private Icon calculate;
		private Icon hostedMount;
		private Icon unhostedMount;
	
		GraphTreeCellRenderer () {
			calculate = new ImageIcon( getClass().getResource("/images/calculate.png" ));
			hostedMount = new ImageIcon( getClass().getResource("/images/hostedMount.png" ));
			unhostedMount = new ImageIcon( getClass().getResource("/images/unhostedMount.png" ));
		}		
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	
			super.getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			
			GraphNode graphNode  = (GraphNode )value;
			
			if (graphNode.isCalculationType()) {
				setIcon(calculate);
			}
			else if (graphNode.isMountType()) {
				if (graphNode.isMountHostedInContainer()) {
					setIcon(hostedMount);
				}
				else {
					setIcon(unhostedMount);
				}
			}
			
			return this;
		}
	}

}
