package com.wesabe.grendel.auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.eclipse.jetty.http.security.B64Code;
import org.eclipse.jetty.util.StringUtil;

import com.codahale.shore.injection.AbstractInjectionProvider;
import com.sun.jersey.api.core.HttpContext;

/**
 * A Jersey injection provider for {@link Credentials} instances.
 * <p>
 * Decodes Basic authentication credentials. If none are present, or the
 * credentials are malformed, throws a {@link WebApplicationException} with an
 * appropriate authentication challenge.
 * <p>
 * To add a Basic authentication requirement to a method, simply annotate a
 * {@link Credentials} parameter with {@link Context}:
 * <pre>
 * public Response show(@Context Credentials creds) {
 *   ...
 * }
 * </pre>
 * 
 * @author coda
 */
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
					final String encoded = header.substring(header.indexOf(' ') + 1);
					final String credentials = B64Code.decode(encoded, StringUtil.__ISO_8859_1);
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
