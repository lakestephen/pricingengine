package com.concurrentperformance.pebble.ui.common.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public abstract class SkelitalTreeModel<T extends TreeNodeSupport<T>> implements TreeModel {

	private final EventListenerList listenerList = new EventListenerList();
	
	protected void fireTreeNodesInserted(T parent, T child) {
		if (parent == null || child == null) { 
			return;
		}
		
		Object[] path = getPathToRoot(parent);
        Object[] children = new Object[1];
        children[0] = child;
        int[] childIndexs = new int[1];
        childIndexs[0] = parent.getChildIndex(child);
		TreeModelEvent e = new TreeModelEvent(this, path, childIndexs, children);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	protected void fireTreeNodesRemoved(T parent, T child, int index) {
		Object[] path = getPathToRoot(parent);
        Object[] children = new Object[1];
        children[0] = child;
        int[] childIndexs = new int[1];
        childIndexs[0] = index;


		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = new TreeModelEvent(this, path, childIndexs, children);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	protected void fireTreeNodeChanged(T node) {
		Object[] path = getPathToRoot(node);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = new TreeModelEvent(this, path);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}
	
	protected void fireTreeStructureChanged(T node) {
		Object[] path = getPathToRoot(node);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = new TreeModelEvent(this, path);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	/**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     */
	private Object[] getPathToRoot(T node) {
		List<T> path = new ArrayList<T> (); 
		
		while (node != null) {
			path.add(0, node);
			node = node.getParent();
		}
		return path.toArray();
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);		
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);		
	}
}
