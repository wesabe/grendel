package com.wesabe.grendel.openpgp;

import java.security.NoSuchProviderException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;

/**
 * A PGP subkey, used for encrypting and decrypting data. <b>Must</b> be
 * certified by a {@link MasterKey}.
 * 
 * @author coda
 */
public class SubKey extends AbstractKey {
	private final MasterKey masterKey;
	
	/**
	 * Loads a subkey from a {@link PGPSecretKey} instance and verifies the
	 * certification by the given {@link MasterKey}.
	 * 
	 * @param key a {@link PGPSecretKey} instance
	 * @param masterKey the signing {@link MasterKey}
	 * @return a {@link SubKey} instance
	 * @throws CryptographicException if {@code masterKey}'s certification is invalid
	 */
	public static SubKey load(PGPSecretKey key, MasterKey masterKey) throws CryptographicException {
		final SubKey subKey = new SubKey(key, masterKey);
		if (verify(subKey, masterKey)) {
			return subKey;
		}

		throw new CryptographicException("not a valid subkey");
	}
	
	private static boolean verify(SubKey subKey, MasterKey masterKey) {
		return subKey.signature.verifyCertification(subKey, masterKey);
	}
	
	protected SubKey(PGPSecretKey key, MasterKey masterKey) {
		super(key, masterKey.getSecretKey(), SignatureType.SUBKEY_BINDING);
		this.masterKey = masterKey;
	}

	@Override
	public String getUserID() {
		return masterKey.getUserID();
	}
	
	@Override
	public List<String> getUserIDs() {
		return masterKey.getUserIDs();
	}
	
	@Override
	public List<CompressionAlgorithm> getPreferredCompressionAlgorithms() {
		return masterKey.getPreferredCompressionAlgorithms();
	}
	
	@Override
	public List<HashAlgorithm> getPreferredHashAlgorithms() {
		return masterKey.getPreferredHashAlgorithms();
	}
	
	@Override
	public List<SymmetricAlgorithm> getPreferredSymmetricAlgorithms() {
		return masterKey.getPreferredSymmetricAlgorithms();
	}
	
	/**
	 * Returns the paired {@link MasterKey}.
	 */
	public MasterKey getMasterKey() {
		return masterKey;
	}

	@Override
	public UnlockedSubKey unlock(char[] passphrase) throws CryptographicException {
		try {
			final PGPPrivateKey privateKey = secretKey.extractPrivateKey(passphrase, "BC");
			return new UnlockedSubKey(secretKey, masterKey, privateKey);
		} catch (NoSuchProviderException e) {
			throw new CryptographicException(e);
		} catch (PGPException e) {
			throw new CryptographicException("incorrect passphrase");
		}
	}
}
