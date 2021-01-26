package com.concurrentperformance.pebble.comms.pipeline.server.socketlistener;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.common.server.socketlistener.impl.SkelitalServerConnectionFactory;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineTranslator;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.DefaultPipelineConnection;
import com.concurrentperformance.pebble.comms.server.AsynchHandOffSocketHandler;

public class PipelineServerConnectionFactory<T> extends SkelitalServerConnectionFactory 
		implements AsynchHandOffSocketHandler, ConnectionListener {  

	private final Log log = LogFactory.getLog(this.getClass());

	private PipelineTranslator<T> translator;
	private PipelineReceiver<T> receiver;
	
	protected Connection buildConnection(Socket client) {
		DefaultPipelineConnection<T> connection = new DefaultPipelineConnection<T>();
		connection.register(this);

		connection.setWeAreA(weAreA);		
		connection.setWeAreAId(weAreAId);		
		connection.setReceiver(receiver);
		connection.setTranslator(translator);
		connection.setSocket(client);
		
		return connection;
	}

	@Override
	public String getServerThreadName() {
		return "PipelineListener:" + weAreListeningFor + ":" + port;
	}
	
	public void setReciever(PipelineReceiver<T> receiver) {
		this.receiver = receiver;
	}
	
	public void setTranslator(PipelineTranslator<T> translator) {
		this.translator = translator;
	}
}
