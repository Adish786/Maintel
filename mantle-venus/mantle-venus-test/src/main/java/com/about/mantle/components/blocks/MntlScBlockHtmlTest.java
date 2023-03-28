package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScBlockHtmlComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public abstract class MntlScBlockHtmlTest extends MntlVenusTest implements MntlCommonTestMethods {
    protected abstract String getTestUrl();

    protected static int expectedNumberOfLinks;

    protected  Matcher<String> paragraphTestMatcher = not(emptyOrNullString());

    protected Consumer<MntlRunner> mntlScBlockHtmlTest = runner -> {
        MntlBasePage<MntlScBlockHtmlComponent> mntlScPage = new MntlBasePage<>(runner.driver(), MntlScBlockHtmlComponent.class);
        MntlScBlockHtmlComponent htmlComponent = mntlScPage.getComponent();

        collector.checkThat("paragraph is empty", htmlComponent.text(), paragraphTestMatcher);
        List<WebElementEx> links = htmlComponent.links();
        collector.checkThat("number link doesn't match", links.size(), is(expectedNumberOfLinks));
        for(int i=0; i < links.size(); ++ i){
            collector.checkThat("link " + (i+1) + " href is empty", links.get(i).href(), not(emptyOrNullString()));
        }
    };
}
