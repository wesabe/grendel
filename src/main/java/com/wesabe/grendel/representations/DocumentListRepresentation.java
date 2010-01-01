package com.wesabe.grendel.representations;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.resources.DocumentResource;

public class DocumentListRepresentation {
	public static class DocumentListItem {
		private final UriInfo uriInfo;
		private final Document document;
		
		public DocumentListItem(UriInfo uriInfo, Document document) {
			this.uriInfo = uriInfo;
			this.document = document;
		}
		
		@JsonGetter("name")
		public String getName() {
			return document.getName();
		}
		
		@JsonGetter("uri")
		public String getURI() {
			return uriInfo.getBaseUriBuilder()
							.path(DocumentResource.class)
							.build(document.getOwner(), document)
							.toASCIIString();
		}
	}
	
	private UriInfo uriInfo;
	private Set<Document> documents;
	
	public DocumentListRepresentation(UriInfo uriInfo, Set<Document> documents) {
		this.uriInfo = uriInfo;
		this.documents = documents;
	}
	
	@JsonGetter("documents")
	public List<DocumentListItem> listDocuments() {
		final List<DocumentListItem> items = Lists.newArrayListWithCapacity(documents.size());
		for (Document doc : documents) {
			items.add(new DocumentListItem(uriInfo, doc));
		}
		return items;
	}
	
	@JsonIgnore
	public Set<Document> getDocuments() {
		return documents;
	}
	
	@JsonIgnore
	public UriInfo getUriInfo() {
		return uriInfo;
	}
}
