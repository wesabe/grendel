package com.wesabe.grendel.entities;

import static com.google.common.base.Objects.*;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Set;

import javax.persistence.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Sets;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.MessageReader;
import com.wesabe.grendel.openpgp.MessageWriter;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.util.HashCode;

/**
 * A document with an abritrary body, stored as an encrypted+signed OpenPGP
 * message.
 * 
 * @author coda
 */
@Entity
@Table(name="documents")
@IdClass(DocumentPK.class)
@NamedQueries({
	@NamedQuery(
		name="com.wesabe.grendel.entities.Document.ByOwnerAndName",
		query="SELECT d FROM Document AS d WHERE d.name = :name AND d.owner = :owner"
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
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="linkedDocuments", cascade={CascadeType.ALL})
	@JoinTable(name="links")
	private Set<User> linkedUsers = Sets.newHashSet();
	
	@Version
	@Column(name="version", nullable=false)
	private long version = 0;
	
	@Deprecated
	public Document() {
		// for Hibernate usage only
	}
	
	/**
	 * Creates a new {@link Document}, owned by the given {@link User} and with
	 * the given name.
	 * 
	 * @param owner the new document's owner
	 * @param name the new document's name
	 * @param contentType the document's content type
	 */
	public Document(User owner, String name, MediaType contentType) {
		this.owner = owner;
		this.name = name;
		this.contentType = contentType.toString();
		
		this.createdAt = new DateTime(DateTimeZone.UTC);
		this.modifiedAt = new DateTime(DateTimeZone.UTC);
	}
	
	/**
	 * Returns the document's owner.
	 */
	public User getOwner() {
		return owner;
	}
	
	/**
	 * Returns the document's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a set of {@link User}s who have read-only access to this
	 * document.
	 */
	public Set<User> getLinkedUsers() {
		return linkedUsers;
	}
	
	/**
	 * Provide a {@link User} with read-only access to this document.
	 */
	public void linkUser(User user) {
		linkedUsers.add(user);
		user.getLinkedDocuments().add(this);
	}
	
	/**
	 * Remove a {@link User}'s read-only access to this document.
	 */
	public void unlinkUser(User user) {
		linkedUsers.remove(user);
		user.getLinkedDocuments().remove(this);
	}
	
	/**
	 * Returns {@code true} if the given {@link User} has read-only access to
	 * this document.
	 */
	public boolean isLinked(User user) {
		return linkedUsers.contains(user);
	}
	
	/**
	 * Returns the document's content type.
	 */
	public MediaType getContentType() {
		return MediaType.valueOf(contentType);
	}
	
	/**
	 * Returns a UTC timestamp of when this document was created.
	 */
	public DateTime getCreatedAt() {
		return toUTC(createdAt);
	}
	
	/**
	 * Sets a UTC timestamp of when this document was created.
	 */
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = toUTC(createdAt);
	}
	
	/**
	 * Returns a UTC timestamp of when this document was last modified.
	 */
	public DateTime getModifiedAt() {
		return toUTC(modifiedAt);
	}
	
	/**
	 * Sets a UTC timestamp of when this document was last modified.
	 */
	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = toUTC(modifiedAt);
	}
	
	/**
	 * Sets the {@link Document}'s body to an encrypted+signed OpenPGP message
	 * containing {@code body}.
	 * 
	 * @param keySet
	 *            the {@link UnlockedKeySet} of the {@link User} that owns this
	 *            {@link Document}
	 * @param random
	 *            a {@link SecureRandom} instance
	 * @param body
	 *            the unencrypted document body
	 * @throws CryptographicException
	 *             if the owner's {@link KeySet} cannot be unlocked with {@code
	 *             ownerPassphrase}
	 * @see MessageWriter
	 */
	public void encryptAndSetBody(UnlockedKeySet keySet, SecureRandom random,
		byte[] body) throws CryptographicException {
		
		final Set<KeySet> recipients = Sets.newHashSetWithExpectedSize(linkedUsers.size());
		for (User linkedUser : linkedUsers) {
			recipients.add(linkedUser.getKeySet());
		}
		
		final MessageWriter writer = new MessageWriter(keySet, recipients, random);
		this.body = writer.write(body);
	}
	
	/**
	 * Decrypts the document's body using the {@link UnlockedKeySet} of the
	 * owner or a recipient;
	 * 
	 * @param unlockedKeySet
	 *             an {@link UnlockedKeySet} belonging to either the
	 *             {@link Document}'s owner or a recipient
	 * @return the decrypted document body
	 * @throws CryptographicException
	 *             if there is an error decrypting and verifying the
	 *             encrypted+signed OpenPGP message
	 * @see MessageReader
	 */
	public byte[] decryptBody(UnlockedKeySet unlockedKeySet) throws CryptographicException {
		final MessageReader reader = new MessageReader(owner.getKeySet(), unlockedKeySet);
		return reader.read(body);
	}
	
	private DateTime toUTC(DateTime dateTime) {
		return dateTime.toDateTime(DateTimeZone.UTC);
	}

	@Override
	public int hashCode() {
		return HashCode.calculate(
			getClass(), body, contentType, createdAt, modifiedAt, name, owner
		);
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
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns an opaque string indicating the {@link Document}'s name and
	 * version.
	 */
	public String getEtag() {
		return new StringBuilder("doc-").append(name).append('-').append(version).toString();
	}
}
