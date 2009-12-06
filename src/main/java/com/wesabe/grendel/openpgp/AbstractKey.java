package com.wesabe.grendel.openpgp;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.util.IntegerEquivalents;
import com.wesabe.grendel.util.Iterators;

/**
 * An abstract base class for asymmetric PGP public keys and their corresponding
 * secret keys.
 * 
 * @author coda
 */
public abstract class AbstractKey {
	protected final PGPSecretKey secretKey;
	protected final PGPPublicKey publicKey;
	protected final KeySignature signature;
	protected final Set<KeyFlag> flags;
	
	/**
	 * Instatiates a new {@link AbstractKey}.
	 * 
	 * @param key the PGP secret key, with public key included
	 * @param signingKey the signing key
	 * @param requiredSignatureType the type of signature required
	 */
	protected AbstractKey(PGPSecretKey key, PGPSecretKey signingKey, SignatureType requiredSignatureType) {
		this.secretKey = key;
		this.publicKey = secretKey.getPublicKey();
		this.signature = getSignature(signingKey, requiredSignatureType);
		if (signature == null) {
			this.flags = ImmutableSet.of();
		} else {
			this.flags = signature.getKeyFlags();
		}
	}
	
	/**
	 * Given the key's passphrase, unlocks the secret key and returns an
	 * {@link UnlockedKey} equivalent of {@code this}.
	 * 
	 * @param passphrase the key's passphrase
	 * @return a {@link UnlockedKey} equivalent of {@code this}
	 * @throws CryptographicException if {@code passphrase} is incorrect
	 */
	public abstract UnlockedKey unlock(char[] passphrase) throws CryptographicException;
	
	/**
	 * Returns this key's public key component.
	 */
	/* default */ PGPPublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * Returns this key's secret key component.
	 * @return
	 */
	/* default */ PGPSecretKey getSecretKey() {
		return secretKey;
	}
	
	/**
	 * Returns this key's user ID, usually in the form of
	 * {@code First Last <email@example.com>}.
	 */
	public String getUserID() {
		return getUserIDs().get(0);
	}
	
	/**
	 * Returns a list of all user IDs attached to this key.
	 * 
	 * @see #getUserID()
	 */
	public List<String> getUserIDs() {
		return Iterators.toList(secretKey.getUserIDs());
	}
	
	/**
	 * Returns the key's ID.
	 */
	public long getKeyID() {
		return secretKey.getKeyID();
	}
	
	/**
	 * Returns a human-readable version of {@link #getKeyID()}.
	 * 
	 * <b>N.B.:</b> This returns a truncated version of the key ID.
	 */
	public String getHumanKeyID() {
		return String.format("%08X", (int) secretKey.getKeyID());
	}
	
	/**
	 * Returns the key's {@link AsymmetricAlgorithm}.
	 */
	public AsymmetricAlgorithm getAlgorithm() {
		return IntegerEquivalents.fromInt(
			AsymmetricAlgorithm.class,
			publicKey.getAlgorithm()
		);
	}
	
	/**
	 * Returns the key's size, in bits.
	 */
	public int getSize() {
		return publicKey.getBitStrength();
	}
	
	/**
	 * Returns {@code true} if this key can be used to encrypt or decrypt data.
	 */
	public boolean canEncrypt() {
		return flags.contains(KeyFlag.ENCRYPTION);
	}
	
	/**
	 * Returns {@code true} if this key can be used to sign or verify data.
	 */
	public boolean canSign() {
		return flags.contains(KeyFlag.SIGNING);
	}
	
	/**
	 * Returns the date and time at which the key was created.
	 */
	public DateTime getCreatedAt() {
		return new DateTime(publicKey.getCreationTime(), DateTimeZone.UTC);
	}
	
	/**
	 * Returns a list of preferred {@link SymmetricAlgorithm}s for this key.
	 */
	public List<SymmetricAlgorithm> getPreferredSymmetricAlgorithms() {
		return signature.getPreferredSymmetricAlgorithms();
	}
	
	/**
	 * Returns a list of preferred {@link CompressionAlgorithm}s for this key.
	 */
	public List<CompressionAlgorithm> getPreferredCompressionAlgorithms() {
		return signature.getPreferredCompressionAlgorithms();
	}
	
	/**
	 * Returns a list of preferred {@link HashAlgorithm}s for this key.
	 */
	public List<HashAlgorithm> getPreferredHashAlgorithms() {
		return signature.getPreferredHashAlgorithms();
	}
	
	@Override
	public String toString() {
		return String.format(
			"%d-%s/%s %s",
			getSize(), getAlgorithm(), getHumanKeyID(), getUserID()
		);
	}
	
	/**
	 * Returns a list of {@link KeyFlag}s associated with this key.
	 */
	public Set<KeyFlag> getKeyFlags() {
		return flags;
	}
	
	private KeySignature getSignature(PGPSecretKey signingKey, SignatureType requiredSignatureType) {
		final Iterator<?> signatures = publicKey.getSignatures();
		while (signatures.hasNext()) {
			final KeySignature signature = new KeySignature((PGPSignature) signatures.next());
			if ((signature.getKeyID() == signingKey.getKeyID())
				&& (signature.getSignatureType() == requiredSignatureType)) {
				return signature;
			}
		}
		return null;
	}
}
