package com.about.mantle.utils.selene.restassured;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.mapper.ObjectMapperType.GSON;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

public class RequestSpec {
	private static RequestSpecification INSTANCE = null;

	private RequestSpec() {}

	public static synchronized RequestSpecification getInstance(String baseUrl) {
		if(INSTANCE == null) {
			synchronized (RequestSpec.class) {
				RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig(GSON))
													  .encoderConfig(new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))
													  .logConfig(new LogConfig().enableLoggingOfRequestAndResponseIfValidationFails());
				JsonSchemaValidator.settings = settings().with().jsonSchemaFactory(
						JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder()
																										 .setDefaultVersion(DRAFTV4).freeze()).freeze());
				RestAssured.baseURI = baseUrl;
				RestAssured.requestSpecification = new RequestSpecBuilder().build();
				INSTANCE = RestAssured.requestSpecification;
			}
		}
		return INSTANCE;
	}
}
