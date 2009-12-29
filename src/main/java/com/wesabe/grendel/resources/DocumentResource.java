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
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wideplay.warp.persist.Transactional;

@Path("/users/{user_id}/{name}")
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
	public DocumentResource(Provider<SecureRandom> randomProvider, UserDAO userDAO, DocumentDAO documentDAO) {
		this.randomProvider = randomProvider;
		this.userDAO = userDAO;
		this.documentDAO = documentDAO;
	}
	
	@GET
	public Response show(@Context Credentials credentials, @PathParam("user_id") String userId, @PathParam("name") String name) {
		final User owner = userDAO.findById(userId);
		if (owner == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (isValidUsername(owner, credentials.getUsername())) {
			Document doc = documentDAO.findByOwnerAndName(owner, name);
			if (doc == null) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			try {
				final byte[] body = doc.decryptBodyForOwner(credentials.getPassword().toCharArray());
				return Response.ok()
						.entity(body)
						.type(doc.getContentType())
						.cacheControl(CACHE_SETTINGS)
						.lastModified(doc.getModifiedAt().toDate())
						.build();
			} catch (CryptographicException e) {
				return Credentials.CHALLENGE;
			}
		}
		
		return Credentials.CHALLENGE;
	}
	
	@DELETE
	@Transactional
	public Response delete(@Context Credentials credentials, @PathParam("user_id") String userId, @PathParam("name") String name) {
		final User owner = userDAO.findById(userId);
		if (owner == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (isValidUsername(owner, credentials.getUsername())) {
			Document doc = documentDAO.findByOwnerAndName(owner, name);
			if (doc == null) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			try {
				doc.decryptBodyForOwner(credentials.getPassword().toCharArray());
				documentDAO.delete(doc);
				return Response.noContent().build();
			} catch (CryptographicException e) {
				return Credentials.CHALLENGE;
			}
		}
		
		return Credentials.CHALLENGE;
	}
	
	
	@PUT
	@Transactional
	public Response create(@Context HttpHeaders headers, @Context Credentials credentials, @PathParam("user_id") String userId, @PathParam("name") String name, byte[] body) {
		final User owner = userDAO.findById(userId);
		if (owner == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (isValidUsername(owner, credentials.getUsername())) {
			Document doc = documentDAO.findByOwnerAndName(owner, name);
			if (doc == null) {
				doc = documentDAO.newDocument(owner, name, headers.getMediaType());
			}
			
			try {
				doc.encryptAndSetBody(credentials.getPassword().toCharArray(), ImmutableList.<KeySet>of(), randomProvider.get(), body);
			} catch (CryptographicException e) {
				return Credentials.CHALLENGE;
			}
			
			documentDAO.saveOrUpdate(doc);
			
			return Response.noContent().build();
		}
		
		return Credentials.CHALLENGE;
	}
	
	private boolean isValidUsername(User user, String username) {
		return user.getId().equals(username);
	}
	
}
