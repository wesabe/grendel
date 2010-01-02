package com.wesabe.grendel.resources;

import java.security.SecureRandom;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wideplay.warp.persist.Transactional;

@Path("/users/{user_id}/documents/{name}/links/{reader_id}")
public class LinkResource {
	private final UserDAO userDAO;
	private final DocumentDAO documentDAO;
	private final Provider<SecureRandom> randomProvider;
	
	@Inject
	public LinkResource(UserDAO userDAO, DocumentDAO documentDAO, Provider<SecureRandom> randomProvider) {
		this.userDAO = userDAO;
		this.documentDAO = documentDAO;
		this.randomProvider = randomProvider;
	}
	
	@PUT
	@Transactional
	public Response createLink(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name,
		@PathParam("reader_id") String readerId) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final User reader = findUser(readerId);
		final Document doc = findDocument(session.getUser(), name);
		
		doc.linkUser(reader);
		reEncrypt(doc, session.getKeySet());
		
		documentDAO.saveOrUpdate(doc);
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Transactional
	public Response deleteLink(@Context Credentials credentials,
		@PathParam("user_id") String userId, @PathParam("name") String name,
		@PathParam("reader_id") String readerId) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		final User reader = findUser(readerId);
		final Document doc = findDocument(session.getUser(), name);
		
		doc.unlinkUser(reader);
		reEncrypt(doc, session.getKeySet());
		
		documentDAO.saveOrUpdate(doc);
		
		return Response.noContent().build();
	}

	private void reEncrypt(Document doc, UnlockedKeySet ownerKeySet) {
		try {
			final byte[] body = doc.decryptBody(ownerKeySet);
			doc.encryptAndSetBody(
				ownerKeySet,
				randomProvider.get(),
				body
			);
		} catch (CryptographicException e) {
			throw new RuntimeException(e);
		}
	}

	private Document findDocument(User owner, String name) {
		final Document doc = documentDAO.findByOwnerAndName(owner, name);
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return doc;
	}

	private User findUser(String id) {
		final User reader = userDAO.findById(id);
		if (reader == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return reader;
	}
}
