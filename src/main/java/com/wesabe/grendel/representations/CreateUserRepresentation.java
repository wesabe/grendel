package com.wesabe.grendel.representations;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonSetter;


/**
 * A data transfer object for a request to create a new user.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "id": "Example User",
 *   "password": "snoopersneekrit"
 * }
 * </pre>
 * 
 * Both {@code id} and {@code password} properties are required.
 * 
 * @author coda
 *
 */
public class CreateUserRepresentation implements Validatable {
	private String id;
	private char[] password;
	
	@JsonGetter("password")
	public char[] getPassword() {
		return password;
	}
	
	@JsonGetter("id")
	public String getId() {
		return id;
	}
	
	@JsonSetter("password")
	public void setPassword(char[] password) {
		this.password = Arrays.copyOf(password, password.length);
		Arrays.fill(password, '\0');
	}
	
	@JsonSetter("id")
	public void setId(String username) {
		this.id = username;
	}
	
	public void sanitize() {
		Arrays.fill(password, '\0');
	}

	@Override
	public void validate() throws ValidationException {
		final ValidationException error = new ValidationException();
		
		if ((id == null) || id.isEmpty()) {
			error.missingRequiredProperty("id");
		}
		
		if ((password == null) || (password.length == 0)) {
			error.missingRequiredProperty("password");
		}
		
		if (error.hasReasons()) {
			throw error;
		}
	}
}
