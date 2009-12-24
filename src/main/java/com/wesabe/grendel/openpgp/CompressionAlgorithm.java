package com.wesabe.grendel.openpgp;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;

import com.wesabe.grendel.util.IntegerEquivalent;

/**
 * A compression algorithm for OpenPGP messages.
 * 
 * @author coda
 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 9.3, RFC 4880</a>
 */
public enum CompressionAlgorithm implements IntegerEquivalent {
	/**
	 * Uncompressed
	 */
	NONE(	"None",		CompressionAlgorithmTags.UNCOMPRESSED),
	
	/**
	 * ZLIB
	 * 
	 * @see <a href="http://www.ietf.org/rfc/rfc1951.txt">RFC 1951</a>
	 */
	ZLIB(	"ZLIB",		CompressionAlgorithmTags.ZLIB),
	
	/**
	 * ZIP
	 * 
	 * @see <a href="http://www.ietf.org/rfc/rfc1950.txt">RFC 1950</a>
	 */
	ZIP(	"ZIP",		CompressionAlgorithmTags.ZIP),
	
	/**
	 * BZip2
	 * 
	 * @see <a href="http://www.bzip.org/">bzip.org</a>
	 */
	BZIP2(	"BZIP2",	CompressionAlgorithmTags.BZIP2);
	
	/**
	 * The default compression algorithm to use.
	 */
	public static final CompressionAlgorithm DEFAULT = ZLIB;
	
	private final String name;
	private final int value;
	
	private CompressionAlgorithm(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the equivalent value of {@link CompressionAlgorithmTags}.
	 * 
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
