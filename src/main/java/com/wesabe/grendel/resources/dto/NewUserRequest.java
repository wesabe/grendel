package com.wesabe.grendel.resources.dto;

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
	private String password;
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void validate() throws ValidationException {
		final ValidationException error = new ValidationException();
		
		if (username == null) {
			error.missingRequiredProperty("username");
		}
		
		if (password == null) {
			error.missingRequiredProperty("password");
		}
		
		if (error.hasReasons()) {
			throw error;
		}
	}
}
