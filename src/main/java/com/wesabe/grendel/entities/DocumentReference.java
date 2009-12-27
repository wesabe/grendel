package com.wesabe.grendel.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A composite primary key for {@link Document}, consisting of an owner (a
 * {@link User}) and a name (a {@link String}.
 * 
 * @author coda
 */
public class DocumentReference implements Serializable {
	private static final long serialVersionUID = -4514388507586009635L;
	
	@Id
	@Column(name="name", nullable=false)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="owner_id", nullable=false)
	private User owner;
	
	@Deprecated
	public DocumentReference() {
		// for Hibernate usage only
	}
	
	public DocumentReference(User owner, String name) {
		this.owner = owner;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public User getOwner() {
		return owner;
	}
}