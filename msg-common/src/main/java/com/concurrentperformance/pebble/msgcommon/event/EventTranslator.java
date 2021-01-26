package com.concurrentperformance.pebble.msgcommon.event;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineTranslator;

import java.io.IOException;

public class EventTranslator implements PipelineTranslator<Event> {

	@Override
	public Event readNext(byte discriminator, PipelineReader reader) throws IOException {
		switch (discriminator) {
			case (IntEvent.SERIALISATION_IDENTIFIER):
				return new IntEvent(reader);
			case (AdvertiseProducerAvailability.SERIALISATION_IDENTIFIER):
				return new AdvertiseProducerAvailability(reader);
			case (RegisterInterestInEvent.SERIALISATION_IDENTIFIER):
				return new RegisterInterestInEvent(reader);
			case (StartSendingEvent.SERIALISATION_IDENTIFIER):
				return new StartSendingEvent(reader);
			default: 
				return null;
		}
	}
}