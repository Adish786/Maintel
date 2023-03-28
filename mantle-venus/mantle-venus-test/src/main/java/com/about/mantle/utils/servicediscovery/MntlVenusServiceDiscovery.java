package com.about.mantle.utils.servicediscovery;

import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.servicediscovery.Service;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/*
Used for setting environment for service discovery using venus params
And return a Service
 */
public class MntlVenusServiceDiscovery {
	Logger logger = LoggerFactory.getLogger(MntlVenusServiceDiscovery.class);
	private final String serviceName;
	private String applicationName;
	private Service service = null;

	public MntlVenusServiceDiscovery(String serviceName) {
		this.serviceName = serviceName;
	}

	public Service getService() {
		if(service == null) {
			setupConsul();
			setupEnvironment();
			System.setProperty("application", serviceName);
			service = CommonPropertyFactory.INSTANCE.get().getService(serviceName, null);
		}
		return service;
	}

	public String getUrl() {
		return getUrl("https");
	}

	public String getUrl(String scheme) {
		service = getService();
		List<URI> uris = service.getUris();
		if (CollectionUtils.isEmpty(uris)) {
			throw new RuntimeException("failed to get base URIs");
		}

		for(URI uri: uris) {
			if (uri.getScheme().equals(scheme)) {
				return uri.toString();
			}
		}
		return null;
	}

	public String applicationName() {
		if(applicationName == null) {
			if (System.getProperty("application") == null) {
				if (System.getenv("APPLICATION") != null) {
					applicationName = System.getenv("APPLICATION");
				} else if (System.getProperty("venusTargetProject") != null) {
					applicationName = System.getProperty("venusTargetProject");
				} else {
					logger.error("application must be set for service discovery to work");
				}
			}
		}
		return applicationName;
	}

	private void setupApplication() {
		if(System.getProperty("application") == null) {
			System.setProperty("application", applicationName());
		}
	}

	private void setupEnvironment() {
		if (System.getProperty("environment") == null) {
			String env = null;
			// pass squadronEnvironment from java cmd in pipeline
			// which is basically the squadron environment ENV parameter on the jenkins job
			if (System.getProperty("squadronEnvironment") != null) {
				env = System.getProperty("squadronEnvironment");
			} else if(System.getProperty("venusTargetProjectBaseUrl") != null) {
				String baseUrl = System.getProperty("venusTargetProjectBaseUrl");
				env = StringUtils.substringAfter(baseUrl, applicationName() + "-");
				env = StringUtils.substringBefore(env, ".");
				// on jenkins ENVIRONMENT env variable is set to aws which we don't want to consider
			} else if (System.getenv("ENVIRONMENT") != null && !System.getenv("ENVIRONMENT").trim().equals("aws") ) {
				env = System.getenv("ENVIRONMENT");
			} else {
				logger.error("environment must be set for service discovery to work");
			}
			System.setProperty("environment", env);
		}
	}

	private void setupConsul() {
		if (System.getProperty("consul.url") == null) {
			System.setProperty("consul.url", "https://consul-qa.a-ue1.dotdash.com:8500");
		}
		if (System.getProperty("consul.enabled") == null) {
			System.setProperty("consul.enabled", "true");
		}
		if (System.getProperty("consul.token") == null) {
			System.setProperty("consul.token", "dev");
		}

	}
}
