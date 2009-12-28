package com.wesabe.grendel.util.tests;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.util.HashCode;

@RunWith(Enclosed.class)
public class HashCodeTest {
	public static class Calculating_A_Hash_Code {
		@Test
		public void itHandlesNullValues() throws Exception {
			assertThat(HashCode.calculate(null, null)).isEqualTo(961);
			
			assertThat(HashCode.calculate(null, null, null)).isEqualTo(29791);
		}
		
		@Test
		public void itHandlesArrays() throws Exception {
			assertThat(HashCode.calculate(null, new int[] { 1, 2, 3 })).isEqualTo(31778);
			
			assertThat(HashCode.calculate(null, new int[] { 1, 2, 4 })).isEqualTo(31779);
		}
		
		@Test
		public void itHandlesObjects() throws Exception {
			assertThat(HashCode.calculate(null, new int[] { 1, 2, 3 }, "blah")).isEqualTo(4011535);
			
			assertThat(HashCode.calculate(null, new int[] { 1, 2, 3 }, "blar")).isEqualTo(4011545);
		}
	}
}
