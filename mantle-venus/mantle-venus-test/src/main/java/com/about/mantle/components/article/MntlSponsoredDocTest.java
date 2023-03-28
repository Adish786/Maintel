package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.selene.document.client.DocumentClient;
import com.about.mantle.utils.selene.documentutils.DocumentHelper;
import com.about.mantle.venus.model.components.blocks.MntlMultipleSponsorshipComponent;
import com.about.mantle.venus.model.components.blocks.MntlSponsorshipComponent;
import com.about.mantle.venus.model.components.blocks.MntlSponsorshipComponent.MntlSponsorshipContentComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.driver.selection.DriverSelection.Matcher;
import com.about.venus.core.test.VenusTest;
import com.jayway.jsonpath.DocumentContext;
import org.hamcrest.Matchers;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.about.mantle.utils.selene.utils.DocumentTestUtils.documentTestWithDelete;
import static com.about.mantle.venus.utils.selene.SeleneUtils.getDocuments;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public class MntlSponsoredDocTest  extends VenusTest implements MntlCommonTestMethods {

    public void sponsoredDocumentTest(String template, String documentFilePath,
                                      String sponsorUrl, Matcher matcher) throws IOException {
        DocumentHelper documentHelper = new DocumentHelper(template);
        DocumentClient client = documentHelper.createDocument(documentFilePath);
        client.update(OK, false, false);
        wait(2, TimeUnit.SECONDS);
        documentTestWithDelete(client, () -> {
            test(matcher, driver -> {
                Runner runner = startTest(documentHelper.getUrl(), driver)
                    .onComponent(MntlSponsorshipComponent.class);
                runner.loadUrl().runTest(
                    test -> {
                        MntlSponsorshipComponent component = (MntlSponsorshipComponent) runner.component();
                        MntlSponsorshipContentComponent sponsorContent = component.content();
                        assertThat("Sponsored component is displayed",
                            component.displayed(), is(true));
                        assertThat("Sponsorship byline is not displayed",
                            component.title().getText().toLowerCase(), is("sponsored by"));
                        assertThat("Sponsorship logo is not displayed",
                            sponsorContent.logo().isDisplayed(), is(true));
                        assertThat("Sponsorship link is not set as expected",
                            sponsorContent.logoWrapper().href(), containsString(sponsorUrl));
                        assertThat("Sponsorship disclaimer is not displayed",
                            sponsorContent.disclaimer().getText().toLowerCase(), is("what's this?"));
                        String disclaimerTextOnHover = sponsorContent.disclaimer()
                            .pseudoElementProperty("after", "content");
                        assertThat("Sponsorship disclaimer text is not displayed on hover",
                            disclaimerTextOnHover, not(emptyOrNullString()));
                    });
            });
        });
    }

    protected Consumer<Runner> mntlMultipleSponsorLogoTest = runner -> {

        String url = runner.url().replace("?globeNoTest=true", "");
        DocumentContext documentData = getDocuments(url.split("-")[url.split("-").length - 1]);

        MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlMultipleSponsorshipComponent.class);
        MntlMultipleSponsorshipComponent sponsorshipComponent = mntlScPage.mntlMultipleSponsorshipComponent();

        assertThat("Sponsored component is not displayed", sponsorshipComponent.displayed(), is(true));
        assertThat("Sponsorship Title is not displayed", sponsorshipComponent.title().isDisplayed(), is(true));
        int numberOfSponsors = getNumberOfSponsorsFromDocument(documentData);
        assertThat("Number of Sponsorship links are not as expected", sponsorshipComponent.itemLinks().size(), is(numberOfSponsors));

        for(MntlMultipleSponsorshipComponent.MntlMultipleSponsorshipComponentItem link : sponsorshipComponent.itemLinks()){
             assertThat("Sponsorship link is not present in Sponsor content", link.getElement().getAttribute("href"), Matchers.not(Matchers.emptyOrNullString()));
             assertThat("Sponsorship Logo is not displayed", link.logo().isDisplayed(), is(true));
             assertThat("Sponsorship Logo image not present", link.logo().getAttribute("src"), not(emptyOrNullString()));
        }
    };

    private int getNumberOfSponsorsFromDocument(DocumentContext documentData) {
        try {
            return documentData.read("$.data.sponsors.totalSize");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
