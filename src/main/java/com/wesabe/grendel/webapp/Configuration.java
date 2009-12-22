package com.wesabe.grendel.webapp;

import java.security.SecureRandom;

import com.codahale.shore.AbstractConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Stage;
import com.wesabe.grendel.modules.SecureRandomProvider;

public class Configuration extends AbstractConfiguration {
	@Override
	protected void configure() {
		addEntityPackage("com.wesabe.grendel.entities");
		addResourcePackage("org.codehaus.jackson.jaxrs");
		addResourcePackage("com.wesabe.grendel.resources");
		addModule(new AbstractModule() {
			@Override
			protected void configure() {
				bind(SecureRandom.class).toProvider(new SecureRandomProvider());
			}
		});
		setStage(Stage.PRODUCTION);
	}

	@Override
	public String getExecutableName() {
		return "grendel";
	}
}
