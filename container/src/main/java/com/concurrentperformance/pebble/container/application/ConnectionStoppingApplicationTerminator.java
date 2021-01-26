package com.concurrentperformance.pebble.container.application;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.util.shutdown.TerminatorService;

/**
 * Service that listens to connections, and when one stopps, initiates
 * shutdown of the application.
 *
 * @author Stephen Lake
 */
public class ConnectionStoppingApplicationTerminator implements ConnectionListener {
//TODO could this live in the comms module? Would have to take an array of connections.  

    private TerminatorService terminatorService;

    @Override
    public void connection_notifyStarted(Connection connection) {
    }

    @Override
    public void connection_notifyStopped(Connection connection, boolean expected) {
        terminatorService.terminateNow("Connection [" + connection + "] stopped. Expected [" + expected + "]");
    }

    public void setTerminatorService(TerminatorService terminatorService) {
        this.terminatorService = terminatorService;
    }

    public void setHubConnection(Connection hubConnection) {
        hubConnection.register(this);
    }
}
