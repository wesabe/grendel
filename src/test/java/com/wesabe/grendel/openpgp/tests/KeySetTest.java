package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import java.io.FileInputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.KeySet;

@RunWith(Enclosed.class)
public class KeySetTest {
	public static class A_Key_Set {
		private KeySet keySet;

		@Before
		public void setup() throws Exception {
			Security.addProvider(new BouncyCastleProvider());
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.keySet = KeySet.load(keyRingFile);
		}

		@Test
		public void itHasAMasterKey() throws Exception {
			assertThat(keySet.getMasterKey().getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}

		@Test
		public void itHasASubKey() throws Exception {
			assertThat(keySet.getSubKey().getKeyID()).isEqualTo(0xA3A5D038FF30574EL);
		}

		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(keySet.toString()).isEqualTo("[2048-RSA/8838238C, 2048-RSA/FF30574E]");
		}
		
		@Test
		public void itHasAUserID() throws Exception {
			assertThat(keySet.getUserID()).isEqualTo("Sample Key <sample@wesabe.com>");
		}

		@Test
		public void itSerializesItselfToAStream() throws Exception {
			final String encoded = new String(Base64.encode(keySet.getEncoded()));
			assertThat(encoded).isEqualTo(
				"lQO+BEpWGSsBCACkn2kY7+LhS+db3A7xb/L5zpm1ddWDGGgTlalR/dpV+YOs" +
				"lDLN9YJM1ftqbgKE7dYN3rnWnoCSAloY3fngmSM/us1taSougiV7Sc/dZnGw" +
				"LNU8oeZATJSZrVzPvQfFBItJjHkg0pt5c561voeua9lu+3ZTfCD0uIgAVu+b" +
				"5+Sds8ncWnsDFp/FLgii/RqxBlFhtWFgQNYEJLMEBRxHqBpSbPYxg/iUliim" +
				"cwv+o73jOXtgl9UQ2lTNp/cpIiKCbzKcZXvEH1LoIremi6iX3kJNRl2ixHW2" +
				"MYaKXCY9SZdNlgzx3g6Aj/9K8opKEdkl6V+2w/dkwu9TwyWkVoGWtUZHABEB" +
				"AAH+AwMC+qNrMJdjGGZgbLOk2HroosVeY1Ue2g2ikPw8G3nYzzoTzJNLIDut" +
				"Wz9Px3v6JX2CYwVGB0WJxoiyusZ/i/cOvreVV9LLN1t6v6t9GKfjOKWZRbVt" +
				"SKn3dwBMHFuv7a6WgQGCsps0glz4tbqzxXwRtDQDdHybDw9DOdsLc8+B6t8Z" +
				"vjhFf7wvVu4MO9EvWGRbBpPQSLWUDlRb+0XVYcgAzs3o8zXsUd+K9A+D8due" +
				"PGhRMnGASvQPSBJBpBdTMK5fEXHfMzbybAqXlAtlZT/tIJk1bS9gA/kADNFo" +
				"FKcdTUhWh4bBwuxWdTNULdaM/BP1HhXA3m5/ejkXwoL8KqI3SbAXgByp5UEL" +
				"IsJ7mLk+mD3mVFBqC2PF76lGfszgVcyRVyXkWvhxrLQ2w6m7eYqUg8nIjbFO" +
				"xR02n+l0ACysfZQD4z/1vVt2X1jeUijji0CfkVSJA/Nso6sQrDw7Ok/5fX9v" +
				"qVL1+KqqAWR8f80h7PF+lWroNr03aLJhsFXY/05Nb4s2BRTG9lMqvsMnjiBZ" +
				"NwrFUNx2WrsvJK8pb5UqwovBnScGtcwAi7K9OEJFuXkbIYNZkkU+01VfN8mb" +
				"NED648kiZUbNiqoPYh6A5wv3BevFlzRZaFd4uRnCRosyu6BGUBrKDSTRlgd5" +
				"QqO3UUC4069v9WPtjJlSpMVh8raUiqCByMVLVGE/cPYqcqdokEYk/Q4p8um2" +
				"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");
		}
	}
}
