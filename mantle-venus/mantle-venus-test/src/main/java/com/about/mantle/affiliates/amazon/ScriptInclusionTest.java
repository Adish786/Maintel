package com.about.mantle.affiliates.amazon;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.model.ScriptTagComponent;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScriptInclusionTest extends MntlVenusTest {

	
	protected String url;
	protected String tag;

	public ScriptInclusionTest(String url, String tag) {
		super();
		this.url = url;
		this.tag = tag;
	}

	public void scriptTest(WebDriverExtended driver, String url, String tag) {
		driver.get(url);
		MntlPage page = new MntlPage(driver);
		page.waitFor().exactMoment(1, TimeUnit.SECONDS);
		driver.waitFor((Predicate<WebDriver>) webdriver -> page.getScriptsFromBody(tag).size() != 0, 30);
		final List<ScriptTagComponent> scripts = page.getScriptsFromBody(tag);
		assertThat("not exactly one instance of the amazon affiliate script included on the page", scripts.size(), is(1));
	}
}
