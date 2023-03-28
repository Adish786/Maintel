package com.about.mantle.clickTracking;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataLayerUtils;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class MntlClickTrackingTest extends MntlVenusTest implements MntlCommonTestMethods {
    Logger logger = LoggerFactory.getLogger("ClickTracking");

    /**
     * Used to hold expected data of Ad Table component to verify against
     */
    protected static class TrackingData {
        public String event = "transmitInteractiveEvent";
        public String action;
        public String category;
        public String label;

        public TrackingData() { }

        public TrackingData event(String event) {
            this.event = event;
            return this;
        }

        public TrackingData action(String action) {
            this.action = action;
            return this;
        }

        public TrackingData category(String category) {
            this.category = category;
            return this;
        }

        public TrackingData label(String label) {
            this.label = label;
            return this;
        }
    }

    /**
     * Checks the datalayer object for the index to use when doing look up against a category
     *
     * @param driver
     * @param category
     * @return
     */
    protected int getLastDataLayerIndex(WebDriverExtended driver, String category) {
        int index = DataLayerUtils.dataLayerEventCategories(driver, category).size();
        logger.info("using index: " + index);
        return index;
    }

    /**
     * Sets the target of the url to launch in separate window.
     * @param driver
     * @param ele
     */
    protected void setLinkTarget(WebDriverExtended driver, WebElementEx ele) {
        // for the links - some don't have the target set to _blank... won't open in a new window otherwise
        driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                ele, "target", "_blank");
    }

    protected ImmutableMap getMap(TrackingData data) {
        HashMap<String, String> map = new HashMap<>();
        if (data.event == null)
            Assert.fail("TestData requires event to be filled");
            map.put("event", data.event);
        if (data.category == null)
            Assert.fail("TestData requires category to be filled");
            map.put("eventCategory", data.category);
        if (data.action != null) {
            map.put("eventAction", data.action);
        }
        if (data.label != null)
            map.put("eventLabel", data.label);

        ImmutableMap<String, String> expectedValues = ImmutableMap.copyOf(map);
        return expectedValues;
    }

    /**
     * tests the clicking on a element by checking datalayer object when link launches a new window
     *
     * @param driver
     * @param data
     * @param index
     * @param elementToClick
     */
     protected void testLinkClick(WebDriverExtended driver, TrackingData data,
                                int index, WebElementEx elementToClick) {
        ImmutableMap<String, String> expectedValues = getMap(data);
        driver.waitFor(1, TimeUnit.SECONDS);

        elementToClick.scrollIntoViewCentered();
        String parent = driver.getWindowHandle();
        logger.info("performing click action " + data.category +" index " + index);
        String handle = driver.getWindowHandle(() ->
                testLinkClickDLEventCategory(data.category, index, expectedValues,
                        driver, elementToClick, collector));
        if (handle != null) driver.switchTo().window(handle);
        driver.close();
        driver.switchTo().window(parent);
        logger.info("window closed ");
    }

    /**
     * Method verifies click tracking on random selected element from list
     * @param driver
     * @param webElements
     * @param category
     * @param event
     */
    protected void testRandomLinkClick(WebDriverExtended driver, List<WebElementEx> webElements,
                                          String category, String event) {
        assertThat("Elements Size is null or zero", webElements.size(), greaterThan(0));
        final int index = new SecureRandom().nextInt(webElements.size());

        WebElementEx webElement = webElements.get(index);
        String expectedAction = webElement.getText();
        if (expectedAction == null || expectedAction.equals("")){
            expectedAction = webElement.getAttribute("id");
        }
        logger.info(String.format("Testing item index: %d", index));
        logger.info(String.format("expected event: %s", event));
        logger.info(String.format("expected label: %s", webElement.href()));
        logger.info(String.format("expected action: %s", expectedAction));
        logger.info(String.format("expected category: %s", category));
        TrackingData data = new TrackingData()
                .event(event)
                .action(expectedAction)
                .label(webElement.href())
                .category(category);
        setLinkTarget(driver, webElement);
        webElement.scrollIntoViewCentered();
        testLinkClick(driver, data, getLastDataLayerIndex(driver, data.category), webElement);
    }

   protected void testRandomLinkClick(WebDriverExtended driver, List<WebElementEx> webElements,
                                                 String category) {
        testRandomLinkClick(driver, webElements, category, "transmitInteractiveEvent");
    }
}