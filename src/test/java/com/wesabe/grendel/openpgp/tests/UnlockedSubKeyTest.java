package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.FileInputStream;
import java.util.List;

import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.MasterKey;
import com.wesabe.grendel.openpgp.SubKey;
import com.wesabe.grendel.openpgp.UnlockedSubKey;
import com.wesabe.grendel.util.Iterators;

@RunWith(Enclosed.class)
public class UnlockedSubKeyTest {
	public static class An_Unlocked_Sub_Key {
		private UnlockedSubKey key;
		
		@Before
		public void setup() throws Exception {
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final PGPSecretKeyRing keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
			
			final List<PGPSecretKey> secretKeys = Iterators.toList(keyRing.getSecretKeys());
			
			final MasterKey masterKey = MasterKey.load(secretKeys.get(0));
			this.key = SubKey.load(secretKeys.get(1), masterKey).unlock("test".toCharArray());
		}
		
		@Test
		public void itHasAPrivateKey() throws Exception {
			assertThat(key.getPrivateKey()).isNotNull();
		}
		
		@Test
		public void itReturnsItselfWhenUnlocked() throws Exception {
			assertThat(key.unlock("blah".toCharArray())).isSameAs(key);
		}
	}
}