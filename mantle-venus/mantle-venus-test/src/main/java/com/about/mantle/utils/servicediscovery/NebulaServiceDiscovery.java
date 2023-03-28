package com.about.mantle.utils.servicediscovery;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dotdash.services.core.config.PropertyFactoryWrapper;

import com.about.hippodrome.config.servicediscovery.Service;
import com.about.mantle.utils.ConfigProperties;
import com.about.mantle.utils.dbutils.PostgresConnectionConfiguration;

@Configuration
public class NebulaServiceDiscovery {
	static final Properties properties = ConfigProperties.loadFileProperties(ConfigProperties.NEBULA_PROPERTIES_FILE);
	static final String POSTGRES_SERVICE_NAME = properties.getProperty(ConfigProperties.NEBULA_SQUADRON_DB_RESOURCE);
	private static final Logger logger = LoggerFactory.getLogger(NebulaServiceDiscovery.class);

	// Set environment variables in static block
  	static {
  		System.setProperty("environment", properties.getProperty(ConfigProperties.NEBULA_SQUADRON_ENVIRONMENT));
		System.setProperty("application", properties.getProperty(ConfigProperties.NEBULA_APP_TITLE));
		System.setProperty("consul.enabled", properties.getProperty(ConfigProperties.NEBULA_CONSUL_ENABLED));
      	System.setProperty("consul.url", properties.getProperty(ConfigProperties.NEBULA_CONSUL_URL));
		System.setProperty("consul.token", properties.getProperty(ConfigProperties.NEBULA_CONSUL_TOKEN));
		logger.info("Set environment variables for 'consul.url', 'consul.enabled', 'consul.token', 'application', 'environment'");
	}
  
	@Inject
	private PropertyFactoryWrapper propertyFactory= new PropertyFactoryWrapper();

	@Bean
	public PostgresConnectionConfiguration postgresConnectionConfiguration() {
		return new PostgresConnectionConfiguration(propertyFactory);
	}

	@Bean
	public List<URI> getURIList(){
		Service postgresService = propertyFactory.getService(POSTGRES_SERVICE_NAME, null);
		if (postgresService == null || CollectionUtils.isEmpty(postgresService.getUris())) {
			throw new IllegalStateException("Failed to get visual baseline updater postgres server address");
		}
		return postgresService.getUris();
	}

	@Bean 
	public String getJDBCUrl() {
		String jdbcUrl = null;
		String dbName = null;
		List<URI> uriList = getURIList();
		logger.info("Postgres Services list ::{}",uriList);
		if (!uriList.isEmpty()) {
			dbName = postgresConnectionConfiguration().getProperty(properties.getProperty(ConfigProperties.NEBULA_DB_TITLE));
			jdbcUrl = postgresConnectionConfiguration().buildJdbcUrl(uriList, dbName, true);
		} else {
			throw new RuntimeException("failed to find the postgres db url");
		}

		return jdbcUrl;
	}
}