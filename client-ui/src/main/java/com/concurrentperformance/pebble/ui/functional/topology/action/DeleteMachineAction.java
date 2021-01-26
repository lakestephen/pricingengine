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
public class DeleteMachineAction extends AbstractAction {

	private static final long serialVersionUID = -1206694670872828302L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private ClientTopologyServiceC2SApi topologyService;
	private MachineTopologyNode machineTopologyNode;
	
	public DeleteMachineAction(ClientTopologyServiceC2SApi topologyService, MachineTopologyNode machineTopologyNode) {
		super("Delete " + machineTopologyNode); //TODO icon
		this.topologyService = topologyService;
		this.machineTopologyNode = machineTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		//Custom button text
		Object[] options = {"Yes, delete " + machineTopologyNode,
		                    "No"};
		int selectedOption = 
			JOptionPane.showOptionDialog(
				(Component) ae.getSource(),
		    "Do you want to delete " + machineTopologyNode + "? " +
		    		"\r\nWARNING: This is not a recoverable action. ",
		    "Delete machine " + machineTopologyNode,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);
		
		if  (selectedOption != 0) {
			return;
		}

		log.info("Deleting machine [" + machineTopologyNode + "]");

		try { 
			topologyService.deleteMachine(machineTopologyNode.getId());
		} 
		catch (ClientException e){
			final String errorTitle = "Error deleting machine: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle + machineTopologyNode, e.getReportingLevel().getjOptionPaneValue());
		}
	}
}