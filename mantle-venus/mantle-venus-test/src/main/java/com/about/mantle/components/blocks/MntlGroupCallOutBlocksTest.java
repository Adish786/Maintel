package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScBlockGroupCalloutComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * The test validates the group callout block(s) on the page.
 * It will first validate there's at least one group callout block on the page, loop through each of them, validate heading and
 * subheading are not null or empty if expected, locate the callout block items, validate there's at least one, loop through
 * each and validate its image (displayed, src and alt attributes are not null or empty), heading, body, date and button
 * are not null or empty if expected. It will also validate the date has a correct format.
 */

public abstract class MntlGroupCallOutBlocksTest extends MntlVenusTest implements MntlCommonTestMethods<MntlCommonTestMethods.MntlRunner> {

    protected boolean expectTitle = false;
    protected boolean expectSubheading = false;
    protected String EXPECT_IMAGE = "expectImage";
    protected String EXPECT_HEADING = "expectHeading";
    protected String EXPECT_DATE = "expectDate";
    protected String EXPECT_BODY = "expectDate";
    protected String EXPECT_LINK = "expectLink";
    protected String EXPECT_BUTTON = "expectButton";

    protected abstract List<Map<String, Boolean>> expectedCalloutItemsData();

    protected Consumer<MntlRunner> groupCallOutBlocksTest = runner -> {
        List<MntlScBlockGroupCalloutComponent> groupCallOutBlocks = new MntlBasePage<>(runner.driver(), MntlScBlockGroupCalloutComponent.class).getComponents();
        assertTrue("There are no group callout blocks present on the page", groupCallOutBlocks.size() > 0);

        int blockCount = 1;
        for (MntlScBlockGroupCalloutComponent groupCalloutBlock : groupCallOutBlocks) {
            groupCalloutBlock.scrollIntoViewCentered();
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            collector.checkThat("Group callout block #" + blockCount + " is not displayed", groupCalloutBlock.displayed(), is(true));

            // validate title if expected
            if (expectTitle) {
                validateField("Title", " for group callout block #" + blockCount, groupCalloutBlock.title().text(), groupCalloutBlock.hasTitle());
            } else {
                collector.checkThat("Title is not expected, though present for group callout block #" + blockCount, groupCalloutBlock.hasTitle(), is(false));
            }

            // validate subheading if expected
            if (expectSubheading) {
                validateField("Subheading", " for group callout block #" + blockCount, groupCalloutBlock.subheading().text(), groupCalloutBlock.hasSubHeading());
            } else {
                collector.checkThat("Subheading is not expected, though present for group callout block #" + blockCount, groupCalloutBlock.hasSubHeading(), is(false));
            }

            // locating group callout block items and looping through each
            List<MntlScBlockGroupCalloutComponent.Callout> calloutItems = groupCalloutBlock.callouts();
            assertTrue("There are no callout block items in group callout block #" + blockCount, calloutItems.size() > 0);
            collector.checkThat("The expected callout items data doesn't have expected size", expectedCalloutItemsData().size(), is(calloutItems.size()));
            int calloutCount = 1;
            for (MntlScBlockGroupCalloutComponent.Callout callout : calloutItems) {
                Map<String, Boolean> expectedData = expectedCalloutItemsData().get(calloutCount - 1);
                callout.scrollIntoViewCentered();
                runner.driver().waitFor(250, TimeUnit.MILLISECONDS);

                // validate image if expected
                if (expectedData.containsKey(EXPECT_IMAGE) && expectedData.get(EXPECT_IMAGE)) {
                    validateField("Image", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.image().src(), callout.hasImage());
                    collector.checkThat("Callout item #" + calloutCount + " in group callout block #" + blockCount + " image is not displayed", callout.image().isDisplayed(), is(true));
                    collector.checkThat("Callout item #" + calloutCount + " in group callout block #" + blockCount + " image alt is null or empty", callout.image().getAttribute("alt"), not(emptyOrNullString()));
                } else {
                    collector.checkThat("Image is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.hasImage(), is(false));
                }

                // validate heading if expected
                if (expectedData.containsKey(EXPECT_HEADING) && expectedData.get(EXPECT_HEADING)) {
                    validateField("Heading", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.heading().text(), callout.hasHeading());
                } else {
                    collector.checkThat("Heading is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.hasHeading(), is(false));
                }

                // validate date if expected
                if (expectedData.containsKey(EXPECT_DATE) && expectedData.get(EXPECT_DATE)) {
                    validateField("Date", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.date().text(), callout.hasDate());
                    collector.checkThat("Callout item #" + calloutCount + " in group callout block #" + blockCount + " date format is not correct", isValidDate(callout.date().text()), is(true));
                } else {
                    collector.checkThat("Date is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.hasDate(), is(false));
                }

                // validate body if expected
                if (expectedData.containsKey(EXPECT_BODY) && expectedData.get(EXPECT_BODY)) {
                    validateField("Body", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.body().text(), callout.hasBody());
                } else {
                    collector.checkThat("Body is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.hasBody(), is(false));
                }

                // validate link if expected
                if (expectedData.containsKey(EXPECT_LINK) && expectedData.get(EXPECT_LINK)) {
                    validateField("Link", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.attributeValue("href"), !callout.attributeValue("href").isEmpty());
                } else {
                    collector.checkThat("Link is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.attributeValue("href").isEmpty(), is(false));
                }

                // validate button if expected
                if (expectedData.containsKey(EXPECT_BUTTON) && expectedData.get(EXPECT_BUTTON)) {
                    validateField("Button", " for callout item #" + calloutCount + " in group callout block #" + blockCount, callout.button().text(), callout.hasButton());
                } else {
                    collector.checkThat("Button is not expected, though present for callout item #" + calloutCount + " in group callout block # " + blockCount, callout.hasButton(), is(false));
                }
                calloutCount++;
            }
            blockCount++;
        }
    };

    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    private void validateField(String field, String message, String element, boolean hasField) {
        collector.checkThat(field + " is expected, though not present" + message, hasField, is(true));
        if (hasField) {
            collector.checkThat(field + message + " is empty or null", element, not(emptyOrNullString()));
        }
    }
}