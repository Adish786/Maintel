package com.about.mantle.spring.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.metrics.concurrent.CustomInstrumentQueuedThreadPool;

import ch.qos.logback.access.jetty.RequestLogImpl;

import javax.annotation.PreDestroy;

@Configuration
public class JettyConfigurations {
	private JettyBaseConfig jettyBaseConfig = null;

	@PreDestroy
	public void destroy() {
		jettyBaseConfig.destroy();
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() throws Exception {

		JettyServletWebServerFactory factory = new JettyServletWebServerFactory();

		jettyBaseConfig = new JettyBaseConfig();

		JettyAppConfig config = jettyBaseConfig.getJettyAppConfig();

		factory.setThreadPool(new CustomInstrumentQueuedThreadPool(jettyBaseConfig.getConfiguredMetricRegistry(),
				config.getMaxThreads(), config.getMinThreads(), config.getIdleTimeout()));

		factory.addServerCustomizers(server -> {
			try {
				// remove default connectors added by spring boot's jetty
				for (Connector connector : server.getConnectors()) {
					if (connector instanceof ServerConnector) {
						server.removeConnector(connector);
					}
				}
				
				// Add our custom connectors, don't add secondary connector as it is obsolete
				jettyBaseConfig.addConnectors(server, JettyBaseConfig.HTTP_PRIMARY_PORT,
						JettyBaseConfig.HTTPS_PRIMARY_PORT, config);

				// Register loggers as MBeans
				jettyBaseConfig.setupJettyJmxBean(server);

				// Add thumbor proxy servlet
				((ServletContextHandler) server.getHandler()).addServlet(jettyBaseConfig.getThumborProxyServlet(),
						JettyBaseConfig.THUMBOR_PROXY_PREFIX + "/*");

				// Add caes proxy servlet
				ServletHolder caesProxy = jettyBaseConfig.getCaesProxyServlet();
				if (caesProxy != null) {
					// Handling '/product' and '/product/' but no wildcard paths, to avoid overlap with legacy urls
					((ServletContextHandler) server.getHandler()).addServlet(caesProxy,
							JettyBaseConfig.CAES_PROXY_PREFIX);
					((ServletContextHandler) server.getHandler()).addServlet(caesProxy,
							JettyBaseConfig.CAES_PROXY_PREFIX + "/");
				}

				// Add orion proxy servlet
				ServletHolder orionProxy = jettyBaseConfig.getOrionProxyServlet();
				if (orionProxy != null) {
					((ServletContextHandler) server.getHandler()).addServlet(orionProxy,
							JettyBaseConfig.ORION_PROXY_PREFIX + "/*");
				}

				// add different handlers
				Handler handler = jettyBaseConfig.configureHandlers(server.getHandler());
				server.setHandler(handler);
				
				//Jetty 9.4 doesn't start requestLogger because of - https://stackoverflow.com/q/56657190/1478852, so we have to start it manually.
				//work-around - https://github.com/qos-ch/logback/pull/269#issuecomment-155881582
				server.setRequestLog(jettyBaseConfig.getRequestLog());
				((RequestLogImpl)(server.getRequestLog())).start();

			} catch (Exception e) {
				throw new GlobeException("Failed to configure Jetty server", e);
			}
		});
		return factory;
	}

}
