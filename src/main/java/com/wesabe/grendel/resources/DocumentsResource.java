package com.wesabe.grendel.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.representations.DocumentListRepresentation;

/**
 * A class which exposes a list of {@link Document}s as a resource.
 * 
 * @author coda
 */
@Path("/users/{id}/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentsResource {
	final UserDAO userDAO;
	
	@Inject
	public DocumentsResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@GET
	public DocumentListRepresentation listDocuments(@Context UriInfo uriInfo,
		@Context Credentials credentials, @PathParam("id") String id) {
		
		final Session session = credentials.buildSession(userDAO, id);
		
		return new DocumentListRepresentation(uriInfo, session.getUser().getDocuments());
	}
}
