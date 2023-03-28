package com.about.mantle.venus.model.components.ads;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusProxy;
import com.google.common.base.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public class AdCallObject {
		
	private static final Logger logger = LoggerFactory.getLogger(AdCallObject.class);
	
	private final VenusProxy proxy;
	private final String proxyPage;
	private final WebDriverExtended driver;
	private final Map<String,List<String>> paramsExpected;
	private final String adType;
	private List<String> missingParams;

	public AdCallObject(VenusProxy proxy, String proxyPage, WebDriverExtended driver, Map<String,List<String>> params, String adType) {
		this.proxy = proxy;
		this.proxyPage = proxyPage;
		this.driver = driver;
		this.paramsExpected = params;
		this.adType = adType;
	}
	
	private VenusHarEntry adCallEntry() {
		List<VenusHarEntry> entries = proxy
			.capture()
			.page(proxyPage)
			.entries("ads?")
			.stream()
			.filter(entry -> entry.request().url().url().contains(adType))
			.collect(Collectors.toList());

		// check that an ad call exists with expected params 
		for(VenusHarEntry entry: entries) {
			boolean paramsFound = true;
 			for(String param: paramsExpected.keySet()) {
				//check next entry if all params were not found in this entry
				// trim url up to the first ? otherwise the URLEncodedUtils.parse will include this as part of the first url param entry
				int index = entry.request().url().url().indexOf("?");
				String url = entry.request().url().url().substring(index + 1);
				Iterator<NameValuePair> adCallParams = URLEncodedUtils.parse(url,Charset.forName("UTF-8")).iterator();
				boolean paramFound = false;
				while (adCallParams.hasNext()){
					NameValuePair nvp = adCallParams.next();
					List<String> expectedParams = paramsExpected.get(param);
					String name = nvp.getName();
					String value = nvp.getValue();
					if(name.equals("scp") || name.equals("cust_params") || name.equals("prev_scp")) {
						if (expectedParams.size() == 0) { // if no expectedParams - were just looking for existence of param
							if (value.contains(param + "=")) {
								paramFound = true;
								break;
							}
						} else { // else we are expecting to match a param value
							int i = 0;
							for (String paramValue : expectedParams) {
								String filter = String.join("=", param, paramValue);
								if (!nvp.getValue().contains(filter)) {
									break;
								}
								i++;
							}
							if (i >= expectedParams.size()) paramFound = true;
						}
					} else if(name.equals(param)) {
						if(expectedParams.size() == 0) { // if no expectedParams - were just looking for existence of param
							paramFound = true;
							break;
						} else { // else we are expecting to match a param value
							int i = 0;
							for (String paramValue : expectedParams) {
								if (!nvp.getValue().contains(paramValue)) break;
								i++;
							}
							if (i >= expectedParams.size()) paramFound = true;
						}
					}
				}
				if(paramFound) {
					missingParams.remove(param);
				} else {
					paramsFound = false;
				}
			}
			//if this entry has all the expected params then its the entry we are looking for
			if(paramsFound) {
				return entry;
			} 
		}
		return null;
	}
	
	public void testMissingParameters() {
		missingParams = new ArrayList<String>(paramsExpected.keySet());
		try {
			driver.waitFor((Predicate<WebDriver>) webdriver -> adCallEntry() != null, 60);
			throw new RuntimeException("All the parameters were found on the page");
		} catch(TimeoutException e){
			ArrayList<String> allExpectedParams = new ArrayList<String>(paramsExpected.keySet());
			allExpectedParams.removeAll(missingParams);
			StringBuilder sb = new StringBuilder();
			for (String param : allExpectedParams) {
				sb.append(param + " " + paramsExpected.get(param) +  " key value pair was found when it's not expected to be presented\n");
			}
			if(allExpectedParams.size() > 0) 
				throw new RuntimeException(sb.toString());
		}
	}

	/**
	 *
	 * Check that the url params are coming back as expected, if not throw error indicating what is missing
	 * and stop test with assert. default of 20 seconds to find all adCalls
	 * @param collector - Error collector
	 * @return
	 */
	public UrlParams urlParams(ErrorCollector collector) {
	 	return urlParams(collector, 20);
	}

	/**
	 * Check that the url params are coming back as expected, if not throw error indicating what is missing
	 * and stop test with assert
	 * @param collector - Error collector
	 * @param timeoutSec - timeout in seconds to wait for ad params to all be found
	 * @return UrlParams
	 */
	public UrlParams urlParams(ErrorCollector collector, int timeoutSec) {
		missingParams = new ArrayList<>(paramsExpected.keySet());
		ArrayList<String> assertMessages = new ArrayList<>();
		try {
			driver.waitFor((Predicate<WebDriver>) webdriver -> adCallEntry() != null, timeoutSec);
		} catch(TimeoutException e){
			Iterator<String> iter = missingParams.iterator();
			while(iter.hasNext()) {
				String param = iter.next();
				String message = String.format("param: %s with value %s - not found in ad calls.", param, paramsExpected.get(param));
				assertMessages.add(message);
			}
		}
		assertThat("Errors found in locating Url Params", assertMessages, IsEmptyCollection.empty());
		return new UrlParams(adCallEntry().request());
	}
}
