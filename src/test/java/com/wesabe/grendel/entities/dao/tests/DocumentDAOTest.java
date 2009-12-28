package com.wesabe.grendel.entities.dao.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.inject.Provider;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;

@RunWith(Enclosed.class)
public class DocumentDAOTest {
	private static abstract class Context {
		protected Session session;
		protected DocumentDAO dao;
		
		public void setup() throws Exception {
			this.session = mock(Session.class);
			this.dao = new DocumentDAO(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
	}
	
	public static class Finding_A_Document_By_Owner_And_Name extends Context {
		private Query query;
		private Document doc;
		private User owner;
		private String name;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
			this.owner = mock(User.class);
			this.name = "woohoo.txt";
			
			this.query = mock(Query.class);
			when(query.setString(Mockito.anyString(), Mockito.anyString())).thenReturn(query);
			when(query.setParameter(Mockito.anyString(), Mockito.anyObject())).thenReturn(query);
			when(query.uniqueResult()).thenReturn(doc);
			
			when(session.getNamedQuery(anyString())).thenReturn(query);
		}
		
		@Test
		public void itCreatesANamedQueryAndParameterizesIt() throws Exception {
			dao.findByOwnerAndName(owner, name);
			
			final InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.grendel.entities.Document.ByOwnerAndName");
			inOrder.verify(query).setParameter("owner", owner)
;			inOrder.verify(query).setString("name", name);
		}
		
		@Test
		public void itReturnsTheDocument() throws Exception {
			assertThat(dao.findByOwnerAndName(owner, name)).isEqualTo(doc);
		}
	}
	
	public static class Deleting_A_Document extends Context {
		private Document doc;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
		}
		
		@Test
		public void itDeletesTheDocument() throws Exception {
			dao.delete(doc);
			
			verify(session).delete(doc);
		}
	}
	
	public static class Saving_Or_Creating_A_User extends Context {
		private Document doc;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
		}
		
		@Test
		public void itReturnsTheDocument() throws Exception {
			assertThat(dao.saveOrUpdate(doc)).isEqualTo(doc);
		}
		
		@Test
		public void itCreatesADatabaseEntry() throws Exception {
			dao.saveOrUpdate(doc);
			
			verify(session).saveOrUpdate(doc);
		}
	}
}
