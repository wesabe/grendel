package com.wesabe.grendel.entities.dao;

import org.hibernate.Session;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.entities.User;

public class UserDAO extends AbstractDAO<User> {
	
	@Inject
	public UserDAO(Provider<Session> provider) {
		super(provider, User.class);
	}
	
	public boolean contains(String id) {
		return namedQuery("com.wesabe.grendel.entities.User.Exists")
					.setString("id", id)
					.uniqueResult() != null;
	}
	
	public User findById(String id) {
		return get(id);
	}
	
	public User create(User user) {
		currentSession().save(user);
		return user;
	}

	public void delete(User user) {
		currentSession().delete(user);
	}

}
