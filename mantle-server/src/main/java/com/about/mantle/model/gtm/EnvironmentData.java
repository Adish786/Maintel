package com.about.mantle.model.gtm;

import static org.apache.commons.lang3.StringUtils.defaultString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder=EnvironmentData.Builder.class)
public class EnvironmentData {

	private final Environment environment;
	private final Server server;
	private final Resources resources;
	private final Client client;

	private EnvironmentData(Builder builder) {
		this.environment = builder.environment;
		this.server = builder.server;
		this.resources = builder.resources;
		this.client = builder.client;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		private Environment environment;
		private Server server;
		private Resources resources;
		private Client client;

		public Builder environment(Environment environment) {
			this.environment = environment;
			return this;
		}

		public Builder server(Server server) {
			this.server = server;
			return this;
		}

		public Builder resources(Resources resources) {
			this.resources = resources;
			return this;
		}

		public Builder client(Client client) {
			this.client = client;
			return this;
		}

		public EnvironmentData build() {
			return new EnvironmentData(this);
		}
	}

	public Environment getEnvironment() {
		return environment;
	}

	public Server getServer() {
		return server;
	}

	public Resources getResources() {
		return resources;
	}

	public Client getClient() {
		return client;
	}

	public static class Environment {
		private final String environment;
		private final String application;
		private final String dataCenter;
		
		@JsonCreator
		public Environment(@JsonProperty("environment") String environment, 
				@JsonProperty("application") String application, 
				@JsonProperty("dataCenter") String dataCenter) {
			this.environment = defaultString(environment, "");
			this.application = defaultString(application, "");
			this.dataCenter = defaultString(dataCenter, "");
		}

		public String getEnvironment() {
			return environment;
		}

		public String getApplication() {
			return application;
		}

		public String getDataCenter() {
			return dataCenter;
		}

	}

	public static class Server {
		private final String version;
		private final String title;
		
		@JsonCreator
		public Server(@JsonProperty("version") String version, @JsonProperty("title") String title)  {
			this.version = defaultString(version, "");

			this.title = defaultString(title, "");
		}

		public String getVersion() {
			return version;
		}

		public String getTitle() {
			return title;
		}

	}

	public static class Resources {

		private final String version;
		private final String loadStartTime;
		private final String loadTimeTaken;

		@JsonCreator
		public Resources(@JsonProperty("version") String version, 
				@JsonProperty("loadStartTime") String loadStartTime,
				@JsonProperty("loadTimeTaken") String loadTimeTaken) {
			this.version = defaultString(version, "");
			this.loadStartTime = defaultString(loadStartTime, "");
			this.loadTimeTaken = defaultString(loadTimeTaken, "");
		}

		public String getVersion() {
			return version;
		}

		public String getLoadStartTime() {
			return loadStartTime;
		}

		public String getLoadTimeTaken() {
			return loadTimeTaken;
		}

	}

	public static class Client {
		
		private final String serverUA;
		private final String deviceType;
		private final String usStateCode;

		@JsonCreator
		public Client(@JsonProperty("serverUA") String serverUA, 
				@JsonProperty("deviceType") String deviceType,
				@JsonProperty("usStateCode") String usStateCode) {
			this.serverUA = defaultString(serverUA, "");
			this.deviceType = defaultString(deviceType, "");
			this.usStateCode = defaultString(usStateCode, "");
		}

		public String getServerUA() {
			return serverUA;
		}

		public String getDeviceType() {
			return deviceType;
		}
		
		public String getUsStateCode() {
			return usStateCode;
		}

	}
}
