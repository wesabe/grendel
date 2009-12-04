package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;

public class UnlockedMasterKey extends MasterKey implements UnlockedKey {
	private final PGPPrivateKey privateKey;
	
	protected UnlockedMasterKey(PGPSecretKey secretKey, PGPPrivateKey privateKey) {
		super(secretKey);
		this.privateKey = privateKey;
	}

	@Override
	public UnlockedMasterKey unlock(char[] passphrase) {
		return this;
	}

	@Override
	public PGPPrivateKey getPrivateKey() {
		return privateKey;
	}
}
