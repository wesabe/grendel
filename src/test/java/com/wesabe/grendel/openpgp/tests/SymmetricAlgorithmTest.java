package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.openpgp.SymmetricAlgorithm;

@RunWith(Enclosed.class)
public class SymmetricAlgorithmTest {
	@SuppressWarnings("deprecation")
	public static class Plaintext {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.PLAINTEXT.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.NULL);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.PLAINTEXT.toString()).isEqualTo("Plaintext");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class IDEA {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.IDEA.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.IDEA);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.IDEA.toString()).isEqualTo("IDEA");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class TripleDES {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.TRIPLE_DES.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.TRIPLE_DES);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.TRIPLE_DES.toString()).isEqualTo("3DES");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class CAST_128 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.CAST_128.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.CAST5);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.CAST_128.toString()).isEqualTo("CAST-128");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class Blowfish {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.BLOWFISH.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.BLOWFISH);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.BLOWFISH.toString()).isEqualTo("Blowfish");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class SAFER_SK {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.SAFER_SK.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.SAFER);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.SAFER_SK.toString()).isEqualTo("SAFER-SK");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class DES {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.DES.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.DES);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.DES.toString()).isEqualTo("DES");
		}
	}
	
	public static class AES_128 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.AES_128.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.AES_128);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.AES_128.toString()).isEqualTo("AES-128");
		}
	}
	
	public static class AES_192 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.AES_192.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.AES_192);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.AES_192.toString()).isEqualTo("AES-192");
		}
	}
	
	public static class AES_256 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.AES_256.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.AES_256);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.AES_256.toString()).isEqualTo("AES-256");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class Twofsh {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SymmetricAlgorithm.TWOFISH.toInteger()).isEqualTo(SymmetricKeyAlgorithmTags.TWOFISH);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SymmetricAlgorithm.TWOFISH.toString()).isEqualTo("Twofish");
		}
	}
	
	public static class Default {
		@Test
		public void itDefaultsToUseAES_256() throws Exception {
			assertThat(SymmetricAlgorithm.DEFAULT).isEqualTo(SymmetricAlgorithm.AES_256);
		}
	}
	
	public static class Acceptable_Algorithms {
		@Test
		public void itOnlySpecifiesAESUsage() throws Exception {
			assertThat(SymmetricAlgorithm.ACCEPTABLE_ALGORITHMS)
				.isEqualTo(ImmutableList.of(
					SymmetricAlgorithm.AES_128,
					SymmetricAlgorithm.AES_192,
					SymmetricAlgorithm.AES_256
				));
		}
	}
}
