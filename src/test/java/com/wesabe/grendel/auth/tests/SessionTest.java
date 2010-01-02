package com.wesabe.grendel.auth.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.UnlockedKeySet;

@RunWith(Enclosed.class)
public class SessionTest {
	public static class A_Session {
		private User user;
		private UnlockedKeySet keySet;
		private Session session;
		
		@Before
		public void setup() throws Exception {
			this.user = mock(User.class);
			this.keySet = mock(UnlockedKeySet.class);
			
			this.session = new Session(user, keySet);
		}
		
		@Test
		public void itHasAUser() throws Exception {
			assertThat(session.getUser()).isEqualTo(user);
		}
		
		@Test
		public void itHasAKeySet() throws Exception {
			assertThat(session.getKeySet()).isEqualTo(keySet);
		}
	}
}
