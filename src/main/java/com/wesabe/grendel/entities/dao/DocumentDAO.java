package com.wesabe.grendel.entities.dao;

import javax.ws.rs.core.MediaType;

import org.hibernate.Session;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;

public class DocumentDAO extends AbstractDAO<Document> {
	
	@Inject
	public DocumentDAO(Provider<Session> provider) {
		super(provider, Document.class);
	}
	
	/**
	 * Returns a new {@link Document} with the provided owner, name, and
	 * content-type.
	 */
	public Document newDocument(User owner, String name, MediaType contentType) {
		return new Document(owner, name, contentType);
	}
	
	/**
	 * Finds a {@link Document} instance with a given owner and name. Returns
	 * {@code null} if the {@link Document} does not exist.
	 */
	public Document findByOwnerAndName(User owner, String name) {
		return uniqueResult(
			namedQuery("com.wesabe.grendel.entities.Document.ByOwnerAndName")
			.setParameter("owner", owner)
			.setString("name", name)
		);
	}
	
	/**
	 * Writes the {@link Document} to the database.
	 * 
	 * @see Session#saveOrUpdate(Object)
	 */
	public Document saveOrUpdate(Document doc) {
		currentSession().saveOrUpdate(doc);
		return doc;
	}

	/**
	 * Deletes the {@link Document} from the database.
	 */
	public void delete(Document doc) {
		currentSession().delete(doc);
	}
	
}
