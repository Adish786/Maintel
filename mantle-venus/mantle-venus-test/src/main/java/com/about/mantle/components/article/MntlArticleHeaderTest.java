package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MtnlArticleHeaderComponent;
import com.about.venus.core.driver.WebDriverExtended;

import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;

public class MntlArticleHeaderTest extends MntlVenusTest implements MntlCommonTestMethods {
    /*
    @param url: run test on this url
    @param driver: driver instance to run test
    @param component: the component class must extend MtnlArticleHeaderComponent
    @param test: must be one of headingTest or subHeadingTest
     */
	protected <T extends MtnlArticleHeaderComponent> void runTest(String url, WebDriverExtended driver
			, Class<T> component, Consumer<Runner> test) {
		startTest(url, driver).onComponent(component).loadUrl().runTest(test);
	}

	protected Consumer<Runner> headingTest = (runner) -> {
		MtnlArticleHeaderComponent articleHeaderComponent = (MtnlArticleHeaderComponent) runner.page().getComponent();
		collector.checkThat("Article Heading is not displayed", articleHeaderComponent.heading().isDisplayed(),
							is(true));
		collector.checkThat("Article Heading Text is empty or null string", articleHeaderComponent.heading().getText(),
							is(not(emptyOrNullString())));
	};

	protected Consumer<Runner> subHeadingTest = (runner) -> {
		MtnlArticleHeaderComponent articleHeaderComponent = (MtnlArticleHeaderComponent) runner.page().getComponent();
		collector.checkThat("Article Sub Heading is not displayed", articleHeaderComponent.subHeading().isDisplayed(),
							is(true));
		collector.checkThat("Article Sub Heading Text is empty or null string", articleHeaderComponent.subHeading().getText(),
							is(not(emptyOrNullString())));
	};

}
