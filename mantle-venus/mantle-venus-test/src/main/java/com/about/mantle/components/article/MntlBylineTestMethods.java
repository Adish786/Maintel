package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlBylineComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebDriverExtended;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@Deprecated // Due to https://dotdash.atlassian.net/browse/AXIS-1766, will be replaced with `MntlBylinesTest` once all the verticals are on the new `bylines` component.
public abstract class MntlBylineTestMethods extends MntlVenusTest implements MntlCommonTestMethods {

	protected void byLineTest(WebDriverExtended driver, String bylineText) {
		MntlBasePage page = new MntlBasePage<>(driver, MntlBylineComponent.class);
		boolean componentExists = page.componentExists(MntlBylineComponent.class);
		assertThat("Byline component doesn't exist", componentExists, is(true));
		MntlBylineComponent bylineComponent = (MntlBylineComponent) page.getComponent();
		bylineComponent.scrollIntoViewCentered();
		collector.checkThat("byline is not displayed", bylineComponent.displayed(), is(true));
		collector.checkThat("byline does not start with the word 'by'", bylineComponent.text(), startsWith(bylineText));
		collector.checkThat("bio link does not have href", bylineComponent.bioLink().getAttribute("href"),
				not(emptyOrNullString()));
	};

}
