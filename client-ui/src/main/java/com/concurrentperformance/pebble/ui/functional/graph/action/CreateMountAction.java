package com.concurrentperformance.pebble.ui.functional.graph.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;

public class CreateMountAction extends AbstractAction {

	private final Log log = LogFactory.getLog(this.getClass());

	private final ClientTopologyServiceC2S topologyService;
	
	public CreateMountAction(ClientTopologyServiceC2S topologyService) {
		super("Create Mount"); //TODO icon
		this.topologyService = topologyService;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Creating new Mount");
		final String mountPath = 
			(String)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Enter new mount path:\n",
                "New Mount",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		if (mountPath == null || mountPath.isEmpty()) {
			log.info("User did not enter mount path");
			return;
		}

		try { 
			topologyService.createMount(mountPath);
		} 
		catch (ClientException e){
			final String errorTitle = "Error creating new mount: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + mountPath, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}
