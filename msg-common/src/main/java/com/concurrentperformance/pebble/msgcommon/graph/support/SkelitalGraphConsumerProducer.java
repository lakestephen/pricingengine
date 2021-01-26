package com.concurrentperformance.pebble.msgcommon.graph.support;

import java.util.Arrays;

import com.concurrentperformance.pebble.msgcommon.graph.GraphConsumerProducer;
import com.concurrentperformance.pebble.msgcommon.graph.OutputEventHandler;


/**
 * Provide boiler plate implementations of some of the GraphCalculation 
 * interface.
 * 
 * @author Stephen Lake
 */
public abstract class SkelitalGraphConsumerProducer implements GraphConsumerProducer {

	private OutputEventHandler outputEventHandler;
	private String outputEventId;
	
	@Override
	public void start() {
		// do nothing - for override
	}
	
	public OutputEventHandler getOutputEventHandler() { 
		return outputEventHandler;
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
		return "ProdCon[" + getCalculationName() + ", inputs="
				+ Arrays.toString(getInputEventDefinition()) + 
				", output=" + getOutputEventDefinition() + "]";
	}
}
