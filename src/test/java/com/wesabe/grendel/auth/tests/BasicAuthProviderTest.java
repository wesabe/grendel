package com.wesabe.grendel.auth.tests;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.wesabe.grendel.auth.BasicAuthProvider;
import com.wesabe.grendel.auth.Credentials;

@RunWith(Enclosed.class)
public class BasicAuthProviderTest {
	private static abstract class Context {
		protected HttpContext context;
		protected HttpRequestContext request;
		protected BasicAuthProvider provider;
		
		public void setup() throws Exception {
			this.request = mock(HttpRequestContext.class);
			when(request.getHeaderValue(HttpHeaders.AUTHORIZATION)).thenReturn(header());
			
			this.context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			this.provider = new BasicAuthProvider();
		}
		
		protected abstract String header();
	}
	
	public static class Decoding_A_Valid_Auth_Header extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}

		@Override
		protected String header() {
			return "Basic bXJwZWVwZXJzOmhhcHB5";
		}
		
		@Test
		public void itReturnsASetOfCredentials() throws Exception {
			final Credentials creds = provider.getValue(context);
			
			assertThat(creds.getUsername()).isEqualTo("mrpeepers");
			assertThat(creds.getPassword()).isEqualTo("happy");
		}
	}
	
	public static class Decoding_An_Invalid_Auth_Header extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}

		@Override
		protected String header() {
			return "Basic bXJwZWVwZXJzIWhhcHB5";
		}
		
		@Test
		public void itReturnsASetOfCredentials() throws Exception {
			try {
				provider.getValue(context);
				fail("should have thrown a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse()).isEqualTo(Credentials.CHALLENGE);
			}
		}
	}
}
