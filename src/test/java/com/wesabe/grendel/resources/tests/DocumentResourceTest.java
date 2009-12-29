package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.resources.DocumentResource;

@RunWith(Enclosed.class)
public class DocumentResourceTest {
	private static abstract class Context {
		protected SecureRandom random;
		protected Provider<SecureRandom> randomProvider;
		protected UserDAO userDAO;
		protected DocumentDAO documentDAO;
		protected DocumentResource resource;
		protected Credentials credentials;
		protected User user;
		protected Document document;
		
		public void setup() throws Exception {
			this.random = mock(SecureRandom.class);
			this.randomProvider = new Provider<SecureRandom>() {
				@Override
				public SecureRandom get() {
					return random;
				}
			};
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("bob");
			
			this.userDAO = mock(UserDAO.class);
			when(userDAO.findById(Mockito.anyString())).thenReturn(user);
			
			this.document = mock(Document.class);
			when(document.getName()).thenReturn("document1.txt");
			when(document.getContentType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);
			when(document.getModifiedAt()).thenReturn(new DateTime(2009, 12, 29, 8, 42, 32, 00, DateTimeZone.UTC));
			when(document.decryptBodyForOwner(Mockito.any(char[].class))).thenReturn("yay for everyone".getBytes());
			
			this.documentDAO = mock(DocumentDAO.class);
			when(documentDAO.findByOwnerAndName(Mockito.any(User.class), Mockito.anyString())).thenReturn(document);
			
			this.credentials = new Credentials("bob", "secret");
			
			this.resource = new DocumentResource(randomProvider, userDAO, documentDAO);
		}
	}
	
	public static class Showing_A_Document extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(userDAO.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.show(credentials, "bob", "document1.txt");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(user.getId()).thenReturn("frank");
			
			try {
				resource.show(credentials, "bob", "document1.txt");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA404IfTheDocumentDoesntExist() throws Exception {
			when(documentDAO.findByOwnerAndName(Mockito.any(User.class), Mockito.anyString())).thenReturn(null);
			
			try {
				resource.show(credentials, "bob", "document1.txt");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheDocumentWontUnlock() throws Exception {
			when(document.decryptBodyForOwner(Mockito.any(char[].class))).thenThrow(new CryptographicException("whups"));
			
			try {
				resource.show(credentials, "bob", "document1.txt");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsTheDecryptedDocumentIfValid() throws Exception {
			final Response response = resource.show(credentials, "bob", "document1.txt");
			
			assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
			assertThat(response.getMetadata().getFirst("Content-Type")).isEqualTo(MediaType.valueOf("text/plain"));
			assertThat(response.getMetadata().getFirst("Cache-Control").toString()).isEqualTo("private, no-cache, no-store, no-transform");
			assertThat(response.getMetadata().getFirst("Last-Modified").toString()).isEqualTo("Tue Dec 29 00:42:32 PST 2009");
			assertThat((byte[]) response.getEntity()).isEqualTo("yay for everyone".getBytes());
			
			ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);
			
			final InOrder inOrder = inOrder(userDAO, documentDAO, document);
			inOrder.verify(userDAO).findById("bob");
			inOrder.verify(documentDAO).findByOwnerAndName(user, "document1.txt");
			inOrder.verify(document).decryptBodyForOwner(password.capture());
			assertThat(password.getValue()).isEqualTo("secret".toCharArray());
		}

	}
	
	public static class Deleting_A_Document extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(userDAO.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.delete(credentials, "bob", "document1.txt");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(user.getId()).thenReturn("frank");
			
			try {
				resource.delete(credentials, "bob", "document1.txt");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA404IfTheDocumentDoesntExist() throws Exception {
			when(documentDAO.findByOwnerAndName(Mockito.any(User.class), Mockito.anyString())).thenReturn(null);
			
			try {
				resource.delete(credentials, "bob", "document1.txt");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheDocumentWontUnlock() throws Exception {
			when(document.decryptBodyForOwner(Mockito.any(char[].class))).thenThrow(new CryptographicException("whups"));
			
			try {
				resource.delete(credentials, "bob", "document1.txt");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itDeletesDocumentIfValid() throws Exception {
			final Response response = resource.delete(credentials, "bob", "document1.txt");
			
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);
			
			final InOrder inOrder = inOrder(userDAO, documentDAO, document);
			inOrder.verify(userDAO).findById("bob");
			inOrder.verify(documentDAO).findByOwnerAndName(user, "document1.txt");
			inOrder.verify(document).decryptBodyForOwner(password.capture());
			assertThat(password.getValue()).isEqualTo("secret".toCharArray());
			inOrder.verify(documentDAO).delete(document);
		}

	}
	
	public static class Updating_A_Document extends Context {
		private byte[] body;
		private HttpHeaders headers;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.body = "hey, it's something new".getBytes();
			
			this.headers = mock(HttpHeaders.class);
			when(headers.getMediaType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);
			
			when(documentDAO.newDocument(Mockito.any(User.class), Mockito.anyString(), Mockito.any(MediaType.class))).thenReturn(document);
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(userDAO.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.create(headers, credentials, "bob", "document1.txt", body);
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(user.getId()).thenReturn("frank");

			try {
				resource.create(headers, credentials, "bob", "document1.txt", body);
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}

		@SuppressWarnings("unchecked")
		@Test
		public void itCreatesANewDocumentIfTheDocumentDoesntExist() throws Exception {
			when(documentDAO.findByOwnerAndName(Mockito.any(User.class), Mockito.anyString())).thenReturn(null);

			final Response response = resource.create(headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(userDAO, documentDAO, document);
			inOrder.verify(userDAO).findById("bob");
			inOrder.verify(documentDAO).findByOwnerAndName(user, "document1.txt");
			inOrder.verify(documentDAO).newDocument(user, "document1.txt", MediaType.TEXT_PLAIN_TYPE);
			
			final ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);
			final ArgumentCaptor<List> recipients = ArgumentCaptor.forClass(List.class);
			final ArgumentCaptor<byte[]> body = ArgumentCaptor.forClass(byte[].class);
			
			inOrder.verify(document).encryptAndSetBody(password.capture(), recipients.capture(), Mockito.eq(random), body.capture());
			
			assertThat(password.getValue()).isEqualTo("secret".toCharArray());
			assertThat(recipients.getValue()).isEmpty();
			assertThat(body.getValue()).isEqualTo(this.body);
			
			inOrder.verify(documentDAO).saveOrUpdate(document);
		}
		
		@SuppressWarnings("unchecked")
		@Test
		public void itUpdatesDocumentIfTheDocumentDoesExist() throws Exception {
			final Response response = resource.create(headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(userDAO, documentDAO, document);
			inOrder.verify(userDAO).findById("bob");
			inOrder.verify(documentDAO).findByOwnerAndName(user, "document1.txt");
			inOrder.verify(documentDAO, never()).newDocument(user, "document1.txt", MediaType.TEXT_PLAIN_TYPE);
			
			final ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);
			final ArgumentCaptor<List> recipients = ArgumentCaptor.forClass(List.class);
			final ArgumentCaptor<byte[]> body = ArgumentCaptor.forClass(byte[].class);
			
			inOrder.verify(document).encryptAndSetBody(password.capture(), recipients.capture(), Mockito.eq(random), body.capture());
			
			assertThat(password.getValue()).isEqualTo("secret".toCharArray());
			assertThat(recipients.getValue()).isEmpty();
			assertThat(body.getValue()).isEqualTo(this.body);
			
			inOrder.verify(documentDAO).saveOrUpdate(document);
		}

		@Test
		public void itReturnsA401IfTheDocumentWontUnlock() throws Exception {
			doThrow(new CryptographicException("whups"))
				.when(document)
				.encryptAndSetBody(Mockito.any(char[].class), Mockito.anyListOf(KeySet.class), Mockito.any(SecureRandom.class), Mockito.any(byte[].class));

			try {
				resource.create(headers, credentials, "bob", "document1.txt", body);
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}

	}
}
