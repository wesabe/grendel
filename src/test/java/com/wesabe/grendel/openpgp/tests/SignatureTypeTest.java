package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import org.bouncycastle.openpgp.PGPSignature;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.SignatureType;

@RunWith(Enclosed.class)
public class SignatureTypeTest {
	public static class Binary_Document {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.BINARY_DOCUMENT.toInteger()).isEqualTo(PGPSignature.BINARY_DOCUMENT);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.BINARY_DOCUMENT.toString()).isEqualTo("binary document");
		}
	}
	
	public static class Text_Document {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.TEXT_DOCUMENT.toInteger()).isEqualTo(PGPSignature.CANONICAL_TEXT_DOCUMENT);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.TEXT_DOCUMENT.toString()).isEqualTo("text document");
		}
	}
	
	public static class Standalone {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.STANDALONE.toInteger()).isEqualTo(PGPSignature.STAND_ALONE);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.STANDALONE.toString()).isEqualTo("standalone");
		}
	}
	
	public static class Default_Certification {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.DEFAULT_CERTIFICATION.toInteger()).isEqualTo(PGPSignature.DEFAULT_CERTIFICATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.DEFAULT_CERTIFICATION.toString()).isEqualTo("default certification");
		}
	}
	
	public static class No_Certification {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.NO_CERTIFICATION.toInteger()).isEqualTo(PGPSignature.NO_CERTIFICATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.NO_CERTIFICATION.toString()).isEqualTo("no certification");
		}
	}
	
	public static class Casual_Certification {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.CASUAL_CERTIFICATION.toInteger()).isEqualTo(PGPSignature.CASUAL_CERTIFICATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.CASUAL_CERTIFICATION.toString()).isEqualTo("casual certification");
		}
	}
	
	public static class Positive_Certification {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.POSITIVE_CERTIFICATION.toInteger()).isEqualTo(PGPSignature.POSITIVE_CERTIFICATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.POSITIVE_CERTIFICATION.toString()).isEqualTo("positive certification");
		}
	}
	
	public static class Subkey_Binding {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.SUBKEY_BINDING.toInteger()).isEqualTo(PGPSignature.SUBKEY_BINDING);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.SUBKEY_BINDING.toString()).isEqualTo("subkey binding");
		}
	}
	
	public static class Primary_Key_Binding {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.PRIMARY_KEY_BINDING.toInteger()).isEqualTo(PGPSignature.PRIMARYKEY_BINDING);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.PRIMARY_KEY_BINDING.toString()).isEqualTo("primary key binding");
		}
	}
	
	public static class Direct_Key {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.DIRECT_KEY.toInteger()).isEqualTo(PGPSignature.DIRECT_KEY);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.DIRECT_KEY.toString()).isEqualTo("direct key");
		}
	}
	
	public static class Key_Revocation {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.KEY_REVOCATION.toInteger()).isEqualTo(PGPSignature.KEY_REVOCATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.KEY_REVOCATION.toString()).isEqualTo("key revocation");
		}
	}
	
	public static class Subkey_Revocation {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.SUBKEY_REVOCATION.toInteger()).isEqualTo(PGPSignature.SUBKEY_REVOCATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.SUBKEY_REVOCATION.toString()).isEqualTo("subkey revocation");
		}
	}
	
	public static class Certificate_Revocation {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.CERTIFICATION_REVOCATION.toInteger()).isEqualTo(PGPSignature.CERTIFICATION_REVOCATION);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.CERTIFICATION_REVOCATION.toString()).isEqualTo("certificate revocation");
		}
	}
	
	public static class Timestamp {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.TIMESTAMP.toInteger()).isEqualTo(PGPSignature.TIMESTAMP);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.TIMESTAMP.toString()).isEqualTo("timestamp");
		}
	}
	
	public static class Third_Party {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(SignatureType.THIRD_PARTY.toInteger()).isEqualTo(0x50);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(SignatureType.THIRD_PARTY.toString()).isEqualTo("third-party confirmation");
		}
	}
}
