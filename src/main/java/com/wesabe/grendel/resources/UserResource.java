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
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.representations.UpdateUserRepresentation;
import com.wesabe.grendel.representations.UserInfoRepresentation;
import com.wideplay.warp.persist.Transactional;

/**
 * A resource for managing individual {@link User}s.
 * 
 * @author coda
 */
@Path("/users/{id}")
public class UserResource {
	private final UserDAO userDAO;
	private final Provider<SecureRandom> randomProvider;

	@Inject
	public UserResource(UserDAO userDAO, Provider<SecureRandom> randomProvider) {
		this.userDAO = userDAO;
		this.randomProvider = randomProvider;
	}
	
	/**
	 * Responds to a {@link GET} request with information about the specified
	 * user.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfoRepresentation show(@Context UriInfo uriInfo, @PathParam("id") String id) {
		final User user = userDAO.findById(id);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return new UserInfoRepresentation(uriInfo, user);
	}
	
	/**
	 * Responds to a {@link PUT} request by changing the user's password.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 * @throws CryptographicException
	 */
	@PUT
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context Credentials credentials,
		@PathParam("id") String id, UpdateUserRepresentation request) throws CryptographicException {
		
		request.validate();
		
		final Session session = credentials.buildSession(userDAO, id);
		final User user = session.getUser();
		final UnlockedKeySet keySet = session.getKeySet();
		
		user.setKeySet(
			keySet.relock(
				credentials.getPassword().toCharArray(),
				request.getPassword(),
				randomProvider.get()
			)
		);
		
		user.setModifiedAt(new DateTime());
		userDAO.saveOrUpdate(user);
		
		return Response.noContent().build();
	}
	
	/**
	 * Responds to a {@link DELETE} request by deleting the user <strong>and
	 * all their {@link Document}s.</strong>
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 */
	@DELETE
	@Transactional
	public Response delete(@Context UriInfo uriInfo,
		@Context Credentials credentials, @PathParam("id") String id) {
		
		final Session session = credentials.buildSession(userDAO, id);
		userDAO.delete(session.getUser());
		return Response.noContent().build();
	}
}
