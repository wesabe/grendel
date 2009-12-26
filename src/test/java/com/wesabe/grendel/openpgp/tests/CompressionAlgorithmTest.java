package com.wesabe.grendel.openpgp.tests;

import static org.fest.assertions.Assertions.*;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.openpgp.CompressionAlgorithm;

@RunWith(Enclosed.class)
public class CompressionAlgorithmTest {
	@SuppressWarnings("deprecation")
	public static class None {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(CompressionAlgorithm.NONE.toInteger()).isEqualTo(CompressionAlgorithmTags.UNCOMPRESSED);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(CompressionAlgorithm.NONE.toString()).isEqualTo("None");
		}
	}
	
	public static class ZLIB {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(CompressionAlgorithm.ZLIB.toInteger()).isEqualTo(CompressionAlgorithmTags.ZLIB);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(CompressionAlgorithm.ZLIB.toString()).isEqualTo("ZLIB");
		}
	}
	
	public static class ZIP {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(CompressionAlgorithm.ZIP.toInteger()).isEqualTo(CompressionAlgorithmTags.ZIP);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(CompressionAlgorithm.ZIP.toString()).isEqualTo("ZIP");
		}
	}
	
	public static class BZIP2 {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(CompressionAlgorithm.BZIP2.toInteger()).isEqualTo(CompressionAlgorithmTags.BZIP2);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(CompressionAlgorithm.BZIP2.toString()).isEqualTo("BZIP2");
		}
	}
	
	public static class Default {
		@Test
		public void itUsesZLIBByDefault() throws Exception {
			assertThat(CompressionAlgorithm.DEFAULT).isEqualTo(CompressionAlgorithm.ZLIB);
		}
	}
}
