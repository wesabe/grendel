package com.wesabe.grendel.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;

@Entity
@Table(name="users")
@NamedQueries({
	@NamedQuery(
		name="com.wesabe.grendel.entities.User.Exists",
		query="SELECT u.id FROM User AS u WHERE u.id = :id"
	)
})
public class User {
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="keyset", nullable=false)
	@Lob
	private byte[] encodedKeySet;
	
	@Transient
	private KeySet keySet = null;
	
	@Column(name="created_at", nullable=false, columnDefinition="datetime")
	private DateTime createdAt;
	
	@Column(name="modified_at", nullable=false, columnDefinition="datetime")
	private DateTime modifiedAt;
	
	@Deprecated
	public User() {
		// blank constructor to be used by Hibernate
	}
	
	public User(KeySet keySet) {
		setKeySet(keySet);
		this.createdAt = new DateTime(DateTimeZone.UTC);
		this.modifiedAt = new DateTime(DateTimeZone.UTC);
	}
	
	public String getId() {
		return id;
	}
	
	public KeySet getKeySet() {
		if (keySet == null) {
			try {
				this.keySet = KeySet.load(encodedKeySet);
			} catch (CryptographicException e) {
				throw new RuntimeException(e);
			}
		}
		return keySet;
	}
	
	public void setKeySet(KeySet keySet) {
		this.keySet = keySet;
		this.id = keySet.getUserID();
		this.encodedKeySet = keySet.getEncoded();
	}
	
	public byte[] getEncodedKeySet() {
		return encodedKeySet;
	}
	
	public void setEncodedKeySet(byte[] encodedKeySet) {
		this.encodedKeySet = encodedKeySet;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public DateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public DateTime getModifiedAt() {
		return modifiedAt;
	}
	
	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
}
