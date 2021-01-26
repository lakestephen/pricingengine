package com.concurrentperformance.pebble.msgcommon.event;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;

import java.io.Serializable;


/**
 * TODO: Should this be serialisable?
 * TODO: Should the top of this class hierarchy be something like Piepable as it goes down a pipeline?
 *
 * @author Stephen Lake
 *
 */
public interface Event extends Serializable {
	
	public String getId(); //TODO use an id that is a long (it will be faster to match, and faster on database.), but in the form of and EventId that also has a String - for the UI
	public void write(PipelineWriter writer);
	
}
