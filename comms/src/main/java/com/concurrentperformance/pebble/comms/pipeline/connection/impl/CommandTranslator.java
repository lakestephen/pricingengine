package com.concurrentperformance.pebble.comms.pipeline.connection.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineTranslator;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.command.HandshakeCommand;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.command.ImminentStopCommand;


public class CommandTranslator implements PipelineTranslator<Boolean> {

	private final Log log = LogFactory.getLog(this.getClass());

	private PipelineConnection connection;
	
	CommandTranslator(PipelineConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Boolean readNext(byte discriminator, PipelineReader reader) throws IOException {
		switch (discriminator) {
			case HandshakeCommand.SERIALISATION_IDENTIFIER:
				HandshakeCommand handshakeCommand = new HandshakeCommand(reader);
				log.info("Handshake command received [" + handshakeCommand + "]");
				connection.handshakeRecieved(handshakeCommand.getName(), handshakeCommand.getId());
				break;
			case ImminentStopCommand.SERIALISATION_IDENTIFIER:
				ImminentStopCommand imminentStopCommand = new ImminentStopCommand(reader);
				log.info("Imminent stop command recieved [" + imminentStopCommand + "]");
				connection.imminentStop();
				break;
			default: 
				throw new IllegalStateException("Unknown Event type during deserialise [" + discriminator + "]");
		}
		return true;
	}
}
