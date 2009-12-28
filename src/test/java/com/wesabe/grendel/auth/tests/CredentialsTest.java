package com.wesabe.grendel.auth.tests;

import static org.fest.assertions.Assertions.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.auth.Credentials;

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
