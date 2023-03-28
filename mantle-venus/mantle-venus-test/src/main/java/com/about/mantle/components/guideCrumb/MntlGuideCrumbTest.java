package com.about.mantle.components.guideCrumb;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.guideCrumb.MntlGuideCrumbComponent;
import com.about.venus.core.driver.WebDriverExtended;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class MntlGuideCrumbTest extends MntlVenusTest implements MntlCommonTestMethods{

    public void testGuideCrumb(WebDriverExtended driver, MntlGuideCrumbComponent guideCrumb) {
        collector.checkThat("Guidecrumb component doesn't exist", guideCrumb.displayed(), is(true));
        if(desktop(driver)){
            collector.checkThat("Guidecrumb component pretext doesn't exist", guideCrumb.preText().isDisplayed(), is(true));
        }
        collector.checkThat("Guidecrumb component main doesn't exist", guideCrumb.guideCrumbMain().isDisplayed(), is(true));
        collector.checkThat("Guidecrumb component button doesn't exist", guideCrumb.guideCrumbButton().isDisplayed(), is(true));
        collector.checkThat("Guidecrumb component icon doesn't exist", guideCrumb.guideCrumbButtonIcon().isDisplayed(), is(true));
        guideCrumb.guideCrumbButtonIcon().click();
        guideCrumb.waitFor().aMoment(2, TimeUnit.SECONDS);
        collector.checkThat("Guidecrumb component dropdown doesn't exist", guideCrumb.guideCrumbDropDown().isDisplayed(), is(true));
        collector.checkThat("Guidecrumb component dropdown list size should not be zero.", guideCrumb.journeyNavSublistItems().size(), notNullValue());
        for(MntlGuideCrumbComponent.JourneyNavSublist item : guideCrumb.journeyNavSublistItems())
        {
            collector.checkThat("Guidecrumb component dropdown list item doesn't have href.", item.journeyNavSublistAnchor().href(), notNullValue());
            collector.checkThat("Guidecrumb component dropdown list item title doesn't show.", item.journeyNavSublistAnchor().text(), notNullValue());
        }
    }

    protected Consumer<Runner> testGuideCrumb = (Runner runner) -> {
        WebDriverExtended driver = runner.driver();
        MntlGuideCrumbComponent guideCrumb = (MntlGuideCrumbComponent)runner.component();
        testGuideCrumb(driver, guideCrumb);
    };
}
