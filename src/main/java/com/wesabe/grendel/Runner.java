package com.wesabe.grendel;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.codahale.shore.Shore;

/**
 * The main Grendel class.
 * 
 * @author coda
 */
public class Runner {
	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Shore.run(new Configuration(), args);
	}
}
