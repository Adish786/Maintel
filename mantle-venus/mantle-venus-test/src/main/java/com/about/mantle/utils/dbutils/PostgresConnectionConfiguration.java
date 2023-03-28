package com.about.mantle.utils.dbutils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.utils.ConfigProperties;
import com.dotdash.services.core.config.PropertyFactoryWrapper;

public class PostgresConnectionConfiguration {
	private static Logger logger = LoggerFactory.getLogger(PostgresConnectionConfiguration.class);
	static final Properties properties = ConfigProperties.loadFileProperties(ConfigProperties.NEBULA_PROPERTIES_FILE);
	static final String POSTGRES_SERVICE_NAME = properties.getProperty(ConfigProperties.NEBULA_SQUADRON_DB_RESOURCE);
	private final PropertyFactoryWrapper propertyFactory ;
	private static final String POSTGRES_SSLMODE = "com.dotdash.postgres.sslmode";
	private static final String RDS_CERTS_FILE = "com.dotdash.rds.certs.file";
	// valid ssl modes: https://jdbc.postgresql.org/documentation/head/ssl-client.html
	// All base-run images have RDS cert downloaded and installed at /usr/share/certs/rds-ca-2019-root.pem
	private static final List<String> SSL_MODES = new ArrayList<String>(Arrays.asList(new String[] {"disable", "allow", "prefer", "require", "verify-ca", "verify-full"}));//List.of("disable", "allow", "prefer", "require", "verify-ca", "verify-full");
	private static final String DFT_SSL_MODES = "require";
	private static final String DFT_RDS_CERT_FILE = "/usr/share/certs/rds-ca-2019-root.pem";

	public PostgresConnectionConfiguration(PropertyFactoryWrapper propertyFactoryWrapper) {
		logger.info("Set propertyFactoryWrapper as : " + propertyFactoryWrapper);
		propertyFactory = propertyFactoryWrapper;
	}

	public String buildJdbcUrl(List<URI> uriList, String dbName, boolean isMaster) {
		StringBuilder builder = new StringBuilder();
		if (CollectionUtils.isNotEmpty(uriList)) {

			builder.append("jdbc:postgresql://");
			builder.append(uriList.stream().map(s -> String.format("%s:%d", s.getHost(), s.getPort()))
					.collect(Collectors.joining(",")));
			builder.append("/").append(dbName);
			if (isMaster) {
				builder.append("?targetServerType=master");
			} else {
				builder.append("?targetServerType=secondary");
			}

			builder.append("&").append(buildSSLConnectionParameters(uriList.get(0)));
		}

		String jdbcUrl = builder.toString();
		logger.info("postgres jdbc url: {}", jdbcUrl);

		return jdbcUrl;
	}

	public String getProperty(String propertyName) {
		return propertyFactory.getProperty(propertyName, String.class);
	}

	public String getSecuredProperty(String propertyName) {
		try {
			return propertyFactory.getEncryptedProperty(propertyName);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load secured property: " + e.getMessage());
		}
	}

	private String buildSSLConnectionParameters(URI postgresUri) {
		String sslMode = propertyFactory.getProperty(POSTGRES_SSLMODE, String.class, DFT_SSL_MODES);
		String rdsCertFile = propertyFactory.getProperty(RDS_CERTS_FILE, String.class, DFT_RDS_CERT_FILE);

		boolean sslRequiredOnRDS = sslRequiredOnRDS(postgresUri);

		if (!SSL_MODES.contains(sslMode)) {
			throw new RuntimeException("invalid sslMode: " + sslMode);
		}

		if (sslRequiredOnRDS && sslMode.equals("disable")) {
			throw new RuntimeException("cannot disable ssl. It's required on RDS");
		}

		StringBuilder sb = new StringBuilder("sslmode=").append(sslMode);
		if (sslMode.equals("verify-ca") || sslMode.equals("verify-full")) {
			sb.append("&sslrootcert=").append(rdsCertFile);
		}

		// we don't set "sslfactory". The default one (LibPQFactory) will be used

		return sb.toString();
	}

	private boolean sslRequiredOnRDS(URI postgresUri) {
		return (StringUtils.isNotEmpty(postgresUri.getQuery())
				&& postgresUri.getQuery().toLowerCase().indexOf("ssl=true") != -1);
	}
}