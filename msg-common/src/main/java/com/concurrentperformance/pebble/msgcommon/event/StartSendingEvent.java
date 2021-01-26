package com.concurrentperformance.pebble.msgcommon.event;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;

@SuppressWarnings("serial")
public class StartSendingEvent implements ControlEvent {

	private static final Log log = LogFactory.getLog(Event.class);

	private final String eventId;
	
	public static final byte SERIALISATION_IDENTIFIER = 92;

	public StartSendingEvent(String eventId) {
		this.eventId = eventId;
	}

	public StartSendingEvent(PipelineReader reader) throws IOException {
		this.eventId = reader.readString();
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public void write(PipelineWriter writer) {
		try {
			writer.writeByte(SERIALISATION_IDENTIFIER);

			writer.writeString(eventId);

			writer.flush();
		} catch (IOException e) {
			// SJL Auto-generated catch block
			log.error("SJL ", e);
		}
		
	}

	@Override
	public String toString() {
		return "StartSendingEvent [eventId=" + eventId + "]";
	}
}
