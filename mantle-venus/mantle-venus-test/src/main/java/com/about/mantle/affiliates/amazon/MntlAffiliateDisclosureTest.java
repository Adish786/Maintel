package com.about.mantle.affiliates.amazon;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.widgets.MntlAffiliateDisclosureComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SuppressWarnings("rawtypes")
public abstract class MntlAffiliateDisclosureTest extends MntlVenusTest implements MntlCommonTestMethods {

	abstract public String setUrl();

	@SuppressWarnings("unchecked")
	public Consumer<MntlRunner> affiliateDisclouserTest = runner -> {
		MntlBasePage page = new MntlBasePage(runner.driver(), MntlAffiliateDisclosureComponent.class);
		String pageSource = runner.driver().getPageSource();

		MntlAffiliateDisclosureComponent disclouser = (MntlAffiliateDisclosureComponent) page.getComponent();
		collector.checkThat("affiliate disclouser label is missing",
				disclouser.affiliateDisclosureLabel().isDisplayed(), is(true));
		collector.checkThat("affiliate label text is empty", disclouser.affiliateDisclosureLabel().text(),
				not(emptyOrNullString()));
		collector.checkThat("affiliate icon is missing", disclouser.affiliateDisclosureIcon().isDisplayed(), is(true));
		collector.checkThat("preconnect skimlink is missing", pageSource,
				containsString("link rel=\"preconnect\" href=\"//s.skimresources.com\""));

	};
}
