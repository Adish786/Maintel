package com.about.mantle.model.services.client.selene;

import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.hippodrome.restclient.CredentialsProvider;
/**
 * Holds credentials for Selene authentication
 */
public class CredentialsProviderImpl implements CredentialsProvider {
	private static HippodromePropertyFactory PROPERTY_FACTORY; 
	private final String userNameKey;
	private final String secretKey;

	// TODO - Remove as part of GLBE-8688 when config reloading is supported
	// This will allow k8s environments to decrypt properties after token rotates
	private String secret = null;

	public CredentialsProviderImpl(String userNameKey, String secretKey) {
		PROPERTY_FACTORY = CommonPropertyFactory.INSTANCE.get();
		this.userNameKey = userNameKey;
		this.secretKey = secretKey;

	}

	@Override
	public String getUserName() {
		return PROPERTY_FACTORY.getProperty(userNameKey).asString(null).get();
	}

	/**
	 *  ps It reads encrypted property unlike getUserName method
	 */
	@Override
	public String getSecret() {
		// TODO - fix in GLBE-8688
		// Cache decrypted secret as k8s environments cannot decrypt properties when token rotates
		if (secret == null) {
			secret = PROPERTY_FACTORY.getEncryptedProperty(secretKey, null).asString(null).get();
		}
		return secret;
	}
	

}