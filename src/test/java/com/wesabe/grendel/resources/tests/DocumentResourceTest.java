package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;

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
import org.mockito.InOrder;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
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
		protected Session session;
		protected User user;
		protected UnlockedKeySet keySet;
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
			
			this.keySet = mock(UnlockedKeySet.class);
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			when(session.getKeySet()).thenReturn(keySet);
			
			this.userDAO = mock(UserDAO.class);
			
			this.document = mock(Document.class);
			when(document.getName()).thenReturn("document1.txt");
			when(document.getContentType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);
			when(document.getModifiedAt()).thenReturn(new DateTime(2009, 12, 29, 8, 42, 32, 00, DateTimeZone.UTC));
			when(document.decryptBody(keySet)).thenReturn("yay for everyone".getBytes());
			
			this.documentDAO = mock(DocumentDAO.class);
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userDAO, "bob")).thenReturn(session);
			
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
		public void itThrowsA404IfTheDocumentIsNotFound() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.show(credentials, "bob", "document1.txt");
				fail("should have return 404 Not Found but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsTheDecryptedDocument() throws Exception {
			final Response response = resource.show(credentials, "bob", "document1.txt");
			
			assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
			assertThat(response.getMetadata().getFirst("Content-Type")).isEqualTo(MediaType.valueOf("text/plain"));
			assertThat(response.getMetadata().getFirst("Cache-Control").toString()).isEqualTo("private, no-cache, no-store, no-transform");
			assertThat(response.getMetadata().getFirst("Last-Modified").toString()).isEqualTo("Tue Dec 29 00:42:32 PST 2009");
			assertThat((byte[]) response.getEntity()).isEqualTo("yay for everyone".getBytes());
		}

	}
	
	public static class Deleting_A_Document extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}

		@Test
		public void itThrowsA404IfTheDocumentIsNotFound() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.delete(credentials, "bob", "document1.txt");
				fail("should have return 404 Not Found but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}

		@Test
		public void itDeletesDocumentIfValid() throws Exception {
			final Response response = resource.delete(credentials, "bob", "document1.txt");

			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			verify(documentDAO).delete(document);
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

			when(documentDAO.newDocument(user, "document1.txt", MediaType.TEXT_PLAIN_TYPE)).thenReturn(document);
		}

		@Test
		public void itCreatesANewDocumentIfTheDocumentDoesntExist() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			final Response response = resource.create(headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(document, documentDAO);
			inOrder.verify(document).encryptAndSetBody(keySet, ImmutableList.<KeySet>of(), random, body);
			inOrder.verify(documentDAO).saveOrUpdate(document);
		}
		
		@Test
		public void itUpdatesTheDocumentIfTheDocumentDoesExist() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			final Response response = resource.create(headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(document, documentDAO);
			inOrder.verify(document).encryptAndSetBody(keySet, ImmutableList.<KeySet>of(), random, body);
			inOrder.verify(documentDAO).saveOrUpdate(document);
			
			verify(documentDAO, never()).newDocument(any(User.class), anyString(), any(MediaType.class));
		}
	}
}
