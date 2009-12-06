package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;

/**
 * An unlocked {@link SubKey}.
 * 
 * @author coda
 */
public class UnlockedSubKey extends SubKey implements UnlockedKey {
	private final PGPPrivateKey privateKey;
	
	protected UnlockedSubKey(PGPSecretKey key, MasterKey masterKey, PGPPrivateKey privateKey) {
		super(key, masterKey);
		this.privateKey = privateKey;
	}

	@Override
	public UnlockedSubKey unlock(char[] passphrase) {
		return this;
	}

	@Override
	public PGPPrivateKey getPrivateKey() {
		return privateKey;
	}

}
