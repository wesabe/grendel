package com.wesabe.grendel.representations;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.representations.UserListRepresentation.UserListItem;
import com.wesabe.grendel.resources.LinkResource;

/**
 * A list of a {@link Document}'s links.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "links":[
 *     {
 *       "user":{
 *         "id":"precipice",
 *         "uri":"http://example.com/users/precipice"
 *       },
 *       "uri":"http://example.com/users/codahale/documents/document1.txt/links/precipice"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author coda
 */
public class LinkListRepresentation {
	public static class LinkListItem {
		private final UriInfo uriInfo;
		private final Document document;
		private final User user;
		
		public LinkListItem(UriInfo uriInfo, Document document, User user) {
			this.uriInfo = uriInfo;
			this.document = document;
			this.user = user;
		}
		
		@JsonGetter("user")
		public UserListItem getUser() {
			return new UserListItem(uriInfo, user);
		}
		
		@JsonGetter("uri")
		public String getUri() {
			return uriInfo.getBaseUriBuilder()
							.path(LinkResource.class)
							.build(document.getOwner(), document, user)
							.toASCIIString();
		}
		
	}
	
	private final UriInfo uriInfo;
	private final Document document;
	
	public LinkListRepresentation(UriInfo uriInfo, Document document) {
		this.uriInfo = uriInfo;
		this.document = document;
	}
	
	@JsonIgnore
	public Document getDocument() {
		return document;
	}
	
	@JsonIgnore
	public UriInfo getUriInfo() {
		return uriInfo;
	}
	
	@JsonGetter("links")
	public List<LinkListItem> getLinks() {
		final List<LinkListItem> links = Lists.newArrayList();
		for (User user : document.getLinkedUsers()) {
			links.add(new LinkListItem(uriInfo, document, user));
		}
		return links;
	}
}
