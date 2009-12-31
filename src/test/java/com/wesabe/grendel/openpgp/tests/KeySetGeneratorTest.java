package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.security.SecureRandom;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.openpgp.AsymmetricAlgorithm;
import com.wesabe.grendel.openpgp.CompressionAlgorithm;
import com.wesabe.grendel.openpgp.HashAlgorithm;
import com.wesabe.grendel.openpgp.KeyFlag;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.openpgp.SymmetricAlgorithm;

@RunWith(Enclosed.class)
public class KeySetGeneratorTest {
	public static class A_Freshly_Generated_KeySet {
		private static KeySet keySet;

		@BeforeClass
		public static void setupOnce() throws Exception {
			final KeySetGenerator generator = new KeySetGenerator(new SecureRandom());
			keySet = generator.generate("Sample User <sample@example.com", "hello there".toCharArray());
		}

		@Test
		public void itHasAMasterKeyWithTheGivenUserID() throws Exception {
			assertThat(keySet.getMasterKey().getUserID()).isEqualTo("Sample User <sample@example.com");
		}

		@Test
		public void itHasAMasterKeyOfTheDefaultType() throws Exception {
			assertThat(keySet.getMasterKey().getAlgorithm()).isEqualTo(AsymmetricAlgorithm.SIGNING_DEFAULT);
		}

		@Test
		public void itHasAMasterKeyWhichCannotEncrypt() throws Exception {
			assertThat(keySet.getMasterKey().canEncrypt()).isFalse();
		}

		@Test
		public void itHasAMasterKeyWhichCanSign() throws Exception {
			assertThat(keySet.getMasterKey().canSign()).isTrue();
		}

		@Test
		public void itHasAMasterKeyWhichPrefersStrongEncryptionAlgorithms() throws Exception {
			assertThat(keySet.getMasterKey().getPreferredSymmetricAlgorithms()).isEqualTo(SymmetricAlgorithm.ACCEPTABLE_ALGORITHMS);
		}

		@Test
		public void itHasAMasterKeyWhichPrefersStrongHashAlgorithms() throws Exception {
			assertThat(keySet.getMasterKey().getPreferredHashAlgorithms()).isEqualTo(HashAlgorithm.ACCEPTABLE_ALGORITHMS);
		}

		@Test
		public void itHasAMasterKeyWhichPrefersCompressionAlgorithms() throws Exception {
			assertThat(keySet.getMasterKey().getPreferredCompressionAlgorithms()).isEqualTo(ImmutableList.of(CompressionAlgorithm.BZIP2, CompressionAlgorithm.ZLIB, CompressionAlgorithm.ZIP));
		}

		@Test
		public void itHasAMasterKeyWhichCanSignBeSplitAndAuthenticate() throws Exception {
			assertThat(keySet.getMasterKey().getKeyFlags()).isEqualTo(ImmutableSet.of(KeyFlag.AUTHENTICATION, KeyFlag.SIGNING, KeyFlag.SPLIT));
		}

		@Test
		public void itHasASubKeyWithTheGivenUserID() throws Exception {
			assertThat(keySet.getSubKey().getUserID()).isEqualTo("Sample User <sample@example.com");
		}

		@Test
		public void itHasASubKeyOfTheDefaultType() throws Exception {
			assertThat(keySet.getSubKey().getAlgorithm()).isEqualTo(AsymmetricAlgorithm.ENCRYPTION_DEFAULT);
		}

		@Test
		public void itHasASubKeyWhichCanEncrypt() throws Exception {
			assertThat(keySet.getSubKey().canEncrypt()).isTrue();
		}

		@Test
		public void itHasASubKeyWhichCannotSign() throws Exception {
			assertThat(keySet.getSubKey().canSign()).isFalse();
		}

		@Test
		public void itHasASubKeyWhichCanEncryptAndBeSplit() throws Exception {
			assertThat(keySet.getSubKey().getKeyFlags()).isEqualTo(ImmutableSet.of(KeyFlag.ENCRYPTION, KeyFlag.SPLIT));
		}
	}
}
