package com.wesabe.grendel.util.tests;

import static org.fest.assertions.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.util.Iterators;

@RunWith(Enclosed.class)
public class IteratorsTest {
	public static class Converting_An_Iterator_Into_A_List {
		@Test
		public void itReturnsAList() throws Exception {
			final List<String> numbers = ImmutableList.of("one", "two", "three");
			final List<String> otherNumbers = Iterators.toList(numbers.iterator());
			assertThat(otherNumbers).isEqualTo(numbers);
		}
	}
	
	public static class Converting_An_Iterator_Into_A_Set {
		@Test
		public void itReturnsAList() throws Exception {
			final Set<String> numbers = ImmutableSet.of("one", "two", "three");
			final Set<String> otherNumbers = Iterators.toSet(numbers.iterator());
			assertThat(otherNumbers).isEqualTo(numbers);
		}
	}
}
