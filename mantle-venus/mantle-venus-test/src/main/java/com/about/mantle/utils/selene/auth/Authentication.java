package com.about.mantle.utils.selene.auth;

import com.about.mantle.utils.selene.auth.api.User;
import com.about.mantle.utils.selene.auth.client.AuthenticationClient;

//Ensure that only one token is created
public class Authentication {
	private static String TOKEN = null;

	private Authentication(){}

	public static String getToken() {
		if(TOKEN == null) {
			synchronized (Authentication.class) {
				if (TOKEN == null) {
					TOKEN = new Authentication().getToken(User.builder().build());
				}
			}
		}
		return TOKEN;
	}

	private String getToken(User user) {
		return new AuthenticationClient().getSignedToken(user.getUsername(), user.getSecret());
	}
}
