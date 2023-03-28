package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import org.openqa.selenium.By;

//removed lazy to get dynamically changed class name
@MntlComponentCssSelector(".mntl-article-sources")
public class MntlArticleSourcesComponent extends MntlComponent {


	
	public MntlArticleSourcesComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}
	
	public MntlCitationSourcesComponent mntlCitationSourcesComponent() {
		return findComponent(MntlCitationSourcesComponent.class);
	}
	
	public MntlAdditionalReadingComponent mntlArticleSourcesAdditionalReading() {
		return findComponent(MntlAdditionalReadingComponent.class);
	}
	
	public WebElementEx mntlArticleSourceHeading() {
		return findElement(By.cssSelector(".mntl-article-sources__heading"));
	}
	
	public WebElementEx mntlArticleSourceSubHeading() {
		return findElement(By.cssSelector(".mntl-article-sources__subheading.mntl-text-block"));
	}
	
	public boolean isSubHeadingPresent() {
		return this.className().contains("mntl-article-sources__subheading");
	}
	
	public boolean isExpanded() {
		return this.getWebElement().className().contains("is-expanded");
	}
	
	public WebElementEx getWebElement(){
		return getElement();
	}	
	
}