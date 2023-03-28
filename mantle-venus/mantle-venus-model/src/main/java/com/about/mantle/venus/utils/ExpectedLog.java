package com.about.mantle.venus.utils;

import com.about.hippodrome.url.ExternalUrlData;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.utils.CookieUtils;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.about.venus.core.driver.selection.Device.Mobile_ChromeEmulator;
import static com.about.venus.core.driver.selection.Device.PC;
import static com.about.venus.core.driver.selection.Device.SmartPhone;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExpectedLog {

	private static final Logger logger = LoggerFactory.getLogger(ExpectedLog.class);

	private final Entry entry = new Entry();
	private final String templateName;
	private boolean isStatic = false;
	private final TemplateMap template;

	/**
	 *
	 * @param driver
	 *            driver instance
	 * @param userID
	 *            expected userID for the url hit
	 * @param template
	 *            map of template name in the log and TemplateMap object
	 * @param userAgentString
	 *            user agent string grabbed from the network request
	 */
	public ExpectedLog(WebDriverExtended driver, String userID, TemplateMap template, String userAgentString) {
		this.template = template;
		this.templateName = template.globeTmpl();
		assertThat("template name must be set in order to continue", this.templateName, is(notNullValue()));
		entry.setWebPlatform("globe");
		entry.setClient(getCurrentIp(driver));
		DocType docType = getDocType(template.templateId(), template.globeTmpl());
		entry.setDocType(docType);
		entry.setLayout(resolveUserAgentLayout(driver));
		entry.setUserId(userID);
		entry.setUserAgent(userAgentString);
		String protocol = urlParts(driver.getCurrentUrl()).getScheme();
		String path = urlParts(driver.getCurrentUrl()).getPath();
		String domain = MntlConfigurationProperties.getProperty("venusTargetProjectBaseUrl", null).replace("https://", "");
		entry.setUrl(getUrl(protocol, domain, path));
		entry.setSessId(CookieUtils.cookieValue(driver, "Mint"));
		entry.setIsPv(true);
	}

	private DocType getDocType(String id, String name) {
		DocType docType = new DocType();
		docType.setId(id);
		docType.setName(name);
		return docType;
	}

	private UrlLog getUrl(String protocol, String domain, String path) {
		UrlLog urlLog = new UrlLog();
		urlLog.setDomain(domain);
		urlLog.setPath(path);
		urlLog.setProtocol(protocol);
		return urlLog;
	}

	private ExternalUrlData urlParts(String url) {
		try {
			return new MntlUrl(url).urlBuilder(url).environment(null).query(null).build();
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			e.printStackTrace();

		}
		return null;
	}

	private String getCurrentIp(WebDriverExtended driver) {
		String currentUrl = driver.getCurrentUrl();
		driver.get("http://checkip.amazonaws.com");
		String ip = driver.findElementEx(By.tagName("body")).getText();
		logger.info("Current IP: " + ip);
		driver.get(currentUrl);
		return ip;
	}

	private String resolveUserAgentLayout(WebDriverExtended driver) {
		Device device = driver.getDriverConfig().getDevice();
		if (device.is(PC)) {
			return "desktop";
		} else if (device.is(Mobile_ChromeEmulator) || device.is(SmartPhone)) {
			return "mobile";
		} else {
			return "tablet";
		}
	}

	public Entry getEntry() {
		return entry;
	}

	/**
	 * list of all of the validateEntry methods asserts.
	 */
	public enum validateEntryAsserts {
		authId,
		client,
		domain,
		globeTmpl,
		layout,
		path,
		protocol,
		reqId,
		templateId,
		userAgent,
		userId,
		webPlatform,
		isPv
	}

	public void validateEntry(Entry entry, ErrorCollector collector) {
		validateEntry(entry, collector, new ArrayList<>());
	}

	/**
	 * validateEntry
	 * @param entry
	 * @param collector
	 * @param excludedAsserts ArrayList<validateEntryAsserts> you want to exclude from the collector checks
	 */
	public void validateEntry(Entry entry, ErrorCollector collector, ArrayList<validateEntryAsserts> excludedAsserts) {
		List<String> templatesList = Arrays.asList("65", "3", "110", "115", "80", "29", "17", "100", "90");
		String path = this.entry.getUrl().getPath();
		collector.checkThat("for path " + path + " reqId is not correct", entry.getReqId(), matchesPattern("n\\w{32}(2[0-3]|[0-1][0-9])"));

		if (!excludedAsserts.contains(validateEntryAsserts.authId)) {
			collector.checkThat("for path " + path + " authId value did not match", entry.getAuthId(),
				templatesList.contains(this.entry.getDocType().id()) ? not(emptyOrNullString()) : emptyOrNullString());
		} else {
			logger.info("Skipping authId check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.client)) {
			collector.checkThat("for path " + path + " client value did not match", entry.getClient(), is(this.entry.getClient()));
		} else {
			logger.info("Skipping client check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.domain)) {
			collector.checkThat("for path " + path + " domain value did not match", entry.getUrl().getDomain(), is(this.entry.getUrl().getDomain()));
		} else {
			logger.info("Skipping domain check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.globeTmpl)) {
			collector.checkThat("for path " + path + " globeTmpl value did not match", entry.getGlobeTmpl(), not(emptyOrNullString()));
		} else {
			logger.info("Skipping globeTmpl check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.layout)) {
			collector.checkThat("for path " + path + " layout value did not match", entry.getLayout(),
				is(this.entry.getLayout()));
		} else {
			logger.info("Skipping layout check");
		}

		if (!path.contains("about-us") && !path.contains("legal") && !path.contains("search")) {
			if (!excludedAsserts.contains(validateEntryAsserts.path)) {
				collector.checkThat("for path " + path + " name value did not match", entry.getDocType().name(), is(this.entry.getDocType().name()));
			} else {
				logger.info("Skipping path check");
			}
		}

		if (!excludedAsserts.contains(validateEntryAsserts.protocol)) {
			collector.checkThat("for path " + path + " protocol value did not match", entry.getUrl().getProtocol(), is("https"));
		} else {
			logger.info("Skipping protocol check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.reqId)) {
			collector.checkThat("for path " + path + " reqId is not correct", entry.getReqId(), matchesPattern("n\\w{32}(2[0-3]|[0-1][0-9])"));
		} else {
			logger.info("Skipping reqId check");
		}

		if (!path.contains("about-us") && !path.contains("legal") && !path.contains("search")) {
			if (!excludedAsserts.contains(validateEntryAsserts.templateId)) {
				collector.checkThat("for path " + path + " templateId value did not match", entry.getDocType().id(),
					is(this.entry.getDocType().id()));
			} else {
				logger.info("Skipping templateId check");
			}
		}

		if (!excludedAsserts.contains(validateEntryAsserts.userAgent)) {
			collector.checkThat("for path " + path + " userAgent value did not match", entry.getUserAgent(), is(this.entry.getUserAgent())); 
		} else {
			logger.info("Skipping userAgent check");
		}
		
		if (!excludedAsserts.contains(validateEntryAsserts.userId)) {
			collector.checkThat("for path " + path + " userId value did not match", entry.getUserId(), is(this.entry.getUserId()));
		} else {
			logger.info("Skipping userId check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.webPlatform)) {
			collector.checkThat("for path " + path + " webPlatform value did not match", entry.getWebPlatform(), is(this.entry.getWebPlatform()));
		} else {
			logger.info("Skipping webPlatform check");
		}

		if (!excludedAsserts.contains(validateEntryAsserts.isPv)) {
			collector.checkThat("for path " + path + " isPv value did not match", entry.getIsPv(), is(this.entry.getIsPv()));
		} else {
			logger.info("Skipping isPv check");
		}
	}
}