package com.concurrentperformance.pebble.ui.functional.graph.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.ui.functional.graph.model.GraphNode;

public class DehostMountAction extends AbstractAction {

	private final Log log = LogFactory.getLog(this.getClass());

	private final GraphNode node;
	private final ClientTopologyServiceC2S topologyService;
	
	public DehostMountAction(GraphNode node, ClientTopologyServiceC2S topologyService) {
		super("Dehost Mount"); //TODO icon
		this.node = node;
		this.topologyService = topologyService;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Dehost Mount");
		
		//Custom button text
		Object[] options = {"Yes, dehost " + node,
		                    "No"};
		int selectedOption = 
			JOptionPane.showOptionDialog(
				(Component) ae.getSource(),
		    "Do you want to dehost " + node + "? " ,
		    "Delete mount " + node,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);
		
		if  (selectedOption != 0) {
			return;
		}

		log.info("Dehosting mount [" + node + "]");

		try { 
			topologyService.dehostMount(node.getMountId());
		} 
		catch (ClientException e){
			final String errorTitle = "Error dehosting mount: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + node, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}