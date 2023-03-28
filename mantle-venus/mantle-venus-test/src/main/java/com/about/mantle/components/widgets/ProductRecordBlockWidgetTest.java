package com.about.mantle.components.widgets;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.buttons.MntlProductBlockCommerceButton;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.components.lists.MntlProductBlockCommerceItemComponent;
import com.about.mantle.venus.model.pages.MntlListScPage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.startsWithIgnoringCase;

public abstract class ProductRecordBlockWidgetTest extends MntlVenusTest implements MntlCommonTestMethods<MntlRunner> {

	abstract public boolean isRevenueGroupCommerce();

	protected Consumer<MntlRunner> productRecordBlockTest = runner -> {
		MntlListScPage<MntlComponent> mntlListScPage = new MntlListScPage<>(runner.driver(), MntlListScItemComponent.class);
		MntlListScPage<MntlComponent> mntlListScProduct = new MntlListScPage<>(runner.driver(), MntlProductBlockCommerceItemComponent.class);
		List<String> retailers = new ArrayList<String>();
		mntlListScProduct.scrollIntoViewCentered();
		List<MntlProductBlockCommerceItemComponent> listScProductBlocks = mntlListScPage.ProductRecordBlocks();
		collector.checkThat("ListSc blocks are missing ", listScProductBlocks.size(), greaterThan(0));

		String customAmazonRelAttributes = customAmazonRelAttributes();
		String customOtherRetailersAttributes = customOtherRetailersAttributes();
		for (MntlProductBlockCommerceItemComponent productBlock : listScProductBlocks) {
			productBlock.scrollIntoView();
			List<MntlProductBlockCommerceButton> retailersButtons = productBlock.commerceProductButtons();
			collector.checkThat("product block buttons are missing ", retailersButtons.size(), greaterThan(0));
			collector.checkThat("product block image is not displayed", productBlock.productBlockImage().isDisplayed(),
					is(true));
			collector.checkThat("product block header is empty or null", productBlock.productBlockHeading().getText(),
					not(emptyOrNullString()));
			if (productBlock.hasStarRating()) {
				collector.checkThat("star rating attribute is empty or null", productBlock.productStarRating().getText(),
						not(emptyOrNullString()));
			}
			for (MntlProductBlockCommerceButton retailerButton : retailersButtons) {

				retailerButton.scrollIntoViewCentered();
				collector.checkThat("retailer button is not present", retailerButton.displayed(), is(true));
				collector.checkThat("retailer button is not linkable", retailerButton.href(),
						not(emptyOrNullString()));
				collector.checkThat("retailer button text is not correct",
						retailerButton.ProductBlockButtonText().getText(), startsWithIgnoringCase("Buy On"));
				String retailerLink = retailerButton.href();
				if (retailerLink.contains("amazon.com")) {
					runner.page().waitFor().aMoment();
					collector.checkThat("product block image src is not correct", productBlock.productBlockImage().src(),
							anyOf(startsWith("https://images-na.ssl-images-amazon.com/images/"),
									anyOf(endsWith(".jpg"), endsWith(".png"), endsWith(".JPG"), endsWith(".jpeg"))));
					collector.checkThat("Rel attribute is not correct", retailerButton.attributeValue("rel"),
							is(customAmazonRelAttributes == null ? "noskim noopener nofollow" : customAmazonRelAttributes));
					if (isRevenueGroupCommerce()) {
						collector.checkThat("Amazon link does not contain ascsubtag", retailerLink,
								containsString("ascsubtag"));
						if (retailerLink.contains("amazon")
								&& retailerButton.className().contains("extended-commerce--prime-eligible")) {
							collector.checkThat("Price does not start with $ sign",
									retailerButton.attributeValue("data-commerce-price"), startsWith("$"));
							collector.checkThat("price tag is not displayed",
									retailerButton.attributeValue("data-commerce-price").substring(1),
									not(emptyOrNullString()));
						}
					}
				} else {
					collector.checkThat("Rel attribute is not correct", retailerButton.attributeValue("rel"),
							is(customOtherRetailersAttributes == null ? "noopener nofollow" : customOtherRetailersAttributes));
				}

				retailerLink = StringUtils.substringBetween(retailerLink, ".", "/");
				if (!retailers.contains(retailerLink)) {
					retailers.add(retailerLink);
					retailerButton.click();
					String parentWindow = runner.driver().getWindowHandle();

					for (String winHandle : runner.driver().getWindowHandles()) {
						runner.driver().switchTo().window(winHandle);
					}
					runner.page().waitFor().aMoment();
					String currentUrl = runner.driver().getCurrentUrl();
					retailerLink = retailersEditedList(retailerLink);
					collector.checkThat("retailer page link is not correct", currentUrl, containsString(retailerLink));
					runner.driver().close();
					runner.driver().switchTo().window(parentWindow);

				}
			}
		}
	};

	public String retailersEditedList(String retailerName) {

		Map<String, String> retailerList = new HashMap<>();
		retailerList.put("verizonwireless.com", "verizon");
		retailerList.put("sonymobile.com", "sony");

		return retailerList.containsKey(retailerName) ? retailerList.get(retailerName) : retailerName;
	}

	protected String customAmazonRelAttributes() {
		return null;
	}

	protected String customOtherRetailersAttributes() {
		return null;
	}
}
