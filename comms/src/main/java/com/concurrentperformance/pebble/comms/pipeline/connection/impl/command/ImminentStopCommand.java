package com.concurrentperformance.pebble.comms.pipeline.connection.impl.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.HessianPipelineWriter;

public class ImminentStopCommand {

	/** This is deliberately a static as we will be creating a lot of these */
	private static final Log log = LogFactory.getLog(ImminentStopCommand.class);

	public static final byte SERIALISATION_IDENTIFIER = -2;


	public ImminentStopCommand(PipelineReader reader) throws IOException {
		//The discriminator has already been read. 
		log.info(this);
	}

	public ImminentStopCommand() {
	}


	public void serialialise(HessianPipelineWriter writer) {
		log.info("Sending [" + this + "]");
		try { 
			writer.writeByte(SERIALISATION_IDENTIFIER);

		} catch (IOException e) {
			// SJL Auto-generated catch block
			log.error("SJL ", e);
		}
	}
	
	@Override
	public String toString() {
		return "ImminentStopCommand";
	}


}
