package com.wesabe.grendel;

import java.security.SecureRandom;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;

import com.codahale.shore.AbstractConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Stage;
import com.wesabe.grendel.modules.SecureRandomProvider;

public class Configuration extends AbstractConfiguration {
	@Override
	protected void configure() {
		addEntityPackage("com.wesabe.grendel.entities");
		addResourcePackage("org.codehaus.jackson.jaxrs");
		addResourcePackage("com.wesabe.grendel.auth");
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
	protected void configureRequestLog(RequestLog log) {
		final NCSARequestLog ncsaLog = (NCSARequestLog) log;
		ncsaLog.setExtended(true);
		ncsaLog.setLogLatency(true);
	}

	@Override
	public String getExecutableName() {
		return "grendel";
	}
}
