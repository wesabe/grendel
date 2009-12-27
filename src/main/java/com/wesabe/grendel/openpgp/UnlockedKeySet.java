package com.wesabe.grendel.openpgp;

import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPSecretKey;

/**
 * An unlocked {@link KeySet}.
 * 
 * @author coda
 */
public class UnlockedKeySet extends KeySet {
	
	protected UnlockedKeySet(UnlockedMasterKey masterKey, UnlockedSubKey subKey) {
		super(masterKey, subKey);
	}
	
	/**
	 * Returns the {@link UnlockedMasterKey}.
	 */
	public UnlockedMasterKey getUnlockedMasterKey() {
		return (UnlockedMasterKey) getMasterKey();
	}
	
	/**
	 * Returns the {@link UnlockedSubKey}.
	 */
	public UnlockedSubKey getUnlockedSubKey() {
		return (UnlockedSubKey) getSubKey();
	}
	
	/**
	 * Re-encrypts the key set with a new passphrase and returns it in locked
	 * form.
	 * 
	 * @param oldPassphrase
	 *            the old passphrase
	 * @param newPassphrase
	 *            the new passphrase
	 * @param random
	 *            a {@link SecureRandom} instance
	 * @return {@code this}, re-encrypted with {@code newPassphrase}
	 * @throws CryptographicException
	 *             if {@code oldPassphrase} is incorrect
	 */
	public KeySet relock(char[] oldPassphrase, char[] newPassphrase, SecureRandom random) throws CryptographicException {
		try {
			final PGPSecretKey masterSecretKey = PGPSecretKey.copyWithNewPassword(
				getUnlockedMasterKey().getSecretKey(),
				oldPassphrase,
				newPassphrase,
				SymmetricAlgorithm.DEFAULT.toInteger(),
				random,
				"BC"
			);
			final PGPSecretKey subSecretKey = PGPSecretKey.copyWithNewPassword(
				getUnlockedSubKey().getSecretKey(),
				oldPassphrase,
				newPassphrase,
				SymmetricAlgorithm.DEFAULT.toInteger(),
				random,
				"BC"
			);
			
			final MasterKey newMasterKey = new MasterKey(masterSecretKey);
			final SubKey newSubKey = new SubKey(subSecretKey, newMasterKey);
			
			return new KeySet(newMasterKey, newSubKey);
		} catch (NoSuchProviderException e) {
			throw new CryptographicException(e);
		} catch (PGPException e) {
			throw new CryptographicException(e);
		}
	}
}
