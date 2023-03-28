package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.components.buttons.ClickActions;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;
public class PrintButtonPage {

	public static class PrintPage extends Page {

		public PrintPage(WebDriverExtended driver) {
			super(driver);
		}
		
		public MntlUrl printUrl() {
			return new MntlUrl(getDriver().getCurrentUrl(), true);						 
		}
		
		public String printAddress() {
			return getDriver().getCurrentUrl();
		}
	}
	
	public static class MntlPrintButton extends ClickActions<PrintPage> {
		
		private final Lazy<WebElementEx> printButton;
		private final Lazy<WebElementEx> printButtonIcon;
	
		public MntlPrintButton(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.printButton = lazy(() -> findElement(By.className("mntl-print-button__btn")));
			this.printButtonIcon = lazy(() -> findElement(By.className("mntl-print-button__icon")));
		}
		
		public WebElementEx printButton() {
			return printButton.get();
		}
		
		public WebElementEx printButtonIcon() {
			return printButtonIcon.get();
		}
		
		@Override
		public PrintPage createPage() {
			return new PrintPage(getDriver());
		}
		
		public WebElementEx printButtonElement() {
			return getElement();
		}
	}
}
