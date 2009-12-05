package com.wesabe.grendel.openpgp;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.joda.time.DateTime;

import com.google.inject.internal.ImmutableList;
import com.wesabe.grendel.util.IntegerEquivalents;

public class KeySetGenerator {
	private final SecureRandom random;
	private final ExecutorService executor;

	public KeySetGenerator(SecureRandom random) {
		this.random = random;
		this.executor = Executors.newCachedThreadPool();
	}

	public KeySet generate(String userId, char[] passphrase) throws ExecutionException, InterruptedException, NoSuchProviderException, PGPException, CryptographicException {
		final Future<KeyPair> masterKeyPairFuture = generateKeyPair(AsymmetricAlgorithm.SIGNING_DEFAULT);
		final Future<KeyPair> subKeyPairFuture = generateKeyPair(AsymmetricAlgorithm.ENCRYPTION_DEFAULT);

		final PGPKeyPair masterPGPKeyPair = new PGPKeyPair(AsymmetricAlgorithm.SIGNING_DEFAULT.toInteger(), masterKeyPairFuture.get(), new DateTime().toDate());

		final PGPKeyRingGenerator generator = new PGPKeyRingGenerator(
			SignatureType.POSITIVE_CERTIFICATION.toInteger(),
			masterPGPKeyPair,
			userId,
			SymmetricAlgorithm.DEFAULT.toInteger(),
			passphrase,
			true, // use SHA-1 instead of MD5
			generateMasterKeySettings(),
			null, // don't store any key settings unhashed
			random,
			BouncyCastleProvider.PROVIDER_NAME
		);

		final PGPKeyPair subPGPKeyPair = new PGPKeyPair(AsymmetricAlgorithm.ENCRYPTION_DEFAULT.toInteger(), subKeyPairFuture.get(), new DateTime().toDate());

		generator.addSubKey(subPGPKeyPair, generateSubKeySettings(), null);

		final PGPSecretKeyRing keyRing = generator.generateSecretKeyRing();
		return KeySet.load(keyRing);
	}

	private PGPSignatureSubpacketVector generateSubKeySettings() {
		final PGPSignatureSubpacketGenerator settings = new PGPSignatureSubpacketGenerator();
		settings.setKeyFlags(false, IntegerEquivalents.toBitmask(KeyFlag.SUB_KEY_DEFAULTS));
		return settings.generate();
	}

	private PGPSignatureSubpacketVector generateMasterKeySettings() {
		final PGPSignatureSubpacketGenerator settings = new PGPSignatureSubpacketGenerator();
		settings.setKeyFlags(false, IntegerEquivalents.toBitmask(KeyFlag.MASTER_KEY_DEFAULTS));
		settings.setPreferredSymmetricAlgorithms(false, IntegerEquivalents.toIntArray(SymmetricAlgorithm.ACCEPTABLE_ALGORITHMS));
		settings.setPreferredHashAlgorithms(false, IntegerEquivalents.toIntArray(HashAlgorithm.ACCEPTABLE_ALGORITHMS));
		settings.setPreferredCompressionAlgorithms(false,
				IntegerEquivalents.toIntArray(
					ImmutableList.of(
						CompressionAlgorithm.BZIP2,
						CompressionAlgorithm.ZLIB,
						CompressionAlgorithm.ZIP
					)
				)
		);
		return settings.generate();
	}

	private Future<KeyPair> generateKeyPair(final AsymmetricAlgorithm algorithm) {
		return executor.submit(new Callable<KeyPair>() {
			@Override
			public KeyPair call() throws Exception {
				final KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm.getName(), "BC");
				generator.initialize(algorithm.getAlgorithmParameterSpec(), random);
				return generator.generateKeyPair();
			}
		});
	}
}
