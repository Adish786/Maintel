package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlSponsorshipComponent;
import com.about.mantle.venus.model.pages.MntlScPage;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public class MntlSponsoredBlockTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected Consumer<MntlRunner> mntlSponsoredBlockTest = runner -> {
        MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlSponsorshipComponent.class);
        MntlSponsorshipComponent sponsorshipComponent = mntlScPage.mntlSponsorshipComponent();

        assertThat("Sponsored component is not displayed", sponsorshipComponent.displayed(), is(true));
        assertThat("Sponsorship Title is not displayed", sponsorshipComponent.title().isDisplayed(), is(true));

        MntlSponsorshipComponent.MntlSponsorshipContentComponent sponsorContent = sponsorshipComponent.content();
        assertThat("Sponsorship image is not displayed", sponsorContent.logo().isDisplayed(), is(true));
        final String sponsorUrl = sponsorContent.logoWrapper().href();
        assertThat("Sponsorship link is not present in Sponsor content", sponsorUrl, not(emptyOrNullString()));

        String windowHandle = runner.driver().getWindowHandle(() -> sponsorContent.logoWrapper().click());
        runner.driver().switchTo().window(windowHandle);
        runner.page().waitFor().pageTitleIsNot("about:blank");
        assertThat("Sponsored item page not loaded", runner.driver().getCurrentUrl().equals(sponsorUrl), is(true));

    };

}
