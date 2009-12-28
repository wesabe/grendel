package com.wesabe.grendel.entities;

import static com.google.common.base.Objects.*;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Sets;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.util.HashCode;

@Entity
@Table(name="users")
@NamedQueries({
	@NamedQuery(
		name="com.wesabe.grendel.entities.User.Exists",
		query="SELECT u.id FROM User AS u WHERE u.id = :id"
	),
	@NamedQuery(
		name="com.wesabe.grendel.entities.User.All",
		query="SELECT u FROM User AS u ORDER BY u.id"
	)
})
public class User implements Serializable {
	private static final long serialVersionUID = -8270919660085011028L;

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="keyset", nullable=false)
	@Lob
	private byte[] encodedKeySet;
	
	@Transient
	private KeySet keySet = null;
	
	@Column(name="created_at", nullable=false)
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	@Column(name="modified_at", nullable=false)
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime modifiedAt;
	
	// FIXME coda@wesabe.com -- Dec 27, 2009: User#documents double-loads document primary keys.
	// This may be a bug in Hibernate, but the SQL this generates produces
	// queries which look like this:
	//    documents0_.owner_id as owner6_1_,
	//    documents0_.name as name1_,
	//    documents0_.name as name1_0_,
	//    documents0_.owner_id as owner6_1_0_,
	//    documents0_.body as body1_0_,
	//    documents0_.content_type as content3_1_0_,
	//    documents0_.created_at as created4_1_0_,
	//    documents0_.modified_at as modified5_1_0_
	//
	// I've tried just about every approach to composite keys in Hibernate that
	// I found, and none of them changed this behavior.
	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<Document> documents = Sets.newHashSet();
	
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
		return toUTC(createdAt);
	}
	
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = toUTC(createdAt);
	}
	
	public DateTime getModifiedAt() {
		return toUTC(modifiedAt);
	}
	
	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = toUTC(modifiedAt);
	}

	public Set<Document> getDocuments() {
		return documents;
	}
	
	private DateTime toUTC(DateTime dateTime) {
		return dateTime.toDateTime(DateTimeZone.UTC);
	}

	@Override
	public int hashCode() {
		return HashCode.calculate(createdAt, encodedKeySet, id, modifiedAt);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof User)) {
			return false;
		}
		
		final User that = (User) obj;
		return equal(id, that.id) && equal(encodedKeySet, that.encodedKeySet) &&
				equal(createdAt, that.createdAt) && equal(modifiedAt, that.modifiedAt) &&
				equal(encodedKeySet, that.encodedKeySet);
	}
}
