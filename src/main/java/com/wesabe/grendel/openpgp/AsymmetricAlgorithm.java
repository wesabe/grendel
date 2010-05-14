package com.wesabe.grendel.openpgp;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;

import com.wesabe.grendel.util.IntegerEquivalent;

/**
 * An asymmetric encryption or signing algorithm for OpenPGP messages.
 * 
 * @author coda
 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 9.1, RFC 4880</a>
 */
public enum AsymmetricAlgorithm implements IntegerEquivalent {
	/**
	 * Elgamal (Encrypt-Only)
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/ElGamal_encryption">Wikipedia</a>
	 */
	ELGAMAL(	"ElGamal",		PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT) {
		@Override
		public AlgorithmParameterSpec getAlgorithmParameterSpec() {
			return new PregeneratedDHParameterSpec();
		}},
	
	/**
	 * DSA (Digital Signature Algorithm)
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Digital_Signature_Algorithm">Wikipedia</a>
	 */
	DSA(		"DSA",			PublicKeyAlgorithmTags.DSA) {
		@Override
		public AlgorithmParameterSpec getAlgorithmParameterSpec() {
			return new PregeneratedDSAParameterSpec();
		}},
	
	/**
	 * RSA (Encrypt or Sign)
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/RSA">Wikipedia</a>
	 */
	RSA(		"RSA",			PublicKeyAlgorithmTags.RSA_GENERAL) {
		@Override
		public AlgorithmParameterSpec getAlgorithmParameterSpec() {
			return new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
		}},
	
	/**
	 * RSA Encrypt-Only
	 * 
	 * @deprecated Sign-only keys must be expressed with subpackets in v4 keys.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.5, RFC 4880</a>
	 */
	@Deprecated
	RSA_E(		"RSA(e)",		PublicKeyAlgorithmTags.RSA_ENCRYPT),
	
	/**
	 * RSA Sign-Only
	 * 
	 * @deprecated Sign-only keys must be expressed with subpackets in v4 keys.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.5, RFC 4880</a>
	 */
	@Deprecated
	RSA_S(		"RSA(s)",		PublicKeyAlgorithmTags.RSA_SIGN),
	
	/**
	 * Elliptic Curve
	 * 
	 * @deprecated Underspecified in RFC 4880.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.8, RFC 4880</a>
	 */
	@Deprecated
	EC(			"EC",			PublicKeyAlgorithmTags.EC),
	
	/**
	 * Elliptic Curve Digital Signature Algorithm.
	 * 
	 * @deprecated Underspecified in RFC 4880.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.8, RFC 4880</a>
	 */
	@Deprecated
	ECDSA(		"ECDSA",		PublicKeyAlgorithmTags.ECDSA),
	
	/**
	 * Elgamal (Encrypt or Sign)
	 * 
	 * @deprecated Prohibited by RFC 4880 due to vulnerabilities.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.8, RFC 4880</a>
	 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.45.3347">Generating ElGamal signatures without knowing the secret key; Daniel Bleichenbacher</a>
	 */
	@Deprecated
	ELGAMAL_G(	"ElGamal(g)",	PublicKeyAlgorithmTags.ELGAMAL_GENERAL),
	
	/**
	 * Diffie-Hellman (X9.42, as defined for IETF-S/MIME)
	 * 
	 * @deprecated Underspecified in RFC 4880.
	 * @see <a href="http://www.ietf.org/rfc/rfc4880.txt">Section 13.8, RFC 4880</a>
	 */
	@Deprecated
	DH(			"DH",			PublicKeyAlgorithmTags.DIFFIE_HELLMAN);

	/**
	 * The default asymmetric encryption algorithm, to be used when generating
	 * new subkeys.
	 */
	public static final AsymmetricAlgorithm ENCRYPTION_DEFAULT = RSA;
	
	/**
	 * The default digital signature algorithm, to be used when generating new
	 * master keys.
	 * 
	 */
	public static final AsymmetricAlgorithm SIGNING_DEFAULT = RSA;

	private final String name;
	private final int value;
	
	private AsymmetricAlgorithm(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the algorithm's standard name, which can be passed to
	 * {@link java.security.KeyPairGenerator}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link java.security.spec.AlgorithmParameterSpec} required to
	 * generate keys for this algorithm.
	 */
	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		throw new UnsupportedOperationException(this + " keys cannot be generated");
	}

	/**
	 * Returns the equivalent value of {@link PublicKeyAlgorithmTags}.
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
