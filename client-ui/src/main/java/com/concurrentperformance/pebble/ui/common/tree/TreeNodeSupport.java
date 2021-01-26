package com.concurrentperformance.pebble.ui.common.tree;

public interface TreeNodeSupport<T extends TreeNodeSupport<T>> { //TDOO make a lot more of this common

	/**
	 * Get the parent node. This can return null for the root
	 */
	T getParent();
	
	int getChildIndex(T child); 
	
	String getName();	
	
	void removeAllChildren();
}
