package com.wesabe.grendel.openpgp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import com.wesabe.grendel.util.Iterators;

/**
 * A {@link MasterKey} and {@link SubKey} pair.
 * 
 * @author coda
 */
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
	
	/**
	 * Returns the keyset's {@link MasterKey}.
	 */
	public MasterKey getMasterKey() {
		return masterKey;
	}
	
	/**
	 * Returns the keyset's {@link SubKey}.
	 */
	public SubKey getSubKey() {
		return subKey;
	}
	
	/**
	 * Writes the keyset in encoded form, to {@code output}.
	 * 
	 * @param output an {@link OutputStream}
	 * @throws IOException if there is an error writing to {@code output}
	 */
	public void encode(OutputStream output) throws IOException {
		masterKey.getSecretKey().encode(output);
		subKey.getSecretKey().encode(output);
	}
	
	/**
	 * Returns the keyset in encoded form.
	 * 
	 * @throws IOException if there is an error encoding the keyset
	 */
	public byte[] getEncoded() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		encode(output);
		return output.toByteArray();
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s]", masterKey, subKey);
	}
	
	/**
	 * Given the keyset's passphrase, unlocks the secret keys and returns an
	 * {@link UnlockedKeySet} equivalent of {@code this}.
	 * 
	 * @param passphrase the key's passphrase
	 * @return a {@link UnlockedKeySet} equivalent of {@code this}
	 * @throws CryptographicException if {@code passphrase} is incorrect
	 */
	public UnlockedKeySet unlock(char[] passphrase) throws CryptographicException {
		final UnlockedMasterKey unlockedMasterKey = masterKey.unlock(passphrase);
		final UnlockedSubKey unlockedSubKey = subKey.unlock(passphrase);
		return new UnlockedKeySet(unlockedMasterKey, unlockedSubKey);
	}
}
