package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.MntlArticleSourcesComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;
import java.util.function.Supplier;

public class InlineCitationCommonPage extends MntlPage {

	private final Lazy<MntlArticleSourcesComponent> mntlArticleSourcesComponent;
	private final Supplier<WebElementEx> toolTip;
	private final Lazy<WebElementEx> continueReading;

	public InlineCitationCommonPage(WebDriverExtended driver) {
		super(driver);
		this.mntlArticleSourcesComponent = lazy(
				() -> findElement(By.className("mntl-article-sources"), MntlArticleSourcesComponent::new));
		this.toolTip = (() -> findElement(By.cssSelector(".mntl-dynamic-tooltip--content")));
		this.continueReading = lazy(() -> findElement(By.className("btn-chop")));
	}

	public MntlArticleSourcesComponent mntlArticleSourcesComponent() {
		return mntlArticleSourcesComponent.get();
	}

	public WebElementEx inlineCitation(int n) {
		return findElement(By.cssSelector("span[data-id=\"#citation-" + n + "\"]"));
	}

	public List<WebElementEx> duplicateInlineCitations(int n) {
		return findElements(By.cssSelector("span[data-id=\"#citation-" + n + "\"]"));
	}

	public WebElementEx toolTip() {
		return toolTip.get();
	}

	public WebElementEx showIcon() {
		return findElement(By.cssSelector("svg.icon.show-icon"));
	}

	public WebElementEx articleSourcesListLink(int n) {
		return findElement(By.cssSelector("#citation-" + n + "> p > a"));
	}

	public WebElementEx sourceGuidelines() {
		return findElement(By.cssSelector(".source-guidelines"));
	}

	public WebElementEx sourceGuidelinesLink() {
		return findElement(By.cssSelector(".source-guidelines a"));
	}

	public boolean hasContinueReading() {
		return this.getElement().elementExists(".btn-chop");
	}

	public WebElementEx continueReading() {
		return continueReading.get();
	}
}