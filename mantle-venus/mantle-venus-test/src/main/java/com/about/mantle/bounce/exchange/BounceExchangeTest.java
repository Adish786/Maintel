package com.about.mantle.bounce.exchange;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusProxy;
import com.google.common.base.Predicate;
import org.apache.http.NameValuePair;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.about.mantle.venus.utils.GeoIps.AUSTRALIA;
import static com.about.mantle.venus.utils.GeoIps.CANADA;
import static com.about.mantle.venus.utils.GeoIps.INDIA;
import static com.about.mantle.venus.utils.GeoIps.UK;
import static com.about.mantle.venus.utils.GeoIps.US;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

public class BounceExchangeTest implements MntlCommonTestMethods {

	private final String bounceExchangeUrlPrefix = "//tag.bounceexchange.com/";
	private final String bounceExchangeUrlSuffix = "/i.js";
	
	private String url;
	private VenusProxy proxy;
	private WebDriverExtended driver;
	private String region;
	private String bounceUrl;
	private String bounceSrc;
	private ErrorCollector collector;
	private List<NameValuePair> headers;
	
	public BounceExchangeTest(String url, VenusProxy proxy, WebDriverExtended driver, String region, String bounceID, ErrorCollector collector) {
		this.url = url;
		this.proxy = proxy;
		this.driver = driver;
		this.region = region;
		this.bounceSrc = bounceExchangeUrlPrefix + bounceID + bounceExchangeUrlSuffix;
		this.bounceUrl = "https:" + bounceSrc;
		this.collector = collector;
		setHeaders();
	}
	
	private Consumer<MntlRunner> bounceExchangeNetowrkRequestTest = runner -> {
		runner.page().waitFor().entries(runner.proxyPage(),"bounce",1,30);
		runner.driver().waitFor((Predicate<WebDriver>) webdriver -> runner.proxy().capture().page(runner.proxyPage()).entries("bounce").stream().
				filter(e -> e.request().url().url().contains("i.js")).collect(Collectors.toList()).size() >= 1, 120);
		List<VenusHarEntry> bounceRequests = runner.proxy().capture().page(runner.proxyPage()).entries("bounce").stream().filter((entry) -> entry.request().url().url().contains("i.js"))
			.collect(Collectors.toList());
		collector.checkThat("bounceExchange Request entry is not present ", bounceRequests.get(0).request().url().url(), is(this.bounceUrl));	
	};
	
	private Consumer<MntlRunner> bounceExchangeScriptTest = runner -> {
		String pageSource = runner.driver().getPageSource();
		collector.checkThat("bounceExchange script is not present in page source ", pageSource, containsString(this.bounceSrc));
	};
	
	private Consumer<MntlRunner> bounceExchangeNoScriptTest = runner -> {
		String pageSource = runner.driver().getPageSource();
		collector.checkThat("bounceExchange script is present in page source ", pageSource.contains(this.bounceSrc), is(false));
	};
	
	private void setHeaders() {
		switch(this.region.toUpperCase()) {
		case "USA":
			this.headers = US.geoLocation();
			break;
		case "CANADA":
			this.headers = CANADA.geoLocation();
			break;
		case "UK":
			this.headers = UK.geoLocation();
			break;
		case "INDIA":
			this.headers = INDIA.geoLocation();
			break;
		case "AUSTRALIA":
			this.headers = AUSTRALIA.geoLocation();
			break;	
		default:
			throw new IllegalArgumentException(this.region + "is not supported!");
		}
	}
	
	public void runBounceExchangeTest() {
		startTest(this.url, this.driver).withProxyAndHeaders(this.proxy, headers).loadUrl().runTest(bounceExchangeNetowrkRequestTest).runTest(bounceExchangeScriptTest);
	}
	
	public void runBounceExchangeNegativeTest() {
		startTest(this.url, this.driver).withProxyAndHeaders(this.proxy, headers).loadUrl().runTest(bounceExchangeNoScriptTest);
	}
}
