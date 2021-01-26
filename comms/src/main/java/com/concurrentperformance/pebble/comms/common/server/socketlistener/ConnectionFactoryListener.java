package com.concurrentperformance.pebble.comms.common.server.socketlistener;

import com.concurrentperformance.pebble.comms.common.connection.Connection;

public interface ConnectionFactoryListener {

	void connectionFactory_notifyNewConnection(Connection connection);

	void connectionFactory_notifyStoppedConnection(Connection connection, boolean expected);
}