package com.about.mantle.components.navigation;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MntlSocialShareComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class MntlSocialShareTest extends MntlVenusTest implements MntlCommonTestMethods {

    /** This test validates Social Share component on the page.
     * It first checks the component is present in the DOM and displayed, 'data-title' and 'data-description' are properly formatted.
     * It will also validate 'data-email-subject' and 'data-email-body' fields are properly formatted if present.
     * Then it collects all the social share items, loops through each and validates the button is displayed and enabled, data-href, data-network and title
     * attributes are not null or empty. */
    protected Consumer<MntlRunner> socialShareTest = (runner) -> {
        // Validating Social Share component is present in the DOM and displayed
        assertTrue("Social share component is not present in the DOM", runner.page().componentExists(MntlSocialShareComponent.class));
        MntlSocialShareComponent socialShare = new MntlBasePage<>(runner.driver(), MntlSocialShareComponent.class).getComponent();
        socialShare.scrollIntoViewCentered();
        collector.checkThat("Social share component is not displayed", socialShare.displayed(), is(true));

        // Validating 'data-title' and 'data-description' fields are properly formatted
        String dataTitle = socialShare.attributeValue("data-title");
        collector.checkThat("Social share 'data-title' is not properly formatted", dataTitle, is(Jsoup.parse(dataTitle).text()));
        String dataDescription = socialShare.attributeValue("data-description");
        collector.checkThat("Social share 'data-description' is not properly formatted", dataDescription, is(Jsoup.parse(dataDescription).text()));

        // Validating 'data-email-subject' and 'data-email-body' fields are properly formatted if present
        if (socialShare.attributeValue("data-email-subject") != null && !socialShare.attributeValue("data-email-subject").isEmpty()) {
            String dataEmailSubject = socialShare.attributeValue("data-email-subject");
            collector.checkThat("Social share 'data-email-subject' is not properly formatted", dataEmailSubject, is(Jsoup.parse(dataEmailSubject).text()));
        }
        if (socialShare.attributeValue("data-email-body") != null && !socialShare.attributeValue("data-email-body").isEmpty()) {
            String dataEmailBody = socialShare.attributeValue("data-email-body");
            collector.checkThat("Social share 'data-email-body' is not properly formatted", dataEmailBody, is(Jsoup.parse(dataEmailBody).text()));
        }

        // Locating Social Share items, looping through and validating each of them
        List<MntlSocialShareComponent.SocialShareItems> socialShareItems = socialShare.socialShareItems();
        for (MntlSocialShareComponent.SocialShareItems socialShareItem : socialShareItems) {
            String socialShareButtonType = socialShareItem.className().substring(socialShareItem.className().lastIndexOf("-")+1);
            WebElementEx socialShareItemLink = socialShareItem.shareLink();
            socialShareItemLink.scrollIntoViewCentered();
            collector.checkThat("Social share button " + socialShareButtonType + " is not displayed or enabled", socialShareItemLink.isDisplayed() && socialShareItemLink.isEnabled(), is(true));
            collector.checkThat("Data-href for social share item link " + socialShareButtonType + " is null or empty", socialShareItemLink.getAttribute("data-href"), not(emptyOrNullString()));
            collector.checkThat("Data-network for social share item link " + socialShareButtonType + " is null or empty", socialShareItemLink.getAttribute("data-network"), not(emptyOrNullString()));
            collector.checkThat("Title attribute for social share item link " + socialShareButtonType + " is null or empty", socialShareItemLink.getAttribute("title"), not(emptyOrNullString()));
        }
    };
}
