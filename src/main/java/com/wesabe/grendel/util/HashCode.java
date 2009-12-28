package com.wesabe.grendel.util;

import java.util.Arrays;

/**
 * A simple utility singleton for generating hash codes.
 * 
 * @author coda
 */
public class HashCode {
	private HashCode() {}
	
	public static int calculate(Object... objects) {
		return Arrays.deepHashCode(objects);
	}
}
