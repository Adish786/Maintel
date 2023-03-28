package com.about.mantle.components.widgets;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.widgets.MntlDisqusComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

public class MntlDisqusTest extends MntlVenusTest {

	protected void disqusTest(WebDriverExtended driver, MntlDisqusComponent disqus) {
		MntlScPage mntlScPage = new MntlScPage<>(driver, MntlScBlockComponent.class);
		if (mntlScPage.hasContinueReading()) {
			mntlScPage.scrollIntoViewCentered();
			mntlScPage.continueReading().jsClick();
		}
		disqus.scrollIntoViewCentered();
		driver.waitFor((Predicate<WebDriver>) wd -> disqus.displayed(), 30);
		collector.checkThat("Mntl Disqus component is not displayed", disqus.displayed(), is(true));
		collector.checkThat("Mntl Disqus component is collapsed", disqus.size().height, greaterThan(0));
		driver.waitFor((Predicate<WebDriver>) wd -> disqus.disqusIframe().isDisplayed(), 30);
		collector.checkThat("Mntl Disqus component iframe is not displayed", disqus.disqusIframe().isDisplayed(),
				is(true));

	}

}