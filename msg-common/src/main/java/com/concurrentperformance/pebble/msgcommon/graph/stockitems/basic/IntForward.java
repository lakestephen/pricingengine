package com.concurrentperformance.pebble.msgcommon.graph.stockitems.basic;

import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.IntEvent;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculationInputDefinition;
import com.concurrentperformance.pebble.msgcommon.graph.GraphConsumerProducer;
import com.concurrentperformance.pebble.msgcommon.graph.support.SkelitalGraphConsumerProducer;

/**
 * A class that simply mirrors the value it receives. 
 *
 * @author Stephen Lake
 */
public final class IntForward extends SkelitalGraphConsumerProducer 
	implements GraphConsumerProducer {
	
	private static GraphCalculationInputDefinition[] inputEventTypes = new GraphCalculationInputDefinition[] { 
		new GraphCalculationInputDefinition(int.class,true), 
		};
	
	private static Class<?> outputEventType = int.class;
	
	@Override
	public String getCalculationName() {
		return "Forward";
	}

	@Override
	public String getHelpText() {
		return "Will emit the the value it receives";
	} 

	@Override
	public GraphCalculationInputDefinition[] getInputEventDefinition() {
		return inputEventTypes;
	}

	@Override
	public Class<?> getOutputEventDefinition() {
		return outputEventType;
	}

	@Override
	public void calculate(Event[] inputs) {
		// get the inputs
		int value = ((IntEvent)inputs[0]).getValue();
		
		// handle the output
		Event event = new IntEvent(getOutputEventId(), value);
		getOutputEventHandler().acceptOutputEvent(event);

	}
}