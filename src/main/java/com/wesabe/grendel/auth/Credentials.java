package com.wesabe.grendel.auth;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class Credentials {
	public static final Response CHALLENGE = Response.status(Status.UNAUTHORIZED)
														.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Grendel\"")
														.build();
	
	private final String username;
	private final String password;

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
