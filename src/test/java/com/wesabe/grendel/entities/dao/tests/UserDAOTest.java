package com.wesabe.grendel.entities.dao.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.inject.Provider;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;

@RunWith(Enclosed.class)
public class UserDAOTest {
	private static abstract class Context {
		protected Session session;
		protected UserDAO dao;
		
		public void setup() throws Exception {
			this.session = mock(Session.class);
			this.dao = new UserDAO(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
	}
	
	public static class Checking_For_A_Users_Existence extends Context {
		private Query query;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.query = mock(Query.class);
			when(query.setString(Mockito.anyString(), Mockito.anyString())).thenReturn(query);
			
			when(session.getNamedQuery(anyString())).thenReturn(query);
		}
		
		@Test
		public void itCreatesANamedQueryAndParameterizesIt() throws Exception {
			dao.contains("woo");
			
			final InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.grendel.entities.User.Exists");
			inOrder.verify(query).setString("id", "woo");
		}
		
		@Test
		public void itReturnsTrueIfTheUserWasFound() throws Exception {
			when(query.uniqueResult()).thenReturn("woo");
			
			assertThat(dao.contains("woo")).isTrue();
		}
		
		@Test
		public void itReturnsFalseIfTheUserWasNotFound() throws Exception {
			when(query.uniqueResult()).thenReturn(null);
			
			assertThat(dao.contains("woo")).isFalse();
		}
	}
	
	public static class Finding_A_User_By_Id extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itReturnsTheUser() throws Exception {
			when(session.get(Mockito.any(Class.class), Mockito.any(Serializable.class))).thenReturn(user);
			
			assertThat(dao.findById("woo")).isEqualTo(user);
		}
		
		@Test
		public void itScopesTheQueryToTheClassAndId() throws Exception {
			dao.findById("woo");
			
			verify(session).get(User.class, "woo");
		}
	}
	
	public static class Creating_A_User extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itReturnsTheUser() throws Exception {
			assertThat(dao.create(user)).isEqualTo(user);
		}
		
		@Test
		public void itCreatesADatabaseEntry() throws Exception {
			dao.create(user);
			
			verify(session).save(user);
		}
	}
	
	public static class Deleting_A_User extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itDeletesTheUser() throws Exception {
			dao.delete(user);
			
			verify(session).delete(user);
		}
	}
}
