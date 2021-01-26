package com.concurrentperformance.pebble.ui.functional.topology.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class NewMachineAction extends AbstractAction {

	private static final long serialVersionUID = 3651158836814193887L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final ClientTopologyServiceC2SApi topologyService;
	
	public NewMachineAction(ClientTopologyServiceC2SApi topologyService) {
		super("Add New Machine"); //TODO icon
		this.topologyService = topologyService;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		log.info("Creating new machine");
		final String machineName = 
			(String)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Enter new machine host name:\n",
                "New Machine",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		if (machineName == null || machineName.isEmpty()) {
			log.info("User did not enter machine name");
			return;
		}

		try { 
			topologyService.createMachine(machineName);
		} 
		catch (ClientException e){
			final String errorTitle = "Error creating new machine: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + machineName, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}
