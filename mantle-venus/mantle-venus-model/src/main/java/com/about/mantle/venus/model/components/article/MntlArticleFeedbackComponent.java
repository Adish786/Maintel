package com.about.mantle.venus.model.components.article;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.forms.MntlNewsletterSignupComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

public class MntlArticleFeedbackComponent extends MntlComponent {
	
	private final Lazy<WebElementEx> feedbackHeading;
	private final Lazy<RatingSectionComponent> ratingSection;
	private final Lazy<SuccessSectionComponent> successSection;
	private final Lazy<FeedbackSectionComponent> feedbackSection;
	
	public MntlArticleFeedbackComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.feedbackHeading = lazy(() -> findElement(By.className("article-feedback__heading")));
		this.ratingSection = lazy(() -> findElement(By.className("article-feedback__rating-section"), RatingSectionComponent::new));
		this.successSection = lazy(() -> findElement(By.className("article-feedback__success-section"), SuccessSectionComponent::new));
		this.feedbackSection = lazy(() -> findElement(By.className("article-feedback__feedback-section"), FeedbackSectionComponent::new));
	}
	
	public WebElementEx feedbackHeading() {
		return feedbackHeading.get();
	}
	
	public RatingSectionComponent ratingSection() {
		return ratingSection.get();
	}
	
	public SuccessSectionComponent successSection() {
		return successSection.get();
	}
	
	public FeedbackSectionComponent feedbackSection() {
		return feedbackSection.get();
	}
	
	public class FeedbackSectionComponent extends MntlComponent{

		private final Lazy<WebElementEx> heading;
		private final Lazy<WebElementEx> notEnoughDetails;
		private final Lazy<WebElementEx> hardToUnderstand;
		private final Lazy<WebElementEx> other;
		private final Lazy<WebElementEx> articleFeedbackTextArea;
		private final Lazy<WebElementEx> submitButton;
		private final Lazy<WebElementEx> inaccurate;

		public FeedbackSectionComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.heading = lazy(() -> findElement(By.className("article-feedback__heading")));
			this.notEnoughDetails = lazy(() -> findElement(By.className("article-feedback__not-enough-details-button")));
			this.hardToUnderstand = lazy(() -> findElement(By.className("article-feedback__hard-to-understand-button")));
			this.other = lazy(() -> findElement(By.className("article-feedback__open-form-button")));
			this.articleFeedbackTextArea = lazy(() -> findElement(By.className("article-feedback__feedback-text")));
			this.submitButton = lazy(() -> findElement(By.className("article-feedback__submit-button")));
			this.inaccurate = lazy(() -> findElement(By.id("not-accurate-button")));
		}
		
		public WebElementEx heading() {
			return heading.get();
		}
		
		public WebElementEx notEnoughDetails() {
			return notEnoughDetails.get();
		}
		
		public WebElementEx inaccurate() {
			return inaccurate.get();
		}
		
		public WebElementEx hardToUnderstand() {
			return hardToUnderstand.get();
		}

		public WebElementEx other() {
			return other.get();
		}
		
		public WebElementEx articleFeedbackTextArea() {
			return articleFeedbackTextArea.get();
		}

		public WebElementEx submitButton() {
			return submitButton.get();
		}

	}

	public static class SuccessSectionComponent extends MntlComponent {
		
		private final Lazy<WebElementEx> heading;
		private final Lazy<MntlNewsletterSignupComponent> newsletterSignup;

		public SuccessSectionComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.heading = lazy(() -> findElement(By.className("article-feedback__heading")));
			this.newsletterSignup = lazy(() -> findComponent(MntlNewsletterSignupComponent.class));
		}
		
		public WebElementEx heading() {
			return heading.get();
		}
		
		public MntlNewsletterSignupComponent newsletterSignup() {
			return newsletterSignup.get();
		}

	} // End of SuccessSectionComponent
	
	public static class RatingSectionComponent extends MntlComponent {
		
		private final Lazy<List<WebElementEx>> articleFeedbackRatingButtons;
		public RatingSectionComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.articleFeedbackRatingButtons = lazy(() -> findElements(By.cssSelector(".article-feedback__rating-button")));
		}
		
		public WebElementEx thumbsUpButton() {
			return articleFeedbackRatingButtons.get().get(0);
		}
		
		public WebElementEx thumbsDownButton() {
			return articleFeedbackRatingButtons.get().get(1);
		}
		
		public WebElementEx thumbsUpButtonIcon() {
			return articleFeedbackRatingButtons.get().get(0).findElementEx(By.tagName("svg"));
		}
		public WebElementEx thumbsDownButtonIcon() {
			return articleFeedbackRatingButtons.get().get(1).findElementEx(By.tagName("svg"));
		}
		
	} // ENd of Rating section component
	
	
	public static class ArticleFeedBackResultsComponent extends Component{
		
		private final Lazy<WebElementEx> thumbsUpIcon;
		private final Lazy<WebElementEx> thumbsUpCount;
		private final Lazy<WebElementEx> toolTip;


		public ArticleFeedBackResultsComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.thumbsUpIcon = lazy(() -> findElement(By.className("feedback-display__thumb-icon")));
			this.thumbsUpCount = lazy(() -> findElement(By.className("feedback-display__count")));
			this.toolTip = lazy(() -> findElement(By.className("feedback-display__tooltip")));

		}
		
		public WebElementEx thumbsUpIcon() {
			return thumbsUpIcon.get();
		}
		
		public WebElementEx thumbsUpCount() {
			return thumbsUpCount.get();
		}
		
		public WebElementEx toolTip() {
			return toolTip.get();
		}
		
	} // End of ArticleFeedBackResultsComponent
}
