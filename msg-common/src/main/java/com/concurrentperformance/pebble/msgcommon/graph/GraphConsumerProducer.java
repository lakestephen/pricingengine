package com.concurrentperformance.pebble.msgcommon.graph;

/**
 * Interface for calculation implementations that both consume 
 * messages and produce messages.  
 * 
 * @author Stephen Lake
 */
public interface GraphConsumerProducer extends GraphCalculation, GraphCalculationInput, GraphCalculationOutput {

}
