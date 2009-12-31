package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.DSAParameterSpec;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.PregeneratedDSAParameterSpec;

@RunWith(Enclosed.class)
public class PregeneratedDSAParameterSpecTest {
	public static class A_Pregenerated_DSAParameterSpec {
		private DSAParameterSpec spec = new PregeneratedDSAParameterSpec();
		
		@Test
		public void itHasA1024bitPrimeP() throws Exception {
			assertThat(spec.getP().bitLength()).isEqualTo(1024);
			assertThat(spec.getP().isProbablePrime(1000000)).isTrue();
		}
		
		@Test
		public void itHasA160bitPrimeQ() throws Exception {
			assertThat(spec.getQ().bitLength()).isEqualTo(160);
			assertThat(spec.getQ().isProbablePrime(1000000)).isTrue();
		}
		
		@Test
		public void itCanBeUsedToGenerateDSAKeyPairs() throws Exception {
			final KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
			generator.initialize(spec);
			
			final KeyPair kp = generator.generateKeyPair();
			
			final Signature signer = Signature.getInstance("DSA");
			signer.initSign(kp.getPrivate());
			signer.update("oh hi, just testing my new keypair".getBytes());
			final byte[] signature = signer.sign();
			
			final Signature verifier = Signature.getInstance("DSA");
			verifier.initVerify(kp.getPublic());
			verifier.update("oh hi, just testing my new keypair".getBytes());
			
			assertThat(verifier.verify(signature)).isTrue();
			
			final Signature falsifier = Signature.getInstance("DSA");
			falsifier.initVerify(kp.getPublic());
			falsifier.update("oh hi, just MOO HOO HA HA testing my new keypair".getBytes());
			
			assertThat(falsifier.verify(signature)).isFalse();
		}
	}
}
