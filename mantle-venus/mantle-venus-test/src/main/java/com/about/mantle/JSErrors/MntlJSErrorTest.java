package com.about.mantle.JSErrors;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MntlJSErrorTest<T extends MntlCommonTestMethods.Runner<T>> extends MntlVenusTest implements MntlCommonTestMethods<T> {

	static final Logger logger = LoggerFactory.getLogger(MntlCommonTestMethods.class);
	protected final List<NameValuePair> headers = Arrays.asList(new BasicNameValuePair("X-Abt-Debug-JS-Errors", "true"));
	protected final List<NameValuePair> queryParams = Arrays.asList(new BasicNameValuePair("globeResourceMinify", "false"));

	protected Consumer<T> jsErrorTest = (runner) -> {
		ArrayList<Object> errors = runner.driver().executeScript("return Mntl.jsErrors");
		int count = 0;
		while (errors.size() <= 0) {
			if (count++ >= 15)
				break;
			for (int i = 0; i < 5; i++) {
				try {
					errors = runner.driver().executeScript("return Mntl.jsErrors");
				} catch (TimeoutException e) {
					e.printStackTrace();
					sleep(5);
					continue;
				}
				break;
			}
			sleep(1);
		}
		assertThat(errors.size(), is(0));
	};
	
	public void scriptTest(WebDriverExtended driver, WebElementEx webElement) {
		driver.executeScript(				
				"Mntl.allErrs = [];" 
						+ "(function() {"
								+ "window.onerror = function(error, url, lineNo, colNo) {"
								+ "var errorData = {};" 
								+ "errorData.errName = error;" 
								+ "errorData.urlName = url;"
								+ "errorData.lineNum = lineNo;" 
								+ "errorData.colNum = colNo;" 
								+ "Mntl.allErrs.push(errorData);"
								+ "};"
								+ "})();"							
				);			
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			webElement.mouseHover();
			webElement.clickWithKey(Keys.COMMAND);
		} else {	
			webElement.mouseHover();			
			webElement.clickWithKey(Keys.CONTROL);
		}
		driver.waitFor(1, TimeUnit.SECONDS);
		ArrayList<Object> errorsCaptured = driver.executeScript("return Mntl.allErrs;");
		collector.checkThat("Size is 0", errorsCaptured.size(), is(0));	
		if(errorsCaptured.size()>0){			
			logger.info("List of JS errors on page: " + errorsCaptured );			
		}
	}
	

}
