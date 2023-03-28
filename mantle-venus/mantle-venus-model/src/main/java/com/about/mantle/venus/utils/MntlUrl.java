package com.about.mantle.venus.utils;
import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.venus.core.utils.ConfigurationProperties;
import com.about.venus.core.utils.Url;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.about.mantle.venus.utils.MntlConfigurationProperties.getAdditionalQueryParamValues;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.junit.Assert.fail;

public class MntlUrl extends Url{
	protected static final Logger logger = LoggerFactory.getLogger(MntlUrl.class);
	private static final String PROJECT_BASE_URL = trimToNull(ConfigurationProperties.getTargetProjectBaseUrl(null));
	public MntlUrl(String url) {
		super( url);
	}

	public MntlUrl(String url, boolean forceAdsEnabled) {
		super( url, forceAdsEnabled);
	}
	
	public MntlUrl(String url, String environment, String port, boolean forceAdsEnabled) {
		super(url, environment, port, forceAdsEnabled);
	}

	public MntlUrl(String url, List<NameValuePair> nvps)
	{
		super(url, nvps);
	}

	public MntlUrl(String url, String environment, String port, List<NameValuePair> params) {
		super(url, environment, port, params);
	}

	private static String url(String url, boolean adTest) {
		List<NameValuePair> nvp = new ArrayList<>();
		nvp.add(new BasicNameValuePair("globeNoTest", "true"));
		if(adTest == true)
			nvp.add(new BasicNameValuePair("kw", "automation"));
		// additional query parameters can be passed as an argument as ampersand separated String to the test
		// theses query parameters and their corresponding values will be added to test url
		BasicNameValuePair[] additionalQueryParams = getAdditionalQueryParamValues();
		if(additionalQueryParams != null) {
			// add all additional query parameters to the url
			for(BasicNameValuePair additionalQueryParam: additionalQueryParams) {
				nvp.add(additionalQueryParam);
			}
		}
		return new MntlUrl(url, nvp).url();
	}

	public static String nonAdUrl(String url) {
		return url(url,false);
	}

	public static String adTestUrl(String url) {
		return url(url,true);
	}

	@Override
	protected void createUrl(String url, String environment, String port, List<NameValuePair> params) {
		if (!url.contains("://")) url = PROJECT_BASE_URL + url;
		super.createUrl(url, environment, port, params);
		logger.info(String.format("URL: %s", this.url));
	}

	@Override
	protected void createUrl(String url, String environment, String port, boolean adsEnabled) {
		PlatformUrlDataFactory urlDataFactory = new ValidDomainUrlDataFactory(ConfigurationProperties.getTargetProject(null));
		ExternalUrlData.Builder builder = ExternalUrlData.builder(urlDataFactory);
		String inHouseAdsQueryString = "kw_only=automation";
		logger.info("the input to the MntlUrl class (without base url added ) was  " + url);
		if (!url.contains("://")) {
			url = PROJECT_BASE_URL + url;
		}
		try {
			builder = builder.from(url);
			if (port != null) {
				builder = builder.environment(environment).port(
						Integer.parseInt(port));
			} else if (environment != null) {
				builder = builder.environment(environment);
			}
			this.url = builder.build().getUrl();
			int indexQuestion = this.url.indexOf('?');
			if (!adsEnabled) {
				if (indexQuestion==-1) {
					builder.query("google_nofetch=true");
				} else {
					builder.query("google_nofetch=true&" + this.url.substring(indexQuestion + 1));
				}
			}
          // GLBE-6912 - below code will uncover few of invalid use of mntlUrl class in tests and will fail the tests. Commenting it for now and will make it a breaking change as fix require some effort
      /*    else if(!MntlConfigurationProperties.withGoogleAds()) {
				if (indexQuestion==-1) {
					builder.query(inHouseAdsQueryString);
				} else {
					builder.query(inHouseAdsQueryString+ "&" + this.url.substring(indexQuestion + 1));
				}
			}
            */
			
		} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e1) {
			e1.printStackTrace();
			fail("UnsupportedEncodingException : URL: " + url + " seems to be malformed");
		}
		catch (IllegalArgumentException e2) {
			e2.printStackTrace();
			logger.info("IllegalArgumentException :skipping for bad url : " + url);
		}
		this.url = builder.build().getUrl();
		logger.info("UrlCreated by MntlUrl class is " + this.url);
	}
	public String getDomain() {
		return urlBuilder(this.url).build().getDomain();
	}

	/**
	 * Will return base prod url
	 * eg. given the following url:
	 * https://www.finance-finance-dev-us-east-1.external3.persistent.app.qa.aws.investopedia.com/volatility-across-asset-classes-raises-giant-red-flag-4767591
	 * it will return
	 * https://www.investopedia.com
	 * @return
	 */
	public String getProdUrl() {
		ExternalUrlData externalUrl = urlBuilder(this.url).build();
		return  this.url.replace(externalUrl.getSubdomain()+".", "");
	}
}
