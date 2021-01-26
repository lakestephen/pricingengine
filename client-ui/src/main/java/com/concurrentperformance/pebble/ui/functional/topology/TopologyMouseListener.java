package com.concurrentperformance.pebble.ui.functional.topology;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.ui.functional.topology.action.DeleteContainerAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.DeleteHubAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.DeleteMachineAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.NewContainerAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.NewHubAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.NewMachineAction;
import com.concurrentperformance.pebble.ui.functional.topology.action.SetHubListenPortAction;
import com.concurrentperformance.pebble.ui.functional.topology.model.ApplicationTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.ContainerTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.HubTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.MachineTopologyNode;
import com.concurrentperformance.pebble.ui.functional.topology.model.TopologyNode;
/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class TopologyMouseListener extends MouseAdapter { //TODO should this be a spring configured Mouse adapter, not a specific TopologyMouseListener 
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private final TopologyComponent parent;
	private final ClientTopologyServiceC2SApi topologyService;
	
	/**
	 * @param topologyTab
	 */
	public TopologyMouseListener(TopologyComponent parent, ClientTopologyServiceC2SApi topologyService) {  
		this.parent = parent;
		this.topologyService = topologyService;
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
			
			TopologyNode node = (TopologyNode)tp.getLastPathComponent();
			if (node == null) {
				return;
			}
			
			log.debug("Right Click on [" + tp + "], [" + node + "]");
			
			JPopupMenu menu = null;
			if (node instanceof ApplicationTopologyNode) { 
				menu = buildApplicationMenu((ApplicationTopologyNode)node);
			}
			else if (node instanceof MachineTopologyNode) { 
				menu = buildMachineMenu((MachineTopologyNode)node);
			}
			else if (node instanceof HubTopologyNode) { 
				menu = buildHubMenu((HubTopologyNode)node);
			}
			else if (node instanceof ContainerTopologyNode) { 
				menu = buildContainerMenu((ContainerTopologyNode)node);
			}
			else {
				log.warn("node type [" + node.getClass() + "] is not supported");
				return;
			}
				
			menu.show((Component) me.getSource(), me.getX(), me.getY());
		}		
	}

	private JPopupMenu buildApplicationMenu(ApplicationTopologyNode applicationTopologyNode) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new NewMachineAction(topologyService));
		return menu;
	}

	private JPopupMenu buildMachineMenu(MachineTopologyNode machineTopologyNode) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new DeleteMachineAction(topologyService, machineTopologyNode));
		menu.add(new NewHubAction(topologyService, machineTopologyNode));
		return menu;
	}

	private JPopupMenu buildHubMenu(HubTopologyNode hubTopologyNode) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new SetHubListenPortAction(topologyService, hubTopologyNode));
		menu.add(new DeleteHubAction(topologyService, hubTopologyNode));
		menu.add(new NewContainerAction(topologyService, hubTopologyNode));
		return menu;
	}

	private JPopupMenu buildContainerMenu(ContainerTopologyNode containerTopologyNode) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new DeleteContainerAction(topologyService, containerTopologyNode));
		return menu;
	}
}
