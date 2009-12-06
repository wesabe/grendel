package com.wesabe.grendel.openpgp;

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
}
