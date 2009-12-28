package com.wesabe.grendel.representations.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.representations.UserListRepresentation;

@RunWith(Enclosed.class)
public class UserListRepresentationTest {
	public static class Serializing_A_User_List {
		private UriInfo uriInfo;
		private UserListRepresentation rep;
		private User user;
		
		@Before
		public void setup() throws Exception {
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return UriBuilder.fromUri("http://example.com");
				}
			});
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("mrpeepers");
			
			this.rep = new UserListRepresentation(uriInfo, ImmutableList.of(user));
		}
		
		@Test
		public void itSerializesIntoJSON() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			
			assertThat(mapper.writeValueAsString(rep)).isEqualTo(
				"{" +
					"\"users\":[" +
						"{" +
							"\"id\":\"mrpeepers\"," +
							"\"uri\":\"http://example.com/users/mrpeepers\"" +
						"}" +
					"]" +
				"}"
			);
		}
	}
}
