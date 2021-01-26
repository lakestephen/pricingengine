package com.concurrentperformance.pebble.comms.pipeline.connection.impl;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.impl.SkelitalConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineTranslator;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.command.HandshakeCommand;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.command.ImminentStopCommand;

public class DefaultPipelineConnection<T> extends SkelitalConnection 
		implements PipelineConnection {

	private final Log log = LogFactory.getLog(this.getClass());

	private HessianPipelineWriter writer;
	private HessianPipelineReader<T> reader;

	
	public DefaultPipelineConnection() {
		reader = new HessianPipelineReader<T>(this);
		writer = new HessianPipelineWriter(this);
	}
	
	@Override
	protected void startComponents(Socket socket, String threadBaseName) throws IOException {
		reader.start(socket, threadBaseName);
		writer.start(socket);
	}
	
	@Override
	protected void performHandshake() {
		HandshakeCommand handshake = new HandshakeCommand(getWeAreA(), getWeAreAId());
		log.info("Sending Handshake [" + handshake + "]");
		handshake.write(writer); //TODO abstract all write commands behind interface.  
	}
	
	@Override
	protected void doUpdateThreadName(String threadBaseName) {
		reader.setThreadName(threadBaseName);	
		writer.setThreadName(threadBaseName);	
	}	
	
	@Override
	protected void stopComponents() {
		reader.stop();
		writer.stop();
	}
	
	@Override
	protected void signalImminentStopToRemoteSocket() {
		log.debug("Sending stop from [" + this + "]");
		ImminentStopCommand stop = new ImminentStopCommand();
		stop.serialialise(writer); //TODO abstract all commands behind interface.  
	}

	public final void setReceiver(PipelineReceiver<T> receiver) {
		reader.setReceiver(receiver);
	}
	
	public final void setTranslator(PipelineTranslator<T> translator) {
		reader.setTranslator(translator);
	}
	
	@Override
	public PipelineWriter getWriter() {
		return writer;
	}
}