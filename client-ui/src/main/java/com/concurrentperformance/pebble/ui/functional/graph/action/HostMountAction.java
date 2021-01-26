package com.concurrentperformance.pebble.ui.functional.graph.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.ui.functional.graph.model.GraphNode;

public class HostMountAction extends AbstractAction {

	private final Log log = LogFactory.getLog(this.getClass());

	private final GraphNode node;
	private final ClientTopologyServiceC2S topologyService;
	
	public HostMountAction(GraphNode node, ClientTopologyServiceC2S topologyService) {
		super("Host Mount"); //TODO icon
		this.node = node;
		this.topologyService = topologyService;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Host Mount in Container");
		
		Set<ClientContainerDetails> allContainers = topologyService.getAllContainers();

		ClientContainerWrapper[] wrappedContainers = new ClientContainerWrapper[allContainers.size()]; 
		int i = 0;
		for (ClientContainerDetails clientContainerDetails : allContainers) {
			wrappedContainers[i++] = new ClientContainerWrapper(clientContainerDetails);
		}
		
		final ClientContainerWrapper selectedContainer = 
			(ClientContainerWrapper)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Choose a Container to host mount :\n",
                "Host Mount",
                JOptionPane.PLAIN_MESSAGE,
                null,
                wrappedContainers,
                ""); //TODO get the current mount.
		if (selectedContainer == null ) {
			log.info("User did not enter choose a host container");
			return;
		}

		try { 
			topologyService.hostMountInContainer(node.getMountId(), selectedContainer.details.getId());
		} 
		catch (ClientException e){
			final String errorTitle = "Error hosting mount: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + node.getName(), e.getReportingLevel().getjOptionPaneValue());
		}
	}

	/**
	 * Wrapper to allow the toString() to be overwritten.
	 */
	class ClientContainerWrapper {
		final ClientContainerDetails details;
		
		ClientContainerWrapper (ClientContainerDetails details) {
			this.details = details;
		}
		
		@Override
		public String toString() {
			return "Container: " + details.getName() ;
		}
		
	}
	
}
