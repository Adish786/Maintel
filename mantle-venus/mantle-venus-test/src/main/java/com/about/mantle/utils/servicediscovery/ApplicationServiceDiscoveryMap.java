package com.about.mantle.utils.servicediscovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationServiceDiscoveryMap {
	private static Map<String, String> urlMap = new ConcurrentHashMap<>();

	public static String getUrl(String application) {
		synchronized (urlMap) {
			String url = urlMap.get(application);
			if(url == null) {
				url = new MntlVenusServiceDiscovery(application).getUrl();
				urlMap.put(application, url);
			}
			return url;
		}
	}
}
