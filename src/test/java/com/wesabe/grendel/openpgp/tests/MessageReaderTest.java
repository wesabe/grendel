package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.MessageReader;
import com.wesabe.grendel.openpgp.UnlockedKeySet;

@RunWith(Enclosed.class)
public class MessageReaderTest {
	public static class Reading_An_Encrypted_Message {
		private KeySet owner;
		private UnlockedKeySet recipient;
		private byte[] original;
		
		@Before
		public void setup() throws Exception {
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.owner = KeySet.load(keyRingFile);
			
			final FileInputStream anotherKeyRingFile = new FileInputStream("src/test/resources/another-secret-keyring.gpg");
			this.recipient = KeySet.load(anotherKeyRingFile).unlock("test2".toCharArray());
			
			final FileInputStream input = new FileInputStream("src/test/resources/encrypted-and-signed.txt");
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] b = new byte[4096];
			int r = 0;
			while ((r = input.read(b)) >= 0) {
				output.write(b, 0, r);
			}
			this.original = output.toByteArray();
		}
		
		@Test
		public void itReadsAnEncryptedMessage() throws Exception {
			final FileInputStream input = new FileInputStream("src/test/resources/encrypted-and-signed.txt.gpg");
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] b = new byte[4096];
			int r = 0;
			while ((r = input.read(b)) >= 0) {
				output.write(b, 0, r);
			}
			final MessageReader reader = new MessageReader(owner, recipient);
			final byte[] body = reader.read(output.toByteArray());
			assertThat(body).isEqualTo(original);
		}
	}
	
	// TODO coda@wesabe.com -- Dec 23, 2009: check for integrity check failure
	// TODO coda@wesabe.com -- Dec 23, 2009: check for bad signature
	// TODO coda@wesabe.com -- Dec 23, 2009: check for missing signature
	// TODO coda@wesabe.com -- Dec 23, 2009: check for missing one-pass signature
	// TODO coda@wesabe.com -- Dec 23, 2009: check for missing encrypted data
	// TODO coda@wesabe.com -- Dec 24, 2009: check for uncompressed data
}
