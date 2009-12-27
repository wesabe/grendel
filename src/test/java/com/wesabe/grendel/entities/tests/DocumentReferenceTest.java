package com.wesabe.grendel.entities.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.entities.DocumentReference;
import com.wesabe.grendel.entities.User;

@RunWith(Enclosed.class)
public class DocumentReferenceTest {
	public static class A_Document_Reference {
		private User owner;
		private String name;
		private DocumentReference reference;
		
		@Before
		public void setup() throws Exception {
			this.owner = mock(User.class);
			this.name = "seekrits";
			
			this.reference = new DocumentReference(owner, name);
		}
		
		@Test
		public void itHasAnOwner() throws Exception {
			assertThat(reference.getOwner()).isEqualTo(owner);
		}
		
		@Test
		public void itHasAName() throws Exception {
			assertThat(reference.getName()).isEqualTo(name);
		}
	}
}
