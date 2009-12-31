package com.wesabe.grendel;

import com.codahale.shore.Shore;

/**
 * The main Grendel class.
 * 
 * @author coda
 */
public class Runner {
	public static void main(String[] args) {
		Shore.run(new Configuration(), args);
	}
}
