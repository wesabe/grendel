package com.wesabe.grendel.openpgp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Collection;

import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A writer class capable of producing encrypted+signed OpenPGP messages.
 * 
 * <p>
 * This class only produces messages of the following form:
 * 
 * <pre>
 * +---------------------------------------------------------------------------+
 * | Public-Key Encrypted Session Key Packet                                   |
 * +---------------------------------------------------------------------------+
 * | ... (repeated for all recipients)                                         |
 * +---------------------------------------------------------------------------+
 * | Symmetrically Encrypted Integrity Protected Data Packet                   |
 * |                                                                           |
 * | +-----------------------------------------------------------------------+ |
 * | | Compressed Data Packet                                                | |
 * | |                                                                       | |
 * | | +-------------------------------------------------------------------+ | |
 * | | | One-Pass Signature Packet                                         | | |
 * | | +-------------------------------------------------------------------+ | |
 * | | | ... (repeated for all signers)                                    | | |
 * | | +-------------------------------------------------------------------+ | |
 * | | | Literal Data Packet                                               | | |
 * | | |                                                                   | | |
 * | | | +---------------------------------------------------------------+ | | |
 * | | | |                                                               | | | |
 * | | | |                         message body                          | | | |
 * | | | |                                                               | | | |
 * | | | +---------------------------------------------------------------+ | | |
 * | | |                                                                   | | |
 * | | +-------------------------------------------------------------------+ | |
 * | | | Signature Packet                                                  | | |
 * | | +-------------------------------------------------------------------+ | |
 * | | | ... (repeated for all signers)                                    | | |
 * | | +-------------------------------------------------------------------+ | |
 * | |                                                                       | |
 * | +-----------------------------------------------------------------------+ |
 * | |  Modification Detection Code Packet                                   | |
 * | +-----------------------------------------------------------------------+ |
 * |                                                                           |
 * +---------------------------------------------------------------------------+
 * </pre>
 * 
 * First, a signature of the message body is generated using the owner's private
 * key. The body and signature are then compressed and encrypted using a random
 * symmetric session key and stored in a integrity-protected data packet with a
 * matching modification detection code packet. The session key is then
 * encrypted with the owner and receipients' public keys.
 * <p>
 * To prevent adaptive chosen-plaintext attacks, this class enforces two
 * constraints:
 * <ul>
 *   <li>All signed data is compressed before being encrypted.
 *   <li>All encrypted data has an accompanying modification detection code
 *       packet.
 * </ul>
 * 
 * @author coda
 * @see <a href="http://eprint.iacr.org/2005/033.pdf">An Attack on CFB Mode Encryption As Used By OpenPGP</a>
 * @see <a href="http://www.cs.umd.edu/~jkatz/papers/pgp-attack.pdf">Implementation of Chosen-Ciphertext Attacks against PGP and GnuPG</a>
 * @see AsymmetricAlgorithm#ENCRYPTION_DEFAULT
 * @see AsymmetricAlgorithm#SIGNING_DEFAULT
 * @see SymmetricAlgorithm#DEFAULT
 * @see HashAlgorithm#DEFAULT
 * @see CompressionAlgorithm#DEFAULT
 */
public class MessageWriter {
	private static final int BUFFER_SIZE = 1 << 16;
	private static final double ENVELOPE_OVERHEAD = 1.2;
	private static final double RECIPIENT_OVERHEAD = 300;
	private final UnlockedKeySet owner;
	private final Collection<KeySet> recipients;
	private final SecureRandom random;
	
	/**
	 * Creates a new writer for an encrypted+signed message.
	 * 
	 * @param owner
	 *            the {@link UnlockedKeySet} belonging to the message owner
	 * @param recipients
	 *            the {@link KeySet}s belonging to the recipients
	 * @param random
	 *            a {@link SecureRandom} instance
	 */
	public MessageWriter(UnlockedKeySet owner, Collection<KeySet> recipients, SecureRandom random) {
		this.owner = owner;
		this.recipients = recipients;
		this.random = random;
	}
	
	/**
	 * Signs, compresses, and encrypts a message.
	 * 
	 * @param body
	 *            the message body
	 * @return the message, in an encrypted+signed OpenPGP envelope
	 * @throws CryptographicException
	 *             if any error occurs while processing the message
	 */
	public byte[] write(byte[] body) throws CryptographicException {
		try {
			final ByteArrayOutputStream output = new ByteArrayOutputStream(estimateEncryptedSize(body.length));
			signAndCompressAndEncrypt(body, output);
			return output.toByteArray();
		} catch (Exception e) {
			throw new CryptographicException(e);
		}
	}
	
	/*
	 * This formula was empirically determined to return a buffer size which
	 * will fit most messages, including envelope overhead and per-recipient
	 * overhead. Some messages may require another buffer allocation, but this
	 * should be rare.
	 */
	private int estimateEncryptedSize(int unencryptedSize) {
		return (int) Math.round(Math.ceil(
			(unencryptedSize * ENVELOPE_OVERHEAD) +
			(recipients.size() * RECIPIENT_OVERHEAD)
		));
	}

	private void signAndCompressAndEncrypt(byte[] body, OutputStream output) throws Exception {
		final OutputStream encryptedOutput = getEncryptionWrapper(output);
		signAndCompress(body, encryptedOutput);
		encryptedOutput.close();
	}

	private void signAndCompress(byte[] body, OutputStream encryptedOutput) throws Exception {
		final OutputStream compressedOutput = getCompressionWrapper(encryptedOutput);
		sign(body, compressedOutput);
		compressedOutput.close();
	}

	private void sign(byte[] body, OutputStream compressedOutput) throws Exception {
		final PGPSignatureGenerator signatureGenerator = getSignatureGenerator(owner.getUnlockedMasterKey());
		signatureGenerator.generateOnePassVersion(false).encode(compressedOutput);
		final OutputStream literalOutput = getLiteralWrapper(compressedOutput);
		literalOutput.write(body);
		signatureGenerator.update(body);
		literalOutput.close();
		signatureGenerator.generate().encode(compressedOutput);
	}

	private OutputStream getEncryptionWrapper(OutputStream out) throws Exception {

		final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
			SymmetricAlgorithm.DEFAULT.toInteger(), true, random,
			"BC");

		for (KeySet recipient : recipients) {
			if (recipient.getSubKey().getKeyID() != owner.getSubKey().getKeyID()) {
				encryptedDataGenerator.addMethod(recipient.getSubKey().getPublicKey());
			}
		}

		encryptedDataGenerator.addMethod(owner.getSubKey().getPublicKey());

		return encryptedDataGenerator.open(out, new byte[BUFFER_SIZE]);
	}
	
	private OutputStream getCompressionWrapper(OutputStream out) throws Exception {
		return new PGPCompressedDataGenerator(CompressionAlgorithm.DEFAULT.toInteger()).open(out);
	}
	
	private PGPSignatureGenerator getSignatureGenerator(UnlockedMasterKey owner) throws Exception {

		final PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
			owner.getPublicKey().getAlgorithm(),
			HashAlgorithm.DEFAULT.toInteger(),
			"BC");
		signatureGenerator.initSign(PGPSignature.BINARY_DOCUMENT, owner.getPrivateKey());

		final PGPSignatureSubpacketGenerator signatureMetaData = new PGPSignatureSubpacketGenerator();
		signatureMetaData.setSignerUserID(false, owner.getUserID());
		signatureGenerator.setHashedSubpackets(signatureMetaData.generate());
		return signatureGenerator;
	}
	
	private OutputStream getLiteralWrapper(OutputStream output) throws Exception {
		return new PGPLiteralDataGenerator().open(output,
			PGPLiteralData.BINARY,
			PGPLiteralData.CONSOLE,
			new DateTime(DateTimeZone.UTC).toDate(),
			new byte[BUFFER_SIZE]
		);
	}
}
