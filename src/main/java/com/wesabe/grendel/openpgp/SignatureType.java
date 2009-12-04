package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPSignature;

import com.wesabe.grendel.util.IntegerEquivalent;

/**
 * A type of signature in an OpenPGP message.
 * 
 * @author coda
 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 5.2.1, RFC 4880</a>
 */
public enum SignatureType implements IntegerEquivalent {
	/**
	 * A signature of a binary document.
	 * 
	 * This means the signer owns it, created it, or certifies that it has not
	 * been modified.
	 */
	BINARY_DOCUMENT(		 	"binary document",			PGPSignature.BINARY_DOCUMENT),
	
	/**
	 * A signature of a canonical text document.
	 * 
	 * This means the signer owns it, created it, or certifies that it has not
	 * been modified.  The signature is calculated over the text data with its
	 * line endings converted to {@code 0x0D 0x0A} ({@code CR+LF}).
	 */
	TEXT_DOCUMENT(			 	"text document",			PGPSignature.CANONICAL_TEXT_DOCUMENT),
	
	/**
	 * A signature of only its own subpacket contents.
	 */
	STANDALONE(				 	"standalone",				PGPSignature.STAND_ALONE),
	
	/**
	 * A signature indicating the signer does not make any particular assertion
	 * as to how well the signer has checked that the owner of the key is in
	 * fact the person described by the User ID.
	 */
	DEFAULT_CERTIFICATION(	 	"default certification",	PGPSignature.DEFAULT_CERTIFICATION),
	
	/**
	 * A signature indicating the signer has not done any verification of
	 * the signed key's claim of identity.
	 */
	NO_CERTIFICATION(		 	"no certification",			PGPSignature.NO_CERTIFICATION),
	
	/**
	 * A signature indicating the signer has done some casual verification of
	 * the signed key's claim of identity.
	 */
	CASUAL_CERTIFICATION(	 	"casual certification",		PGPSignature.CASUAL_CERTIFICATION),
	
	/**
	 * A signature indicating the signer has done substantial verification of
	 * the signed key's claim of identity.
	 */
	POSITIVE_CERTIFICATION(	 	"positive certification",	PGPSignature.POSITIVE_CERTIFICATION),
	
	/**
	 * A signature by the top-level signing key indicating that it owns the
	 * signed subkey.
	 */
	SUBKEY_BINDING(			 	"subkey binding",			PGPSignature.SUBKEY_BINDING),
	
	/**
	 * A signature by a signing subkey, indicating that it is owned by the
	 * signed primary key.
	 */
	PRIMARY_KEY_BINDING(	 	"primary key binding",		PGPSignature.PRIMARYKEY_BINDING),
	
	/**
	 * A signature calculated directly on a key.
	 * 
	 * It binds the information in the Signature subpackets to the key, and is
	 * appropriate to be used for subpackets that provide information about the
	 * key, such as the Revocation Key subpacket.  It is also appropriate for
	 * statements that non-self certifiers want to make about the key itself,
	 * rather than the binding between a key and a name.
	 */
	DIRECT_KEY(				 	"direct key",				PGPSignature.DIRECT_KEY),
	
	/**
	 * A signature calculated directly on the key being revoked.
	 * 
	 * A revoked key is not to be used.  Only revocation signatures by the key
	 * being revoked, or by an authorized revocation key, should be considered
	 * valid revocation signatures.
	 */
	KEY_REVOCATION(				"key revocation",			PGPSignature.KEY_REVOCATION),
	
	/**
	 * A signature calculated directly on the subkey being revoked.
	 * 
	 * A revoked subkey is not to be used.  Only revocation signatures by the
	 * top-level signature key that is bound to this subkey, or by an authorized
	 * revocation key, should be considered valid revocation signatures.
	 */
	SUBKEY_REVOCATION(			"subkey revocation", 		PGPSignature.SUBKEY_REVOCATION),
	
	/**
	 * A signature revoking an earlier {@link #DEFAULT_CERTIFICATION},
	 * {@link #NO_CERTIFICATION}, {@link #CASUAL_CERTIFICATION},
	 * {@link #POSITIVE_CERTIFICATION} or {@link #DIRECT_KEY} signature.
	 */
	CERTIFICATION_REVOCATION(	"certificate revocation",	PGPSignature.CERTIFICATION_REVOCATION),
	
	/**
	 * A timestamp signature.
	 * 
	 * This signature is only meaningful for the timestamp contained in it.
	 */
	TIMESTAMP(					"timestamp", 				PGPSignature.TIMESTAMP),
	
	/**
	 * A signature over some other OpenPGP Signature packet(s).
	 * 
	 * It is analogous to a notary seal on the signed data.
	 */
	// this value isn't included as a constant in PGPSignature
	THIRD_PARTY(				"third-party confirmation",	0x50);
	
	private final String name;
	private final int value;
	
	private SignatureType(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int toInteger() {
		return value;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
