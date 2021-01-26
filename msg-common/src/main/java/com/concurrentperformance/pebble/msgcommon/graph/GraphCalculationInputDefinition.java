package com.concurrentperformance.pebble.msgcommon.graph;

public class GraphCalculationInputDefinition {

	private final Class<?> type;
	private final boolean mandatory;
	
	public GraphCalculationInputDefinition(Class<?> type, boolean mandatory) {
		this.type = type;
		this.mandatory = mandatory;
	}

	public Class<?> getType() {
		return type; 
	}

	public boolean isMandatory() {
		return mandatory;
	}
	
	@Override
	public String toString() {
		return "GraphCalculationInputDefinition [type=" + type.getSimpleName() + ", mandatory="
				+ mandatory + "]";
	}
}
