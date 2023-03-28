package com.about.mantle.commerce;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.widgets.MntlCommerceDisclosureComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SuppressWarnings("rawtypes")
@Deprecated // use MntlCommerceNewDisclosureTest
public abstract class MntlCommerceDisclosureTest extends MntlVenusTest implements MntlCommonTestMethods {

	private static final String DEFAULT_DISCLOSURE_TEXT =
					"Our editors independently research, test, and recommend the best products;" +
					" you can learn more about our review process here." +
					" We may receive commissions on purchases made from our chosen links.";

	abstract public String getUrl();

	abstract public String getUrlNoAffiliate();

	protected String disclosureText(){
		return DEFAULT_DISCLOSURE_TEXT;
	}

	@SuppressWarnings("unchecked")
	public Consumer<MntlRunner> commerceDisclosureTest = runner -> {
		MntlBasePage page = new MntlBasePage(runner.driver(), MntlCommerceDisclosureComponent.class);
		String pageSource = runner.driver().getPageSource();

		MntlCommerceDisclosureComponent disclosure = (MntlCommerceDisclosureComponent) page
				.getComponent();
		collector.checkThat("Incorrect text found", disclosure.text(), is(disclosureText()));
		collector.checkThat("Incorrect text found", disclosure.reviewProcessLink().getText(),
				is("review process here"));
		collector.checkThat("href is incorrect. ", disclosure.reviewProcessLink().getAttribute("href"),
				not(emptyOrNullString()));
		collector.checkThat("Incorrect preconnect occurrence", pageSource,
				containsString("link rel=\"preconnect\" href=\"//s.skimresources.com\""));
		collector.checkThat("affiliate disclouser is present", pageSource.contains("mntl-affiliate-disclosure"), is(false));

	};
}
