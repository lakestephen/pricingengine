package com.concurrentperformance.pebble.msgcommon.event;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;

/**
 * Event sent by the container to the hub to request listening to an Event. 
 * 
 * @author Stephen Lake
 */
@SuppressWarnings("serial")
public class RegisterInterestInEvent implements ControlEvent {

	private static final Log log = LogFactory.getLog(Event.class);

	public static final byte SERIALISATION_IDENTIFIER = 91;
	
	private final String eventId;
	private final long containerId;
	public RegisterInterestInEvent(String eventId, long  containerId) {
		this.eventId = eventId;
		this.containerId = containerId;
	}

	public RegisterInterestInEvent(PipelineReader reader) throws IOException {
		this.eventId = reader.readString();
		this.containerId = reader.readLong();
	}

	@Override
	public String getId() {
		return eventId;
	}
	
	public long getContainerId() {
		return containerId;
	}

	@Override
	public void write(PipelineWriter writer) {
		try {
			writer.writeByte(SERIALISATION_IDENTIFIER);

			writer.writeString(eventId);
			writer.writeLong(containerId);

			writer.flush();
		} catch (IOException e) {
			// SJL Auto-generated catch block
			log.error("SJL ", e);
		}
	}

	@Override
	public String toString() {
		return "[RegisterInterestInEvent [" + eventId + " sourced from " + containerId + "]";
	}
}
