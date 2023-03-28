package com.about.mantle.affiliates.amazon;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.widgets.MntlAffiliateDisclosureComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings("rawtypes")
public abstract class MntlDisableSkimlinksTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	abstract public String setUrl();
	abstract public String amazonID();
	
	@SuppressWarnings("unchecked")
	public Consumer<MntlRunner> disableSkimlinksTest = runner -> {
		MntlBasePage page = new MntlBasePage(runner.driver(),MntlAffiliateDisclosureComponent.class);
		String pageSource = runner.driver().getPageSource();
		collector.checkThat("affiliate disclouser is present", pageSource.contains("mntl-affiliate-disclosure"), is(false));
		collector.checkThat("amazon affiliate ID is present", pageSource.contains(amazonID()), is(false));
		collector.checkThat("skimlink is present in page source ", pageSource.contains("skimlinks.js"), is(false));
		collector.checkThat("commerce disclouser is present", pageSource.contains("mntl-commerce-disclosure"), is(false));
		collector.checkThat("Incorrect preconnect ", pageSource.contains("link rel=\\\"preconnect\\\" href=\\\"//s.skimresources.com\\\""), is(false));


	};
}


