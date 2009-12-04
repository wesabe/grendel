package com.wesabe.grendel.openpgp;

import java.util.List;
import java.util.Set;

import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.wesabe.grendel.util.IntegerEquivalents;

public class Signature {
	private final PGPSignature signature;
	private final PGPSignatureSubpacketVector subpackets;
	
	public Signature(PGPSignature signature) {
		this.signature = signature;
		this.subpackets = signature.getHashedSubPackets();
	}
	
	public SignatureType getSignatureType() {
		return IntegerEquivalents.fromInt(SignatureType.class, signature.getSignatureType());
	}
	
	public HashAlgorithm getHashAlgorithm() {
		return IntegerEquivalents.fromInt(HashAlgorithm.class, signature.getHashAlgorithm());
	}
	
	public DateTime getCreatedAt() {
		return new DateTime(signature.getCreationTime(), DateTimeZone.UTC);
	}
	
	public AsymmetricAlgorithm getKeyAlgorithm() {
		return IntegerEquivalents.fromInt(AsymmetricAlgorithm.class, signature.getKeyAlgorithm());
	}

	public long getKeyID() {
		return signature.getKeyID();
	}
	
	public Set<KeyFlag> getKeyFlags() {
		return IntegerEquivalents.fromBitmask(KeyFlag.class, subpackets.getKeyFlags());
	}
	
	public List<SymmetricAlgorithm> getPreferredSymmetricAlgorithms() {
		return IntegerEquivalents.fromIntArray(SymmetricAlgorithm.class, subpackets.getPreferredSymmetricAlgorithms());
	}
	
	public List<CompressionAlgorithm> getPreferredCompressionAlgorithms() {
		return IntegerEquivalents.fromIntArray(CompressionAlgorithm.class, subpackets.getPreferredCompressionAlgorithms());
	}
	
	public List<HashAlgorithm> getPreferredHashAlgorithms() {
		return IntegerEquivalents.fromIntArray(HashAlgorithm.class, subpackets.getPreferredHashAlgorithms());
	}
}
