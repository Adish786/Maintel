package com.about.mantle.model.services.looker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Used to deserialize an error response from looker in the AbstractLookerApiService class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookerErrorResponse {
	private String message;
	private String documentation_url;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDocumentation_url() {
		return documentation_url;
	}
	public void setDocumentation_url(String documentation_url) {
		this.documentation_url = documentation_url;
	}
}