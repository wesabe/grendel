package com.wesabe.grendel.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.ImmutableList;

/**
 * Utility methods for dealing with {@link IntegerEquivalent}s.
 * 
 * @author coda
 */
public final class IntegerEquivalents {
	private IntegerEquivalents() {}
	
	/**
	 * Returns the collection of {@code integerEquivs} as an array of {@code int}s.
	 */
	public static int[] toIntArray(Collection<? extends IntegerEquivalent> integerEquivs) {
		final int[] values = new int[integerEquivs.size()];
		int i = 0;
		for (IntegerEquivalent integerEquiv : integerEquivs) {
			values[i] = integerEquiv.toInteger();
			i++;
		}
		return values;
	}
	
	/**
	 * Returns the set of {@code integerEquivs} as a bitmask {@code int}.
	 */
	public static int toBitmask(Set<? extends IntegerEquivalent> integerEquivs) {
		int value = 0;
		for (IntegerEquivalent integerEquiv : integerEquivs) {
			value |= integerEquiv.toInteger();
		}
		return value;
	}
	
	/**
	 * Returns the instance of {@code enumType} which is equivalent to
	 * {@code value}.
	 * 
	 * @throws IllegalArgumentException if {@code value} has no equivalent
	 */
	public static <T extends IntegerEquivalent> T fromInt(Class<T> enumType, int value) throws IllegalArgumentException {
		for (T constant : enumType.getEnumConstants()) {
			if (constant.toInteger() == value) {
				return constant;
			}
		}
		throw new IllegalArgumentException("No enum constant of " + enumType + " exists with value " + value);
	}
	
	/**
	 * Returns the list of the instances of {@code enumType} which are equivalent
	 * to {@code values}.
	 * 
	 * @throws IllegalArgumentException if {@code value} has no equivalent
	 */
	public static <T extends IntegerEquivalent> List<T> fromIntArray(Class<T> enumType, int[] values) throws IllegalArgumentException {
		final ImmutableList.Builder<T> builder = ImmutableList.builder();
		for (int value : values) {
			builder.add(fromInt(enumType, value));
		}
		return builder.build();
	}
	
	/**
	 * Returns the {@code bitMask} as a set of {@link IntegerEquivalent}s.
	 */
	public static <T extends IntegerEquivalent> Set<T> fromBitmask(Class<T> enumType, int bitMask) throws IllegalArgumentException {
		final ImmutableSet.Builder<T> builder = ImmutableSet.builder();
		for (T constant : enumType.getEnumConstants()) {
			if ((bitMask & constant.toInteger()) != 0) {
				builder.add(constant);
			}
		}
		return builder.build();
	}
}
