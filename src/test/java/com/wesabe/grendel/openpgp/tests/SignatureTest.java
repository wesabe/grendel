package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.FileInputStream;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.openpgp.AsymmetricAlgorithm;
import com.wesabe.grendel.openpgp.CompressionAlgorithm;
import com.wesabe.grendel.openpgp.HashAlgorithm;
import com.wesabe.grendel.openpgp.KeyFlag;
import com.wesabe.grendel.openpgp.Signature;
import com.wesabe.grendel.openpgp.SignatureType;
import com.wesabe.grendel.openpgp.SymmetricAlgorithm;

@RunWith(Enclosed.class)
public class SignatureTest {
	public static class A_Self_Signature {
		private PGPSecretKey key;
		private Signature signature;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final PGPSecretKeyRing keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
			
			this.key = keyRing.getSecretKey(0x8C7035EF8838238CL);
			final Iterator<?> sigs = key.getPublicKey().getSignatures();
			this.signature = new Signature((PGPSignature) sigs.next());
		}
		
		@Test
		public void itIsAPositiveCertification() throws Exception {
			assertThat(signature.getSignatureType()).isEqualTo(SignatureType.POSITIVE_CERTIFICATION);
		}
		
		@Test
		public void itHasAKeyID() throws Exception {
			assertThat(signature.getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasAHashAlgorithm() throws Exception {
			assertThat(signature.getHashAlgorithm()).isEqualTo(HashAlgorithm.SHA_1);
		}
		
		@Test
		public void itHasAKeyAlgorithm() throws Exception {
			assertThat(signature.getKeyAlgorithm()).isEqualTo(AsymmetricAlgorithm.RSA);
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			assertThat(signature.getCreatedAt()).isEqualTo(new DateTime(2009, 7, 9, 16, 22, 3, 00, DateTimeZone.UTC));
		}
		
		@Test
		public void itHasKeyFlags() throws Exception {
			assertThat(signature.getKeyFlags()).isEqualTo(ImmutableSet.of(KeyFlag.CERTIFICATION, KeyFlag.SIGNING));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredSymmetricAlgorithms() throws Exception {
			assertThat(signature.getPreferredSymmetricAlgorithms())
				.isEqualTo(ImmutableList.of(
					SymmetricAlgorithm.AES_256,
					SymmetricAlgorithm.AES_192,
					SymmetricAlgorithm.AES_128,
					SymmetricAlgorithm.CAST_128,
					SymmetricAlgorithm.TRIPLE_DES,
					SymmetricAlgorithm.IDEA
				));
		}
		
		@Test
		public void itHasPreferredCompressionAlgorithms() throws Exception {
			assertThat(signature.getPreferredCompressionAlgorithms())
				.isEqualTo(ImmutableList.of(
					CompressionAlgorithm.ZLIB,
					CompressionAlgorithm.BZIP2,
					CompressionAlgorithm.ZIP
				));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredHashAlgorithms() throws Exception {
			assertThat(signature.getPreferredHashAlgorithms())
				.isEqualTo(ImmutableList.of(
					HashAlgorithm.SHA_1,
					HashAlgorithm.SHA_256,
					HashAlgorithm.RIPEMD_160
				));
		}
	}
}
