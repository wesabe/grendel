package com.wesabe.grendel.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Utility methods for dealing with untyped {@link Iterator} instances.
 * 
 * @author coda
 */
public final class Iterators {
	private Iterators() {}
	
	/**
	 * Returns the items available via {@code iterator} as a {@link List}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Iterator<?> iterator) {
		return ImmutableList.copyOf((Iterator<T>) iterator);
	}
	
	/**
	 * Returns the items available via {@code iterator} as a {@link Set}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> toSet(Iterator<?> iterator) {
		return ImmutableSet.copyOf((Iterator<T>) iterator);
	}
}
