package com.wesabe.grendel.auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.eclipse.jetty.http.security.B64Code;
import org.eclipse.jetty.util.StringUtil;

import com.codahale.shore.injection.AbstractInjectionProvider;
import com.sun.jersey.api.core.HttpContext;

@Provider
public class BasicAuthProvider extends AbstractInjectionProvider<Credentials> {
	private static final String HEADER_PREFIX = "Basic ";
	private static final char CREDENTIAL_DELIMITER = ':';

	public BasicAuthProvider() {
		super(Credentials.class);
	}

	@Override
	public Credentials getValue(HttpContext context) {
		String header = context.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
		try {
			try {
				if ((header != null) && header.startsWith(HEADER_PREFIX)) {
					final String encodedCredentials = header.substring(header.indexOf(' ') + 1);
					final String credentials = B64Code.decode(encodedCredentials, StringUtil.__ISO_8859_1);
					final int i = credentials.indexOf(CREDENTIAL_DELIMITER);
					
					final String username = credentials.substring(0, i);
					final String password = credentials.substring(i + 1);
					
					if ((username != null) && (password != null)) {
						return new Credentials(username, password);
					}
				}
			} catch (IllegalArgumentException e) {
				// fall through to sending an auth challenge
			} catch (StringIndexOutOfBoundsException e) {
				// fall through to sending an auth challenge
			}

			throw new WebApplicationException(Credentials.CHALLENGE);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
