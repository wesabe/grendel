package com.wesabe.grendel.openpgp;

import java.util.List;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.util.IntegerEquivalent;

/**
 * A symmetric encryption algorithm for OpenPGP messages.
 * 
 * @author coda
 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 9.2, RFC 4880</a>
 */
public enum SymmetricAlgorithm implements IntegerEquivalent {
	/**
	 * Plaintext or unencrypted data
	 * 
	 * @deprecated Do not store unencrypted data.
	 */
	@Deprecated
	PLAINTEXT(	"Plaintext",	SymmetricKeyAlgorithmTags.NULL),
	
	/**
	 * IDEA
	 * 
	 * @deprecated Encumbered by patents.
	 */
	@Deprecated
	IDEA(		"IDEA",			SymmetricKeyAlgorithmTags.IDEA),
	
	/**
	 * TripleDES (DES-EDE, 168 bit key derived from 192)
	 * 
	 * @deprecated Replaced by AES.
	 */
	@Deprecated
	TRIPLE_DES(	"3DES",			SymmetricKeyAlgorithmTags.TRIPLE_DES),
	
	/**
	 * CAST-128 (also known as CAST5)
	 * 
	 * @deprecated
	 * @see <a href="http://www.ietf.org/rfc/rfc2144.txt">RFC 2144</a>
	 */
	@Deprecated
	CAST_128(	"CAST-128",		SymmetricKeyAlgorithmTags.CAST5),
	
	/**
	 * Blowfish (128 bit key, 16 rounds)
	 * 
	 * @deprecated
	 */
	@Deprecated
	BLOWFISH(	"Blowfish",		SymmetricKeyAlgorithmTags.BLOWFISH),
	
	/**
	 * SAFER-SK (128 bit key, 13 rounds)
	 * 
	 * @deprecated Not specified by RFC 4880.
	 */
	@Deprecated
	SAFER_SK(	"SAFER-SK",		SymmetricKeyAlgorithmTags.SAFER),
	
	/**
	 * DES (56 bit key)
	 * 
	 * @deprecated Not specified by RFC 4880.
	 */
	@Deprecated
	DES(		"DES",			SymmetricKeyAlgorithmTags.DES),
	
	/**
	 * AES with 128-bit key
	 */
	AES_128(	"AES-128",		SymmetricKeyAlgorithmTags.AES_128),
	
	/**
	 * AES with 192-bit key
	 */
	AES_192(	"AES-192",		SymmetricKeyAlgorithmTags.AES_192),
	
	/**
	 * AES with 256-bit key
	 */
	AES_256(	"AES-256",		SymmetricKeyAlgorithmTags.AES_256),
	
	/**
	 * Twofish with 256-bit key
	 * 
	 * @deprecated
	 */
	@Deprecated
	TWOFISH(	"Twofish",		SymmetricKeyAlgorithmTags.TWOFISH);
	
	/**
	 * The default symmetric algorithm to use.
	 */
	public static final SymmetricAlgorithm DEFAULT = AES_256;
	
	/**
	 * A list of symmetric algorithms which are acceptable for use in Grendel.
	 */
	public static final List<SymmetricAlgorithm> ACCEPTABLE_ALGORITHMS = ImmutableList.of(AES_128, AES_192, AES_256);
	
	private final String name;
	private final int value;
	
	private SymmetricAlgorithm(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the equivalent value of {@link SymmetricKeyAlgorithmTags}.
	 */
	@Override
	public int toInteger() {
		return value;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
