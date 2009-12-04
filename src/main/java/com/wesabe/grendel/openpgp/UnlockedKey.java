package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPPrivateKey;

public interface UnlockedKey {
	/* default */ abstract PGPPrivateKey getPrivateKey();
}
