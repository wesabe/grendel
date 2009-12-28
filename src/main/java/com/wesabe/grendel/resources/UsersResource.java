package com.wesabe.grendel.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.representations.CreateUserRepresentation;
import com.wesabe.grendel.representations.UserListRepresentation;
import com.wesabe.grendel.representations.ValidationException;
import com.wideplay.warp.persist.Transactional;

@Path("/users/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
	private final KeySetGenerator generator;
	private final UserDAO userDAO;
	
	@Inject
	public UsersResource(KeySetGenerator generator, UserDAO userDAO) {
		this.generator = generator;
		this.userDAO = userDAO;
	}
	
	@GET
	public UserListRepresentation list(@Context UriInfo uriInfo) {
		final List<User> users = userDAO.findAll();
		return new UserListRepresentation(uriInfo, users);
	}
	
	@POST
	@Transactional
	public Response create(@Context UriInfo uriInfo, CreateUserRepresentation request) throws CryptographicException {
		request.validate();
		
		if (userDAO.contains(request.getId())) {
			final ValidationException e = new ValidationException();
			e.addReason("username is already taken");
			throw e;
		}

		final KeySet keySet = generator.generate(request.getId(), request.getPassword());
		final User user = userDAO.create(new User(keySet));
		
		request.sanitize();
		
		return Response.created(
			// FIXME coda@wesabe.com -- Dec 22, 2009: direct this to where it should go
			uriInfo.getBaseUriBuilder()
						.path(UsersResource.class)
						.build(user.getId())
		).build();
	}
}
