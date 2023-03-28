package com.about.mantle.components.ratings;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

//Bug report for this component GLBE-6285 about Incorrect component id

@MntlComponentId("mntl-made-it")
public class MntlMadeIt extends MntlComponent {
	
	private Lazy<WebElementEx> madeIt;


	public MntlMadeIt(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.madeIt = lazy(() -> findElement(By.cssSelector(".component-id")));

	}
	
	@Validate()
	public WebElementEx madeIt() {
		return madeIt.get(); 
	}

}