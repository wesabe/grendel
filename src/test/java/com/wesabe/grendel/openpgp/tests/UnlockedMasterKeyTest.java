package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.FileInputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.MasterKey;
import com.wesabe.grendel.openpgp.UnlockedMasterKey;

@RunWith(Enclosed.class)
public class UnlockedMasterKeyTest {
	public static class An_Unlocked_Master_Key {
		private UnlockedMasterKey key;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final PGPSecretKeyRing keyRing = new PGPSecretKeyRing(keyRingFile);
			keyRingFile.close();
			
			this.key = MasterKey.load(keyRing.getSecretKey(0x8C7035EF8838238CL)).unlock("test".toCharArray());
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
