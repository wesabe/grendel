package com.wesabe.grendel.representations;

/**
 * An interface for data transfer objects (DTOs) capable of validation.
 * 
 * @author coda
 */
public interface Validatable {
	/**
	 * Validates the object's state, throwing an exception if invalid.
	 * 
	 * @throws ValidationException if the object has an invalid state
	 */
	public abstract void validate() throws ValidationException;
}
