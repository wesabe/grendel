package com.wesabe.grendel.openpgp;

/**
 * A general class of error indicating that something went wrong in a
 * non-recoverable way. (e.g., I/O error, bad signature, malformed packet).
 * 
 * @author coda
 */
public class CryptographicException extends Exception {
	private static final long serialVersionUID = 7018291212808057570L;
	
	public CryptographicException(String message) {
		super(message);
	}
	
	public CryptographicException(Throwable cause) {
		super(cause);
	}
	
	public CryptographicException(String message, Throwable cause) {
		super(message, cause);
	}
}
