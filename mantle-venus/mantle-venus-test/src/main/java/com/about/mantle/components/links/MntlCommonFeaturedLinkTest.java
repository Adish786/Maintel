package com.about.mantle.components.links;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.about.mantle.clickTracking.MntlClickTrackingTest;
import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.selene.vertical.Vertical;
import com.about.mantle.venus.model.components.structuredContent.MntlFeaturedLinkComponent;
import com.about.mantle.venus.model.pages.MntlFeaturedLinkPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.ConfigurationProperties;

public abstract class MntlCommonFeaturedLinkTest extends MntlClickTrackingTest implements MntlCommonTestMethods {

	abstract public String setDomain();


	private ArrayList<String> safeList = getSafeList();

	public Function<Runner, Function<String, Consumer<TrackingData>>> featuredLinkWithClickTrackingFollowTest = runner -> featuredLinkType -> eventData -> {
		WebDriverExtended driver = runner.driver();
			featuredLinkCommonValidations(driver, featuredLinkType).forEach(featuredLink -> {
					WebElementEx link = featuredLink.link();
					setLinkTarget(driver, link);
					driver.waitFor(2, TimeUnit.SECONDS);
					testLinkClick(driver, eventData, 0, link);
				});
	};
	
	
	public BiConsumer<Runner, String> featuredLinkTest = (runner, featuredLinkType) -> {
		WebDriverExtended driver = runner.driver();
		featuredLinkCommonValidations(driver, featuredLinkType);
	};
	
	private List<MntlFeaturedLinkComponent> featuredLinkCommonValidations(WebDriverExtended driver, String featuredLinkType) {
		
		MntlFeaturedLinkPage page = new MntlFeaturedLinkPage(driver);
		List<MntlFeaturedLinkComponent> featuredComponents = page.featuredLinkContent();
		List<MntlFeaturedLinkComponent> featuredLinksWithType = null;
		if (featuredComponents.size() > 0) {
			 featuredLinksWithType = featuredComponents.stream()
					.filter(featuredLink -> featuredLink.className().contains(featuredLinkType.toLowerCase()))
					.collect(Collectors.toList());
			if (featuredLinksWithType.size() > 0) {
				featuredLinksWithType.forEach(featuredLink -> {
					collector.checkThat("featured link doesn't exist", featuredLink.displayed(), is(true));
					featuredLink.scrollIntoViewCentered();
					WebElementEx link = featuredLink.link();
					collector.checkThat("featured link text doesn't match", link.getText(), not(emptyOrNullString()));
					collector.checkThat("featured link href doesn't match", link.getAttribute("href"),
							not(emptyOrNullString()));

					String linkHrefAttribute = link.getAttribute("href");
					String domainName = null;
					try {
						domainName = getDomainName(linkHrefAttribute);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					// checking if featured link is cross-vertical
					boolean isValidEnum = false;
					for (Vertical vertical : Vertical.values()) {
						if (vertical.domainValue().contains(domainName)) {
							isValidEnum = true;
							break;
						}
					}
					if (isValidEnum && !domainName.contains(setDomain() + ".com")) {
						collector.checkThat(
								"Cross-vertical featured link " + linkHrefAttribute + " does not contain nofollow ",
								link.getAttribute("rel"), containsString("nofollow"));
					} else
					// checking if featured link is internal
					if (linkHrefAttribute.contains(setDomain()) || linkHrefAttribute.replace("www-", "")
							.startsWith(ConfigurationProperties.getTargetProjectBaseUrl(null))) {
						collector.checkThat("Internal featured link " + linkHrefAttribute + " does contain nofollow ",
								link.getAttribute("rel"), not(containsString("nofollow")));
					} else
					// checking if featured external link is present on the safelist or not
					if (!isLinkSafe(domainName)) {
						collector.checkThat(
								"External featured link " + linkHrefAttribute
										+ " that is NOT on the safelist does not contain nofollow ",
								link.getAttribute("rel"), containsString("nofollow"));
					} else {
						collector.checkThat(
								"External featured link " + linkHrefAttribute
										+ " that IS 0n the safelist does contain nofollow ",
								link.getAttribute("rel"), not(containsString("nofollow")));
					}
				});
			} else {
				Assert.fail(" featured link of type" + featuredLinkType + " not found on page");
			}
		} else {
			Assert.fail("featured link not found on page");
	
		}
		return featuredLinksWithType;
	}

	public boolean isLinkSafe(String link) {
		boolean isLinkSafe = false;
		if (safeList.contains(link))
			isLinkSafe = true;
		return isLinkSafe;
	}
	
}
