package com.about.mantle.venus.model.components.article;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.article.MntlDynamicTooltipComponent.MntlDynamicTooltipContent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@Deprecated // will be removed in https://dotdash.atlassian.net/browse/AXIS-2022
@MntlComponentCssSelector(".mntl-review-byline")
public class MntlReviewBylineComponent extends MntlComponent {

	private final Lazy<WebElementEx> reviewBylineText;
	private final Lazy<WebElementEx> reviewBylineDate;
	private final Lazy<ReviewBylineLinkWrapper> reviewBylineLinkWrapper;

	public MntlReviewBylineComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.reviewBylineText = lazy(() -> findElement(By.className("mntl-review-byline__text")));
		this.reviewBylineDate = lazy(() -> findElement(By.className("mntl-review-byline__date")));
		this.reviewBylineLinkWrapper = lazy(
				() -> findElement(By.className("mntl-review-byline__link-wrapper"), ReviewBylineLinkWrapper::new));
	}

	public WebElementEx reviewBylineText() {
		return reviewBylineText.get();
	}

	public WebElementEx reviewBylineDate() {
		return reviewBylineDate.get();
	}

	public ReviewBylineLinkWrapper reviewBylineLinkWrapper() {
		return reviewBylineLinkWrapper.get();
	}

	public class ReviewBylineLinkWrapper extends Component {

		private final Lazy<MntlDynamicTooltipComponent> dynamicToolTip;
		private final Lazy<WebElementEx> reviewBylineLink;

		public ReviewBylineLinkWrapper(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.dynamicToolTip = lazy(() -> findComponent(MntlDynamicTooltipComponent.class));
			this.reviewBylineLink = lazy(() -> findElement(By.className("mntl-review-byline__link")));

		}

		public MntlDynamicTooltipComponent dynamicToolTip() {
			return dynamicToolTip.get();
		}

		public WebElementEx reviewBylineLink() {
			return reviewBylineLink.get();
		}

	}

}
