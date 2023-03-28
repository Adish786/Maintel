package com.about.mantle.venus.utils;

import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.utils.ConfigurationProperties;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.junit.Assert.fail;

public class MntlConfigurationProperties extends ConfigurationProperties {

	private static final String CHAPTERS_ENABLED = "chaptersEnabled";
	public static final String PROJECT_BASE_URL = trimToNull(ConfigurationProperties.getTargetProjectBaseUrl(null));
	protected static final String URL_ENVIRONMENT = trimToNull(ConfigurationProperties.getUrlEnvironment(null));
	public static final String ENV = getUrlEnvironment();
	private static final String IS_PROD = "isProd";
	private static final String WITH_GOOGLE_ADS = "withGoogleAds";
	private static final Device ACTUAL_DEVICE = ConfigurationProperties.getDevice(Device.PC);
	public static boolean IS_MOBILE = ACTUAL_DEVICE.is(Device.Mobile_ChromeEmulator) || ACTUAL_DEVICE.is(Device.SmartPhone);
	public static boolean IS_TABLET = ACTUAL_DEVICE.is(Device.Tablet_ChromeEmulator) || ACTUAL_DEVICE.is(Device.Tablet);
	public static boolean IS_PC =  IS_MOBILE ? false : IS_TABLET ? false : true;
	// this should contain an ampersand separated list of additional query parameters that need to be added to each url when running the test
	private static final String TEST_WITH_ADDITIONAL_QUERY_PARAMETER_VALUES = "testWithAdditionalQueryParameterValues";

	/**
	 * Gets the Chapters Enabled property and returns that value or the default value
	 * 
	 * @param defaultChaptersEnabled
	 * @return the property's value or the default chapters switch
	 */
	
	public static boolean getChaptersEnabled(String defaultChaptersEnabled) {
		String chaptersEnabled = getProperty(CHAPTERS_ENABLED, defaultChaptersEnabled);
		if(chaptersEnabled.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	private static String getUrlEnvironment() {

		if (URL_ENVIRONMENT != null) {
			return URL_ENVIRONMENT;
		} else {

			PlatformUrlDataFactory urlDataFactory = new ValidDomainUrlDataFactory(
					ConfigurationProperties.getTargetProject(null));
			ExternalUrlData.Builder builder = ExternalUrlData.builder(urlDataFactory);
			try {
				if(!PROJECT_BASE_URL.contains("prod") && PROJECT_BASE_URL.contains("a-ue1") || PROJECT_BASE_URL.contains("qa.aws"))
					{
					return builder.from(PROJECT_BASE_URL).build().getEnvironment();
					}
				else {
					return "";
				}
			} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
				e.printStackTrace();
				fail("UnsupportedEncodingException url seems to be malformed");
			}
		}
		return null;
	}
	
	public static boolean isProdEnv() {
		boolean isProd = PROJECT_BASE_URL.contains("prod") || !PROJECT_BASE_URL.contains("a-ue1") && !PROJECT_BASE_URL.contains("qa.aws");
		return isProd;
	}
	
	public static boolean withGoogleAds() {
		String isProd = getProperty(WITH_GOOGLE_ADS, "false");
		return Boolean.parseBoolean(isProd);
	}

	public static BasicNameValuePair[] getAdditionalQueryParamValues() {
		String propertyValue = getProperty(TEST_WITH_ADDITIONAL_QUERY_PARAMETER_VALUES, null);
		if(propertyValue != null) {
			return arrayFromAmpersandSeparatedString(propertyValue);
		}
		return null;
	}

	private static BasicNameValuePair[] arrayFromAmpersandSeparatedString(String ampersandSeparatedString) {
		String[] queryParams = ampersandSeparatedString.split("&");
		BasicNameValuePair[] keyValues = new BasicNameValuePair[queryParams.length];
		for(int i = 0; i < queryParams.length ; i++) {
			String[] keyValue = queryParams[i].split("=");
			keyValues[i] = new BasicNameValuePair(trimToNull(keyValue[0]), trimToNull(keyValue[1]));
		}
		return keyValues;
	}
	
}
