package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlTableOfContentsComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataLayerUtils;
import com.google.common.collect.ImmutableMap;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

public class MntlTableOfContentsTest extends MntlVenusTest implements MntlCommonTestMethods {

	/**
	 * This test method should be removed once all verticals upgrade to testTableOfContent
	 * Remove till line 77
	 */
	protected Consumer<MntlRunner> testTOC = (runner) -> {
		WebDriverExtended driver = runner.driver();
		MntlTableOfContentsComponent toc = (MntlTableOfContentsComponent) runner.component();
		MntlScPage<MntlTableOfContentsComponent> page = new MntlScPage(driver, MntlTableOfContentsComponent.class);
		collector.checkThat("TOC is not displayed", toc.header().isDisplayed(), is(true));
		collector.checkThat("Header text doesn't match", toc.header().text(), not(emptyOrNullString()));

		toc.scrollIntoViewCentered();
		toc.waitFor().aMoment();
		String currentUrl = driver.getCurrentUrl();
		List<WebElementEx> h2tags = page.mntlScBlockHeadingText();
		List<WebElementEx> tocItems = toc.items();
		collector.checkThat("not all h3 tags included in table of content", h2tags.size(), is(tocItems.size()));
		for (int i = 0; i < h2tags.size(); i++) {
			tocItems.get(i).scrollIntoViewCentered();
			toc.waitFor().aMoment();
			String href = tocItems.get(i).href().replace(currentUrl, "");
			collector.checkThat("href attribute doesn't start with #", href, startsWith("#"));
			collector.checkThat("TOC item has no text", tocItems.get(i).text(), not(emptyOrNullString()));
			testLinkClickDLEvent("mntl-toc Click", i, expectedValuesTocLinkClick(tocItems.get(i), href),
					runner.driver(), tocItems.get(i), collector);
			toc.waitFor().aMoment();
			collector.checkThat("clicking to toc item didn't open the view with correct h2",
					h2tags.get(i).isInViewPort(), is(true));
		}
	};

	protected ImmutableMap<String, String> expectedValuesTocLinkClick(WebElementEx link, String eventLabel) {
		String eventAction = link.text();
		return ImmutableMap.<String, String>builder().put("event", "transmitInteractiveEvent")
				.put("eventAction", eventAction).put("eventLabel", eventLabel).build();
	}

	protected void testLinkClickDLEvent(String category, int index, ImmutableMap<String, String> expected,
			WebDriverExtended driver, WebElementEx link, ErrorCollector collector) {
		link.click();
		link.waitFor().exactMoment(2, TimeUnit.SECONDS);
		Map<String, Object> linkClickObjects = DataLayerUtils.dataLayerEventCategories(driver, category).get(index);
		for (String key : expected.keySet()) {
			collector.checkThat(key + " didnt match expected ", linkClickObjects.get(key), is(expected.get(key)));
		}
	}

	/**
	 * Will verify that the table of contents is displayed properly and all of the click
	 * functionality takes the user to the expected header link.
	 * verify click tracking
	 * Click tracking event category
	 */
	protected BiConsumer<Runner, Boolean> testTableOfContent = (runner, checkHeader) -> {
		WebDriverExtended driver = runner.driver();
		MntlTableOfContentsComponent toc = (MntlTableOfContentsComponent) runner.component();
		MntlScPage<MntlTableOfContentsComponent> page = new MntlScPage(driver, MntlTableOfContentsComponent.class);
		page.waitFor().documentComplete();
		String currentUrl = driver.getCurrentUrl();

		if(!desktop(driver)){
			if(toc.isMobileToggleExist()) {
				mobileToggleToc(page, toc);
			}
			toc.scrollIntoViewCentered();
		}else {
			toc.scrollIntoViewCentered();
		}
		page.waitFor().aMoment(3, TimeUnit.SECONDS);

		if (checkHeader && desktop(driver)) {
			collector.checkThat("Toc heading is not displayed", toc.header().isDisplayed(), is(true));
			collector.checkThat("Toc Heading text is empty", toc.header().text(), not(emptyOrNullString()));
		}

		List<WebElementEx> tocItems = toc.items();
		assertThat("tocItems size is null or empty", tocItems.size() , greaterThan(0));

		List<WebElementEx> blockHeadings = page.mntlScBlockHeadingText();
		assertThat("blockHeadings size is null or empty", blockHeadings.size() , greaterThan(0));

		AtomicInteger itemCount = new AtomicInteger(1);
		for (int item = 0; item < tocItems.size(); item++ ){
			if(!desktop(driver)){
				if(toc.isMobileToggleExist()) {
					mobileToggleToc(page, toc);
				}
			}
			if(toc.viewAllButtonExist()){
				expandTocList(toc);
				page.waitFor().aMoment();
			}
			String expectedEventCategory = (String) runner.testData();
			String href = tocItems.get(item).href().replace(currentUrl, "");
			String itemText = tocItems.get(item).text();
			collector.checkThat("Item #" + itemCount + " href attribute doesn't start with #",
					href, startsWith("#"));
			collector.checkThat("Item #" + itemCount + itemText + " Toc item text is Empty!",
					itemText, not(emptyOrNullString()));
			collector.checkThat(itemText + " Toc item " + itemCount + " href is Empty!",
					tocItems.get(item).href(), not(emptyOrNullString()));

			driver.waitFor(1, TimeUnit.SECONDS);
			tocItems.get(item).getWrappedElement().click();
			driver.waitFor(1, TimeUnit.SECONDS);
			boolean headerVisible = headerVisible(driver, blockHeadings.get(item).text());
			collector.checkThat("Item #" + itemCount + "Expected header: " + tocItems.get(item).text() +
					" is not viewport", headerVisible, is(true));

			// verify click tracking event
			verifyClickTrackingEvent(runner.driver(), collector, expectedEventCategory, itemText, href);

			tocItems.get(item).scrollIntoViewCentered();
			itemCount.getAndIncrement();
		}
	};

	private void mobileToggleToc(MntlScPage page, MntlTableOfContentsComponent toc){
		if (!toc.mobileToggleInViewport()) {
			getTocToggle(page, toc);
		}
		if (!toc.isTocListExpanded()) {
			expandTocToggle(toc);
		}
	}

	private void getTocToggle(MntlScPage page, MntlTableOfContentsComponent toc){
		do {
			page.scroll().scrollBy(0, 100);
		}while (!toc.mobileToggleInViewport());
	}

	private void expandTocToggle(MntlTableOfContentsComponent toc){
		toc.mobileToggleButton().jsClick();
	}

	private void expandTocList(MntlTableOfContentsComponent toc){
		int count = 1;
		do {
			count ++;
			toc.viewAllButton().jsClick();
			if (count > 5)
			{ break;}
		}while (!toc.isExpanded());
	}

	/**
	 * Will get all visible headers that are displayed to user
	 * @param driver
	 * @return ArrayList<WebElement> of all available visible headers
	 */
	private ArrayList<WebElement> getVisibleHeaders(WebDriverExtended driver) {
		ArrayList<WebElement> visible = new ArrayList<>();
		for (WebElement ele : driver.findElements(By.xpath("//span[contains(@class,'mntl-sc-block-callout-heading')]|" +
				"//*[contains(@class,'mntl-sc-block-heading')]|//h2 | " +
				"//span[contains(@class,'product-record__heading--text')]"))) {
			if (ele.isDisplayed()) {
				visible.add(ele);
			}
		}
		return visible;
	}

	/**
	 * Based off a header string, see if the header is visible
	 * @param driver WebDriver
	 * @param headerText text of the header to verify it's visible
	 * @return true if header is visible, false otherwise
	 */
	private boolean headerVisible(WebDriverExtended driver, String headerText) {
		for (WebElement ele : getVisibleHeaders(driver)) {
			if(ele.getText().contains(headerText)) {
				return true;
			}
		}
		return false;
	}

}
