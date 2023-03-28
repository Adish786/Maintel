package com.about.mantle.analytics;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlTaxonomyNode;
import com.about.mantle.venus.model.analytics.MntlTaxonomyObject;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.utils.DataLayerUtils;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

@SuppressWarnings("rawtypes")
public abstract class MntlDataLayerTests extends MntlVenusTest implements MntlCommonTestMethods {

	abstract public List<String> rtbLibs();
	protected static final String AUTHOR_ID = "authorId";
	protected static final String DOCUMENT_ID = "documentId";
	protected static final String PUBLISH_DATE = "publishDate";
	protected static final String TEMPLATE_ID = "templateId";
	protected static final String CONTENT_GROUP = "contentGroup";
	protected static final String EXPERIENCE_TYPE = "experienceType";
	protected static final String EXPERIENCE_TYPE_NAME = "experienceTypeName";

	/*
	 * This field is being removed as part of GLBE-6818. Marking as deprecated to
	 * avoid breaking compilation in other verticals.
	 */
	@Deprecated
	protected static final String NUM_OF_IMAGES = "numOfImages";

	protected static final String LE_AUTHOR_ID = "lastEditingAuthorId";
	protected static final String LE_USER_ID = "lastEditingUserId";
	protected static final String CHARACTER_COUNT = "characterCount";
	protected static final String ERROR_TYPE = "errorType";
	protected static final String UPDATE_DATE = "updateDate";
	protected static final String DESCRIPTION = "description";
	protected static final String TITLE = "title";
	protected static final String SOCIAL_TITLE = "socialTitle";
	protected static final String IS_ERROR_PAGE = "isErrorPage";
	protected static final String TAXONOMY_NODES = "taxonomyNodes";
	protected static final String NUM_OF_ARTICLE_WORDS = "numOfArticleWords";
	protected static final String NUM_OF_MAP_LABELS = "numOfMapLabels";
	protected static final String EU_TRAFFIC_FLAG = "euTrafficFlag";
	protected static final String INTERNAL_REQUEST_ID = "internalRequestId";
	protected static final String INTERNAL_SESSION_ID = "internalSessionId";
	protected static final String EXCLUDE_FROM_COMSCORE = "excludeFromComscore";
	protected static final String PAGE_VIEW_TYPE = "pageviewType";
	protected static final String BREAKPOINT_NAME = "breakpointName";
	@Deprecated
	protected static final String SOCIAL_IMAGE = "socialImage";
	protected static final String REVENUE_GROUP = "revenueGroup";
	protected static final String BOUNCE_EXCHANGE_ID = "bounceExchangeId";
	protected static final String MANTLE_VERSION = "mantleVersion";
	protected static final String COMMERCE_VERSION = "commerceVersion";
	protected static final String TEMPLATE_TYE = "templateName";
	protected static final String TEMPLATE_NAME = "templateName";

	protected static final String VIEW_TYPE = "viewType";
	protected static final String PRIMARY_TAXONOMY_NAMES = "primaryTaxonomyNames";

	protected static String BOUNCE_EXCHANGE_ID_VALUE;

	protected static final String RECIRC_DOC_ID_FOOTER = "recircDocIdsFooter";

	protected static List<String> BREAKPOINT_DESKTOP = new ArrayList<String>();
	protected static List<String> BREAKPOINT_TABLET = new ArrayList<String>();
	protected static List<String> BREAKPOINT_MOBILE = new ArrayList<String>();

	protected static final String EVENT_ACTION = "eventAction";
	protected static final String EVENT_CATEGORY = "eventCategory";
	protected static final String EVENT_LABEL = "eventLabel";

	protected static final String Link_Target_Type = "linkTargetType";
	protected static final String Link_Target_URL = "linkTargetURL";
	protected static final String Link_Text = "linkText";
	protected static final String Link_Container_Id = "linkContainerId";
	protected static final String Link_Id = "linkId";
	protected static final String Data_Click_Tracked = "dataClickTracked";
	protected static final String Data_Id = "dataId";
	protected static final String PRIMARY_TAXONOMY_IDS = "primaryTaxonomyIds";
	protected static final String DESCRIPTIVE_TAXONOMY = "descriptiveTaxonomy";

	protected static String jsScript = "return $('.mntl-card').map(function(i,el){ var val = el.attributes.href.value.split('-'); return val[val.length-1];});";

	protected static final Logger logger = LoggerFactory.getLogger(MntlDataLayerTests.class);

	public Map<String, Object> setCommonValues() {
		Map<String, Object> commonExpectedValues = new HashMap<String, Object>();
		commonExpectedValues.put(EXCLUDE_FROM_COMSCORE, "false");
		commonExpectedValues.put(CONTENT_GROUP, "Articles");
		commonExpectedValues.put(EXPERIENCE_TYPE, "single page");
		commonExpectedValues.put(BOUNCE_EXCHANGE_ID, BOUNCE_EXCHANGE_ID_VALUE);

		return commonExpectedValues;

	}

	public Map<String, org.hamcrest.Matcher> setCommonValuesWithMatcher() {
		Map<String, org.hamcrest.Matcher> commonExpectedValues = new HashMap<String, org.hamcrest.Matcher>();
		commonExpectedValues.put(BREAKPOINT_NAME, is(""));
		commonExpectedValues.put(RECIRC_DOC_ID_FOOTER, not(nullValue()));
		commonExpectedValues.put(EXCLUDE_FROM_COMSCORE,   is("false"));
		commonExpectedValues.put(EXPERIENCE_TYPE, is("single page"));
		commonExpectedValues.put(EXPERIENCE_TYPE_NAME, is(""));
		commonExpectedValues.put(EU_TRAFFIC_FLAG, is("false"));
		commonExpectedValues.put(AUTHOR_ID, is(""));
		commonExpectedValues.put(DOCUMENT_ID, is(""));
		commonExpectedValues.put(INTERNAL_REQUEST_ID, not(isEmptyOrNullString()));
		commonExpectedValues.put(TEMPLATE_ID, is(""));
		commonExpectedValues.put(TITLE, is(""));
		commonExpectedValues.put(DESCRIPTION, is(""));
		commonExpectedValues.put(REVENUE_GROUP, is(""));
		commonExpectedValues.put(BOUNCE_EXCHANGE_ID, is(BOUNCE_EXCHANGE_ID_VALUE));
		commonExpectedValues.put(MANTLE_VERSION, is(""));
		commonExpectedValues.put(COMMERCE_VERSION, is(""));
		commonExpectedValues.put(TEMPLATE_NAME , is(""));
		commonExpectedValues.put(VIEW_TYPE, is(""));
		commonExpectedValues.put(PRIMARY_TAXONOMY_NAMES, is(""));
		commonExpectedValues.put(PRIMARY_TAXONOMY_IDS, is(""));
		return commonExpectedValues;
	}

	public void testUnifiedPageviewEventValues(Map<String, Object> actualObject, Map<String, Object> expectedObject,
			WebDriverExtended driver, String url) {

		String currentUrl = driver.getCurrentUrl();

		for (String item : expectedObject.keySet()) {
			// To avoid exceptions, verify expected key exists
			collector.checkThat("For Url :" + currentUrl + " : " + item + " exists", actualObject.containsKey(item), is(true));
			if (!actualObject.containsKey(item))return;

			if (item.contains(CHARACTER_COUNT)) {
				if (expectedObject.get(item).equals("") || expectedObject.get(item).equals("0")) {
					collector.checkThat(" For Url :" + currentUrl + " : " + "characterCount",
							(String) actualObject.get(item), is(expectedObject.get(item)));
				} else {
					collector.checkThat(" For Url :" + currentUrl + " : " + "characterCount",
							(Long) actualObject.get(item), is(greaterThanOrEqualTo(0L)));
				}
			} else if (item.contains(BREAKPOINT_NAME)) {
				if (desktop(driver)) {
					collector.checkThat(" For Url :" + currentUrl + " : " + "breakpointName", BREAKPOINT_DESKTOP,
							hasItems((String) actualObject.get(item)));
				} else if (mobile(driver)) {
					collector.checkThat(" For Url :" + currentUrl + " : " + "breakpointName", BREAKPOINT_MOBILE,
							hasItems((String) actualObject.get(item)));
				} else {
					collector.checkThat(" For Url :" + currentUrl + " : " + "breakpointName", BREAKPOINT_TABLET,
							hasItems((String) actualObject.get(item)));
				}

			} else if (item.contains(RECIRC_DOC_ID_FOOTER)) {
				collector.checkThat(" For Url :" + currentUrl + " : " + item, (String) actualObject.get(item),
						not(nullValue()));
			} else if (item.contains(INTERNAL_REQUEST_ID) || item.contains(INTERNAL_SESSION_ID)) {
				collector.checkThat(" For Url :" + currentUrl + " : " + item, (String) actualObject.get(item),
						not(isEmptyOrNullString()));
			} else if (item.contains(PUBLISH_DATE) || item.contains(UPDATE_DATE)) {
				if (!expectedObject.get(item).equals("")) {
					collector.checkThat(" For Url :" + currentUrl + " : " + item,
							dateValid((String) actualObject.get(item)), is(true));
				} else {
					collector.checkThat(" For Url :" + currentUrl + " : " + item, actualObject.get(item),
							is(expectedObject.get(item)));
				}
			} else if (item.contains(TAXONOMY_NODES)) {
				if (!expectedObject.get(TAXONOMY_NODES).equals("")) {
					JsonElement actualElement = new Gson().toJsonTree(actualObject);
					MntlTaxonomyObject actualObjectTaxonomyNodes = new Gson().fromJson(actualElement,
							MntlTaxonomyObject.class);

					int expectedTaxonomyNode = Integer.parseInt((String) expectedObject.get(TAXONOMY_NODES));

					collector.checkThat(" For Url :" + currentUrl + " : " + "taxonomyNodes",
							((MntlTaxonomyObject) actualObjectTaxonomyNodes).getTaxonomyNodes().size(),
							is(expectedTaxonomyNode));
					for (List<MntlTaxonomyNode> taxonomyNode : ((MntlTaxonomyObject) actualObjectTaxonomyNodes)
							.getTaxonomyNodes()) {
						for (MntlTaxonomyNode node : taxonomyNode) {
							collector.checkThat(
									" For Url :" + currentUrl + " : " + "taxonomyNodes shortNames are empty",
									node.getShortName(), not(isEmptyOrNullString()));
							collector.checkThat(
									" For Url :" + currentUrl + " : " + "taxonomyNodes documentIds are empty",
									String.valueOf(node.getDocumentId()), not(isEmptyOrNullString()));

						}
					}

				}
				/*
				 * true - if lastEditingAuthorId/lastEditingUserId = "" true - if
				 * lastEditingAuthorId/lastEditingUserId = "someNumericvalue" false - if
				 * lastEditingAuthorId/lastEditingUserId = "someAlphaNumericvalue"
				 */
			} else if (item.contains(LE_AUTHOR_ID) || item.contains(LE_USER_ID)) {
				if (expectedObject.get(item).equals("")) {
					collector.checkThat(" For Url :" + currentUrl + " : " + item, (String) actualObject.get(item),
							is(expectedObject.get(item)));
				} else {
					collector.checkThat(" For Url :" + currentUrl + " : " + item,
							StringUtils.isNumeric((String) actualObject.get(item)), is(true));
				}
			} else if (item.contains(REVENUE_GROUP)) {
				collector.checkThat(" For Url :" + currentUrl + " : " + item, actualObject.get(item),
						is(expectedObject.get(item)));
			} else if (item.contains(BOUNCE_EXCHANGE_ID)
				|| item.contains(MANTLE_VERSION)
				|| item.contains(COMMERCE_VERSION)
				|| item.contains(TEMPLATE_TYE)
				|| item.contains(VIEW_TYPE)
				|| item.contains(PRIMARY_TAXONOMY_NAMES)
				|| item.contains(PRIMARY_TAXONOMY_IDS))
			{
				collector.checkThat(" For Url :" + currentUrl + " : " + item, actualObject.get(item),
						is(expectedObject.get(item)));
			} else {
				collector.checkThat(" For Url :" + currentUrl + " : " + item, actualObject.get(item).toString(),
						is(expectedObject.get(item)));
			}
		}
	}

	public void testUnifiedPageviewEventValuesWithMatcher(Map<String, Object> actualObject, Map<String, org.hamcrest.Matcher> expectedObject,
														  WebDriverExtended driver, String url) {
		for (String item : expectedObject.keySet()) {
			if (item.contains(BREAKPOINT_NAME)) {
				if (desktop(driver)) {
					collector.checkThat(" For Url :" + url + " : " + "breakpointName", BREAKPOINT_DESKTOP,
						hasItems((String) actualObject.get(item)));
				} else if (mobile(driver)) {
					collector.checkThat(" For Url :" + url + " : " + "breakpointName", BREAKPOINT_MOBILE,
						hasItems((String) actualObject.get(item)));
				} else {
					collector.checkThat(" For Url :" + url + " : " + "breakpointName", BREAKPOINT_TABLET,
						hasItems((String) actualObject.get(item)));
				}
			} else {
				try {
					collector.checkThat("actual is not as expected " + item, actualObject.get(item).toString(), expectedObject.get(item));
				}catch (NullPointerException e){
					logger.error( item + " is not present in the DataLayer Object");
				}
			}
		}
	}

	private boolean dateValid(String date) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	protected final void dataLayerTest(WebDriverExtended driver, String url, Map<String, Object> expectedObject) {
		driver.get(url, true);
		final Map<String, Object> unifiedPageviewObjectsFirst = DataLayerUtils
				.dataLayerEvents(driver, "unifiedPageview").get(0);
		testUnifiedPageviewEventValues(unifiedPageviewObjectsFirst, expectedObject, driver, url);
	}

	protected final void dataLayerTestWithMatcher(WebDriverExtended driver, String url,Map<String, org.hamcrest.Matcher> expectedObject) {
		driver.get(url, true);
		final Map<String, Object> unifiedPageviewObjectsFirst = DataLayerUtils
			.dataLayerEvents(driver, "unifiedPageview").get(0);
		testUnifiedPageviewEventValuesWithMatcher(unifiedPageviewObjectsFirst, expectedObject, driver, url);
	}

	protected final void inlineCitationTrackingEventsTest(WebDriverExtended driver, String url,
			Map<String, Object> expectedObject, int event) {
		final Map<String, Object> dynamicTooltipObjectsFirst = DataLayerUtils
				.dataLayerEvents(driver, "mntlDynamicTooltip").get(event);
		testInlineCitationEventValues(dynamicTooltipObjectsFirst, expectedObject, driver, url);
	}

	protected final void linkClickTrackingEventsTest(WebDriverExtended driver, String url,
			Map<String, Object> expectedObject, int event) {
		final Map<String, Object> linkClickTrackingObjectsFirst = DataLayerUtils.dataLayerEvents(driver, "linkClick")
				.get(event);
		testLinkClickTrackingEventValues(linkClickTrackingObjectsFirst, expectedObject, driver, url);
	}

	private void testInlineCitationEventValues(Map<String, Object> dynamicTooltipObjectsFirst,
			Map<String, Object> expectedObject, WebDriverExtended driver, String url) {
		String currentUrl = driver.getCurrentUrl();
		collector.checkThat(" For Url :" + currentUrl + " : " + "Event Action",
				dynamicTooltipObjectsFirst.get(EVENT_ACTION), is(expectedObject.get(EVENT_ACTION)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "Event Category",
				dynamicTooltipObjectsFirst.get(EVENT_CATEGORY), is(expectedObject.get(EVENT_CATEGORY)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "Event Label",
				dynamicTooltipObjectsFirst.get(EVENT_LABEL), is(expectedObject.get(EVENT_LABEL)));

	}

	private void testLinkClickTrackingEventValues(Map<String, Object> linkClickTrackingObjectsFirst,
			Map<String, Object> expectedObject, WebDriverExtended driver, String url) {
		String currentUrl = driver.getCurrentUrl();
		collector.checkThat(" For Url :" + currentUrl + " : " + "Link Target Type ",
				linkClickTrackingObjectsFirst.get(Link_Target_Type), is(expectedObject.get(Link_Target_Type)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "Link Target URL",
				linkClickTrackingObjectsFirst.get(Link_Target_URL), is(expectedObject.get(Link_Target_URL)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "Link Text",
				linkClickTrackingObjectsFirst.get(Link_Text), is(expectedObject.get(Link_Text)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "link Container Id",
				linkClickTrackingObjectsFirst.get(Link_Container_Id), is(expectedObject.get(Link_Container_Id)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "link Id", linkClickTrackingObjectsFirst.get(Link_Id),
				is(expectedObject.get(Link_Id)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "data Click Tracked",
				linkClickTrackingObjectsFirst.get(Data_Click_Tracked), is(expectedObject.get(Data_Click_Tracked)));
		collector.checkThat(" For Url :" + currentUrl + " : " + "data Id", linkClickTrackingObjectsFirst.get(Data_Id),
				is(expectedObject.get(Data_Id)));

	}

	protected Consumer<Runner> RTBDataLayerTest = (runner) -> {
		WebDriverExtended driver = runner.driver();
		runner.page().scroll().bottom();
		runner.page().waitFor().exactMoment(2,TimeUnit.SECONDS);
		try {
			driver.waitFor(
					(Predicate<WebDriver>) wd -> DataLayerUtils.dataLayerEvents(driver, "analyticsEvent").size() != 0,
					30);
		} catch (TimeoutException e) {
			Assert.fail("Was unable to find dataLayer analyticsEvent");
		}
		final Map<String, Object> analyticsEvent = DataLayerUtils.dataLayerEvents(driver, "analyticsEvent").get(0);

		collector.checkThat(" For Url :" + driver.getCurrentUrl() + "eventCategory value is not correct",
				analyticsEvent.get("eventCategory"), is("Ad Timing Metrics"));
		collector.checkThat(" For Url :" + driver.getCurrentUrl() + "eventAction value is not correct",
				analyticsEvent.get("eventAction"), is("Load"));
		String eventLabel = analyticsEvent.get("eventLabel").toString();
		if (rtbLibs().size() == 0)
			Assert.fail("rtbLibs needs be set via each vertical");
		for (String lib : rtbLibs()) {
			collector.checkThat(" For Url :" + driver.getCurrentUrl() + " event label does not contain library: " + lib,
					eventLabel.contains(lib), is(true));
		}

	};

	@SuppressWarnings("unchecked")
	public void runRTBDataLayerTest(Device device, String url, List<NameValuePair> geoLoaction) {
		test(devices(device), (driver, proxy) -> {
			startTest(url, driver).withAds().withProxyAndHeaders(proxy, geoLoaction).loadUrl()
					.runTest(RTBDataLayerTest);
		});
	}

	protected final void dataLayerReadyForThirdPartyTracking(WebDriverExtended driver, String url) {
		driver.get(url, true);
		collector.checkThat(" For Url :", DataLayerUtils.dataLayerEvents(driver, "readyForThirdPartyTracking").size(),
				is(0));
		driver.waitFor(10, TimeUnit.SECONDS);
		collector.checkThat(" For Url :", DataLayerUtils.dataLayerEvents(driver, "readyForThirdPartyTracking").size(),
				is(1));
	}

	protected void rootdocIdFooterdataLayerTest(WebDriverExtended driver, String url) {
		final Map<String, Object> unifiedPageviewObjectsFirst = DataLayerUtils
				.dataLayerEvents(driver, "unifiedPageview").get(0);

		String recircDocIdsFooter = (String) unifiedPageviewObjectsFirst.get("recircDocIdsFooter");

		List<String> expectedDocIds = driver.executeScript(jsScript);

		for (String string : expectedDocIds) {
			collector.checkThat("The docs ids for" + url + " does not match with the one from unifiedpageview",
					recircDocIdsFooter.contains(string), is(true));

		}

	}

}