package com.wesabe.grendel.entities.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.KeySet;

@RunWith(Enclosed.class)
public class UserTest {
	public static class A_Fresh_User {
		private KeySet keySet;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			
			this.keySet = mock(KeySet.class);
			when(keySet.getUserID()).thenReturn("user");
			when(keySet.getEncoded()).thenReturn(new byte[] { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
		}
		
		@Test
		public void itStoresTheKeySetAsAnEncodedBlob() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getId()).isEqualTo("user");
			assertThat(user.getKeySet()).isEqualTo(keySet);
			assertThat(user.getEncodedKeySet()).isEqualTo(new byte[] { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
		}
	}
	
	public static class A_User_Loaded_From_The_Database {
		private User user;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int r = 0;
			while ((r = keyRingFile.read(buffer)) >= 0) {
				output.write(buffer, 0, r);
			}
			keyRingFile.close();
			this.user = new User(KeySet.load(output.toByteArray()));
		}
		
		@Test
		public void itHasAUserId() throws Exception {
			assertThat(user.getKeySet().getUserID()).isEqualTo("Sample Key <sample@wesabe.com>");
		}
	}
}
