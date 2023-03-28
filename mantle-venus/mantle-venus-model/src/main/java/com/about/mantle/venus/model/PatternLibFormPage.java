package com.about.mantle.venus.model;

import java.util.List;

import org.openqa.selenium.By;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class PatternLibFormPage extends MntlPage {
	
	private final Lazy<List<WebElementEx>> tabs;
	private final Lazy<PatternLibFormComponent> dependenciesTabForm;
	private final Lazy<WebElementEx> update;
	public PatternLibFormPage(WebDriverExtended driver) {
		super(driver.switchToDefaultContent());
		this.tabs = lazy(() -> findElements(By.cssSelector(".mntl-pl-component-tabs > .tabs-title")));
		this.dependenciesTabForm = lazy(() -> new PatternLibFormComponent(driver,findElement(By.cssSelector(".tabs-content form"))));
		this.update = lazy(() -> findElement(By.cssSelector("input.pl-button")));
	}
	public List<WebElementEx> tabs(){
		return tabs.get();
	}
	
	public WebElementEx dependenciesTab(){
		tabs().get(0).scrollIntoView();
		return tabs.get().stream().filter(tab -> tab.text().equals("MODEL")).findFirst().orElse(null);
	}
	
	public PatternLibFormComponent plForm(){
		return dependenciesTabForm.get();
	}
	
	public void update(){
		update.get().submit();
	}

}