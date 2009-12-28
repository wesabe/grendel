package com.wesabe.grendel.entities;

import static com.google.common.base.Objects.equal;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.MessageReader;
import com.wesabe.grendel.openpgp.MessageWriter;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.util.HashCode;

@Entity
@Table(name="documents")
@IdClass(DocumentReference.class)
@NamedQueries({
	@NamedQuery(
		name="com.wesabe.grendel.entities.Document.Exists",
		query="SELECT d.id FROM Document AS d WHERE d.name = :name AND d.owner = :owner"
	),
	@NamedQuery(
		name="com.wesabe.grendel.entities.Document.ForUser",
		query="SELECT d FROM Document AS d WHERE d.owner = :owner"
	)
})
public class Document implements Serializable {
	private static final long serialVersionUID = 5699449595549234402L;

	@Id
	private String name;
	
	@Id
	private User owner;
	
	@Column(name="content_type", nullable=false, length=40)
	private String contentType;
	
	@Column(name="body", nullable=false)
	@Lob
	private byte[] body;
	
	@Column(name="created_at", nullable=false)
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	@Column(name="modified_at", nullable=false)
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime modifiedAt;
	
	@Deprecated
	public Document() {
		// for Hibernate usage only
	}
	
	public Document(User owner, String name) {
		this.owner = owner;
		this.name = name;
		
		this.createdAt = new DateTime(DateTimeZone.UTC);
		this.modifiedAt = new DateTime(DateTimeZone.UTC);
	}
	
	public User getOwner() {
		return owner;
	}
	
	public String getName() {
		return name;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	
	public void encryptAndSetBody(char[] ownerPassphrase, List<KeySet> recipients, SecureRandom random, byte[] body) throws CryptographicException {
		final UnlockedKeySet ownerKeySet = owner.getKeySet().unlock(ownerPassphrase);
		final MessageWriter writer = new MessageWriter(ownerKeySet, recipients, random);
		this.body = writer.write(body);
	}
	
	private byte[] decryptBody(UnlockedKeySet unlockedKeySet) throws CryptographicException {
		final MessageReader reader = new MessageReader(owner.getKeySet(), unlockedKeySet);
		return reader.read(body);
	}
	
	public byte[] decryptBodyForOwner(char[] ownerPassphrase) throws CryptographicException {
		return decryptBody(owner.getKeySet().unlock(ownerPassphrase));
	}
	
	public byte[] decryptBodyForRecipient(KeySet recipient, char[] passphrase) throws CryptographicException {
		return decryptBody(recipient.unlock(passphrase));
	}
	
	private DateTime toUTC(DateTime dateTime) {
		return dateTime.toDateTime(DateTimeZone.UTC);
	}

	@Override
	public int hashCode() {
		return HashCode.calculate(getClass(), body, contentType, createdAt, modifiedAt, name, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Document)) {
			return false;
		}
		
		final Document that = (Document) obj;
		return equal(name, that.name) && equal(owner, that.owner) &&
				equal(body, that.body) && equal(createdAt, that.createdAt) &&
				equal(contentType, that.contentType) &&
				equal(modifiedAt, that.modifiedAt);
	}
}
