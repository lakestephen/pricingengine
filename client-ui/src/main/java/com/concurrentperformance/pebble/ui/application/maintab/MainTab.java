package com.concurrentperformance.pebble.ui.application.maintab;

import java.awt.Component;
import java.util.List;

import javax.swing.JTabbedPane;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class MainTab extends JTabbedPane { //TODO this is a Springed up tab, not a main tab. 

	private static final long serialVersionUID = 1396280700892566266L;

	public void setTabs(List<Component> tabs) {
		for (Component component : tabs) {
			addTab("Topology", null, component, "View and Edit the system topology");
		}
	}
}
