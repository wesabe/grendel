package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.representations.LinkedDocumentListRepresentation;
import com.wesabe.grendel.resources.LinkedDocumentsResource;

@RunWith(Enclosed.class)
public class LinkedDocumentsResourceTest {
	public static class Listing_A_Users_Linked_Documents {
		protected Document document;
		protected Credentials credentials;
		protected User user;
		protected Session session;
		protected UserDAO userDAO;
		protected UriInfo uriInfo;
		protected LinkedDocumentsResource resource;
		
		@Before
		public void setup() throws Exception {
			this.document = mock(Document.class);
			
			this.user = mock(User.class);
			when(user.getLinkedDocuments()).thenReturn(ImmutableSet.of(document));
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			
			this.userDAO = mock(UserDAO.class);
			
			this.resource = new LinkedDocumentsResource(userDAO);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userDAO, "bob")).thenReturn(session);
			
			this.uriInfo = mock(UriInfo.class);
		}
		
		@Test
		public void itReturnsADocumentList() throws Exception {
			final LinkedDocumentListRepresentation docs = resource.listDocuments(uriInfo, credentials, "bob");
			
			assertThat(docs.getUser()).isEqualTo(user);
			assertThat(docs.getUriInfo()).isEqualTo(uriInfo);
		}
	}
}
