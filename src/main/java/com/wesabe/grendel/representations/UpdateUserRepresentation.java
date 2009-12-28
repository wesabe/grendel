package com.wesabe.grendel.representations;

/**
 * A representation of a request to update an existing user.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "id": "Example User",
 *   "password": "snoopersneekrit"
 * }
 * </pre>
 * 
 * One or both of {@code id} or {@code password} properties are required.
 * 
 * @author coda
 */
public class UpdateUserRepresentation extends CreateUserRepresentation {
	@Override
	public void validate() throws ValidationException {
		final ValidationException error = new ValidationException();
		
		if (!hasId() && !hasPassword()) {
			error.addReason("must have id or password");
		}
		
		if (error.hasReasons()) {
			throw error;
		}
	}

	public boolean hasPassword() {
		return (password != null) && (password.length != 0);
	}

	public boolean hasId() {
		return (id != null) && !id.isEmpty();
	}
}
