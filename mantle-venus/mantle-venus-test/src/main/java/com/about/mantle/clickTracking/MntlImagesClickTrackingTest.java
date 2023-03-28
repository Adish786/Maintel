package com.about.mantle.clickTracking;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.links.MntlLinksPageComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.test.ErrorCollectorEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class MntlImagesClickTrackingTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected Consumer<Runner> imagesClickTrackingTest = (runner) -> {
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        MntlBasePage page = new MntlBasePage(runner.driver(), MntlLinksPageComponent.class);
        List<MntlLinksPageComponent> links = page.getComponents();
        List<MntlLinksPageComponent> filteredLinks = links.stream()
                .filter(link -> link.href() != null)
                .filter(link -> link.href().startsWith("https://"))
                .filter(link -> link.className().contains("comp"))
                .filter(link -> link.hasImage())
                .filter(link -> link.displayed())
                .collect(Collectors.toList());
        assertThat("The document provided does not contain images that satisfy the test requirements ", filteredLinks.size(), greaterThan(0));
        Random random = new Random();
        testLink(runner, filteredLinks.get(random.nextInt(filteredLinks.size() - 1)).getElement());
    };

    private void testLink(Runner runner, WebElementEx link) {
        String parent = runner.driver().getWindowHandle();
        String handle = runner.driver().getWindowHandle(() ->
                testLinkEvent(runner, collector, link));
        if (handle != null) {
            runner.driver().switchTo().window(handle);
            runner.driver().close();
            runner.driver().switchTo().window(parent);
        }
    }

    private void testLinkEvent(Runner runner, ErrorCollectorEx collector, WebElementEx link) {
        ctrlClick(link);
        runner.page().waitFor().exactMoment(500, TimeUnit.MILLISECONDS);
        Map<Object, Object> eventItems = returnEvent(runner.driver(), link.href()).get(0);
        if (eventItems != null) {
            String eventAction = (String) eventItems.get("eventAction");
            String eventLabel = (String) eventItems.get("eventLabel");
            collector.checkThat("Event action does contain `noscript` tag for the link with href " + link.href(), eventAction, not(containsStringIgnoringCase("noscript")));
            collector.checkThat("Event label does not contain url for the link with href " + link.href(), link.href(), containsStringIgnoringCase(eventLabel));
        } else {
            collector.checkThat("The transmit interactive event for href " + link.href() + " is empty ", eventItems, not(null));
        }
    }

    private ArrayList<Map<Object, Object>> returnEvent(WebDriverExtended driver, String eventLabel) {
        try {
            return driver.executeScript("return dataLayer.filter(function(dl){return dl.event =='transmitInteractiveEvent' && dl.eventLabel == \"" + eventLabel + "\"})");
        } catch (Exception e) {
            return null;
        }
    }

}
