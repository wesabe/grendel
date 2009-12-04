package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.FileInputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.openpgp.AsymmetricAlgorithm;
import com.wesabe.grendel.openpgp.CompressionAlgorithm;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.HashAlgorithm;
import com.wesabe.grendel.openpgp.KeyFlag;
import com.wesabe.grendel.openpgp.MasterKey;
import com.wesabe.grendel.openpgp.SymmetricAlgorithm;

@RunWith(Enclosed.class)
public class MasterKeyTest {
	public static class A_Master_Key {
		private MasterKey key;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final PGPSecretKeyRing keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
			
			this.key = MasterKey.load(keyRing.getSecretKey(0x8C7035EF8838238CL));
		}
		
		@Test
		public void itHasAnID() throws Exception {
			assertThat(key.getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}
		
		@Test
		public void itHasAHumanReadableID() throws Exception {
			assertThat(key.getHumanKeyID()).isEqualTo("8838238C");
		}
		
		@Test
		public void itHasAUserID() throws Exception {
			assertThat(key.getUserID()).isEqualTo("Sample Key <sample@wesabe.com>");
			assertThat(key.getUserIDs()).isEqualTo(ImmutableList.of("Sample Key <sample@wesabe.com>"));
		}
		
		@Test
		public void itIsAnRSAKey() throws Exception {
			assertThat(key.getAlgorithm()).isEqualTo(AsymmetricAlgorithm.RSA);
		}
		
		@Test
		public void itIs2048BitsLong() throws Exception {
			assertThat(key.getSize()).isEqualTo(2048);
		}
		
		@Test
		public void itCannotEncryptData() throws Exception {
			assertThat(key.canEncrypt()).isFalse();
		}
		
		@Test
		public void itCanSignData() throws Exception {
			assertThat(key.canSign()).isTrue();
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			assertThat(key.getCreatedAt()).isEqualTo(new DateTime(2009, 7, 9, 16, 22, 3, 0, DateTimeZone.UTC));
		}
		
		@Test
		public void itHasKeyFlags() throws Exception {
			assertThat(key.getKeyFlags()).containsOnly(KeyFlag.SIGNING, KeyFlag.CERTIFICATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(key.toString()).isEqualTo("2048-RSA/8838238C Sample Key <sample@wesabe.com>");
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredSymmetricAlgorithms() throws Exception {
			assertThat(key.getPreferredSymmetricAlgorithms())
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
			assertThat(key.getPreferredCompressionAlgorithms())
				.isEqualTo(ImmutableList.of(
					CompressionAlgorithm.ZLIB,
					CompressionAlgorithm.BZIP2,
					CompressionAlgorithm.ZIP
				));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredHashAlgorithms() throws Exception {
			assertThat(key.getPreferredHashAlgorithms())
				.isEqualTo(ImmutableList.of(
					HashAlgorithm.SHA_1,
					HashAlgorithm.SHA_256,
					HashAlgorithm.RIPEMD_160
				));
		}
	}
	
	public static class A_Sub_Key {
		private PGPSecretKeyRing keyRing;

		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
		}

		@Test
		public void itCannotBeLoadedAsAMasterKey() throws Exception {
			try {
				MasterKey.load(keyRing.getSecretKey(0xA3A5D038FF30574EL));
			} catch (CryptographicException e) {
				assertThat(e.getMessage()).isEqualTo("not a self-signed master key");
			}
		}
	}
	
	public static class Unlocking_A_Master_Key {
		private MasterKey key;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final PGPSecretKeyRing keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
			
			this.key = MasterKey.load(keyRing.getSecretKey(0x8C7035EF8838238CL));
		}
		
		@Test
		public void itReturnsAnUnlockedMasterKeyForTheCorrectPassphrase() throws Exception {
			assertThat(key.unlock("test".toCharArray())).isNotNull();
		}
		
		@Test
		public void itThrowsACryptographicExceptionForTheIncorrectPassphrase() throws Exception {
			try {
				key.unlock("wonk".toCharArray());
			} catch (CryptographicException e) {
				assertThat(e.getMessage()).isEqualTo("incorrect passphrase");
			}
		}
	}
}
