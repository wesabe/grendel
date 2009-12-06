package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPPrivateKey;

/**
 * A PGP key which has been unlocked by its passphrase.
 * 
 * @author coda
 */
public interface UnlockedKey {
	/**
	 * Returns the key's private key.
	 */
	/* default */ abstract PGPPrivateKey getPrivateKey();
}
