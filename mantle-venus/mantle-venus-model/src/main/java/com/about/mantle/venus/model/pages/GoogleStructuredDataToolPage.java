package com.about.mantle.venus.model.pages;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class GoogleStructuredDataToolPage extends Page {
	private final Lazy<WebElementEx> codeTab;
	private final Lazy<WebElementEx> textArea;
	private final Lazy<WebElementEx> runTestButton;
	private final Supplier<List<WebElementEx>> resultList;
	private final Lazy<WebElementEx> newTest;
	private final Supplier<List<WebElementEx>> resultRowCells;

	public GoogleStructuredDataToolPage(WebDriverExtended driver) {
		super(driver);
		this.codeTab = lazy(() -> findElement(By.id("new-test-code")));
		this.textArea = lazy(() -> findElement(By
				.cssSelector(".mdl-tabs__panel.is-active> div> div:nth-child(2) > div:nth-child(1) > textarea:nth-child(1)")));
		this.runTestButton = lazy(() -> findElement(By.id("new-test-submit-button")));
		this.resultList = () -> findElements(By.cssSelector(".mdl-list li"));
		this.newTest = lazy(() -> findElement(By.id("new-test")));
		this.resultRowCells = () -> findElements(By.cssSelector(".mdl-list__item-primary-content"));
	}

	public void submitHtmlSource(String htmlSource) {
		codeTab.get().click();
		waitFor().aMoment(1, TimeUnit.SECONDS);
		textArea.get().jsType(htmlSource);
		waitFor().aMoment(3, TimeUnit.SECONDS);
		runTestButton.get().click();
		waitFor().aMoment();
	}

	public List<WebElementEx> resultList() {
		return resultList.get();
	}

	public boolean resultsAllGood() {
		return resultList.get().stream().noneMatch(result -> !result.getText().contains("0 ERRORS"));
	}
	
	public WebElementEx newTest() {
		return newTest.get();
	}
	
	public List<WebElementEx> resultRowCells() {
		return resultRowCells.get();
	}
}
