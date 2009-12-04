package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.PregeneratedDHParameterSpec;

@RunWith(Enclosed.class)
public class PregeneratedDHParameterSpecTest {
	public static class A_Pregenerated_DHParameterSpec {
		private DHParameterSpec spec = new PregeneratedDHParameterSpec();
		
		@Test
		public void itCanBeUsedToGenerateElGamalKeyPairs() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			
			final KeyPairGenerator generator = KeyPairGenerator.getInstance("ElGamal");
			generator.initialize(spec);
			final KeyPair kp = generator.generateKeyPair();
			
			final Cipher encrypter = Cipher.getInstance("ElGamal/None/OAEPWITHSHA512ANDMGF1PADDING");
			encrypter.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			final byte[] encrypted = encrypter.doFinal("hello everybody!".getBytes());
			
			final Cipher decrypter = Cipher.getInstance("ElGamal/None/OAEPWITHSHA512ANDMGF1PADDING");
			decrypter.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			final byte[] decrypted = decrypter.doFinal(encrypted);
			
			assertThat(new String(decrypted)).isEqualTo("hello everybody!");
		}
	}
}
