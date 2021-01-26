package com.concurrentperformance.pebble.controller.functional.topology.service;

/**
 * The State that the topology can be in. 
 * See individual enum's comments for the state transitions. </br>
 * </br>
 * // TODO Check all state transitions against the actual code 
 * // TODO this needs to be a proper state engine with typed transitions.
 * 
 * @author Stephen Lake
 */
public enum TopologyMonitorState { 
	
	/** 
	 * Initial state when the topology is first created. </br>
	 * <li>Transitions to AWAITING_REGISTRATION when the new 
	 * topology node construction has been requested.</li>
	 */
	AWAITING_CONSTRUCTION, 
	
	/**
	 * Topology has been created. </br>
	 * <li>Transitions to RUNNING  when the topology registers.</li>
	 * <li>Transitions to DECLARED_DEAD if the topology does not register in time</li>
	 */
	AWAITING_REGISTRATION, 
	
	/**
	 * Topology is running normally.</br>
	 * <li>Transitions to MISSED_HEARTBEAT if no heart beat arrives from the topology
	 * in the set timeout period.</li>
	 * <li>Transitions to AWAITING_DELETION if the topology is removed from the system  //TODO check this
	 * </li> 
	 */
	RUNNING,
	
	/**
	 * Topology has missed the first heart beat timeout. </br>
	 * <li>Transitions to RUNNING if heart beat comes in within the second chance 
	 * heart beat timeout period.</li>
	 * <li>Transitions to DECLARED_DEAD if heart beat does not come in within the 
	 * second chance heart beat timeout period</li>
	 */
	MISSED_HEARTBEAT,
	
	/**
	 * Topology (instance) is not recovering, nor allowed to recover. </br>
	 * <li>Transition to AWAITING_CONSTRUCTION to retry to build this topology</li>
	 */
	DECLARED_DEAD,
	
	/**
	 * Topology removal has been requested through the TopologyService </br>
	 * <li>Transition to DELETED when component shutdown has been performed</li>
	 */
	AWAITING_DELETION,
	
	/**
	 * End game - never to be revived. 
	 * End State 
	 */
	DELETED
}

