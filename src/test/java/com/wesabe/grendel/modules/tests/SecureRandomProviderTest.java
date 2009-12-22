package com.wesabe.grendel.modules.tests;

import static org.fest.assertions.Assertions.*;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.grendel.modules.SecureRandomProvider;

@RunWith(Enclosed.class)
public class SecureRandomProviderTest {
	public static class Providing_A_CSPRNG {
		private SecureRandomProvider provider;
		
		@Before
		public void setup() throws Exception {
			Logger.getLogger(SecureRandomProvider.class.getCanonicalName()).setLevel(Level.OFF);
			this.provider = new SecureRandomProvider();
		}
		
		@Test
		public void itProvidesASecureRandomInstance() throws Exception {
			assertThat(provider.get()).isInstanceOfAny(SecureRandom.class);
		}
		
		@Test
		public void itProvidesTheSameInstance() throws Exception {
			assertThat(provider.get()).isSameAs(provider.get());
		}
	}
}
