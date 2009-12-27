package com.wesabe.grendel.resources.dto;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonSetter;

/**
 * A data transfer object for a request to create a new user.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "username": "Example User",
 *   "password": "snoopersneekrit"
 * }
 * </pre>
 * 
 * Both {@code username} and {@code password} properties are required.
 * 
 * @author coda
 *
 */
public class NewUserRequest implements Validatable {
	private String username;
	private char[] password;
	
	@JsonGetter("password")
	public char[] getPassword() {
		return password;
	}
	
	@JsonGetter("username")
	public String getUsername() {
		return username;
	}
	
	@JsonSetter("password")
	public void setPassword(char[] password) {
		this.password = Arrays.copyOf(password, password.length);
		Arrays.fill(password, '\0');
	}
	
	@JsonSetter("username")
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void sanitize() {
		Arrays.fill(password, '\0');
	}

	@Override
	public void validate() throws ValidationException {
		final ValidationException error = new ValidationException();
		
		if ((username == null) || username.isEmpty()) {
			error.missingRequiredProperty("username");
		}
		
		if ((password == null) || (password.length == 0)) {
			error.missingRequiredProperty("password");
		}
		
		if (error.hasReasons()) {
			throw error;
		}
	}
}
