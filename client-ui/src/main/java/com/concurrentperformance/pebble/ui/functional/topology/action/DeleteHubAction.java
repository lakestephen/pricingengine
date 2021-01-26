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
public class DeleteHubAction extends AbstractAction {

	private static final long serialVersionUID = 1056897154788194417L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private ClientTopologyServiceC2SApi topologyService;
	private HubTopologyNode hubTopologyNode;
	
	public DeleteHubAction(ClientTopologyServiceC2SApi topologyService, HubTopologyNode hubTopologyNode) {
		super("Delete " + hubTopologyNode); //TODO icon
		this.topologyService = topologyService;
		this.hubTopologyNode = hubTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		//Custom button text
		Object[] options = {"Yes, delete " + hubTopologyNode,
		                    "No"};
		int selectedOption = 
			JOptionPane.showOptionDialog(
				(Component) ae.getSource(),
		    "Do you want to delete " + hubTopologyNode + "? " +
		    		"\r\nWARNING: This is not a recoverable action. ",
		    "Delete hub " + hubTopologyNode,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);
		
		if  (selectedOption != 0) {
			return;
		}

		log.info("Deleting hub [" + hubTopologyNode + "]");

		try { 
			topologyService.deleteHub(hubTopologyNode.getId());
		} 
		catch (ClientException e){
			final String errorTitle = "Error deleting hub: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + hubTopologyNode, e.getReportingLevel().getjOptionPaneValue());
		}
	}
}