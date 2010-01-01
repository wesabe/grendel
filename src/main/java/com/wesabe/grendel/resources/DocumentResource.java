package com.wesabe.grendel.resources;

import java.security.SecureRandom;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wideplay.warp.persist.Transactional;

/**
 * A class which exposes {@link Document} as a resource.
 * 
 * @author coda
 */
@Path("/users/{user_id}/documents/{name}")
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class DocumentResource {
	private static final CacheControl CACHE_SETTINGS;
	static {
		CACHE_SETTINGS = new CacheControl();
		CACHE_SETTINGS.setNoCache(true);
		CACHE_SETTINGS.setNoStore(true);
		CACHE_SETTINGS.setPrivate(true);
	}
	
	private final Provider<SecureRandom> randomProvider;
	private final UserDAO userDAO;
	private final DocumentDAO documentDAO;
	
	@Inject
	public DocumentResource(Provider<SecureRandom> randomProvider, UserDAO userDAO,
		DocumentDAO documentDAO) {
		this.randomProvider = randomProvider;
		this.userDAO = userDAO;
		this.documentDAO = documentDAO;
	}
	
	/**
	 * Responds to a {@link GET} request by decrypting the {@link Document} body
	 * and returning it.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 * @throws CryptographicException
	 */
	@GET
	public Response show(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name) throws CryptographicException {
		
		final Session session = credentials.buildSession(userDAO, userId);
		
		final Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		final byte[] body = doc.decryptBody(session.getKeySet());
		return Response.ok()
				.entity(body)
				.type(doc.getContentType())
				.cacheControl(CACHE_SETTINGS)
				.lastModified(doc.getModifiedAt().toDate())
				.build();
	}
	
	/**
	 * Responds to a {@link DELETE} request by deleting the {@link Document}.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 */
	@DELETE
	@Transactional
	public Response delete(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		documentDAO.delete(doc);
		return Response.noContent().build();
	}
	
	/**
	 * Responds to a {@link PUT} request by replacing the specified
	 * {@link Document} with the request entity.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 * @throws CryptographicException
	 */
	@PUT
	@Transactional
	public Response create(@Context HttpHeaders headers,
		@Context Credentials credentials, @PathParam("user_id") String userId,
		@PathParam("name") String name, byte[] body) throws CryptographicException {
		
		final Session session = credentials.buildSession(userDAO, userId);
		Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		if (doc == null) {
			doc = documentDAO.newDocument(session.getUser(), name, headers.getMediaType());
		}
		
		doc.encryptAndSetBody(
			session.getKeySet(),
			ImmutableList.<KeySet>of(),
			randomProvider.get(),
			body
		);
		
		documentDAO.saveOrUpdate(doc);
			
		return Response.noContent().build();
	}
	
}
