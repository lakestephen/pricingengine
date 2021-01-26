package com.concurrentperformance.pebble.msgcommon.graph;


/**
 * Marker interface for Graph Calculation items that produce output. 
 * <br/>
 * NOTE: Do not implement directly Instead implement one of 
 * GraphProducer or GraphConsumerProducer
 * 
 * @author Stephen Lake
 */

public interface GraphCalculationOutput extends GraphCalculation {

	/**
	 * Get the Class<>-type (and implicitly the number) of output
	 * parameter. This needs to be done at a per calculation 
	 * type basis. 
	 * 
	 * @return Class<?>[]
	 */
	public Class<?> getOutputEventDefinition();

	/** 
	 * Sets the handler that the GraphCalculation item can broadcast 
	 * events to. The framework will call this as the calculation 
	 * instance is placed into an active container. 
	 * 
	 * @param handler OutputEventHandler
	 */
	public void setOutputEventHandler(OutputEventHandler handler);

	/**
	 * Sets the value of the output event id to allow the creation 
	 * of new events for distribution into the OutputEventHandler.
	 *  
	 * @param outputEventId String
	 */
	public void setOutputEventId(String outputEventId);
	
	/**
	 * Called by the framework to bootstrap any producer processes.
	 */
	public void start();
}
