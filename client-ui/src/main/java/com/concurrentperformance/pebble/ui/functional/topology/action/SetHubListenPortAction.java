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
public class SetHubListenPortAction extends AbstractAction {

	private static final long serialVersionUID = 1319375491633450562L;

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final HubTopologyNode hubTopologyNode;
	private final ClientTopologyServiceC2SApi topologyService;
	
	public SetHubListenPortAction(ClientTopologyServiceC2SApi topologyService, HubTopologyNode hubTopologyNode) {
		super("Set Hub " + " listen port"); //TODO icon
		this.topologyService = topologyService;
		this.hubTopologyNode = hubTopologyNode;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("Set hub listen port");
		final String response = 
			(String)JOptionPane.showInputDialog(
				(Component) ae.getSource(),
                "Enter hub listen port:\n",
                "Listen Port",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		if (response == null || response.isEmpty()) {
			log.info("User did not enter listen port");
			return;
		}

		int hubListenPort = 0;
		try {
			hubListenPort = Integer.parseInt(response);
		}
		catch (NumberFormatException e) {
			final String errorTitle = "Error setting listen port: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle, JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try { 
			long hubId = hubTopologyNode.getId();
			topologyService.setHubListenPort(hubId, hubListenPort);
		} 
		catch (ClientException e){
			final String errorTitle = "Error setting listen port: ";
			log.error(errorTitle +  e.getMessage());
			JOptionPane.showMessageDialog((Component) ae.getSource(),
				    e.getMessage(), errorTitle, e.getReportingLevel().getjOptionPaneValue());
		}
	}

}
