package com.wesabe.grendel.representations;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.annotate.JsonGetter;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.resources.UserResource;

public class UserListRepresentation {
	public static class UserListItem {
		private final String id;
		private final URI uri;
		
		public UserListItem(String id, URI uri) {
			this.id = id;
			this.uri = uri;
		}
		
		@JsonGetter("id")
		public String getId() {
			return id;
		}
		
		@JsonGetter("uri")
		public String getUri() {
			return uri.toASCIIString();
		}
	}
	
	private final UriInfo uriInfo;
	private final List<User> users;
	
	public UserListRepresentation(UriInfo uriInfo, List<User> users) {
		this.uriInfo = uriInfo;
		this.users = users;
	}
	
	@JsonGetter("users")
	public List<UserListItem> getUsers() {
		final List<UserListItem> items = Lists.newArrayListWithExpectedSize(users.size());
		for (User user : users) {
			items.add(new UserListItem(
				user.getId(),
				uriInfo.getBaseUriBuilder()
							.path(UserResource.class)
							.build(user.getId())
			));
		}
		return items;
	}

}
