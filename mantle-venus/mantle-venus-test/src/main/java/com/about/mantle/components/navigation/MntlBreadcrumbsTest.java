package com.about.mantle.components.navigation;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.navigation.MntlBreadcrumbsComponent;
import com.about.venus.core.driver.WebElementEx;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MntlBreadcrumbsTest extends MntlVenusTest {

    protected  <T extends MntlBreadcrumbsComponent> void breadcrumbsTest(T mntlBreadcrumbsComponent) {
        if (!mntlBreadcrumbsComponent.displayed()) {
            assertThat("Breadcrumbs component doesn't display", false, is(true));
            return;
        }

        mntlBreadcrumbsComponent.scrollIntoViewCentered();
        boolean hasBreadcrumbsItems = !mntlBreadcrumbsComponent.breadcrumbsItems().isEmpty();
        if (hasBreadcrumbsItems) {
            for (WebElementEx tagNavItem : mntlBreadcrumbsComponent.breadcrumbsItems()) {
                collector.checkThat("tag-nav component is not displayed",
                    tagNavItem.isDisplayed(), is(true));
                collector.checkThat("tag-nav link href is blank",
                    tagNavItem.href(), notNullValue());
                collector.checkThat("tag-nav text is blank",
                    tagNavItem.text(), notNullValue());
            }
        } else {
            collector.checkThat("there is no breadcrumbs items",
                mntlBreadcrumbsComponent.breadcrumbsItems().isEmpty(), is(false));
        }
    }

}
