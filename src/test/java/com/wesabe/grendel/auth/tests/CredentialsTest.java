package com.wesabe.grendel.auth.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.UnlockedKeySet;

@RunWith(Enclosed.class)
public class CredentialsTest {
	public static class A_Set_Of_Credentials {
		private Credentials creds;
		
		@Before
		public void setup() throws Exception {
			this.creds = new Credentials("woo", "hah");
		}
		
		@Test
		public void itHasAUsername() throws Exception {
			assertThat(creds.getUsername()).isEqualTo("woo");
		}
		
		@Test
		public void itHasAPassword() throws Exception {
			assertThat(creds.getPassword()).isEqualTo("hah");
		}
	}
	
	private static abstract class Session_Context {
		protected Credentials creds;
		protected UserDAO userDAO;
		protected KeySet keySet;
		protected UnlockedKeySet unlockedKeySet;
		protected User user;

		public void setup() throws Exception {
			this.unlockedKeySet = mock(UnlockedKeySet.class);
			
			this.keySet = mock(KeySet.class);
			when(keySet.unlock(any(char[].class))).thenReturn(unlockedKeySet);
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("woo");
			when(user.getKeySet()).thenReturn(keySet);
			
			this.userDAO = mock(UserDAO.class);
			when(userDAO.findById("woo")).thenReturn(user);
			
			this.creds = new Credentials("woo", "hah");
		}
		
	}
	
	public static class Building_A_Session_For_A_Nonexistent_User extends Session_Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			when(userDAO.findById("woo")).thenReturn(null);
		}
		
		@Test
		public void itThrowsAnAuthChallenge() throws Exception {
			try {
				creds.buildSession(userDAO);
				fail("should have thrown an auth challenge, but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse()).isEqualTo(Credentials.CHALLENGE);
			}
		}
	}
	
	public static class Building_A_Session_For_A_Bad_Password extends Session_Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			when(keySet.unlock(any(char[].class))).thenThrow(new CryptographicException("augh"));
		}
		
		@Test
		public void itAttemptsToUnlockTheKeySet() throws Exception {
			try {
				creds.buildSession(userDAO);
			} catch (WebApplicationException e) {}
			
			verify(keySet).unlock("hah".toCharArray());
		}
		
		@Test
		public void itThrowsAnAuthChallenge() throws Exception {
			try {
				creds.buildSession(userDAO);
				fail("should have thrown an auth challenge, but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse()).isEqualTo(Credentials.CHALLENGE);
			}
		}
	}
	
	public static class Building_A_Session_For_Valid_Creds extends Session_Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itAttemptsToUnlockTheKeySet() throws Exception {
			try {
				creds.buildSession(userDAO);
			} catch (WebApplicationException e) {}
			
			verify(keySet).unlock("hah".toCharArray());
		}
		
		@Test
		public void itReturnsASessionWithTheUserAndKeySet() throws Exception {
			final Session session = creds.buildSession(userDAO);
			
			assertThat(session.getUser()).isEqualTo(user);
			assertThat(session.getKeySet()).isEqualTo(unlockedKeySet);
		}
	}
	
	public static class Building_A_Session_For_Valid_Creds_With_An_Expected_id extends Session_Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsASessionWithTheUserAndKeySet() throws Exception {
			final Session session = creds.buildSession(userDAO, "woo");
			
			assertThat(session.getUser()).isEqualTo(user);
			assertThat(session.getKeySet()).isEqualTo(unlockedKeySet);
		}
	}
	
	public static class Building_A_Session_For_Valid_Creds_With_An_Unxpected_Id extends Session_Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itThrowsAn401() throws Exception {
			try {
				creds.buildSession(userDAO, "whee");
				fail("should have thrown an auth challenge, but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
			}
		}
	}
	
	public static class An_Authentication_Challenge {
		@Test
		public void itReturnsA401() throws Exception {
			assertThat(Credentials.CHALLENGE.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
		}
		
		@Test
		public void itHasRealmInformation() throws Exception {
			assertThat(Credentials.CHALLENGE.getMetadata().getFirst(HttpHeaders.WWW_AUTHENTICATE)).isEqualTo("Basic realm=\"Grendel\"");
		}
	}
}
