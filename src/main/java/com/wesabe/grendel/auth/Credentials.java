package com.wesabe.grendel.auth;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A set of Basic authentication credentials.
 *
 * @see BasicAuthProvider
 * @author coda
 */
public class Credentials {
	/**
	 * An authentication challenge {@link Response}. Use this when a client's
	 * provided credentials are invalid.
	 */
	public static final Response CHALLENGE =
		Response.status(Status.UNAUTHORIZED)
			.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Grendel\"")
			.build();
	
	private final String username;
	private final String password;
	
	/**
	 * Creates a new set of credentials.
	 * 
	 * @param username the client's provided username
	 * @param password the client's provided password
	 */
	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Returns the client's provided username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the client's provided password.
	 */
	public String getPassword() {
		return password;
	}
}
