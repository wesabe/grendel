package com.wesabe.grendel.entities;

import static com.google.common.base.Objects.equal;

import java.io.Serializable;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.wesabe.grendel.util.HashCode;

/**
 * A composite primary key for {@link Document}, consisting of an owner (a
 * {@link User}) and a name (a {@link String}.
 * 
 * @author coda
 */
public class DocumentPK implements Serializable {
	private static final long serialVersionUID = -4514388507586009635L;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="owner_id", nullable=false)
	private User owner;
	
	@Deprecated
	public DocumentPK() {
		// for Hibernate usage only
	}

	@Override
	public int hashCode() {
		return HashCode.calculate(getClass(), name, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof DocumentPK)) {
			return false;
		}
		
		final DocumentPK that = (DocumentPK) obj;
		return equal(name, that.name) && equal(owner, that.owner);
	}
}