package com.about.mantle.utils.url;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {

	Logger logger = LoggerFactory.getLogger(URLUtils.class);

	private final String url;
	private static final String PROD_SERVERS_SELENE = "selene-.*-prod.*\\.dotdash\\.com";
	private static final String PROD_URL = "https:\\/\\/www\\.[a-z]{6,18}\\.com";

	public URLUtils(String url) {
		this.url = url;
	}

	public Long docIdFromUrl() {
		String urlWithoutQueryParams = StringUtils.substringBefore(url, "?");
		String[] splitOnDash = urlWithoutQueryParams.split("-");
		try{
			return Long.valueOf(splitOnDash[splitOnDash.length - 1]);
		}catch (NumberFormatException nfe){
			logger.info("It seems " + url + " has no docID");
			return null;
		}
	}

	/**
	 * Will check if the current BaseSeleneUrl is production or dev env
	 * @return boolean
	 */
	public boolean isProdSelene() {
		return matchPattern(PROD_SERVERS_SELENE);
	}

	/**
	 * Will check if the current BaseUrl is production or dev env
	 * @return boolean
	 */
	//TODO update this method to query consul instead of checking against a hard coded pattern
	public boolean isProd() {
		return matchPattern(PROD_URL) || url.contains("-prod");
	}

	public boolean matchPattern(String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(url);
		if (m.find()) {
			return true;
		}
		return false;
	}

}
