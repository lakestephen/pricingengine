package com.concurrentperformance.pebble.ui.functional.topology.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.ui.functional.topology.model.MachineTopologyNode;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class NewHubAction extends AbstractAction {

	private static final long serialVersionUID = 3297059304354674076L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final MachineTopologyNode parentMachineTopologyNode;
	private final ClientTopologyServiceC2SApi topologyService;
	
	public NewHubAction(ClientTopologyServiceC2SApi topologyService, MachineTopologyNode parentMachineTopologyNode) {
		super("Add New Hub to " + parentMachineTopologyNode); //TODO icon
		this.topologyService = topologyService;
		this.parentMachineTopologyNode = parentMachineTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Creating new hub");
		final String hubName = 
			(String)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Enter new hub name:\n",
                "New Hub",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		if (hubName == null || hubName.isEmpty()) {
			log.info("User did not enter hub name");
			return;
		}

		try { 
			long parentMachineId = parentMachineTopologyNode.getId();
			topologyService.createHub(parentMachineId, hubName);
		} 
		catch (ClientException e){
			final String errorTitle = "Error creating new machine: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + hubName, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}
