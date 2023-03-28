package com.about.mantle.utils.servicediscovery;

import com.about.mantle.venus.utils.MntlConfigurationProperties;

//Ensure that application service discovery happens only once
public class ApplicationServiceDiscovery {
	private static String APPLICATION_URL = null;

	private ApplicationServiceDiscovery(){}

	public static synchronized String getUrl(String appName) {
		if(APPLICATION_URL == null) {
			synchronized (ApplicationServiceDiscovery.class) {
				if(APPLICATION_URL == null) {
					APPLICATION_URL = new ApplicationServiceDiscovery().getApplicationUrl(appName);
				}
			}
		}
		return APPLICATION_URL;
	}

	private String getApplicationUrl(String appName) {
		return MntlConfigurationProperties
				.getTargetProjectBaseUrl(null);
	}
}
