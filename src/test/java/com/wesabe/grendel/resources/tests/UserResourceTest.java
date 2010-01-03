package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.representations.UpdateUserRepresentation;
import com.wesabe.grendel.representations.UserInfoRepresentation;
import com.wesabe.grendel.resources.UserResource;

@RunWith(Enclosed.class)
public class UserResourceTest {
	private static abstract class Context {
		protected UserResource resource;
		protected UserDAO dao;
		protected SecureRandom random;
		protected Credentials credentials;
		protected UnlockedKeySet keySet;
		protected Session session;
		protected UriInfo uriInfo;
		protected Request request;
		protected User user;
		protected DateTime modifiedAt;
		
		public void setup() throws Exception {
			this.keySet = mock(UnlockedKeySet.class);
			this.user = mock(User.class);
			this.modifiedAt = new DateTime(2010, 1, 3, 9, 51, 32, 0, DateTimeZone.UTC);
			when(user.getModifiedAt()).thenReturn(modifiedAt);
			when(user.getEtag()).thenReturn("user-bob-4");
			
			this.session = new Session(user, keySet);
			
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return UriBuilder.fromUri("http://example.com");
				}
			});
			
			this.request = mock(Request.class);
			
			this.random = mock(SecureRandom.class);
			this.dao = mock(UserDAO.class);
			
			this.credentials = mock(Credentials.class);
			when(credentials.getPassword()).thenReturn("secret");
			when(credentials.buildSession(dao, "bob")).thenReturn(session);
			
			this.resource = new UserResource(dao, new Provider<SecureRandom>() {
				@Override
				public SecureRandom get() {
					return random;
				}
			});
		}
	}
	
	public static class Showing_A_User extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			when(dao.findById("bob")).thenReturn(user);
		}
		
		@Test
		public void itReturnsAUserIfFound() throws Exception {
			final UserInfoRepresentation list = resource.show(request, uriInfo, "bob");
			assertThat(list.getUser()).isEqualTo(user);
			assertThat(list.getUriInfo()).isEqualTo(uriInfo);
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.show(request, uriInfo, "bob");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.show(request, uriInfo, "bob");
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("user-bob-4"));
		}
		
		@Test
		public void itThrowsA404IfNotFound() throws Exception {
			when(dao.findById("bob")).thenReturn(null);
			
			try {
				resource.show(request, uriInfo, "bob");
				fail("should have throw a 404 Not Found but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
	}
	
	public static class Deleting_A_User extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.delete(request, uriInfo, credentials, "bob");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
			
			verify(dao, never()).delete(user);
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.delete(request, uriInfo, credentials, "bob");
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("user-bob-4"));
		}
		
		@Test
		public void itDeletesTheUserIfValid() throws Exception {
			final Response response = resource.delete(request, uriInfo, credentials, "bob");
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());

			verify(dao).delete(user);
		}
	}

	public static class Changing_A_Users_Password extends Context {
		private KeySet newKeySet;
		private UpdateUserRepresentation entity;

		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			this.newKeySet = mock(KeySet.class);

			when(keySet.relock(
				any(char[].class), any(char[].class), any(SecureRandom.class))
			).thenReturn(newKeySet);

			this.entity = new UpdateUserRepresentation();
			entity.setPassword("woohoo".toCharArray());
		}
		
		@Test
		public void itReturnsIfPreconditionsFail() throws Exception {
			when(request.evaluatePreconditions(any(Date.class), any(EntityTag.class))).thenReturn(Response.notModified());
			
			try {
				resource.update(request, credentials, "bob", entity);
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
			}
			
			verify(dao, never()).saveOrUpdate(user);
		}
		
		@Test
		public void itChecksPreconditions() throws Exception {
			resource.update(request, credentials, "bob", entity);
			verify(request).evaluatePreconditions(modifiedAt.toDate(), new EntityTag("user-bob-4"));
		}
		
		@Test
		public void itChangesTheUsersPassword() throws Exception {
			final Response response = resource.update(request, credentials, "bob", entity);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());

			final ArgumentCaptor<char[]> captor = ArgumentCaptor.forClass(char[].class);
			verify(keySet).relock(captor.capture(), captor.capture(), eq(random));
			assertThat(captor.getAllValues().get(0)).isEqualTo("secret".toCharArray());
			assertThat(captor.getAllValues().get(1)).isEqualTo("woohoo".toCharArray());

			final InOrder inOrder = inOrder(user, dao);
			inOrder.verify(user).setKeySet(newKeySet);
			inOrder.verify(dao).saveOrUpdate(user);
		}
	}
}
