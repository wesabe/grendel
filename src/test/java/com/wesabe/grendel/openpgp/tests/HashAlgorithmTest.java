package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.inject.internal.ImmutableList;
import com.wesabe.grendel.openpgp.HashAlgorithm;

@RunWith(Enclosed.class)
public class HashAlgorithmTest {
	@SuppressWarnings("deprecation")
	public static class MD5 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.MD5.toInteger()).isEqualTo(HashAlgorithmTags.MD5);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.MD5.toString()).isEqualTo("MD5");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class SHA_1 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.SHA_1.toInteger()).isEqualTo(HashAlgorithmTags.SHA1);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.SHA_1.toString()).isEqualTo("SHA-1");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class RIPEMD_160 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.RIPEMD_160.toInteger()).isEqualTo(HashAlgorithmTags.RIPEMD160);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.RIPEMD_160.toString()).isEqualTo("RIPEMD-160");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class DoubleWideSHA_1 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.DOUBLE_SHA.toInteger()).isEqualTo(HashAlgorithmTags.DOUBLE_SHA);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.DOUBLE_SHA.toString()).isEqualTo("2xSHA-1");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class MD2 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.MD2.toInteger()).isEqualTo(HashAlgorithmTags.MD2);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.MD2.toString()).isEqualTo("MD2");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class TIGER_192 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.TIGER_192.toInteger()).isEqualTo(HashAlgorithmTags.TIGER_192);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.TIGER_192.toString()).isEqualTo("TIGER-192");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class HAVAL_5_160 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.HAVAL_5_160.toInteger()).isEqualTo(HashAlgorithmTags.HAVAL_5_160);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.HAVAL_5_160.toString()).isEqualTo("HAVAL-5-160");
		}
	}
	
	public static class SHA_224 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.SHA_224.toInteger()).isEqualTo(HashAlgorithmTags.SHA224);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.SHA_224.toString()).isEqualTo("SHA-224");
		}
	}
	
	public static class SHA_256 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.SHA_256.toInteger()).isEqualTo(HashAlgorithmTags.SHA256);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.SHA_256.toString()).isEqualTo("SHA-256");
		}
	}
	
	public static class SHA_384 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.SHA_384.toInteger()).isEqualTo(HashAlgorithmTags.SHA384);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.SHA_384.toString()).isEqualTo("SHA-384");
		}
	}
	
	public static class SHA_512 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(HashAlgorithm.SHA_512.toInteger()).isEqualTo(HashAlgorithmTags.SHA512);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(HashAlgorithm.SHA_512.toString()).isEqualTo("SHA-512");
		}
	}
	
	public static class Default {
		@Test
		public void itDefaultsToSHA_512() throws Exception {
			assertThat(HashAlgorithm.DEFAULT).isEqualTo(HashAlgorithm.SHA_512);
		}
	}
	
	public static class Acceptable_Algorithms {
		@SuppressWarnings("deprecation")
		@Test
		public void itAcceptsAllSHA2Variants() throws Exception {
			assertThat(HashAlgorithm.ACCEPTABLE_ALGORITHMS)
				.isEqualTo(ImmutableList.of(
					HashAlgorithm.SHA_224,
					HashAlgorithm.SHA_256,
					HashAlgorithm.SHA_384,
					HashAlgorithm.SHA_512,
					HashAlgorithm.SHA_1
				));
		}
	}
}
