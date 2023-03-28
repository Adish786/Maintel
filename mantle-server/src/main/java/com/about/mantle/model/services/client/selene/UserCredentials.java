package com.about.mantle.model.services.client.selene;

import java.io.Serializable;

public class UserCredentials implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private String secret;

	public UserCredentials(String username, String secret) {
		this.username = username;
		this.secret = secret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}