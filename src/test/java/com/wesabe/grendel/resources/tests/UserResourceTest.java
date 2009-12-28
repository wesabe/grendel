package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
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
		protected UriInfo uriInfo;
		protected User user;
		
		public void setup() throws Exception {
			this.user = mock(User.class);
			
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return UriBuilder.fromUri("http://example.com");
				}
			});
			
			this.credentials = new Credentials("bob", "secret");
			this.random = mock(SecureRandom.class);
			this.dao = mock(UserDAO.class);
			
			this.resource = new UserResource(dao, new Provider<SecureRandom>() {
				@Override
				public SecureRandom get() {
					return random;
				}
			});
		}
	}
	
	public static class Showing_A_User extends Context {
		private KeySet keySet;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.keySet = mock(KeySet.class);
			when(user.getKeySet()).thenReturn(keySet);
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(dao.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.show(uriInfo, credentials, "bob");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("frank");
			
			try {
				resource.show(uriInfo, credentials, "bob");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfThePasswordDoesntMatch() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("bob");
			
			when(keySet.unlock(Mockito.any(char[].class))).thenThrow(new CryptographicException("whups"));
			
			try {
				resource.show(uriInfo, credentials, "bob");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsAUserListIfValid() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("bob");
			
			when(keySet.unlock(Mockito.any(char[].class))).thenReturn(null);
			
			final UserInfoRepresentation list = resource.show(uriInfo, credentials, "bob");
			assertThat(list.getUser()).isEqualTo(user);
			assertThat(list.getUriInfo()).isEqualTo(uriInfo);
			
			final ArgumentCaptor<char[]> captor = ArgumentCaptor.forClass(char[].class);
			verify(keySet).unlock(captor.capture());
			assertThat(captor.getValue()).isEqualTo("secret".toCharArray());
		}
	}
	
	public static class Deleting_A_User extends Context {
		private KeySet keySet;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.keySet = mock(KeySet.class);
			when(user.getKeySet()).thenReturn(keySet);
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(dao.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.delete(uriInfo, credentials, "bob");
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("frank");
			
			try {
				resource.delete(uriInfo, credentials, "bob");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfThePasswordDoesntMatch() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("bob");
			
			when(keySet.unlock(Mockito.any(char[].class))).thenThrow(new CryptographicException("whups"));
			
			try {
				resource.delete(uriInfo, credentials, "bob");
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itDeletesTheUserIfValid() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("bob");
			
			when(keySet.unlock(Mockito.any(char[].class))).thenReturn(null);
			
			final Response response = resource.delete(uriInfo, credentials, "bob");
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			final ArgumentCaptor<char[]> captor = ArgumentCaptor.forClass(char[].class);
			verify(keySet).unlock(captor.capture());
			assertThat(captor.getValue()).isEqualTo("secret".toCharArray());
			
			verify(dao).delete(user);
		}
	}
	
	public static class Changing_A_Users_Password extends Context {
		private KeySet keySet, newKeySet;
		private UnlockedKeySet unlockedKeySet;
		private UpdateUserRepresentation request;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.keySet = mock(KeySet.class);
			this.newKeySet = mock(KeySet.class);
			this.unlockedKeySet = mock(UnlockedKeySet.class);
			
			when(keySet.unlock(Mockito.any(char[].class))).thenReturn(unlockedKeySet);
			when(unlockedKeySet.relock(Mockito.any(char[].class), Mockito.any(char[].class), Mockito.any(SecureRandom.class))).thenReturn(newKeySet);
			when(user.getKeySet()).thenReturn(keySet);
			
			this.request = new UpdateUserRepresentation();
			request.setPassword("woohoo".toCharArray());
		}
		
		@Test
		public void itReturnsA404IfTheUserDoesntExist() throws Exception {
			when(dao.findById(Mockito.anyString())).thenReturn(null);
			
			try {
				resource.update(credentials, "bob", request);
				fail("should have thrown a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsA401IfTheUsernameDoesntMatch() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("frank");
			
			try {
				resource.update(credentials, "bob", request);
				fail("should have thrown a 401 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
			}
		}
		
		@Test
		public void itChangesTheUsersPasswordIfValid() throws Exception {
			when(dao.findById("bob")).thenReturn(user);
			when(user.getId()).thenReturn("bob");
			
			
			
			final Response response = resource.update(credentials, "bob", request);
			assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
			
			ArgumentCaptor<char[]> captor = ArgumentCaptor.forClass(char[].class);
			verify(keySet).unlock(captor.capture());
			assertThat(captor.getValue()).isEqualTo("secret".toCharArray());
			
			captor = ArgumentCaptor.forClass(char[].class);
			verify(unlockedKeySet).relock(captor.capture(), captor.capture(), Mockito.eq(random));
			assertThat(captor.getAllValues().get(0)).isEqualTo("secret".toCharArray());
			assertThat(captor.getAllValues().get(1)).isEqualTo("woohoo".toCharArray());
			
			final InOrder inOrder = inOrder(user, dao);
			inOrder.verify(user).setKeySet(newKeySet);
			inOrder.verify(dao).saveOrUpdate(user);
		}
	}
}
