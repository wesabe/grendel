package com.wesabe.grendel.openpgp;

import java.util.List;

import org.bouncycastle.bcpg.HashAlgorithmTags;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.util.IntegerEquivalent;

/**
 * A hash algorithm for OpenPGP messages.
 * 
 * @author coda
 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 9.4, RFC 4880</a>
 */
public enum HashAlgorithm implements IntegerEquivalent {
	/**
	 * MD5
	 * 
     * @deprecated Prohibited by RFC 4880, thoroughly broken.
     * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 14, RFC 4880</a>
     * @see <a href="http://eprint.iacr.org/2006/105">Tunnels in Hash Functions: MD5 Collisions Within a Minute</a>
     */
	@Deprecated
	MD5(			"MD5",			HashAlgorithmTags.MD5),
	
	/**
	 * SHA-1
	 * @deprecated Unsuitable for usage in new systems.
	 * @see <a href="http://eurocrypt2009rump.cr.yp.to/837a0a8086fa6ca714249409ddfae43d.pdf">SHA-1 collisions now 2⁵²</a>
	 */
	@Deprecated
    SHA_1(			"SHA-1",		HashAlgorithmTags.SHA1),
    
    /**
     * RIPEMD-160
     * 
     * @deprecated Based on same design as {@link #MD5} and {@link #SHA_1}.
     */
    @Deprecated
    RIPEMD_160(		"RIPEMD-160",	HashAlgorithmTags.RIPEMD160),
    
    /**
     * Double-width SHA-1
     * 
     * @deprecated Not specified by RFC 4880. Only used by CKT builds of PGP.
     * @see <a href="http://www.ietf.org/rfc/rfc2440.txt">RFC 2440</a>
     */
    @Deprecated
    DOUBLE_SHA(		"2xSHA-1",		HashAlgorithmTags.DOUBLE_SHA),
    
    /**
     * MD2
     * 
     * @deprecated Not specified by RFC 4880. Only used by CKT builds of PGP.
     * @see <a href="http://www.ietf.org/rfc/rfc2440.txt">RFC 2440</a>
     */
    @Deprecated
    MD2(			"MD2",			HashAlgorithmTags.MD2),
    
    /**
     * TIGER-192
     * 
     * @deprecated Not specified by RFC 4880. Only used by CKT builds of PGP.
     * @see <a href="http://www.ietf.org/rfc/rfc2440.txt">RFC 2440</a>
     */
    @Deprecated
    TIGER_192(		"TIGER-192",	HashAlgorithmTags.TIGER_192),
    
    /**
     * HAVAL-5-160
     * 
     * @deprecated Not specified by RFC 4880. Only used by CKT builds of PGP.
     * @see <a href="http://www.ietf.org/rfc/rfc2440.txt">RFC 2440</a>
     */
    @Deprecated
    HAVAL_5_160(	"HAVAL-5-160",	HashAlgorithmTags.HAVAL_5_160),
    
    /**
     * SHA-224
     * 
     * Use only for DSS compatibility.
     */
    SHA_224(		"SHA-224",		HashAlgorithmTags.SHA224),
    
    /**
     * SHA-256
     */
    SHA_256(		"SHA-256",		HashAlgorithmTags.SHA256),
    
    /**
     * SHA-384
     * 
     * Use only for DSS compatibility.
     */
    SHA_384(		"SHA-384",		HashAlgorithmTags.SHA384),
    
    /**
     * SHA-512
     */
    SHA_512(		"SHA-512",		HashAlgorithmTags.SHA512);
	
	/**
	 * The default hash algorithm to use.
	 */
	public static final HashAlgorithm DEFAULT = SHA_512;
	
	/**
	 * A list of hash algorithms which are acceptable for use in Grendel.
	 */
	public static final List<HashAlgorithm> ACCEPTABLE_ALGORITHMS = ImmutableList.of(SHA_224, SHA_256, SHA_384, SHA_512);
	
	private final int value;
	private final String name;
	
	private HashAlgorithm(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the equivalent value of {@link HashAlgorithmTags}.
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
