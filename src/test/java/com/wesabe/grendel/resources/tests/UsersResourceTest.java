package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.resources.UsersResource;
import com.wesabe.grendel.resources.dto.NewUserRequest;
import com.wesabe.grendel.resources.dto.ValidationException;

@RunWith(Enclosed.class)
public class UsersResourceTest {
	private static abstract class Context {
		protected KeySetGenerator generator;
		protected UserDAO userDAO;
		protected UsersResource resource;
		
		public void setup() throws Exception {
			this.generator = mock(KeySetGenerator.class);
			this.userDAO = mock(UserDAO.class);
			
			this.resource = new UsersResource(generator, userDAO);
		}
	}
	
	public static class Creating_A_New_User extends Context {
		private UriInfo uriInfo;
		private NewUserRequest request;
		private KeySet keySet;
		private User user;
		private UriBuilder uriBuilder;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(NewUserRequest.class);
			when(request.getUsername()).thenReturn("username");
			when(request.getPassword()).thenReturn("password".toCharArray());
			
			this.keySet = mock(KeySet.class);
			when(keySet.getEncoded()).thenReturn(new byte[] { 1, 2, 3 });
			when(keySet.getUserID()).thenReturn("username");

			this.user = mock(User.class);
			
			when(generator.generate(Mockito.anyString(), Mockito.any(char[].class))).thenReturn(keySet);
			
			when(userDAO.contains(Mockito.anyString())).thenReturn(false);
			when(userDAO.create(Mockito.any(User.class))).thenReturn(user);
			
			this.uriBuilder = mock(UriBuilder.class);
			when(uriBuilder.path(Mockito.any(Class.class))).thenReturn(uriBuilder);
			when(uriBuilder.build(Mockito.anyVararg())).thenReturn(URI.create("http://example.com/woot/"));
			when(uriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
		}
		
		@Test
		public void itValidatesTheRequest() throws Exception {
			resource.create(uriInfo, request);
			
			verify(request).validate();
		}
		
		@Test
		public void itSanitizesTheRequest() throws Exception {
			resource.create(uriInfo, request);
			
			verify(request).sanitize();
		}
		
		@Test
		public void itChecksToSeeIfTheUsernameIsTaken() throws Exception {
			resource.create(uriInfo, request);
			
			verify(userDAO).contains("username");
		}
		
		@Test
		public void itGeneratesAKeySet() throws Exception {
			resource.create(uriInfo, request);
			
			final ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);
			verify(generator).generate(Mockito.eq("username"), password.capture());
			assertThat(password.getValue()).isEqualTo("password".toCharArray());
		}
		
		@Test
		public void itCreatesANewUser() throws Exception {
			resource.create(uriInfo, request);
			
			final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userDAO).create(userCaptor.capture());
			
			assertThat(userCaptor.getValue().getKeySet()).isSameAs(keySet);
		}
		
		@Test
		public void itReturnsA201CreatedWithTheUsersLocation() throws Exception {
			final Response r = resource.create(uriInfo, request);
			
			assertThat(r.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
			assertThat(r.getMetadata().getFirst("Location")).isEqualTo(URI.create("http://example.com/woot/"));
		}
	}
	
	public static class Creating_A_New_User_With_A_Conflicting_Username extends Context {
		private UriInfo uriInfo;
		private NewUserRequest request;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(NewUserRequest.class);
			when(request.getUsername()).thenReturn("username");
			when(request.getPassword()).thenReturn("password".toCharArray());
			
			when(userDAO.contains(Mockito.anyString())).thenReturn(true);
		}
		
		@Test
		public void itThrowsAValidationException() throws Exception {
			try {
				resource.create(uriInfo, request);
			} catch (ValidationException e) {
				final String msg = (String) e.getResponse().getEntity();
				
				assertThat(msg).isEqualTo(
					"Grendel was unable to process your request for the following reason(s):\n" +
					"\n" +
					"* username is already taken\n"
				);
			}
		}
	}
}
