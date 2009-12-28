package com.wesabe.grendel.representations.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.representations.UpdateUserRepresentation;
import com.wesabe.grendel.representations.ValidationException;

@RunWith(Enclosed.class)
public class UpdateUserRepresentationTest {
	public static class A_Valid_New_User_Request {
		private UpdateUserRepresentation req;
		
		@Before
		public void setup() throws Exception {
			this.req = new UpdateUserRepresentation();
			
			req.setId("dingo");
			req.setPassword("happenstance".toCharArray());
		}
		
		@Test
		public void itIsValid() throws Exception {
			try {
				req.validate();
				assertThat(true).isTrue();
			} catch (ValidationException e) {
				fail("didn't expect a ValidationException but one was thrown");
			}
		}
		
		@Test
		public void itHasAUsername() throws Exception {
			assertThat(req.getId()).isEqualTo("dingo");
		}
		
		@Test
		public void itHasAPassword() throws Exception {
			assertThat(req.getPassword()).isEqualTo("happenstance".toCharArray());
		}
		
		@Test
		public void itCanBeSanitized() throws Exception {
			assertThat(req.getPassword()).isEqualTo("happenstance".toCharArray());
			req.sanitize();
			assertThat(req.getPassword()).isEqualTo("\0\0\0\0\0\0\0\0\0\0\0\0".toCharArray());
		}
	}
	
	public static class An_Invalid_New_User_Request {
		private UpdateUserRepresentation req;
		
		@Before
		public void setup() throws Exception {
			this.req = new UpdateUserRepresentation();
		}
		
		@Test
		public void itThrowsAnExceptionOnValidation() throws Exception {
			try {
				req.validate();
				fail("should have thrown a ValidationException but didn't");
			} catch (ValidationException e) {
				final String msg = (String) e.getResponse().getEntity();
				
				assertThat(msg).isEqualTo(
					"Grendel was unable to process your request for the following reason(s):" +
					"\n" +
					"\n" +
					"* must have id or password\n"
				);
			}
		}
	}
	
	public static class Deserializing_From_Json {
		@Test
		public void itDeserializesJSON() throws Exception {
			final String json = "{\"id\":\"mrpeepers\",\"password\":\"hoohah\"}";
			
			final ObjectMapper mapper = new ObjectMapper();
			final UpdateUserRepresentation rep = mapper.readValue(json, UpdateUserRepresentation.class);
			
			assertThat(rep.getId()).isEqualTo("mrpeepers");
			assertThat(rep.getPassword()).isEqualTo("hoohah".toCharArray());
		}
	}
}
