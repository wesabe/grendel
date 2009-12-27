package com.wesabe.grendel.entities.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.MessageReader;
import com.wesabe.grendel.openpgp.MessageWriter;

@RunWith(Enclosed.class)
public class DocumentTest {
	public static class A_Fresh_Document {
		private User owner;
		private String name;
		private DateTime now;
		
		@Before
		public void setup() throws Exception {
			this.now = new DateTime(2009, 12, 27, 10, 0, 43, 0, DateTimeZone.UTC);
			DateTimeUtils.setCurrentMillisFixed(now.getMillis());

			this.owner = mock(User.class);
			this.name = "sekrit";
		}
		
		@After
		public void teardown() {
			DateTimeUtils.setCurrentMillisSystem();
		}
		
		@Test
		public void itHasAnOwner() throws Exception {
			final Document doc = new Document(owner, name);
			
			assertThat(doc.getOwner()).isEqualTo(owner);
		}
		
		@Test
		public void itHasAName() throws Exception {
			final Document doc = new Document(owner, name);
			
			assertThat(doc.getName()).isEqualTo(name);
		}
		
		@Test
		public void itHasAModificationTimestamp() throws Exception {
			final Document doc = new Document(owner, name);
			
			assertThat(doc.getModifiedAt()).isEqualTo(now);
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			final Document doc = new Document(owner, name);
			
			assertThat(doc.getCreatedAt()).isEqualTo(now);
		}
		
		@Test
		public void itHasAContentType() throws Exception {
			final Document doc = new Document(owner, name);
			doc.setContentType("text/plain");
			
			assertThat(doc.getContentType()).isEqualTo("text/plain");
		}
	}
	
	public static class Encrypting_A_Document_Body {
		private KeySet ownerKeySet, recipientKeySet;
		private User owner;
		private Document doc;
		
		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			
			final FileInputStream ownerKeyring = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.ownerKeySet = KeySet.load(ownerKeyring);
			ownerKeyring.close();
			
			final FileInputStream recipientKeyring = new FileInputStream("src/test/resources/another-secret-keyring.gpg");
			this.recipientKeySet = KeySet.load(recipientKeyring);
			recipientKeyring.close();
			
			this.owner = new User(ownerKeySet);
			this.doc = new Document(owner, "test");
		}
		
		@Test
		public void itStoresItAsAnEncryptedOpenPGPMessage() throws Exception {
			final byte[] originalBody = "I am a secret document".getBytes();
			
			doc.encryptAndSetBody("test".toCharArray(), ImmutableList.of(recipientKeySet), new SecureRandom(), originalBody);
			
			final Field bodyField = doc.getClass().getDeclaredField("body");
			bodyField.setAccessible(true);
			
			final byte[] encryptedBody = (byte[]) bodyField.get(doc);
			final MessageReader reader = new MessageReader(ownerKeySet, recipientKeySet.unlock("test2".toCharArray()));
			
			final byte[] decryptedBody = reader.read(encryptedBody);
			assertThat(decryptedBody).isEqualTo(originalBody);
		}
	}

	public static class Decrypting_A_Document_Body {
		private KeySet ownerKeySet, recipientKeySet;
		private User owner;
		private Document doc;

		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());

			final FileInputStream ownerKeyring = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.ownerKeySet = KeySet.load(ownerKeyring);
			ownerKeyring.close();

			final FileInputStream recipientKeyring = new FileInputStream("src/test/resources/another-secret-keyring.gpg");
			this.recipientKeySet = KeySet.load(recipientKeyring);
			recipientKeyring.close();

			this.owner = new User(ownerKeySet);
			this.doc = new Document(owner, "test");
		}

		@Test
		public void itCanDecryptItForARecipient() throws Exception {
			final byte[] originalBody = "I am a secret document".getBytes();
			
			final MessageWriter writer = new MessageWriter(ownerKeySet.unlock("test".toCharArray()), ImmutableList.of(recipientKeySet), new SecureRandom());
			final byte[] encryptedBody = writer.write(originalBody);
			
			final Field bodyField = doc.getClass().getDeclaredField("body");
			bodyField.setAccessible(true);
			bodyField.set(doc, encryptedBody);
			
			final byte[] decryptedBody = doc.decryptBodyForRecipient(recipientKeySet, "test2".toCharArray());
			assertThat(decryptedBody).isEqualTo(originalBody);
		}
		
		@Test
		public void itCanDecryptItForTheOwner() throws Exception {
			final byte[] originalBody = "I am a secret document".getBytes();
			
			final MessageWriter writer = new MessageWriter(ownerKeySet.unlock("test".toCharArray()), ImmutableList.of(recipientKeySet), new SecureRandom());
			final byte[] encryptedBody = writer.write(originalBody);
			
			final Field bodyField = doc.getClass().getDeclaredField("body");
			bodyField.setAccessible(true);
			bodyField.set(doc, encryptedBody);
			
			final byte[] decryptedBody = doc.decryptBodyForOwner("test".toCharArray());
			assertThat(decryptedBody).isEqualTo(originalBody);
		}
	}
}
