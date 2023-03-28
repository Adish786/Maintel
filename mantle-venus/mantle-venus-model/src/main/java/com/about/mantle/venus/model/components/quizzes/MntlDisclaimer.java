package com.about.mantle.venus.model.components.quizzes;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

//Bug report for this component GLBE-6284 about incorrect component

@MntlComponentId("mntl-disclaimer")

public class MntlDisclaimer extends MntlComponent {
	
	private Lazy<WebElementEx> quizDisclaimer;
	private Lazy<WebElementEx> disclaimer;

	public MntlDisclaimer(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.disclaimer = lazy(() -> findElement(By.cssSelector(".component-id")));
		this.quizDisclaimer = lazy(() -> findElement(By.cssSelector(".quiz-disclaimer")));
		
	}
	
	@Validate()
	public WebElementEx quizDisclaimer() {
		return quizDisclaimer.get();
	}
	
	public WebElementEx disclaimer() {
		return disclaimer.get(); 
	}
	
}