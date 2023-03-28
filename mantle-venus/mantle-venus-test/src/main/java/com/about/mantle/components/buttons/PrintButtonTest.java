package com.about.mantle.components.buttons;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.pages.PrintButtonPage;
import com.about.mantle.venus.model.pages.PrintButtonPage.MntlPrintButton;
import com.about.mantle.venus.model.pages.PrintButtonPage.PrintPage;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.driver.WebDriverExtended;
import com.google.common.collect.ImmutableMap;

import java.util.Set;
import java.util.function.Consumer;

import static com.about.venus.core.driver.selection.Device.PC;
import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.Matchers.is;

public class PrintButtonTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	private static final String DATA_ORDINAL = "dataOrdinal";
	private static final String LINK_CONTAINER_ID = "linkContainerId";
	private static final String LINK_TEXT = "linkText";
	
	private static final ImmutableMap<String, String> printButtonLinkClickMap = ImmutableMap.<String, String> builder()
			.put(DATA_ORDINAL, "")
			.put(LINK_CONTAINER_ID, "print-button_1-0")
			.put(LINK_TEXT, "PRINT")
			.build();	
	
	protected Consumer<MntlRunner> printButtonTest = runner -> {
		WebDriverExtended driver = runner.driver();
		String linkText = runner.testData() != null ? runner.testData().toString() : printButtonLinkClickMap.get(LINK_TEXT);
		String parent = driver.getWindowHandle();
		PrintButtonPage.MntlPrintButton button = (MntlPrintButton) runner.component();
		String action = button.attributeValue("action");
		collector.checkThat("print button text is not Print", button.printButton().text(), is(linkText));
		collector.checkThat("print button icon is not displayed", button.printButtonIcon().isDisplayed(), is(true));
		PrintPage printPage = button.openInNewWindow();
		String handle = driver.getWindowHandle();
		Set<String> windows = driver.getWindowHandles();
		// switch to a window that is neither parent nor the current handle
		for (String window : windows) {
			if (!window.equals(parent)) {
				if (!window.equals(handle)) {
					driver.switchTo().window(window);
					break;
				}
			}
		}
		MntlUrl printUrl = printPage.printUrl();
		collector.checkThat("print page url does not end with print button's action attribute", printUrl.url(), endsWith(action));
	};

	protected Consumer<MntlRunner> printButtonTrackingTest = runner -> {
		PrintButtonPage.MntlPrintButton button = (MntlPrintButton) runner.component();
		testLinkClickDLEvent(printButtonLinkClickMap, runner.driver(), button.printButtonElement(), collector);
		testAnalyticsEvent(runner.driver(), runner.page(), runner.proxy(), runner.proxyPage(), collector,"PRINT","ec=print-button%20Click");
	};
	
	protected void testPrintButton(String url, Class<? extends MntlPrintButton> button) {
        test(devices(PC), driver -> {
        	startTest(url, driver).onComponent(button).loadUrl().runTest(printButtonTest);
        });
		test(devices(PC), (driver, proxy) -> {
        	proxy.setCaptureContent();
        	startTest(url, driver).withProxy(proxy).onComponent(button).loadUrl().runTest(printButtonTrackingTest);
		});
    }
	
}
