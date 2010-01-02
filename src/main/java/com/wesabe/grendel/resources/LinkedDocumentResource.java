package com.wesabe.grendel.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wideplay.warp.persist.Transactional;

/**
 * A class which exposes a linked {@link Document} as a resource.
 * 
 * @author coda
 */
@Path("/users/{user_id}/linked-documents/{owner_id}/{name}")
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class LinkedDocumentResource {
	private static final CacheControl CACHE_SETTINGS;
	static {
		CACHE_SETTINGS = new CacheControl();
		CACHE_SETTINGS.setNoCache(true);
		CACHE_SETTINGS.setNoStore(true);
		CACHE_SETTINGS.setPrivate(true);
	}

	private final UserDAO userDAO;
	private final DocumentDAO documentDAO;

	@Inject
	public LinkedDocumentResource(UserDAO userDAO, DocumentDAO documentDAO) {
		this.userDAO = userDAO;
		this.documentDAO = documentDAO;
	}

	/**
	 * Responds to a {@link GET} request by decrypting the {@link Document} body
	 * and returning it.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 */
	@GET
	public Response show(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("owner_id") String ownerId,
		@PathParam("name") String name) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final User owner = findUser(ownerId);
		final Document doc = findDocument(owner, name);
		
		checkLinkage(doc, session.getUser());
		
		try {
			final byte[] body = doc.decryptBody(session.getKeySet());
			return Response.ok()
					.entity(body)
					.type(doc.getContentType())
					.cacheControl(CACHE_SETTINGS)
					.lastModified(doc.getModifiedAt().toDate())
					.build();
		} catch (CryptographicException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Responds to a {@link DELETE} request by deleting the {@link User}'s
	 * access to this {@link Document}. This does <strong>not</strong> delete
	 * the document itself, nor does it re-encrypt the document.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 */
	@DELETE
	@Transactional
	public Response delete(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("owner_id") String ownerId,
		@PathParam("name") String name) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final User owner = findUser(ownerId);
		final Document doc = findDocument(owner, name);
		
		checkLinkage(doc, session.getUser());
		
		doc.unlinkUser(session.getUser());
		documentDAO.saveOrUpdate(doc);
		
		return Response.noContent().build();
	}
	
	private Document findDocument(User owner, String name) {
		final Document doc = documentDAO.findByOwnerAndName(owner, name);
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return doc;
	}

	private void checkLinkage(Document doc, User user) {
		if (!doc.isLinked(user)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	private User findUser(String id) {
		final User user = userDAO.findById(id);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return user;
	}
}
