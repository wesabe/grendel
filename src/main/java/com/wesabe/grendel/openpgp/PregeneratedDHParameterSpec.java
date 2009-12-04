package com.wesabe.grendel.openpgp;

import java.math.BigInteger;

import javax.crypto.spec.DHParameterSpec;

/**
 * A pre-generated {@link DHParameterSpec} for 2048-bit
 * {@link AsymmetricAlgorithm#ELGAMAL} keys.
 * <p>
 * <b>N.B.:</b> {@code P} and {@code G} are both public values and can be shared
 * across networks of users.
 * @see <a href="http://en.wikipedia.org/wiki/ElGamal_encryption">ElGamal encryption</a>
 * @author coda
 */
public final class PregeneratedDHParameterSpec extends DHParameterSpec {
	private static final BigInteger G = new BigInteger(
		"v459uv2gxjbl1jqu7fhlvhe23oi1qtwqs6n8h635dkmc2o58kwa4jurbem9h9h87iq1k" +
		"6rqj5fxowbyvpeobz9k9ijcq03sue3o45506zmhw0husbxgwy8g14gzio6ct22k45zev" +
		"n6bwj7vpwq5eat72oervw0pccp9gg45qs9m6k4fn6vrp5avmmdbu91qlv075n4ojf8iv" +
		"9r7zc4mdvvb5akkwvl36hrqd3wei9e3p5ilk1z2vnenitzau40satbcx6eqfmivvsn7m" +
		"n8schdd4irr45yakbthfu3cw896r7ygx44r534sp7r5pkldeih6fp7cin6jysr4b7woe" +
		"aglyy167976n4eg1y99i1eb6561mg587hcf05j1woxzfi8m0565nvkpz",
		36
	);
	private static final BigInteger P = new BigInteger(
		"1ikmyh3qcdgz825eegsk41g7msaustr13k16h06zxy6pwrh1bt2d7888nv77oybgmqok" +
		"8947twild1j14miwjfc9l0jr02a1dk6t1t5ynyeyh08dyisonl2fjlsp3eyz3936vtac" +
		"idp0pll9pr52crqoektouivzt4v3jk0jgp3dvux628zvrstd143zifw2dj3ed8kd4o37" +
		"0ze6qf53sx9nyv816kpihdw10723p7igep2fe5fe8fxpg8vqf4wyttnejwho4aa0eo15" +
		"q7noeeegck2h53q2o5e00myfdnn7y7dls52ixfr1wiyk2ovq1fg66jl382t0lb76usxj" +
		"5qifjs2hqioup6premvu6u1dwb8d0qucscfq3itqolmsdpkns5vu9rfsz",
		36
	);
	
	public PregeneratedDHParameterSpec() {
		super(P, G);
	}
}
