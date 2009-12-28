package com.wesabe.grendel.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.representations.CreateUserRepresentation;
import com.wesabe.grendel.representations.ValidationException;
import com.wesabe.grendel.representations.UserListRepresentation.UserListItem;
import com.wesabe.grendel.resources.UsersResource;

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
	
	public static class Listing_All_Users extends Context {
		private User user;
		private UriInfo uriInfo;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return UriBuilder.fromUri("http://example.com");
				}
			});
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("mrpeeper");
			
			when(userDAO.findAll()).thenReturn(ImmutableList.of(user));
		}
		
		@Test
		public void itFindsAllUsers() throws Exception {
			resource.list(uriInfo);
			
			verify(userDAO).findAll();
		}
		
		@Test
		public void itReturnsAListOfAllUsers() throws Exception {
			final List<UserListItem> list = resource.list(uriInfo).getUsers();
			
			assertThat(list).hasSize(1);
			
			assertThat(list.get(0).getId()).isEqualTo("mrpeeper");
			// FIXME coda@wesabe.com -- Dec 27, 2009: direct this to where it should go
			assertThat(list.get(0).getUri()).isEqualTo("http://example.com/users/");
		}
	}
	
	public static class Creating_A_New_User extends Context {
		private UriInfo uriInfo;
		private CreateUserRepresentation request;
		private KeySet keySet;
		private User user;
		private UriBuilder uriBuilder;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(CreateUserRepresentation.class);
			when(request.getId()).thenReturn("username");
			when(request.getPassword()).thenReturn("password".toCharArray());
			
			this.keySet = mock(KeySet.class);
			when(keySet.getEncoded()).thenReturn(new byte[] { 1, 2, 3 });
			when(keySet.getUserID()).thenReturn("username");

			this.user = mock(User.class);
			
			when(generator.generate(Mockito.anyString(), Mockito.any(char[].class))).thenReturn(keySet);
			
			when(userDAO.contains(Mockito.anyString())).thenReturn(false);
			when(userDAO.saveOrUpdate(Mockito.any(User.class))).thenReturn(user);
			
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
			verify(userDAO).saveOrUpdate(userCaptor.capture());
			
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
		private CreateUserRepresentation request;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(CreateUserRepresentation.class);
			when(request.getId()).thenReturn("username");
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
