package com.about.mantle.components.navigation;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.global.MntlFooterComponent;
import com.about.mantle.venus.model.components.navigation.MntlDotdashUniversalNavComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

/*
This test will first validate whether Dotdash Meredith Universal Nav component is present in the DOM.
Then it will check if the component and logo are displayed, family nav title is correct, redirect to Dotdash Meredith url
and check if it is "https://www.dotdashmeredith.com/", logo has the correct size and Dotdash Meredith hyperlink target tag is _blank.
It will then check the privacy policy text, redirect to privacy policy url and check if it's "https://www.dotdashmeredith.com/brands-privacy"
and hyperlink target tag is _blank.
 */

public abstract class MntlDotdashMeredithUniversalNavTest<T extends MntlFooterComponent> extends MntlVenusTest implements MntlCommonTestMethods {

    protected abstract Class<T> getFooter();

    protected abstract String familyNavTitle();

    protected String logoSizeMobile = "(100, 25)";
    protected String logoSizePC = "(100, 25)";
    protected String logoSizeTablet = "(100, 25)";

    protected Consumer<MntlRunner> testDotdashNavComponent = (runner) -> {
        assertTrue("Dotdash Meredith Universal Nav component is not present in the DOM", runner.page().componentExists(MntlDotdashUniversalNavComponent.class));
        MntlBasePage mntlBasePage = new MntlBasePage<>(runner.driver(), getFooter());
        MntlFooterComponent footerComponent = (MntlFooterComponent) mntlBasePage.getComponent();
        MntlDotdashUniversalNavComponent dotdashUniversalNavComponent = footerComponent.findComponent(MntlDotdashUniversalNavComponent.class);
        dotdashUniversalNavComponent.scrollIntoViewCentered();
        runner.driver().waitFor(3, TimeUnit.SECONDS);
        collector.checkThat("Footer Family Nav Component is not displayed on the page", dotdashUniversalNavComponent.displayed(), is(true));

        // test universal nav logo, family nav title, Dotdash Meredith url, logo size and hyperlink target tag is '_blank'
        collector.checkThat("Dotdash Meredith Nav logo is not displayed", dotdashUniversalNavComponent.dotdashLogo().isDisplayed(), is(true));
        collector.checkThat("Dotdash Meredith family nav title is not correct", dotdashUniversalNavComponent.title().getText(), is(familyNavTitle()));
        String parent = runner.driver().getWindowHandle();
        dotdashUniversalNavComponent.titleLink().click();
        for (String windowHandle : runner.driver().getWindowHandles()) {
            runner.driver().switchTo().window(windowHandle);
        }
        collector.checkThat("Dotdash Meredith url is not correct", runner.driver().getCurrentUrl(), is("https://www.dotdashmeredith.com/"));
        runner.driver().close();
        runner.driver().switchTo().window(parent);
        collector.checkThat("Dotdash Meredith logo size is not correct", dotdashUniversalNavComponent.dotdashLogo().getSize().toString(),
                is(mobile(runner.driver()) ? logoSizeMobile : tablet(runner.driver()) ? logoSizeTablet : logoSizePC));
        collector.checkThat("Universal nav hyperlink target tag is not _blank", dotdashUniversalNavComponent.titleLink().getAttribute("target"), is("_blank"));
    };
}
