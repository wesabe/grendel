package com.wesabe.grendel.entities.dao;

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
	
	public Document findByOwnerAndName(User owner, String name) {
		return uniqueResult(
			namedQuery("com.wesabe.grendel.entities.Document.ByOwnerAndName")
			.setParameter("owner", owner)
			.setString("name", name)
		);
	}
	
	public Document saveOrUpdate(Document doc) {
		currentSession().saveOrUpdate(doc);
		return doc;
	}

	public void delete(Document doc) {
		currentSession().delete(doc);
	}
	
}
