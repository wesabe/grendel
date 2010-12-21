package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
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
		protected Request request;
		protected DateTime modifiedAt, now;
		
		public void setup() throws Exception {
			this.modifiedAt = new DateTime(2009, 12, 29, 8, 42, 32, 00, DateTimeZone.UTC);
			this.now = new DateTime(2010, 1, 3, 9, 1, 45, 00, DateTimeZone.UTC);
			
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
			when(document.getModifiedAt()).thenReturn(modifiedAt);
			when(document.decryptBody(keySet)).thenReturn("yay for everyone".getBytes());
			when(document.getEtag()).thenReturn("doc-document1.txt-50");
			
			this.documentDAO = mock(DocumentDAO.class);
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userDAO, "bob")).thenReturn(session);
			
			this.request = mock(Request.class);
			
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
				resource.show(request, credentials, "bob", "document1.txt");
				fail("should have return 404 Not Found but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.show(request, credentials, "bob", "document1.txt");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.show(request, credentials, "bob", "document1.txt");
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("doc-document1.txt-50"));
		}
		
		@Test
		public void itReturnsTheDecryptedDocument() throws Exception {
			final Response response = resource.show(request, credentials, "bob", "document1.txt");
			
			SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			formatter.applyPattern("EEE MMM dd HH:mm:ss z yyyy");
			
			assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
			assertThat(response.getMetadata().getFirst("Content-Type")).isEqualTo(MediaType.valueOf("text/plain"));
			assertThat(response.getMetadata().getFirst("Cache-Control").toString()).isEqualTo("private, no-cache, no-store, no-transform");
			assertThat(formatter.format(response.getMetadata().getFirst("Last-Modified"))).isEqualTo("Tue Dec 29 08:42:32 UTC 2009");
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
				resource.delete(request, credentials, "bob", "document1.txt");
				fail("should have return 404 Not Found but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.delete(request, credentials, "bob", "document1.txt");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.delete(request, credentials, "bob", "document1.txt");
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("doc-document1.txt-50"));
		}
		
		@Test
		public void itDeletesDocumentIfValid() throws Exception {
			final Response response = resource.delete(request, credentials, "bob", "document1.txt");

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
			
			DateTimeUtils.setCurrentMillisFixed(now.getMillis());

			this.body = "hey, it's something new".getBytes();

			this.headers = mock(HttpHeaders.class);
			when(headers.getMediaType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);

			when(documentDAO.newDocument(user, "document1.txt", MediaType.TEXT_PLAIN_TYPE)).thenReturn(document);
		}
		
		@After
		public void teardown() {
			DateTimeUtils.setCurrentMillisSystem();
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.store(request, headers, credentials, "bob", "document1.txt", body);
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.store(request, headers, credentials, "bob", "document1.txt", body);
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("doc-document1.txt-50"));
		}
		
		@Test
		public void itDoesNotCheckPreconditionsOnANewDocument() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			resource.store(request, headers, credentials, "bob", "document1.txt", body);
			verify(request, never()).evaluatePreconditions(any(Date.class), any(EntityTag.class));
		}

		@Test
		public void itCreatesANewDocumentIfTheDocumentDoesntExist() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			final Response response = resource.store(request, headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(document, documentDAO);
			inOrder.verify(document).setModifiedAt(now);
			inOrder.verify(document).encryptAndSetBody(keySet, random, body);
			inOrder.verify(documentDAO).saveOrUpdate(document);
		}
		
		@Test
		public void itUpdatesTheDocumentIfTheDocumentDoesExist() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			final Response response = resource.store(request, headers, credentials, "bob", "document1.txt", body);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final InOrder inOrder = inOrder(document, documentDAO);
			inOrder.verify(document).setModifiedAt(now);
			inOrder.verify(document).encryptAndSetBody(keySet, random, body);
			inOrder.verify(documentDAO).saveOrUpdate(document);
			
			verify(documentDAO, never()).newDocument(any(User.class), anyString(), any(MediaType.class));
		}
	}
}
