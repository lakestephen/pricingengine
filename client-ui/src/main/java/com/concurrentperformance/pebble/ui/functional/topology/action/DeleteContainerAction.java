package com.concurrentperformance.pebble.ui.functional.topology.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.ui.functional.topology.model.ContainerTopologyNode;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class DeleteContainerAction extends AbstractAction {

	private static final long serialVersionUID = 3164250622119454814L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private ClientTopologyServiceC2SApi topologyService;
	private ContainerTopologyNode containerTopologyNode;
	
	public DeleteContainerAction(ClientTopologyServiceC2SApi topologyService, ContainerTopologyNode containerTopologyNode) {
		super("Delete " + containerTopologyNode); //TODO icon
		this.topologyService = topologyService;
		this.containerTopologyNode = containerTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		//Custom button text
		Object[] options = {"Yes, delete " + containerTopologyNode,
		                    "No"};
		int selectedOption = 
			JOptionPane.showOptionDialog(
				(Component) ae.getSource(),
		    "Do you want to delete " + containerTopologyNode + "? " +
		    		"\r\nWARNING: This is not a recoverable action. ",
		    "Delete container " + containerTopologyNode,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);
		
		if  (selectedOption != 0) {
			return;
		}

		log.info("Deleting container [" + containerTopologyNode + "]");

		try { 
			topologyService.deleteContainer(containerTopologyNode.getId());
		} 
		catch (ClientException e){
			final String errorTitle = "Error deleting container: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + containerTopologyNode, e.getReportingLevel().getjOptionPaneValue());
		}
	}
}