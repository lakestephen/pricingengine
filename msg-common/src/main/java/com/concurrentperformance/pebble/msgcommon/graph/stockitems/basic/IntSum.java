package com.concurrentperformance.pebble.msgcommon.graph.stockitems.basic;

import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.IntEvent;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculationInputDefinition;
import com.concurrentperformance.pebble.msgcommon.graph.GraphConsumerProducer;
import com.concurrentperformance.pebble.msgcommon.graph.support.SkelitalGraphConsumerProducer;

/**
 * A simple calculation to sum two integers. 
 *
 * @author Stephen Lake
 */
public final class IntSum extends SkelitalGraphConsumerProducer 
	implements GraphConsumerProducer {
	
	private static GraphCalculationInputDefinition[] inputEventTypes = new GraphCalculationInputDefinition[] { 
		new GraphCalculationInputDefinition(int.class,true), 
		new GraphCalculationInputDefinition(int.class,true), 
		};
	
	private static Class<?> outputEventType = int.class;
	
	@Override
	public String getCalculationName() {
		return "Integer Sum";
	}

	@Override
	public String getHelpText() {
		return "Will emit the sum of the two integer inputs";
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
		int left = ((IntEvent)inputs[0]).getValue();
		int right = ((IntEvent)inputs[1]).getValue();
		
		//perform the calculation 
		int result = left + right;
		
//		log.info("IntSum [" + left + "] + [" + right + "] = [" + result + "]");
		
		// handle the output
		Event event = new IntEvent(getOutputEventId(), result);
		getOutputEventHandler().acceptOutputEvent(event);

	}
}