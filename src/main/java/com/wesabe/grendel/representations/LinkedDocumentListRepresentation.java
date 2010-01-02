package com.wesabe.grendel.representations;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.representations.UserListRepresentation.UserListItem;
import com.wesabe.grendel.resources.LinkedDocumentResource;

public class LinkedDocumentListRepresentation {
	public static class DocumentListItem {
		private final UriInfo uriInfo;
		private final User user;
		private final Document document;
		
		public DocumentListItem(UriInfo uriInfo, User user, Document document) {
			this.uriInfo = uriInfo;
			this.user = user;
			this.document = document;
		}
		
		@JsonGetter("name")
		public String getName() {
			return document.getName();
		}
		
		@JsonGetter("owner")
		public UserListItem getOwner() {
			return new UserListItem(uriInfo, document.getOwner());
		}
		
		@JsonGetter("uri")
		public String getURI() {
			return uriInfo.getBaseUriBuilder()
							.path(LinkedDocumentResource.class)
							.build(user, document.getOwner(), document)
							.toASCIIString();
		}
	}
	
	private final UriInfo uriInfo;
	private final User user;
	
	public LinkedDocumentListRepresentation(UriInfo uriInfo, User user) {
		this.uriInfo = uriInfo;
		this.user = user;
	}
	
	@JsonGetter("linked-documents")
	public List<DocumentListItem> listDocuments() {
		final List<DocumentListItem> items = Lists.newArrayList();
		for (Document doc : user.getLinkedDocuments()) {
			items.add(new DocumentListItem(uriInfo, user, doc));
		}
		return items;
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
