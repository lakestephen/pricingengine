package com.concurrentperformance.pebble.ui.functional.topology.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.ui.functional.topology.model.HubTopologyNode;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class NewContainerAction extends AbstractAction {

	private static final long serialVersionUID = 8933349641917705267L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final HubTopologyNode parentHubTopologyNode;
	private final ClientTopologyServiceC2SApi topologyService;
	
	public NewContainerAction(ClientTopologyServiceC2SApi topologyService, HubTopologyNode parentHubTopologyNode) {
		super("Add New Container to " + parentHubTopologyNode); //TODO icon
		this.topologyService = topologyService;
		this.parentHubTopologyNode = parentHubTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Creating new container");
		final String containerName = 
			(String)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Enter new container name:\n",
                "New Container",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		if (containerName == null || containerName.isEmpty()) {
			log.info("User did not enter container name");
			return;
		}

		try { 
			long parentHubId = parentHubTopologyNode.getId();
			topologyService.createContainer(parentHubId, containerName);
		} 
		catch (ClientException e){
			final String errorTitle = "Error creating new container: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + containerName, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}
