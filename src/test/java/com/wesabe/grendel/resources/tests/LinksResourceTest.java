package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.representations.LinkListRepresentation;
import com.wesabe.grendel.resources.LinksResource;

@RunWith(Enclosed.class)
public class LinksResourceTest {
	public static class Listing_Links {
		protected Document document;
		protected Credentials credentials;
		protected User user;
		protected Session session;
		protected UserDAO userDAO;
		protected DocumentDAO documentDAO;
		protected UriInfo uriInfo;
		protected LinksResource resource;
		
		@Before
		public void setup() throws Exception {
			this.document = mock(Document.class);
			
			this.user = mock(User.class);
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			
			this.userDAO = mock(UserDAO.class);
			
			this.documentDAO = mock(DocumentDAO.class);
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			this.resource = new LinksResource(userDAO, documentDAO);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userDAO, "bob")).thenReturn(session);
			
			this.uriInfo = mock(UriInfo.class);
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentDAO.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.listLinks(uriInfo, credentials, "bob", "document1.txt");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsADocumentList() throws Exception {
			final LinkListRepresentation docs = resource.listLinks(uriInfo, credentials, "bob", "document1.txt");
			
			assertThat(docs.getDocument()).isEqualTo(document);
			assertThat(docs.getUriInfo()).isEqualTo(uriInfo);
		}
	}
}
