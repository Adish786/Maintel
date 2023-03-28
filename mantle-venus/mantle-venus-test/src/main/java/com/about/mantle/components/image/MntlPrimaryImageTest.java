package com.about.mantle.components.image;

import com.about.mantle.venus.model.components.images.MntlPrimaryImageComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public class MntlPrimaryImageTest extends MntlImageTest {

	/**
	 * This test will verify primary image attributes.
	 */
	protected Consumer<Runner> primaryImagesTest = runner -> {
		MntlBasePage page = new MntlBasePage(runner.driver(), MntlPrimaryImageComponent.class);
		MntlPrimaryImageComponent primaryImage = (MntlPrimaryImageComponent) page.getComponent();
		primaryImage.scrollIntoViewCentered();
		collector.checkThat("primary image doesn't display as expected", primaryImage.displayed(), is(true));
		verifyImageAttributes(primaryImage);
		collector.checkThat("primary image is not blurry", primaryImage.attributeValue("style"), containsStringIgnoringCase("--blurry"));
	};

	/**
	 * The test gets the network call (will filter based on the current url) and retrieves link header value.
	 * If image has both src and srcset then it checks if link header is using srcset value, if there's only src, then image link
	 * header should use src value.
	 * */
	protected Consumer<MntlRunner> verifyPrimaryImageLinkHeader = runner -> {
		assertTrue("There's no primary image on the page", runner.page().componentExists(MntlPrimaryImageComponent.class));
		runner.page().waitFor().documentComplete();
		runner.driver().navigate().refresh();
		runner.driver().waitFor(3, TimeUnit.SECONDS);

		MntlBasePage page = new MntlBasePage(runner.driver(), MntlPrimaryImageComponent.class);
		MntlPrimaryImageComponent primaryImage = (MntlPrimaryImageComponent) page.getComponent();
		primaryImage.scrollIntoView();
		collector.checkThat("Primary image src attribute is null or empty", primaryImage.src(), not(emptyOrNullString()));
		String imageSrc = primaryImage.src();
		collector.checkThat("Primary image " + imageSrc + " did not display", primaryImage.displayed(), is(true));

		// Checking if image has srcset
		boolean imageHasSrcSet = primaryImage.attributeValue("srcset") != null && !primaryImage.attributeValue("srcset").isEmpty();
		String imageSrcSet;
		if (imageHasSrcSet) {
			imageSrcSet = primaryImage.attributeValue("srcset");
			collector.checkThat("The primary image " + imageSrc + " has both src and srcset, but the preload image link header is not using srcset",
					imageSrcSet.substring(imageSrcSet.indexOf("/thmb"), imageSrcSet.lastIndexOf(" ")), equalTo(getHrefValueFromResponseHeaders(runner)));
		} else {
			collector.checkThat("The primary image " + imageSrc + " has only src, but the preload image link header is not using src",
					imageSrc.substring(imageSrc.indexOf("/thmb")), equalTo(getHrefValueFromResponseHeaders(runner)));
		}
	};

	/** The method will return href value from Link Header Response.
	 *
	 * @param runner
	 * */
	private String getHrefValueFromResponseHeaders(MntlRunner runner) {
		String hrefValue = "";

		// Getting network call (the one with current url)
		String linkHeaderResponse = getLinkHeaderResponse(runner);
		String[] headersArr = linkHeaderResponse.split(";");
		for (String pair : headersArr) {
			if (pair.contains("href")) {
				hrefValue = pair.substring(pair.indexOf("=") + 1).replaceAll("\"", "");
			}
		}
		return hrefValue.substring(hrefValue.indexOf("/thmb"));
	}

}