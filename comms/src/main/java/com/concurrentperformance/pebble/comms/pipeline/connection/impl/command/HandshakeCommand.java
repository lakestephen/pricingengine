package com.concurrentperformance.pebble.comms.pipeline.connection.impl.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.impl.HessianPipelineWriter;

public class HandshakeCommand {

	private final Log log = LogFactory.getLog(this.getClass());

	public static final byte SERIALISATION_IDENTIFIER = -1;

	final String name;
	final long id;

	public HandshakeCommand(PipelineReader reader) throws IOException {
		//The discriminator has already been read. 
		this.name = reader.readString();
		this.id = reader.readLong();
	}

	public HandshakeCommand(String name, long id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}
	
	public void write(HessianPipelineWriter writer) {
		try { 
			writer.writeByte(SERIALISATION_IDENTIFIER);

			writer.writeString(name);
			writer.writeLong(id);
			
			writer.flush();			
		} catch (IOException e) {
			// SJL Auto-generated catch block
			log.error("SJL ", e);
		}
	}
	
	@Override
	public String toString() {
		return "HandshakeCommand [name=" + name + ", id=" + id + "]";
	}
}
