package com.about.mantle.utils.servicediscovery;

import com.about.mantle.utils.url.URLUtils;
import com.about.mantle.venus.utils.MntlConfigurationProperties;

//Ensure that Selene service discovery happens only once
public class SeleneServiceDiscovery {
	private static String SELENE_URL = null;

	private SeleneServiceDiscovery() {}

	public static synchronized String getInstance() {
		if(SELENE_URL == null) {
			synchronized (SeleneServiceDiscovery.class) {
				if (SELENE_URL == null) {
					SELENE_URL = new SeleneServiceDiscovery().getSeleneUrl();
					if(new URLUtils(SELENE_URL).isProdSelene()) {
						throw new RuntimeException(SELENE_URL + " matches prod server url. This test is not supported on prod.");
					}
				}
			}
		}
		return SELENE_URL;
	}

	private String getSeleneUrl() {
		return MntlConfigurationProperties
				.getTargetSelene(null);
	}
}
