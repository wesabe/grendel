package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;

import java.security.spec.RSAKeyGenParameterSpec;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.AsymmetricAlgorithm;
import com.wesabe.grendel.openpgp.PregeneratedDHParameterSpec;
import com.wesabe.grendel.openpgp.PregeneratedDSAParameterSpec;

@RunWith(Enclosed.class)
public class AsymmetricAlgorithmTest {
	public static class RSA {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA.toInteger()).isEqualTo(PublicKeyAlgorithmTags.RSA_GENERAL);
		}

		@Test
		public void itHasADefaultSizeOf2048Bits() throws Exception {
			final RSAKeyGenParameterSpec spec = (RSAKeyGenParameterSpec) AsymmetricAlgorithm.RSA.getAlgorithmParameterSpec();
			assertThat(spec.getKeysize()).isEqualTo(2048);
		}

		@Test
		public void itIsNamedRSA() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA.getName()).isEqualTo("RSA");
			assertThat(AsymmetricAlgorithm.RSA.toString()).isEqualTo("RSA");
		}
	}

	public static class ElGamal {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.ELGAMAL.toInteger()).isEqualTo(PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT);
		}

		@Test
		public void itUsesPregeneratedSpecs() throws Exception {
			assertThat(AsymmetricAlgorithm.ELGAMAL.getAlgorithmParameterSpec()).isInstanceOf(PregeneratedDHParameterSpec.class);
		}

		@Test
		public void itIsNamedElGamal() throws Exception {
			assertThat(AsymmetricAlgorithm.ELGAMAL.getName()).isEqualTo("ElGamal");
		}
	}

	public static class DSA {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.DSA.toInteger()).isEqualTo(PublicKeyAlgorithmTags.DSA);
		}

		@Test
		public void itUsesPregeneratedSpecs() throws Exception {
			assertThat(AsymmetricAlgorithm.DSA.getAlgorithmParameterSpec()).isInstanceOf(PregeneratedDSAParameterSpec.class);
		}

		@Test
		public void itIsNamedDSA() throws Exception {
			assertThat(AsymmetricAlgorithm.DSA.getName()).isEqualTo("DSA");
		}
	}

	@SuppressWarnings("deprecation")
	public static class RSA_E {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA_E.toInteger()).isEqualTo(PublicKeyAlgorithmTags.RSA_ENCRYPT);
		}

		@Test
		public void itProhibitsKeyGeneration() throws Exception {
			try {
				AsymmetricAlgorithm.RSA_E.getAlgorithmParameterSpec();
				fail("should have thrown an UnsupportedOperationException but didn't");
			} catch (UnsupportedOperationException e) {
				assertThat(e.getMessage()).isEqualTo("RSA(e) keys cannot be generated");
			}
		}

		@Test
		public void itIsNamedRSAe() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA_E.getName()).isEqualTo("RSA(e)");
		}
	}

	@SuppressWarnings("deprecation")
	public static class RSA_S {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA_S.toInteger()).isEqualTo(PublicKeyAlgorithmTags.RSA_SIGN);
		}

		@Test
		public void itProhibitsKeyGeneration() throws Exception {
			try {
				AsymmetricAlgorithm.RSA_S.getAlgorithmParameterSpec();
				fail("should have thrown an UnsupportedOperationException but didn't");
			} catch (UnsupportedOperationException e) {
				assertThat(e.getMessage()).isEqualTo("RSA(s) keys cannot be generated");
			}
		}

		@Test
		public void itIsNamedRSAs() throws Exception {
			assertThat(AsymmetricAlgorithm.RSA_S.getName()).isEqualTo("RSA(s)");
		}
	}

	@SuppressWarnings("deprecation")
	public static class EC {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.EC.toInteger()).isEqualTo(PublicKeyAlgorithmTags.EC);
		}

		@Test
		public void itProhibitsKeyGeneration() throws Exception {
			try {
				AsymmetricAlgorithm.EC.getAlgorithmParameterSpec();
				fail("should have thrown an UnsupportedOperationException but didn't");
			} catch (UnsupportedOperationException e) {
				assertThat(e.getMessage()).isEqualTo("EC keys cannot be generated");
			}
		}

		@Test
		public void itIsNamedEC() throws Exception {
			assertThat(AsymmetricAlgorithm.EC.getName()).isEqualTo("EC");
		}
	}

	@SuppressWarnings("deprecation")
	public static class ECDSA {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.ECDSA.toInteger()).isEqualTo(PublicKeyAlgorithmTags.ECDSA);
		}

		@Test
		public void itProhibitsKeyGeneration() throws Exception {
			try {
				AsymmetricAlgorithm.ECDSA.getAlgorithmParameterSpec();
				fail("should have thrown an UnsupportedOperationException but didn't");
			} catch (UnsupportedOperationException e) {
				assertThat(e.getMessage()).isEqualTo("ECDSA keys cannot be generated");
			}
		}
		@Test
		public void itIsNamedECDSA() throws Exception {
			assertThat(AsymmetricAlgorithm.ECDSA.getName()).isEqualTo("ECDSA");
		}
	}

	@SuppressWarnings("deprecation")
	public static class ELGAMAL_G {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(AsymmetricAlgorithm.ELGAMAL_G.toInteger()).isEqualTo(PublicKeyAlgorithmTags.ELGAMAL_GENERAL);
		}

		@Test
		public void itProhibitsKeyGeneration() throws Exception {
			try {
				AsymmetricAlgorithm.ELGAMAL_G.getAlgorithmParameterSpec();
				fail("should have thrown an UnsupportedOperationException but didn't");
			} catch (UnsupportedOperationException e) {
				assertThat(e.getMessage()).isEqualTo("ElGamal(g) keys cannot be generated");
			}
		}

		@Test
		public void itIsNamedElGamalg() throws Exception {
			assertThat(AsymmetricAlgorithm.ELGAMAL_G.getName()).isEqualTo("ElGamal(g)");
		}
	}
	
	public static class Default_Encryption_Algorithm {
		@Test
		public void itUsesRSAByDefault() throws Exception {
			assertThat(AsymmetricAlgorithm.ENCRYPTION_DEFAULT).isEqualTo(AsymmetricAlgorithm.RSA);
		}
	}
	
	public static class Default_Siging_Algorithm {
		@Test
		public void itUsesRSAByDefault() throws Exception {
			assertThat(AsymmetricAlgorithm.SIGNING_DEFAULT).isEqualTo(AsymmetricAlgorithm.RSA);
		}
	}
}
