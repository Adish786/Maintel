package com.about.mantle.source;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MntlArticleSourcesComponent;
import com.about.mantle.venus.model.pages.InlineCitationCommonPage;
import com.about.venus.core.driver.WebElementEx;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@SuppressWarnings("rawtypes")
public class MntlInlineCitationTest extends MntlVenusTest implements MntlCommonTestMethods {
	/*
	 @param editorialProcess : a static String value that needs to be passed from vertical side to check
	 							source guideline href value
	 @param hasAdditionalReading : a boolean value to check Additional reading 							
	 								if additional reading is available on the page
	 */
	protected static String editorialProcess = "";

	protected BiConsumer<Runner, Boolean> inlineCitationsTest = (runner, hasAdditionalReading) -> {
		InlineCitationCommonPage page = new InlineCitationCommonPage(runner.driver());
		page.slowScrollToBottom();
		MntlArticleSourcesComponent articleSourcesComponent = page.mntlArticleSourcesComponent();
		expandCitationOn(runner, page, articleSourcesComponent);
		List<WebElementEx> contentList = articleSourcesComponent.mntlCitationSourcesComponent().contentList();
		collector.checkThat("Number of citation on page is zero", contentList.size(), greaterThan(0));
		for (int i = 1; i <= contentList.size(); i++) {
			List<WebElementEx> citations = page.duplicateInlineCitations(i);
			WebElementEx citation = citations.get(0);
			citation.scrollIntoViewCentered();
			page.waitFor().aMoment(2, TimeUnit.SECONDS);
			citation.click();
			citation.scrollIntoViewCentered();
			Actions action = new Actions(runner.driver());
			action.moveToElement(page.toolTip()).perform();
			page.waitFor().aMoment();
			String tooltipText = page.toolTip().getText();
			collector.checkThat("text is not correct", tooltipText, is(notNullValue()));
			List<WebElementEx> citationSourcesComponent = articleSourcesComponent.mntlCitationSourcesComponent()
					.contentList();
			String inlineCitationText = citationSourcesComponent.get(i - 1).getWrappedElement()
					.findElement(By.cssSelector("p")).getText();
			citation.click();
			articleSourcesComponent.mntlCitationSourcesComponent().list().scrollIntoViewCentered();
			collector.checkThat("Citations are not displayed", articleSourcesComponent.isExpanded(), is(true));
			collector.checkThat("text is not correct", inlineCitationText, containsString(tooltipText));
			page.waitFor().aMoment();
		}
		if (hasAdditionalReading) {
			for (WebElementEx element : articleSourcesComponent.mntlArticleSourcesAdditionalReading().contentList()) {
				collector.checkThat("Additional reading is not displayed", element.getText(), is(notNullValue()));
			}
		}
		articleSourcesComponent.scrollIntoViewCentered();
		sourceGuidelinesTest(runner, page, articleSourcesComponent);
	};

	private void expandCitationOn(Runner runner, InlineCitationCommonPage page,
			MntlArticleSourcesComponent articleSourcesComponent) {
		if (mobile(runner.driver())) {
			if (page.hasContinueReading()) {
				WebElementEx continueReading = page.continueReading();
				continueReading.scrollIntoViewCentered();
				page.waitFor().aMoment();
				continueReading.jsClick();
				runner.page().waitFor().exactMoment(2, TimeUnit.SECONDS);
			}
			page.scroll().bottom();
			WebElementEx showIcon = articleSourcesComponent.mntlArticleSourceHeading();
			showIcon.scrollIntoViewCentered();
			showIcon.jsClick();
			page.waitFor().aMoment();
			page.scroll().bottom();
			page.waitFor().aMoment();
		} else {
			page.showIcon().scrollIntoViewCentered();
			page.showIcon().click();
		}
		page.waitFor().aMoment();
		collector.checkThat("Inline citation is not displayed", articleSourcesComponent.isExpanded(), is(true));
	}

	private void sourceGuidelinesTest(Runner runner, InlineCitationCommonPage page,
			MntlArticleSourcesComponent articleSourcesComponent) {
		articleSourcesComponent.scrollIntoViewCentered();
		collector.checkThat("source guidelines are not present", page.sourceGuidelines().isDisplayed(), is(true));
		collector.checkThat("source guideline href is incorrect", page.sourceGuidelinesLink().href(),
				endsWith(editorialProcess));

	}
}