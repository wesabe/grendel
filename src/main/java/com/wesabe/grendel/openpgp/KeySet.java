package com.wesabe.grendel.openpgp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import com.wesabe.grendel.util.Iterators;

public class KeySet {
	private final MasterKey masterKey;
	private final SubKey subKey;
	
	public static KeySet load(PGPSecretKeyRing keyRing) throws CryptographicException {
		final List<PGPSecretKey> secretKeys = Iterators.toList(keyRing.getSecretKeys());
		final MasterKey masterKey = MasterKey.load(secretKeys.get(0));
		final SubKey subKey = SubKey.load(secretKeys.get(1), masterKey);
		
		return new KeySet(masterKey, subKey);
	}
	
	protected KeySet(MasterKey masterKey, SubKey subKey) {
		this.masterKey = masterKey;
		this.subKey = subKey;
	}
	
	public MasterKey getMasterKey() {
		return masterKey;
	}
	
	public SubKey getSubKey() {
		return subKey;
	}
	
	public void encode(OutputStream output) throws IOException {
		masterKey.getSecretKey().encode(output);
		subKey.getSecretKey().encode(output);
	}
	
	public byte[] getEncoded() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		encode(output);
		return output.toByteArray();
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s]", masterKey, subKey);
	}
	
	public UnlockedKeySet unlock(char[] passphrase) throws CryptographicException {
		final UnlockedMasterKey unlockedMasterKey = masterKey.unlock(passphrase);
		final UnlockedSubKey unlockedSubKey = subKey.unlock(passphrase);
		return new UnlockedKeySet(unlockedMasterKey, unlockedSubKey);
	}
}
