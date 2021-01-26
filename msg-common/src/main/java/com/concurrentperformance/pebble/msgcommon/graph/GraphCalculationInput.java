package com.concurrentperformance.pebble.msgcommon.graph;

import com.concurrentperformance.pebble.msgcommon.event.Event;


/**
 * Marker interface for Graph Calculation items that take input. 
 * <br/>
 * NOTE: Do not implement directly Instead implement one of 
 * GraphConsumer or GraphConsumerProducer
 * 
 * @author Stephen Lake
 */

public interface GraphCalculationInput extends GraphCalculation {

	/**
	 * Get the array of  Class<>-type (and implicitly the number) 
	 * of input parameters. This needs to be done at a per 
	 * calculation type basis. 
	 * 
	 * @return Class<?>[]
	 */
	public GraphCalculationInputDefinition[] getInputEventDefinition();

	/**
	 * Allow the framework to pass in the inputs for the Calculation to 
	 * 'do its stuff' The calculation should pass its result to the 
	 * OutputEventHandler handler
	 * 
	 * @param inputs Event[] containing the input parameters, as per the 
	 * defined input event types. 
	 */
	public void calculate(Event[] inputs);
}
