package com.wesabe.grendel.openpgp;

import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;

public class MasterKey extends AbstractKey {
	public static MasterKey load(PGPSecretKey key) throws CryptographicException {
		final MasterKey masterKey = new MasterKey(key);
		if (verify(masterKey)) {
			return masterKey;
		}
		throw new CryptographicException("not a self-signed master key");
	}

	private static boolean verify(MasterKey key) {
		return (key.signature != null) && key.signature.verifyCertification(key);
	}

	protected MasterKey(PGPSecretKey secretKey) {
		super(secretKey, secretKey, SignatureType.POSITIVE_CERTIFICATION);
	}

	@Override
	public UnlockedMasterKey unlock(char[] passphrase) throws CryptographicException {
		try {
			final PGPPrivateKey privateKey = secretKey.extractPrivateKey(passphrase, "BC");
			return new UnlockedMasterKey(secretKey, privateKey);
		} catch (NoSuchProviderException e) {
			throw new CryptographicException(e);
		} catch (PGPException e) {
			throw new CryptographicException("incorrect passphrase");
		}
	}
}
