package com.about.mantle.commerce;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.widgets.MntlCommerceDisclosureComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import org.junit.Assert;

@SuppressWarnings("unchecked")
public abstract class MntlCommerceNewDisclosureTest extends MntlVenusTest implements MntlCommonTestMethods {

	abstract public String getDocId();
	private final String disclosureText = "We independently evaluate all recommended products and services. If you click on links we provide, we may receive compensation. Learn more.";
	private final String path = "$.data.revenueGroup";

	public Consumer<MntlRunner> commerceDisclosureTest = runner -> {
		Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
		MntlBasePage page = new MntlBasePage(runner.driver(), MntlCommerceDisclosureComponent.class);
		DocumentContext seleneData = SeleneUtils.getDocuments(getDocId());
		var revenuePath = JsonPath.using(conf).parse(seleneData.jsonString()).read(path);
		if(revenuePath != null) {
			String revenueGroup = seleneData.read(path).toString().toLowerCase();
			boolean isCommerce = revenueGroup.equalsIgnoreCase("commerce")
					|| revenueGroup.equalsIgnoreCase("commercenewsdeals") ? true : false;
			if (isCommerce) {
				MntlCommerceDisclosureComponent disclosure = (MntlCommerceDisclosureComponent) page.getComponent();
				disclosure.reviewProcessLink().click();
				String parentWindow = runner.driver().getWindowHandle();
				for (String winHandle : runner.driver().getWindowHandles()) {
					runner.driver().switchTo().window(winHandle);
				}
				runner.page().waitFor().aMoment();
				String currentUrl = runner.driver().getCurrentUrl();
				runner.driver().close();
				runner.driver().switchTo().window(parentWindow);
				collector.checkThat("Disclosure text is incorrect", disclosure.text(), is(disclosureText));
				collector.checkThat("'Learn more' text is incorrect", disclosure.reviewProcessLink().getText().trim(),
						is("Learn more"));
				collector.checkThat("href is incorrect. ", disclosure.reviewProcessLink().getAttribute("href"),
						not(emptyOrNullString()));
				collector.checkThat("disclosure review process here click is not correct", currentUrl,
						is(disclosure.reviewProcessLink().href()));
			} else {
				boolean disclosure = page.componentExists(MntlCommerceDisclosureComponent.class);
				Assert.assertFalse("Disclosure is displayed when revenue group is not either commerce or commerceNewsDeal", disclosure);
			}
		} else {
			Assert.fail("Document should have revenue group which is either commerce or commerceNewsDeal");
		}

	};

}
