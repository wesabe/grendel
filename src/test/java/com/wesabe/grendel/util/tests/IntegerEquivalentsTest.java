package com.wesabe.grendel.util.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.ImmutableList;
import com.wesabe.grendel.util.IntegerEquivalent;
import com.wesabe.grendel.util.IntegerEquivalents;

@RunWith(Enclosed.class)
public class IntegerEquivalentsTest {
	@Ignore
	public static enum Letter implements IntegerEquivalent {
		A(1), B(2), C(4);
		
		private final int value;
		
		private Letter(int value) {
			this.value = value;
		}
		
		@Override
		public int toInteger() {
			return value;
		}
	}
	
	public static class Converting_A_Collection_Of_Integer_Equivalents_To_An_Array_Of_Ints {
		@Test
		public void itReturnsAnArrayOfInts() throws Exception {
			assertThat(IntegerEquivalents.toIntArray(ImmutableList.of(Letter.A, Letter.C))).isEqualTo(new int[] { 1, 4 });
		}
	}
	
	public static class Converting_A_Set_Of_Integer_Equivalents_To_A_Bitmask {
		@Test
		public void itReturnsAnInt() throws Exception {
			assertThat(IntegerEquivalents.toBitmask(ImmutableSet.of(Letter.B, Letter.C))).isEqualTo(6);
		}
	}
	
	public static class Converting_An_Integer_To_An_Integer_Equivalent {
		@Test
		public void itReturnsAnInstanceOfTheEnumClass() throws Exception {
			assertThat(IntegerEquivalents.fromInt(Letter.class, 2)).isEqualTo(Letter.B);
		}
		
		@Test
		public void itThrowsAnIllegalArgumentExceptionIfTheValueHasNoCorrespondingInstance() throws Exception {
			try {
				IntegerEquivalents.fromInt(Letter.class, 18);
				fail("should have thrown an IllegalArgumentException but didn't");
			} catch (IllegalArgumentException e) {
				assertThat(e.getMessage()).isEqualTo("No enum constant of class com.wesabe.grendel.util.tests.IntegerEquivalentsTest$Letter exists with value 18");
			}
		}
	}
	
	public static class Converting_An_Array_Of_Ints_To_A_Collection_Of_Integer_Equivalents {
		@Test
		public void itReturnsASetOfIntegerEquivalents() throws Exception {
			assertThat(IntegerEquivalents.fromIntArray(Letter.class, new int[] { 1, 4 })).isEqualTo(ImmutableList.of(Letter.A, Letter.C));
		}
	}
	
	public static class Converting_A_Bitmask_To_A_Set_Of_Integer_Equivalents {
		@Test
		public void itReturnsASetOfIntegerEquivalents() throws Exception {
			assertThat(IntegerEquivalents.fromBitmask(Letter.class, 6)).isEqualTo(ImmutableSet.of(Letter.B, Letter.C));
		}
	}
}
