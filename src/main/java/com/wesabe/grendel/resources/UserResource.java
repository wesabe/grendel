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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.representations.UpdateUserRepresentation;
import com.wesabe.grendel.representations.UserInfoRepresentation;
import com.wideplay.warp.persist.Transactional;

@Path("/users/{id}")
public class UserResource {
	private final UserDAO userDAO;
	private final Provider<SecureRandom> randomProvider;

	@Inject
	public UserResource(UserDAO userDAO, Provider<SecureRandom> randomProvider) {
		this.userDAO = userDAO;
		this.randomProvider = randomProvider;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfoRepresentation show(@Context UriInfo uriInfo, @Context Credentials credentials, @PathParam("id") String id) {
		final User user = findUser(id);
		
		if (isValidUsername(user, credentials.getUsername())) {
			if (isValidPassword(user, credentials.getPassword())) {
				
				return new UserInfoRepresentation(uriInfo, user);
			}
		}
		
		throw new WebApplicationException(Credentials.CHALLENGE);
	}
	
	@PUT
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context Credentials credentials, @PathParam("id") String id, UpdateUserRepresentation request) {
		request.validate();
		
		final User user = findUser(id);
		
		if (isValidUsername(user, credentials.getUsername())) {
			try {
				final UnlockedKeySet keySet = user.getKeySet().unlock(credentials.getPassword().toCharArray());
				user.setKeySet(keySet.relock(credentials.getPassword().toCharArray(), request.getPassword(), randomProvider.get()));
				
				user.setModifiedAt(new DateTime());
				userDAO.saveOrUpdate(user);
				
				return Response.noContent().build();
			} catch (CryptographicException e) {
				throw new WebApplicationException(Credentials.CHALLENGE);
			}
		}
		
		throw new WebApplicationException(Credentials.CHALLENGE);
	}
	
	@DELETE
	@Transactional
	public Response delete(@Context UriInfo uriInfo, @Context Credentials credentials, @PathParam("id") String id) {
		final User user = findUser(id);
		
		if (isValidUsername(user, credentials.getUsername())) {
			if (isValidPassword(user, credentials.getPassword())) {
				userDAO.delete(user);
				
				return Response.noContent().build();
			}
		}
		
		throw new WebApplicationException(Credentials.CHALLENGE);
	}

	private User findUser(String id) {
		final User user = userDAO.findById(id);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return user;
	}
	
	private boolean isValidPassword(User user, String password) {
		try {
			user.getKeySet().unlock(password.toCharArray());
			return true;
		} catch (CryptographicException e) {
			throw new WebApplicationException(Credentials.CHALLENGE);
		}
	}

	private boolean isValidUsername(User user, String username) {
		if (!user.getId().equals(username)) {
			throw new WebApplicationException(Credentials.CHALLENGE);
		}
		
		return true;
	}
}
