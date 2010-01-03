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
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
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
	public Response show(@Context Request request, @Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name) throws CryptographicException {
		
		final Session session = credentials.buildSession(userDAO, userId);
		
		final Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		checkPreconditions(request, doc);
		
		final byte[] body = doc.decryptBody(session.getKeySet());
		return Response.ok()
				.entity(body)
				.type(doc.getContentType())
				.cacheControl(CACHE_SETTINGS)
				.lastModified(doc.getModifiedAt().toDate())
				.tag(doc.getEtag())
				.build();
	}
	
	/**
	 * Responds to a {@link DELETE} request by deleting the {@link Document}.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 */
	@DELETE
	@Transactional
	public Response delete(@Context Request request, @Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		checkPreconditions(request, doc);
		
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
	public Response store(@Context Request request, @Context HttpHeaders headers,
		@Context Credentials credentials, @PathParam("user_id") String userId,
		@PathParam("name") String name, byte[] body) throws CryptographicException {
		
		final Session session = credentials.buildSession(userDAO, userId);
		Document doc = documentDAO.findByOwnerAndName(session.getUser(), name);
		if (doc == null) {
			doc = documentDAO.newDocument(session.getUser(), name, headers.getMediaType());
		} else {
			checkPreconditions(request, doc);
		}
		
		doc.setModifiedAt(new DateTime(DateTimeZone.UTC));
		doc.encryptAndSetBody(
			session.getKeySet(),
			randomProvider.get(),
			body
		);
		
		documentDAO.saveOrUpdate(doc);
			
		return Response
				.noContent()
				.tag(doc.getEtag())
				.build();
	}
	
	/**
	 * If the request has {@code If-Modified-Since} or {@code If-None-Match}
	 * headers, and the resource has a matching {@link Document#getModifiedAt()}
	 * or {@link Document#getEtag()}, returns a {@code 304 Unmodified},
	 * indicating the client has the most recent version of the resource.
	 * 
	 * If the request has a {@code If-Unmodified-Since} or {@code If-Match}
	 * headers, and the resource has a more recent
	 * {@link Document#getModifiedAt()} or {@link Document#getEtag()}, returns
	 * a {@code 412 Precondition Failed}, indicating the client should re-read
	 * the resource before overwriting it.
	 */
	private void checkPreconditions(Request request, Document document) {
		final EntityTag eTag = new EntityTag(document.getEtag());
		final ResponseBuilder response = request.evaluatePreconditions(document.getModifiedAt().toDate(), eTag);
		if (response != null) {
			throw new WebApplicationException(response.build());
		}
	}
	
}
