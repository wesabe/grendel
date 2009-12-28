package com.wesabe.grendel.representations;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.resources.UserResource;

public class UserInfoRepresentation {
	private static final DateTimeFormatter ISO_DATETIME = ISODateTimeFormat.basicDateTimeNoMillis();
	
	public static class DocumentLink {
		private final String name;
		private final URI uri;
		
		public DocumentLink(String name, URI uri) {
			this.name = name;
			this.uri = uri;
		}
		
		@JsonGetter("name")
		public String getName() {
			return name;
		}
		
		@JsonGetter("uri")
		public String getUri() {
			return uri.toASCIIString();
		}
		
	}
	
	private final User user;
	private final UriInfo uriInfo;
	
	public UserInfoRepresentation(UriInfo uriInfo, User user) {
		this.user = user;
		this.uriInfo = uriInfo;
	}
	
	@JsonGetter("id")
	public String getId() {
		return user.getId();
	}
	
	@JsonGetter("created-at")
	public String getCreatedAt() {
		return ISO_DATETIME.print(user.getCreatedAt());
	}
	
	@JsonGetter("modified-at")
	public String getModifiedAt() {
		return ISO_DATETIME.print(user.getModifiedAt());
	}
	
	@JsonGetter("keys")
	public String getKeys() {
		return user.getKeySet().toString();
	}
	
	@JsonGetter("documents")
	public List<DocumentLink> getDocuments() {
		final List<DocumentLink> uris = Lists.newArrayListWithCapacity(user.getDocuments().size());
		for (Document doc : user.getDocuments()) {
			uris.add(new DocumentLink(
				doc.getName(),
				// FIXME coda@wesabe.com -- Dec 28, 2009: replace this when DocumentResource is done
				uriInfo.getBaseUriBuilder().path(UserResource.class).path("documents/"+doc.getName()).build(user.getId())
			));
		}
		return uris;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}
	
	@JsonIgnore
	public UriInfo getUriInfo() {
		return uriInfo;
	}
}
