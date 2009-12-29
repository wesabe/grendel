package com.wesabe.grendel.entities.dao;

import java.util.List;

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
	
	/**
	 * Returns {@code true} if a user already exists with the given id.
	 */
	public boolean contains(String id) {
		return namedQuery("com.wesabe.grendel.entities.User.Exists")
					.setString("id", id)
					.uniqueResult() != null;
	}
	
	/**
	 * Returns the {@link User} with the given id, or {@code null} if the user
	 * does not exist.
	 */
	public User findById(String id) {
		return get(id);
	}
	
	/**
	 * Returns a list of all {@link User}s.
	 */
	public List<User> findAll() {
		return list(namedQuery("com.wesabe.grendel.entities.User.All"));
	}
	
	/**
	 * Writes the {@link User} to the database.
	 * 
	 * @see Session#saveOrUpdate(Object)
	 */
	public User saveOrUpdate(User user) {
		currentSession().saveOrUpdate(user);
		return user;
	}

	/**
	 * Deletes the {@link User} from the database.
	 */
	public void delete(User user) {
		currentSession().delete(user);
	}
}
