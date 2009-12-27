package com.wesabe.grendel.representations;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An exception class which will return a 422 Unprocessable Entity response to
 * the client.
 * 
 * @author coda
 */
public class ValidationException extends WebApplicationException {
	private static final int UNPROCESSABLE_ENTITY = 422;
	private static final long serialVersionUID = -6730797215368434430L;
	private final StringBuilder msgBuilder;
	private boolean hasReasons = false;

	public ValidationException() {
		super();
		this.msgBuilder = new StringBuilder(
			"Grendel was unable to process your request for the following reason(s):\n\n"
		);
	}
	
	/**
	 * Adds a reason for validation failure to the list.
	 */
	public void addReason(String reason) {
		this.hasReasons = true;
		msgBuilder.append("* ").append(reason).append('\n');
	}
	
	/**
	 * Adds a failure to include a required property to the list.
	 */
	public void missingRequiredProperty(String propertyName) {
		addReason("missing required property: " + propertyName);
	}
	
	/**
	 * Returns {@code true} if the exception has reasons, {@code false}
	 * otherwise.
	 */
	public boolean hasReasons() {
		return hasReasons;
	}
	
	@Override
	public Response getResponse() {
		return Response
				.status(UNPROCESSABLE_ENTITY)
				.type(MediaType.TEXT_PLAIN)
				.entity(msgBuilder.toString())
				.build();
	}
}
