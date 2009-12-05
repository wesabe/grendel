package com.wesabe.grendel.openpgp;

public class UnlockedKeySet extends KeySet {
	
	protected UnlockedKeySet(UnlockedMasterKey masterKey, UnlockedSubKey subKey) {
		super(masterKey, subKey);
	}
	
	public UnlockedMasterKey getUnlockedMasterKey() {
		return (UnlockedMasterKey) getMasterKey();
	}
	
	public UnlockedSubKey getUnlockedSubKey() {
		return (UnlockedSubKey) getSubKey();
	}
}
