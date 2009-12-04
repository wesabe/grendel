package com.wesabe.grendel.openpgp;

import java.math.BigInteger;
import java.security.spec.DSAParameterSpec;

/**
 * A pre-generated {@link DSAParameterSpec} for 1024-bit
 * {@link AsymmetricAlgorithm#DSA} keys.
 * <p>
 * <b>N.B.:</b> {@code P}, {@code Q}, and {@code G} are all public values and
 * can be shared across networks of users.
 * @see <a href="http://en.wikipedia.org/wiki/Digital_Signature_Algorithm">Digital Signature Algorithm</a>
 * @author coda
 */
public final class PregeneratedDSAParameterSpec extends DSAParameterSpec {
	private static final BigInteger G = new BigInteger(
		"42iz5oiscx7wtyascmwkesjh4socl98ex5y2kgmnl5xnc4ny2romijch7uk53qxtnq2k" +
		"grvbx4z5qclbkkz930by9iva1dk7o5s816nen7vdwtzo6bk7nnx40y2gu55wdyzirjct" +
		"5dzh0jqjjbl0vzqmzw2si1abrrrzfaskkpb7kyqne1qctmrt2j0ozls69boond",
		36
	);
	private static final BigInteger Q = new BigInteger(
		"pcemwdiwzfzg7vw8n8el73hi1v3pelp",
		36
	);
	private static final BigInteger P = new BigInteger(
		"pmqpa15uksb3tr1710v3m0ohs0i1utcoavzgk066lbp5rkvgjtjgqb0fj847osr54s23" +
		"w4g60p0a7v3yn0twefnvvqdqn29xpe9auvblylpirmeio1usdnxwdp9bcu9n1i9jtvty" +
		"glg49753mkd5wnyaztp3qo5sm6ussie7fsf2rss7jjbcj2trgnfq4sshdm6sp7",
		36
	);
	
	public PregeneratedDSAParameterSpec() {
		super(P, Q, G);
	}
}
