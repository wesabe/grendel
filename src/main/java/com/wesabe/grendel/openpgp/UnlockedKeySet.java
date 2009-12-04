package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPPrivateKey;

public class UnlockedKeySet extends KeySet {
	
	protected UnlockedKeySet(UnlockedMasterKey masterKey, UnlockedSubKey subKey) {
		super(masterKey, subKey);
	}
	
	public PGPPrivateKey getMasterPrivateKey() {
		return ((UnlockedMasterKey) getMasterKey()).getPrivateKey();
	}
	
	public PGPPrivateKey getSubPrivateKey() {
		return ((UnlockedSubKey) getSubKey()).getPrivateKey();
	}
}
