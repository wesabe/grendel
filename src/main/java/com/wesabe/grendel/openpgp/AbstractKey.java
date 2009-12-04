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

public abstract class AbstractKey {
	protected final PGPSecretKey secretKey;
	protected final PGPPublicKey publicKey;
	protected final Signature signature;
	protected final Set<KeyFlag> flags;
	
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

	protected Signature getSignature(PGPSecretKey signingKey, SignatureType requiredSignatureType) {
		final Iterator<?> signatures = publicKey.getSignatures();
		while (signatures.hasNext()) {
			final Signature signature = new Signature((PGPSignature) signatures.next());
			if ((signature.getKeyID() == signingKey.getKeyID())
				&& (signature.getSignatureType() == requiredSignatureType)) {
				return signature;
			}
		}
		return null;
	}
	
	/* default */ PGPPublicKey getPublicKey() {
		return publicKey;
	}
	
	/* default */ PGPSecretKey getSecretKey() {
		return secretKey;
	}
	
	public String getUserID() {
		return getUserIDs().get(0);
	}
	
	public List<String> getUserIDs() {
		return Iterators.toList(secretKey.getUserIDs());
	}

	public long getKeyID() {
		return secretKey.getKeyID();
	}
	
	public String getHumanKeyID() {
		return Integer.toHexString((int) secretKey.getKeyID()).toUpperCase();
	}
	
	public AsymmetricAlgorithm getAlgorithm() {
		return IntegerEquivalents.fromInt(AsymmetricAlgorithm.class, publicKey.getAlgorithm());
	}
	
	public int getSize() {
		return publicKey.getBitStrength();
	}
	
	public boolean canEncrypt() {
		return flags.contains(KeyFlag.ENCRYPTION);
	}
	
	public boolean canSign() {
		return flags.contains(KeyFlag.SIGNING);
	}
	
	public DateTime getCreatedAt() {
		return new DateTime(publicKey.getCreationTime(), DateTimeZone.UTC);
	}
	
	public List<SymmetricAlgorithm> getPreferredSymmetricAlgorithms() {
		return signature.getPreferredSymmetricAlgorithms();
	}
	
	public List<CompressionAlgorithm> getPreferredCompressionAlgorithms() {
		return signature.getPreferredCompressionAlgorithms();
	}
	
	public List<HashAlgorithm> getPreferredHashAlgorithms() {
		return signature.getPreferredHashAlgorithms();
	}
	
	@Override
	public String toString() {
		return String.format("%d-%s/%s %s", getSize(), getAlgorithm(), getHumanKeyID(), getUserID());
	}

	public Set<KeyFlag> getKeyFlags() {
		return flags;
	}
	
	public abstract UnlockedKey unlock(char[] passphrase) throws CryptographicException;
}
