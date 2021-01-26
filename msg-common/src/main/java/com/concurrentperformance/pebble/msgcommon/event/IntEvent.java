package com.concurrentperformance.pebble.msgcommon.event;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;

public class IntEvent implements Event {

	private static final Log log = LogFactory.getLog(Event.class);

	private static final long serialVersionUID = -8194390717630426708L;
	
	public static final byte SERIALISATION_IDENTIFIER = 1;
	
	private final String eventId;
	private final int value;
	
	public IntEvent(String eventId, int value) {
		this.eventId = eventId;
		this.value = value;
	}

	public IntEvent(PipelineReader reader) throws IOException {
		eventId = reader.readString();
		value = reader.readInt();
	}

	@Override
	public String getId() {
		return eventId;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public void write(PipelineWriter writer) {
		try {
			writer.writeByte(SERIALISATION_IDENTIFIER);

			writer.writeString(eventId);
			writer.writeInt(value);

			writer.flush();
		} catch (IOException e) {
			// SJL Auto-generated catch block
			log.error("SJL ", e);
		}
	}

	@Override
	public String toString() {
		return "[Event:" + eventId + " = " + value + "]";
	}
}
