package com.concurrentperformance.pebble.ui.application.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.concurrentperformance.pebble.comms.client.ConnectService;
import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;


public class LoginGlassPane extends JComponent implements ConnectionListener {

	private ConnectService connectService;

	public void start() {
		setLayout(new GridBagLayout());
		JButton hideButton = new JButton("Connect");
		add(hideButton);

		// hide button hides the Glass Pane to show what's under.
		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectService.start();
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {});
		addMouseMotionListener(new MouseMotionAdapter() {});
		addKeyListener(new KeyAdapter() {});
	}
	
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(100,100,100,150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

	public void setConnectService(ConnectService connectService) {
		this.connectService = connectService;
		connectService.register(this);
	}

	@Override
	public void connection_notifyStarted(Connection connection) {
		setVisible(false);
		repaint();
	}

	@Override
	public void connection_notifyStopped(Connection connection, boolean expected) {
		setVisible(true);
		repaint();
	}
}
