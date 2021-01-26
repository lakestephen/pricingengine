package com.concurrentperformance.pebble.msgcommon.graph;


/**
 * Marker interface for the base Graph Calculation items. 
 * <br/>
 * NOTE: Do not implement directly Instead implement one of 
 * GraphProducer, GraphConsumer or GraphConsumerProducer
 * 
 * @author Stephen Lake
 */
public interface GraphCalculation {

	/**
	 * Get the name that the calculation will be identified by 
	 * in the UI. This is a UI pseudonym for the fully qualified 
	 * class name. 	It is acceptable for this to change during its 
	 * development lifecycle.  
	 * 
	 * @return
	 */
	public String getCalculationName();
	
	/**
	 * Get the help text that the UI will display for this 
	 * calculation. It is acceptable for this to change during its 
	 * development lifecycle.  
	 * 
	 * @return
	 */
	public String getHelpText();

}
