package com.concurrentperformance.pebble.msgcommon.graph.support;

import com.concurrentperformance.pebble.msgcommon.graph.GraphProducer;
import com.concurrentperformance.pebble.msgcommon.graph.OutputEventHandler;


/**
 * Provide boiler plate implementations of some of the GraphCalculation 
 * interface.
 * 
 * @author Stephen Lake
 */
public abstract class SkelitalGraphProducer implements GraphProducer { // TODO is this different enough from SkelitalGraphConsumerProducer to warrent a sep class?

	private OutputEventHandler outputEventHandler;
	private String outputEventId;
	
	public OutputEventHandler getOutputEventHandler() { 
		return outputEventHandler;
	}

	@Override
	public void start() {
		// do nothing - for override
	}

	@Override
	public void setOutputEventHandler(OutputEventHandler outputEventHandler) { //Should this be in constructor??
		this.outputEventHandler = outputEventHandler;
	}

	public String getOutputEventId() { 
		return outputEventId;
	}

	@Override
	public void setOutputEventId(String outputEventId) { //Should this be in constructor??
		this.outputEventId = outputEventId;
	}
	
	@Override
	public String toString() {
		return "Prod[" + getCalculationName() + 
				", output=" + getOutputEventDefinition() + "]";
	}
}
