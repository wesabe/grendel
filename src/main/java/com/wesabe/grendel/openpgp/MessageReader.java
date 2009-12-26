package com.wesabe.grendel.openpgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.*;

import com.wesabe.grendel.util.IntegerEquivalents;

/**
 * A reader class capable of decrypting OpenPGP messages created by
 * {@link MessageWriter}.
 * <p>
 * For security reasons, this class enforces the following constraints:
 * <ul>
 *   <li>Uncompressed data is not accepted, due to adaptive-chosen plaintext
 *       attacks.
 *   <li>Integrity-protected data is required, and modification detection code
 *       packets are always verified.
 *   <li>OpenPGP/CFB mode's "quick check" is disabled, due to adaptive
 *       chosen-ciphertext oracle attacks.
 *   <li>Weak algorithms are not accepted.
 * </ul>
 * 
 * Any deviation from the format described by {@link MessageWriter} is
 * considered an unrecoverable error.
 * 
 * @see <a href="http://eprint.iacr.org/2005/033.pdf">An Attack on CFB Mode Encryption As Used By OpenPGP</a>
 * @see <a href="http://www.cs.umd.edu/~jkatz/papers/pgp-attack.pdf">Implementation of Chosen-Ciphertext Attacks against PGP and GnuPG</a>
 * @see MessageWriter
 * @see HashAlgorithm#ACCEPTABLE_ALGORITHMS
 * @see SymmetricAlgorithm#ACCEPTABLE_ALGORITHMS
 * @author coda
 */
public class MessageReader {
	private static final int BUFFER_SIZE = 1024 * 16; // 16KB
	private final KeySet signer;
	private final UnlockedKeySet recipient;
	
	/**
	 * Creates a new reader for a encrypted+signed message.
	 * 
	 * @param signer
	 *            the {@link KeySet} belonging to the user who signed the
	 *            message
	 * @param recipient
	 *            the {@link UnlockedKeySet} belonging to the user whose public
	 *            key the message is encrypted with
	 */
	public MessageReader(KeySet signer, UnlockedKeySet recipient) {
		this.signer = signer;
		this.recipient = recipient;
	}
	
	/**
	 * Decrypts the message and verifies its signature and integrity packet.
	 * 
	 * @param encrypted
	 *            the encrypted message body
	 * @return the decrypted message body
	 * @throws CryptographicException
	 *             if any error occurs while processing the message. This should
	 *             be taken as an indicator that the message has been tampered
	 *             with or is invalid, and that retrying the operation would be
	 *             pointless.
	 */
	public byte[] read(byte[] encrypted) throws CryptographicException {
		try {
			final PGPPublicKeyEncryptedData encryptedData = getEncryptedData(new ByteArrayInputStream(encrypted));
			final InputStream decryptedData = encryptedData.getDataStream(recipient.getUnlockedSubKey().getPrivateKey(), "BC");
			final InputStream decompressedData = getCompressedData(decryptedData);
			
			final PGPObjectFactory factory = getFactory(decompressedData);
			
			final PGPOnePassSignature signature = getOnePassSignature(signer, factory);
			signature.initVerify(signer.getMasterKey().getPublicKey(), "BC");
			
			final InputStream body = getLiteralData(factory);
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			byte[] b = new byte[BUFFER_SIZE];
			int r = 0;
			while ((r = body.read(b)) >= 0) {
				output.write(b, 0, r);
				signature.update(b, 0, r);
			}
			
			signature.verify(getSignature(signer, factory));
			
			encryptedData.verify();
			
			return output.toByteArray();
		} catch (IOException e) {
			throw new CryptographicException(e);
		} catch (ClassCastException e) {
			throw new CryptographicException(e);
		} catch (GeneralSecurityException e) {
			throw new CryptographicException(e);
		} catch (PGPException e) {
			throw new CryptographicException(e);
		}
	}

	private PGPSignature getSignature(KeySet owner, PGPObjectFactory factory) throws CryptographicException, IOException {
		final PGPSignatureList signatures = (PGPSignatureList) factory.nextObject();
		for (int i = 0, size = signatures.size(); i < size; i++) {
			final PGPSignature signature = signatures.get(i);
			if (signature.getKeyID() == owner.getMasterKey().getKeyID()) {
				final HashAlgorithm hashAlgorithm = IntegerEquivalents.fromInt(
					HashAlgorithm.class,
					signature.getHashAlgorithm()
				);
				
				if (!HashAlgorithm.ACCEPTABLE_ALGORITHMS.contains(hashAlgorithm)) {
					throw new CryptographicException("data was signed with " + hashAlgorithm + " which is unacceptable");
				}
				
				return signature;
			}
		}
		
		throw new CryptographicException("couldn't find a signature by " + owner);
	}

	private InputStream getLiteralData(PGPObjectFactory factory) throws IOException {
		return ((PGPLiteralData) factory.nextObject()).getDataStream();
	}

	private PGPOnePassSignature getOnePassSignature(KeySet owner, PGPObjectFactory factory) throws CryptographicException, IOException {
		final PGPOnePassSignatureList signatures = (PGPOnePassSignatureList) factory.nextObject();
		for (int i = 0, size = signatures.size(); i < size; i++) {
			final PGPOnePassSignature signature = signatures.get(i);
			if (signature.getKeyID() == owner.getMasterKey().getKeyID()) {
				return signature;
			}
		}
		
		throw new CryptographicException("couldn't find a one-pass signature by " + owner);
	}

	@SuppressWarnings("deprecation")
	private InputStream getCompressedData(InputStream decryptedData) throws PGPException, IOException, CryptographicException {
		final PGPObjectFactory factory = getFactory(decryptedData);
		final PGPCompressedData compressedData = (PGPCompressedData) factory.nextObject();
		if (compressedData.getAlgorithm() == CompressionAlgorithm.NONE.toInteger()) {
			throw new CryptographicException("encrypted data is uncompressed");
		}
		return compressedData.getDataStream();
	}

	private PGPPublicKeyEncryptedData getEncryptedData(InputStream input) throws IOException,
		CryptographicException, IllegalArgumentException, NoSuchProviderException, PGPException {
		
		final PGPObjectFactory factory = getFactory(input);
		final PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) factory.nextObject();
		
		for (int i = 0, size = encryptedDataList.size(); i < size; i++) {
			final PGPEncryptedData encryptedData = (PGPEncryptedData) encryptedDataList.get(i);
			if (encryptedData instanceof PGPPublicKeyEncryptedData) {
				final PGPPublicKeyEncryptedData pkEncryptedData = (PGPPublicKeyEncryptedData) encryptedData;
				if (pkEncryptedData.getKeyID() == recipient.getSubKey().getKeyID()) {
					final SymmetricAlgorithm symmetricAlgorithm = IntegerEquivalents.fromInt(
						SymmetricAlgorithm.class,
						pkEncryptedData.getSymmetricAlgorithm(recipient.getUnlockedSubKey().getPrivateKey(), "BC")
					);
					
					if (!SymmetricAlgorithm.ACCEPTABLE_ALGORITHMS.contains(symmetricAlgorithm)) {
						throw new CryptographicException("data is encrypted with " + symmetricAlgorithm + " which is unacceptable");
					}
					
					if (!pkEncryptedData.isIntegrityProtected()) {
						throw new CryptographicException("missing integrity packet");
					}
					return pkEncryptedData;
				}
			}
		}
		
		throw new CryptographicException("no encrypted data for " + recipient + " found");
	}
	
	private PGPObjectFactory getFactory(InputStream input) throws IOException {
		return new PGPObjectFactory(PGPUtil.getDecoderStream(input));
	}
}
