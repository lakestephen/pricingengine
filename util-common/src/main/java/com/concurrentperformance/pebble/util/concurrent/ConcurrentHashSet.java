package com.concurrentperformance.pebble.util.concurrent;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E> {

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

	private final ConcurrentHashMap<E, Object> map ;

	// TODO add other constructors that map to ConcurrentHashMap constructors
	public ConcurrentHashSet() {
		map = new ConcurrentHashMap<E, Object>();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public boolean add(E e) {
		return map.put(e, PRESENT)==null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o)==PRESENT;
	}

	@Override
	public void clear() {
		map.clear();
	}

}
