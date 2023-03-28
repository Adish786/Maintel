package com.about.mantle.commons;

import com.about.mantle.commons.MntlCommonTestMethods.Runner;
import com.about.mantle.utils.ReadJsonData;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.ads.AdCallObject;
import com.about.mantle.venus.model.components.navigation.MntlPaginationComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.AccessLog;
import com.about.mantle.venus.utils.Direction;
import com.about.mantle.venus.utils.Entry;
import com.about.mantle.venus.utils.ExpectedLog;
import com.about.mantle.venus.utils.KafkaLogs;
import com.about.mantle.venus.utils.MntlConfigurationProperties;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.mantle.venus.utils.TemplateMap;
import com.about.mantle.venus.utils.UrlParams;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;
import com.about.venus.core.driver.proxy.VenusProxy;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.driver.selection.DriverSelection;
import com.about.venus.core.driver.selection.Platform;
import com.about.venus.core.model.Component;
import com.about.venus.core.model.Page;
import com.about.venus.core.test.ErrorCollectorEx;
import com.about.venus.core.utils.ConfigurationProperties;
import com.about.venus.core.utils.CookieUtils;
import com.about.venus.core.utils.DataLayerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPostData;
import net.minidev.json.JSONArray;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.about.venus.core.driver.selection.Device.Mobile_ChromeEmulator;
import static com.about.venus.core.driver.selection.Device.PC;
import static com.about.venus.core.driver.selection.Device.SmartPhone;
import static com.about.venus.core.driver.selection.Device.Tablet;
import static com.about.venus.core.driver.selection.Device.Tablet_ChromeEmulator;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.junit.Assert.fail;

public interface MntlCommonTestMethods<T extends Runner<T>> {

	default T startTest(String url, WebDriverExtended driver) {
		return (T) new MntlRunner(url, driver);
	}

	public static class MntlRunner extends Runner<MntlRunner>{

		public MntlRunner(String url, WebDriverExtended driver) {
			super(url, driver);
		}

	}

	public static class Runner<T extends Runner<T>> {
		protected final String url;
		protected String domain = null;
		protected boolean adsEnabled;
		protected final WebDriverExtended driver;
		protected VenusProxy proxy;
		protected String proxyPage;
		protected List<NameValuePair> queryParams;
		protected boolean pagination;
		protected List<Map<String, List<String>>> valuesToTest;
		protected Map<String, Object> valuesMap;
		protected AccessLog accessLog;
		protected ErrorCollector collector;
		protected String userId;
		protected TemplateMap template;
		protected String layout;
		protected Dimension dimension;
		protected MntlBasePage<? extends MntlComponent> page;
		protected Class<? extends MntlComponent> component;
		protected Boolean lazyload;
		protected List<Class<? extends MntlComponent>> components;
		protected boolean firstTest;
		protected Thread stopPageRefreshPeriodically;
		protected int paginateTo;
		protected Class<? extends MntlPaginationComponent> paginationComponent;
		protected MntlBasePage<? extends MntlPaginationComponent> paginationPage;
		protected KafkaLogs kfk;
		protected static final String USERID_PREFIX = "n";
		private Object testData;
		protected boolean withITO = false;

		public Runner(String url, WebDriverExtended driver) {
			this.url = url;
			this.driver = driver;
			this.adsEnabled = false;
			this.pagination = false;
			this.valuesToTest = null;
			this.page = null;
			this.firstTest = true;
			this.stopPageRefreshPeriodically = new Thread(() -> {
				while(true) {
					try {
						Thread.sleep(45000);
					} catch (InterruptedException e) {
						logger().error(e.getMessage(), e);
					}
					for(int i = 0; i < 10; i++) {
						if(stopLoading()) break;
					}
				}
			});
		}

		public Object testData() {
			return this.testData;
		}

		public T withData(Object testData) {
			synchronized(Runner.class) {
				this.testData = testData;
				return (T) this;
			}
		}

		public String getDomain() {
			this.domain = new MntlUrl(url(),true).getDomain();
			return this.domain;
		}

		public WebDriverExtended driver() {
			return this.driver;
		}

		public VenusProxy proxy() {
			return this.proxy;
		}

		public String proxyPage() {
			return this.proxyPage;
		}

		public List<Map<String, List<String>>> values() {
			return this.valuesToTest;
		}

		public T withValues(Map<String, Object> withValues) {
			this.valuesMap = withValues;
			return (T) this;
		}

		public Map<String, Object> valuesMap() {
			return this.valuesMap;
		}

		public String url() {
			return this.url;
		}

		public boolean pagination() {
			return this.pagination;
		}

		public MntlComponent component() {
			return this.page.getComponent();
		}

		public List<? extends MntlComponent> components() {
			return this.page.getComponents();
		}

		@SuppressWarnings("rawtypes")
		public MntlBasePage page() {
			return this.page;
		}

		public Class<? extends MntlComponent> componentClass() {
			return this.component;
		}

		public T withAds() {
			if (!this.adsEnabled) this.adsEnabled = true;
			return (T) this;
		}

		public T withDimension(Dimension dimension) {
			this.dimension = dimension;
			return (T) this;
		}

		public T onComponent(Class<? extends MntlComponent> component, Boolean lazyload) {
			this.component = component;
			this.lazyload = lazyload;
			if(!this.firstTest) {
				this.page = new MntlBasePage<>(driver, this.component);
			}
			return (T) this;
		}

		public T onComponent(Class<? extends MntlComponent> component) {
			return onComponent(component, false);
		}

		public T onComponents(List<Class<? extends MntlComponent>> components) {
			this.components = components;
			if(!this.firstTest) {
				this.page = new MntlBasePage<>(driver, this.component);
			}
			return (T) this;
		}

		public T withProxy(VenusProxy proxy) {
			assertThat("proxy is already set", this.proxy, is(nullValue()));
			return (T) withProxyAndHeaders(proxy, null);
		}

		public T withProxyAndHeaders(VenusProxy proxy, List<NameValuePair> headers) {
			if (this.proxy == null) {
				this.proxy = proxy;
				this.proxyPage = proxy.startPage();
			}
			if (headers != null) {
				for (NameValuePair header : headers) {
					this.proxy.addHeader(header.getName(), header.getValue());
				}
			}
			return (T) this;
		}

		public T withProxyAndTmogCookie(VenusProxy proxy) {
			synchronized (Runner.class) {
				assertThat("must call withAccessLogs before withProxyAndTmogCookie", this.accessLog,
						is(notNullValue()));
				List<NameValuePair> headers = new ArrayList<>();
				headers.add(new BasicNameValuePair("cookie", "TMog=" + this.userId));
				return (T) withProxyAndHeaders(proxy, headers);
			}
		}

		public T withQueryParams(List<NameValuePair> queryParams) {
			this.queryParams = queryParams;
			return (T) this;
		}

		public T withValuesToTest(List<Map<String, List<String>>> valuesToTest) {
			this.valuesToTest = valuesToTest;
			return (T) this;
		}

		public T withPagination(int index, Class<? extends MntlPaginationComponent> pagination) {
			if (!this.pagination) {
				this.pagination = true;
				this.paginateTo = index;
				this.paginationComponent = pagination;
			}
			return (T) this;
		}

		public T withITO() {
				this.withITO = true;
			return (T) this;
		}


		public T loadUrl() {
			this.driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			if(this.dimension != null)
				this.driver.manage().window().setSize(this.dimension);
			if(this.queryParams == null ) {
				try {
					this.driver.get(url, adsEnabled);
				} catch(TimeoutException e) {
					e.printStackTrace();
					for(int i = 0; i < 10; i++) {
						if(stopLoading()) break;
					}
				}
			} else {
				try {
					this.driver.get(url, this.queryParams, adsEnabled);
				} catch(TimeoutException e) {
					e.printStackTrace();
					for(int i = 0; i < 10; i++) {
						if(stopLoading()) break;
					}
				}
			}
			if(this.pagination)
				this.paginationPage = new MntlBasePage<>(driver, this.paginationComponent);
			this.page = new MntlBasePage<>(driver, this.component);
			if(this.component != null) {
				if(this.lazyload) {
					this.page.slowScrollToBottom();
				}
				// wait for component to be present on the page
				this.driver.waitFor((Predicate<WebDriver>) driver -> page.getComponentsRefreshed().size() > 0, 20);
			}

			if(!this.withITO) {
				driver.manage().addCookie(new Cookie("ddmCampaignSession", ""));
			}

			return (T) this;
		}

		public boolean stopLoading() {
			try {
				this.driver.executeScript("return window.stop();");
			} catch(TimeoutException e2) {
				e2.printStackTrace();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					logger().error(e1.getMessage(), e1);
				}
				return false;
			} catch (NoSuchSessionException ignore) { }
			return true;
		}

		public T runTest(Consumer<T> test) {
			assertThat("must call loadUrl before runTest", this.page, is(notNullValue()));
			test.accept((T) this);
			if(this.pagination) {
				MntlPaginationComponent paginationComponent = this.paginationPage.getComponent();
				paginationComponent.scrollIntoViewCentered();
				paginationComponent.waitFor().exactMoment(1, TimeUnit.SECONDS);
				paginationComponent.paginationItems().get(this.paginateTo).click();
				paginationComponent.waitFor().staleness(30);
				this.page = new MntlBasePage<>(driver, this.component);
				test.accept((T) this);
			}
			firstTest = false;
			return (T) this;
		}

		public T withAccessLogs(Map<String, TemplateMap> template) {
			synchronized(Runner.class) {
				try {
					this.template = template.get(url);
					Thread.currentThread().setPriority(10);
					this.kfk = new KafkaLogs(MntlConfigurationProperties.getTargetProject(null) + "-access-logs");
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				Device device = driver.getDriverConfig().getDevice();
				if (device.is(PC)) {
					this.layout = "desktop";
				} else if (device.is(Mobile_ChromeEmulator)) {
					this.layout = "mobile";
				} else {
					this.layout = "tablet";
				}
				this.userId = USERID_PREFIX + UUID.randomUUID().toString().replace("-", "") + String.format("%02d", new LocalDateTime().getHourOfDay());

				String path="";
				try {
					path = new MntlUrl(url).urlBuilder(url).environment(null).query(null).build()
							.getPath();
				} catch (UnsupportedEncodingException | URISyntaxException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				this.accessLog = new AccessLog(this.kfk, this.userId, this.layout, path);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return (T) this;
			}
		}

		/**
		 * runs access logs test
		 *
		 */
		public void runAccessLogsTest(ErrorCollector collector) {
			runAccessLogsTest(collector, new ArrayList<>());
		}

		public void runAccessLogsTest(ErrorCollector collector, ArrayList<ExpectedLog.validateEntryAsserts> excludedAsserts) {
			synchronized(Runner.class) {
				this.collector = collector;
				assertThat("access log must be initialized", this.accessLog, is(notNullValue()));
				accessLogsTest(this.accessLog, excludedAsserts);
			}
		}

		private void accessLogsTest(AccessLog accessLog, ArrayList<ExpectedLog.validateEntryAsserts> validateEntryAsserts) {
			synchronized (Runner.class) {
				String currentUrl = this.driver.currentUrlWithoutQuery();
				String path = null;
				try {
					path = new MntlUrl(currentUrl).urlBuilder(currentUrl).environment(null).query(null).build().getPath();
				} catch (UnsupportedEncodingException | URISyntaxException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				final String PATH = path;

				try {
					this.driver.waitFor((Predicate<WebDriver>) wd -> accessLog.getLayoutPvEntries(PATH, this.layout).size() > 0, 90);
				} catch (TimeoutException e) {
					assertThat("Did not find proxy entries in the time: 90s = " + accessLog.getLayoutPvEntries(PATH, this.layout).size(), false);
				}

				VenusHarRequest request = this.proxy.capture().page(this.proxyPage).entries(PATH).get(0).request();
				assertThat("can't run test with a null request! Path: " + path, request, is(notNullValue()));
				Entry entry = accessLog.getLayoutPvEntry(path, this.layout);
				assertThat("can't run test with a null entry! Path: " + path, entry, is(notNullValue()));

				String userAgent = request.header("User-Agent");
				ExpectedLog exLog = new ExpectedLog(this.driver, this.userId, this.template, userAgent);
				exLog.validateEntry(entry, this.collector, validateEntryAsserts);
			}
		}

	}

	default void validateImageSource(String imgSrc, ErrorCollector collector) {
		URI uri = null;
		try {
			uri = new URI(imgSrc);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		String path = uri.getPath();
		String[] segments = path.split("/");
		assertThat("img url path does not have 6 segments", segments.length, is(6));
		collector.checkThat("protocol is not https", uri.getScheme(), is("https"));
		collector.checkThat("host is not correct", uri.getHost(), endsWith(".com"));
		collector.checkThat("prefix is set", segments[1], is("thmb"));
		collector.checkThat("security hash is not being set", segments[2], not(emptyOrNullString()));
		collector.checkThat("image size is not being set properly", segments[3], containsString("x"));
		collector.checkThat("filter is not being set", segments[4], not(emptyOrNullString()));
		collector.checkThat("file name does not end with jpg or png", segments[5], anyOf(endsWith(".png"), endsWith(".jpg")));
	}

	default WebClient webClient() {
		WebClient client = new WebClient();
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setRedirectEnabled(false);
		client.getOptions().setUseInsecureSSL(true);
		return client;
	}

	default void testCookie(WebDriverExtended driver, String cookie, String cookieValue, ErrorCollector collector) {
		driver.waitFor((Predicate<WebDriver>) wd -> CookieUtils.cookieValue(driver, cookie) != null, 30);
		collector.checkThat(cookie + " cookie value is not correct", CookieUtils.cookieValue(driver, cookie), is(cookieValue));
	}

	static Logger logger() {
		return LoggerFactory.getLogger(MntlCommonTestMethods.class);
	}

	default Device getDevice(WebDriverExtended driver) {
		Device device = driver.getDriverConfig().getDevice();
		if(device.is(Tablet_ChromeEmulator)) {
			device = Tablet_ChromeEmulator;
		} else if(device.is(Tablet)) {
			device = Tablet;
		} else if(device.is(SmartPhone)) {
			device = SmartPhone;
		} else if(device.is(Mobile_ChromeEmulator)) {
			device = Mobile_ChromeEmulator;
		}
		return device;
	}

	default void createEntry(Map<String, List<String>> values, String key, String... strings) {
		List<String> entries = new ArrayList<>();
		entries.addAll(Arrays.asList(strings));
		values.put(key, entries);
	}

	/**
	 * Will test that the params values are verified used an expected set of values
	 * @param actual
	 * @param values
	 * @param param
	 * @param collector
	 */
	default void testParamValues(List<String> actual, Map<String, List<String>> values, String param, ErrorCollector collector) {
		if(values.containsKey(param)) {
			List<String> expected = values.get(param);
			// if for a given key, expected array is empty
			// just test that value is not being set to null or empty value
			if(expected.size() == 0) {
				collector.checkThat(param + " ad call param is not being set", actual.get(0), not(emptyOrNullString()));
			} else {
				for(int i = 0; i < expected.size(); i++ ) {
					collector.checkThat(param + " ad call param " + i + " is not being set properly ", actual.get(i), is(expected.get(i)));
				}
			}
		}
	}

	/**
	 * Verify that the list of expected values are matching the actual values
	 *
	 * NOTE: for arguments with multiple values (i.e. prev_scp with slots, pos with mobile) You need to fill out the
	 * setEntry like follows (leveraging AdCallExpectedValues)
	 *
	 * example:
	 * setEntry(mobile, "iu_parts", IU_PREFIX_MOBILE + ptax + "," + tableName + "1," + tableName + "2");
	 * setEntry(mobile, "prev_iu_szs", "320x50,320x50");
	 * setEntry(mobile, "priority", "9");
	 * setEntry(mobile, "pos", tableName + "1", tableName + "2");
	 * setEntry(mobile, "slot", tableName + "1", tableName + "2");
	 * setValues(mobile);
	 * @param runner
	 * @param adCallParams
	 * @param values
	 * @param collector
	 */
	default void testAdCallParams(T runner ,UrlParams adCallParams, Map<String, List<String>> values, ErrorCollector collector) {
		Device device = runner.driver.getDriverConfig().getDevice();
		if(adCallParams.getUrlParamList("iu") != null)
			testParamValues(adCallParams.getUrlParamList("iu"), values, "iu", collector);
		else
			testParamValues(adCallParams.getUrlParamList("iu_parts"), values, "iu_parts", collector);

		if(adCallParams.getUrlParamList("sz") != null)
			testParamValues(adCallParams.getUrlParamList("sz"), values, "sz", collector);
		else
			testParamValues(adCallParams.getUrlParamList("prev_iu_szs"), values, "prev_iu_szs", collector);

		// Video templates can have cust only so check that we have scpParam before attempting to compare
		if(adCallParams.scpParam().params != null) {
			testParamValues(adCallParams.getScpParamList("pos"), values, "pos", collector);
			testParamValues(adCallParams.getScpParamList("priority"), values, "priority", collector);
			testParamValues(adCallParams.getScpParamList("entryType"), values, "entryType", collector);
			testParamValues(adCallParams.getScpParamList("inf"), values, "inf", collector);
			testParamValues(adCallParams.getScpParamList("vid"), values, "vid", collector);
			testParamValues(adCallParams.getScpParamList("chpg"), values, "chpg", collector);
			testParamValues(adCallParams.getScpParamList("pc"), values, "pc", collector);
			testParamValues(adCallParams.getScpParamList("ptax"), values, "ptax", collector);
			testParamValues(adCallParams.getScpParamList("tax1"), values, "tax1", collector);
			testParamValues(adCallParams.getScpParamList("tax2"), values, "tax2", collector);
			testParamValues(adCallParams.getScpParamList("t"), values, "t", collector);
			testParamValues(adCallParams.getScpParamList("au"), values, "au", collector);
			testParamValues(adCallParams.getScpParamList("aid"), values, "aid", collector);
			testParamValues(adCallParams.getScpParamList("tile"), values, "tile", collector);
			testParamValues(adCallParams.getScpParamList("docId"), values, "docId", collector);
			testParamValues(adCallParams.getScpParamList("jnyroot"), values, "jnyroot", collector);
			if (adCallParams.scpParam().slot() != null)
				testParamValues(adCallParams.getScpParamList("slot"), values, "slot", collector);
			if (adCallParams.scpParam().floor() != null)
				testParamValues(adCallParams.getScpParamList("floor"), values, "floor", collector);
			if (adCallParams.scpParam().floor_id() != null)
				testParamValues(adCallParams.getScpParamList("floor_id"), values, "floor_id", collector);
			if (adCallParams.scpParam().gridId() != null)
				testParamValues(adCallParams.getScpParamList("gridId"), values, "gridId", collector);
			if (adCallParams.scpParam().gridPb() != null)
				testParamValues(adCallParams.getScpParamList("gridPb"), values, "gridPb", collector);
			if (adCallParams.scpParam().gridSize() != null)
				testParamValues(adCallParams.getScpParamList("gridSize"), values, "gridSize", collector);
		}

		// Make sure cust param exists before running checks against it
		if(adCallParams.custParam().params != null) {
			testParamValues(adCallParams.getCustomParamList("tax0"), values, "tax0", collector);
			testParamValues(adCallParams.getCustomParamList("ugc"), values, "ugc", collector);
			if (adCallParams.custParam().slot() != null)
				testParamValues(adCallParams.getCustomParamList("slot"), values, "slot", collector);
		}
	}

	default void testAdCallsWithFilter(String filter, T runner, ErrorCollector collector) {
		for(Map<String, List<String>> values : runner.values()) {
			UrlParams adCallParams = new AdCallObject(runner.proxy(), runner.proxyPage(), runner.driver(), values, filter).urlParams(collector);
			testAdCallParams(runner,adCallParams, values, collector);
		}
	}

	/**
	 * The method is only for negative testing, it will verify all the parameters we are passing to the test are not presented in the ad calls
	 */

	default void testAdCallsWithFilterNegative(String filter, T runner, ErrorCollector collector) {
		for(Map<String, List<String>> values : runner.values()) {
			AdCallObject adCallObject = new AdCallObject(runner.proxy(), runner.proxyPage(), runner.driver(), values, filter);
			adCallObject.testMissingParameters();
		}
	}

	default void slowScroll(Component component, Page page) {
		slowScroll(component, page, 100, 500);
	}

	default void slowScroll(Component component, Page page, int pixelPerStep, int waitBetweenStepIn) {
		int scroll = component.location().getY();
		Long currentAfterScroll = currentAfterScroll(page);
		Long currentBeforeScroll = (long) Double.NEGATIVE_INFINITY;
		while(currentAfterScroll < scroll) {
			if(currentBeforeScroll.equals(currentAfterScroll))
				break;
			currentBeforeScroll = page.scroll().currentPosition();
			page.scroll().scrollBy(0, pixelPerStep);
			wait(waitBetweenStepIn, TimeUnit.MILLISECONDS);
			currentAfterScroll = currentAfterScroll(page);
		}
		wait(1, TimeUnit.SECONDS);
	}

	default Long currentAfterScroll(Page page) {
		Long currentAfterScroll = null;
		boolean currentPositionNotFound = true;
		int counter = 0;
		while(currentPositionNotFound && counter++ < 10) {
			try{
				currentAfterScroll = page.scroll().currentPosition();
			} catch(TimeoutException e) {
				e.printStackTrace();
				wait(5, TimeUnit.SECONDS);
				continue;
			}
			currentPositionNotFound = false;
		}
		return currentAfterScroll;
	}

	default void wait(int duration, TimeUnit timeUnit) {
		try {
			Thread.sleep(timeUnit.toMillis(duration));
		} catch (InterruptedException e) {
			logger().error(e.getMessage(), e);
		}
	}

	/**
	 * Will check for the existence of an analytics event in the network traffic
	 * Usage:
	 *  When using this method, you need to have the proxy test in use and set to capture content as follows.
	 *
	 *  example:
	 *  test(devices(Any), (driver, proxy) -> {
	 *    proxy.setCaptureContent();
	 *    startTest(SOMEURL.url(), driver)
	 *      .loadUrlWithPage(SomePage.class)
	 *      .withProxy(proxy)
	 *      .runTest(someTest);
	 *  });
	 *
	 * @param driver - WebDriverExtended
	 * @param page - Page to use
	 * @param proxy - proxy to use
	 * @param proxyPage - proxyPage to use
	 * @param collector - ErrorCollector
	 * @param ea - ea string to search for
	 * @param ec - ec string to search for
	 */
	default void testAnalyticsEvent(WebDriverExtended driver, Page page, VenusProxy proxy, String proxyPage, ErrorCollector collector, String ea, String ec) {
		try {
			driver.waitFor((Predicate<WebDriver>) wd -> {
				// find all entries that have collect - These represent google analytics requests
				List<VenusHarEntry> collectEntries = proxy.capture().page(proxyPage).entries("collect");
				if (collectEntries.size() == 0) return false;

				// Filter out all of these that are POST
				List<VenusHarEntry> postMethods = new ArrayList<>();
				for (VenusHarEntry entry : collectEntries) {
					if (entry.request().harRequest().getMethod().equals("POST"))
						postMethods.add(entry);
				}
				if (postMethods.size() == 0) return false;

				// and check each for match ea, ec
				List<VenusHarEntry> filtered = new ArrayList<>();
				for (VenusHarEntry entry : postMethods) {
					HarPostData postData = entry.request().harRequest().getPostData();
					if (postData != null) {
						if (postData.getText().contains("ea=" + ea) && postData.getText().contains(ec)) {
							filtered.add(entry);
						}
					}
				}
				return filtered.size() > 0;
			}, 30);
		} catch (TimeoutException e) {
			collector.addError(new Error("analytics event, ea=" + ea + ", ec=" + ec + " is not collected."));
		}
	}

	default void testAnalyticsEventConsolidated(WebDriverExtended driver, Page page, VenusProxy proxy, String proxyPage, ErrorCollector collector, String ea, String ec) {
		driver.waitFor((Predicate<WebDriver>) webdriver -> proxy.capture().page(proxyPage).entries("collect").stream().
				filter(e -> e.request().url().url().contains("ea=" + ea) && e.request().url().url().contains(ec)).collect(Collectors.toList()).size() >= 1, 30);
		List<VenusHarEntry> entries = proxy
				.capture()
				.page(proxyPage)
				.entries("collect")
				.stream()
				.filter(entry -> entry.request().url().url().contains("ea=" + ea) && entry.request().url().url().contains(ec))
				.collect(Collectors.toList());
		collector.checkThat("analytics event, ea=" + ea + ", ec=" + ec + " is not collected." , entries.size(), is(1));

	}


	default void ctrlClick(WebElementEx element) {
		element.scrollIntoViewCentered();
		if (DriverSelection.getCurrentPlatform().is(Platform.OSX)){
			element.clickWithKey(Keys.COMMAND);
		} else {
			element.clickWithKey(Keys.LEFT_CONTROL);
		}
	}

	default void testLinkClickDLEvent(ImmutableMap<String, String> expected, WebDriverExtended driver, WebElementEx link, int indexValue, ErrorCollector collector) {
		testDL(() -> DataLayerUtils.dataLayerEvents(driver,"linkClick").get(indexValue),
				expected, driver, link, collector);
	}

	default void testLinkClickDLEvent(ImmutableMap<String, String> expected, WebDriverExtended driver,
									  WebElementEx link, ErrorCollector collector) {
		testLinkClickDLEvent(expected, driver, link, 0, collector);
	}

	default void testLinkClickDLEventCategory(String category, int index, ImmutableMap<String, String> expected, WebDriverExtended driver, WebElementEx link, ErrorCollector collector) {
		testDL(() -> DataLayerUtils.dataLayerEventCategories(driver,category).get(index),
				expected, driver, link, collector);
	}

	default void testDL(Supplier<Map<String, Object>> dlEventSupplier, ImmutableMap<String, String> expected, WebDriverExtended driver, WebElementEx link, ErrorCollector collector) {
		ctrlClick(link);
		link.waitFor().exactMoment(2, TimeUnit.SECONDS);
		Map<String, Object> linkClickObjects = dlEventSupplier.get();
		for(String key:expected.keySet()) {
			collector.checkThat(key + " didnt match expected ", linkClickObjects.get(key) ,is(expected.get(key)));
		}
	}


	default void swipe(WebDriverExtended driver, WebElementEx drag, Direction direction) {
		int wide  = drag.getSize().width;
		int length  = drag.getSize().height;
		int startx = (int) (wide * (0.5));
		int starty = (int) (length * (0.5));

		Actions action = new Actions(driver.returnDriver());
		switch (direction) {
			case LEFT:
				action.clickAndHold(drag.webElement()).moveByOffset(-startx, 0).release().build().perform();
				break;
			case RIGHT:
				action.clickAndHold(drag.webElement()).moveByOffset(startx, 0).release().build().perform();
				break;
			case UP:
				action.clickAndHold(drag.webElement()).moveByOffset(0, -starty).release().build().perform();
				break;
			case DOWN:
				action.clickAndHold(drag.webElement()).moveByOffset(0, starty).release().build().perform();
				break;
		}
	}

	/**
	 * Function will consume a link's `href` attribute and return a domain name
	 */
	default String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	default ArrayList<String> getSafeList() {
		String seleneEndPoint = "/curateddomain/bysource?type=SEO_SAFELIST";
		ArrayList<String> safelistArray = new ArrayList<>();
		DocumentContext seleneData = SeleneUtils.getSeleneData(seleneEndPoint);
		safelistArray = seleneData.read("$.data.list");
		return safelistArray;
	}

	default HashSet<String> getSponsoredList() {
		String seleneEndPoint = "/curateddomain/bysource?type=REVGROUP_SPONSOREDLIST";
		HashSet<String> sponsoredlistSet = new HashSet<>() {};
		ArrayList<String> sponsoredlistArray;
		DocumentContext seleneData = SeleneUtils.getSeleneData(seleneEndPoint);
		sponsoredlistArray = seleneData.read("$.data.list");
		for (String item : sponsoredlistArray) {
			sponsoredlistSet.add(item);
		}
		return sponsoredlistSet;
	}

	default HashSet<String> getCAESSponsoredList() {
		String seleneEndPoint = "/curateddomain/bysource?type=CAES_SPONSOREDLIST";
		ArrayList<String> caesSponsoredListArray;
		DocumentContext seleneData = SeleneUtils.getSeleneData(seleneEndPoint);
		caesSponsoredListArray = seleneData.read("$.data.list");
		return new HashSet<>(caesSponsoredListArray);
	}

	default String getSeleneData(String url, String path) throws IndexOutOfBoundsException {
		String data = "";
		try {
			if (!ConfigurationProperties.getTargetProject("").contains("mantle-ref")) {
				DocumentContext seleneData = SeleneUtils.getDocuments(url.split("-")[url.split("-").length - 1]);
				data = seleneData.read(path).toString();
			}
		} catch (com.jayway.jsonpath.PathNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}


	default boolean eventExists(WebDriverExtended driver, String eventName, String eventCategory, String eventAction) {
		try {
			return driver.executeScript("return dataLayer.filter(" + "dl=> dl.event != null "
					+ "&& dl.event == \"" + eventName + "\" " + "&& dl.eventCategory == \"" + eventCategory + "\" "
					+ "&& dl.eventAction == \"" + eventAction + "\").length > 0");
		} catch (Exception e) {
			return false;
		}
	};

	/**
	 * Method verifies click tracking
	 * @param driver
	 * @param collector
	 * @param eventCategory
	 * @param eventAction
	 * @param eventLabel
	 */
	default void verifyClickTrackingEvent(WebDriverExtended driver, ErrorCollectorEx collector, String eventCategory, String eventAction, String eventLabel) {
		String eventName = "transmitInteractiveEvent";
		collector.checkThat(
				"\nEvent with following isn't found: \n\tname: " + eventName + "\n\teventCategory: " + eventCategory + "\n\teventAction: "
						+ eventAction + "\n\teventLabel: " + eventLabel,
				eventExists(driver, eventName, eventCategory, eventAction, eventLabel), CoreMatchers.is(true));
	}

	/**
	 * Method verify presence of data layer event
	 */
	default boolean eventExists(WebDriverExtended driver, String eventName, String eventCategory, String eventAction, String eventLabel) {
		try {
			return driver.executeScript("return dataLayer.filter(" + "dl=> dl.event != null "
					+ "&& dl.event == \"" + eventName + "\" " + "&& dl.eventCategory == \"" + eventCategory + "\" "
					+ "&& dl.eventAction == \"" + eventAction + "\" " + "&& dl.eventLabel == \"" + eventLabel + "\").length > 0");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Dynamic method verify presence of data layer event
	 */
	default boolean eventExists(WebDriverExtended driver, String eventName, Map<String, String> otherDataLayerParams) {
		String preScriptContent = "return dataLayer.filter("
				+ "dl=> dl.event != null " + "&& dl.event == \"" + eventName;
		String middleScriptContent = "";
		String postScriptContent = "\").length > 0";

		for(String key : otherDataLayerParams.keySet()){
			middleScriptContent = "\" " + "&& dl." + key + "==" + "\"" + otherDataLayerParams.get(key);
		}

		try {
			return driver.executeScript(preScriptContent + middleScriptContent + postScriptContent);
		} catch (Exception e) {
			return false;
		}
	}

	default JSONArray getListOfJSONitems(Runner runner, String path) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> data;
		JSONArray listOfJSONitems;
		try {
			InputStream inputStream = ReadJsonData.class.getResourceAsStream(runner.testData().toString());
			data = objectMapper.readValue(inputStream, HashMap.class);
			listOfJSONitems = JsonPath.read(data, path);
		} catch (Exception e) {
			throw new RuntimeException("Error reading json data from: " + runner.testData().toString(), e);
		}
		return listOfJSONitems;
	}

	default void readMore(Runner runner) {
		MntlBasePage page = runner.page();
		if (page.getElement().elementExists(".btn-chop")) {
			WebElementEx chopButton = page.findElement(By.cssSelector(".btn-chop"));
			chopButton.scrollIntoViewCentered();
			if(chopButton.isDisplayed()) {
				chopButton.click();
			}
		}
	}

	/**
	 * This method will get the request in the network tab (will filter based on the current url), loop through
	 * the Response Headers and return the value of link.
	 *
	 * @param runner
	 * */
	default String getLinkHeaderResponse(MntlRunner runner) {
		String currentUrl = runner.driver().getCurrentUrl();
		var venusHarEntry = runner.proxy().capture().page(runner.proxyPage()).entries(currentUrl).get(0);
		String linkHeaderValue = "";
		try {
			var headers = venusHarEntry.getResponse().response().getHeaders();
			for (HarNameValuePair header : headers) {
				if (header.getName().equalsIgnoreCase("link")) linkHeaderValue = header.getValue();
			}
		} catch (Exception e) {
			Assert.fail("Could not get Response Headers");
		}

		return linkHeaderValue;
	}
}