package com.about.mantle.venus.model.components.taxonomySC;

import java.util.List;

import com.about.mantle.venus.model.components.quizzes.MntlQuizQuestionsComponent;
import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-faq")
public class MntlTaxonomySCFAQBlockComponent extends MntlComponent{

	private final Lazy<WebElementEx> title;
	private final Lazy<List<AccordionItem>> accordionItems;

	public MntlTaxonomySCFAQBlockComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
        this.title = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-faq__title")));
        this.accordionItems = lazy(() -> findElements(By.cssSelector(".js-accordion-item"), AccordionItem::new));
	}
	
	public WebElementEx title() {
		return title.get();
	}
	
	public List<AccordionItem> accordionItems(){
		return accordionItems.get();
	}
	
	public static class AccordionItem extends MntlComponent {
		
		private final Lazy<WebElementEx> title;
		private final Lazy<WebElementEx> body;
		private final Lazy<WebElementEx> accordionText;
		private final Lazy<List<Answer>> answers;
		private final Lazy<WebElementEx> icon;
		private final Lazy<WebElementEx> featuredLink;
		
		public AccordionItem(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
	        this.title = lazy(() -> findElement(By.cssSelector(".accordion__title")));
	        this.body = lazy(() -> findElement(By.cssSelector(".accordion__body")));
	        this.accordionText = lazy(() -> findElement(By.cssSelector(".accordion__header span")));
	        this.answers = lazy(() -> findElements(By.cssSelector(".faq-accordion__item-answer p"), Answer::new));
	        this.icon = lazy(() -> findElement(By.cssSelector(".accordion__icon")));
			this.featuredLink = lazy(() -> findElement(By.cssSelector(".faq-accordion__feature-link")));

		}
		
		public WebElementEx title() {
			return title.get();
		}
		
		public WebElementEx body() {
			return body.get();
		}
		
		public WebElementEx accordionText() {
			return accordionText.get();
		}
		
		public List<Answer> answers() { return answers.get(); }

		public WebElementEx featuredLink() {
			return featuredLink.get();
		}

		public boolean hasFeaturedLink() {
			return getElement().elementExists(".faq-accordion__feature-link");
		}

		public String featuredLinkHref() {
			return featuredLink.get().findElement(By.tagName("a")).toString();
		}
		
		public WebElementEx icon() {
			return icon.get();
		}

		public static class Answer extends MntlComponent {

			private final Lazy<WebElementEx> inlineLink;

			public Answer(WebDriverExtended driver, WebElementEx element) {
				super(driver, element);
				this.inlineLink = lazy(() -> findElement(By.tagName("a")));
			}

			public boolean hasLink() {
				return getElement().elementExists("a");
			}

			public WebElementEx inlineLink() {
				return inlineLink.get();
			}

		}


	}
}
