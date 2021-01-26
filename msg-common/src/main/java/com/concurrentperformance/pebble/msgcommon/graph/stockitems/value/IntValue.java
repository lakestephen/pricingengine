package com.concurrentperformance.pebble.msgcommon.graph.stockitems.value;

import com.concurrentperformance.pebble.msgcommon.graph.GraphProducer;
import com.concurrentperformance.pebble.msgcommon.graph.support.SkelitalGraphProducer;

public class IntValue extends SkelitalGraphProducer 
	implements GraphProducer {
	
	private static Class<?> outputEventType = int.class;
	
	@Override
	public String getCalculationName() {
		return "Int value";
	}

	@Override
	public String getHelpText() {
		return "static int value that can be set and persisted";
	}

	@Override
	public Class<?> getOutputEventDefinition() {
		return outputEventType;
	}
}
