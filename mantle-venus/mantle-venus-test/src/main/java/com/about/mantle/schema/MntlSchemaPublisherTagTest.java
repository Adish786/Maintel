package com.about.mantle.schema;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import org.openqa.selenium.By;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

public class MntlSchemaPublisherTagTest extends MntlVenusTest {
	

	public void testSchemaPublisherTag(WebDriverExtended driver ,String url,String publisherName) {
		List<WebElementEx> ldJsons = null;
		driver.get(url);
		MntlPage page = new MntlPage(driver);
		page.waitFor().exactMoment(1, TimeUnit.SECONDS);
		try {
			//get all schema tags
			ldJsons = page.head().findElements(By.tagName("script")).stream()
					.filter(js -> js.getAttribute("type").equals("application/ld+json"))
					.filter(script -> script.getAttribute("innerHTML").contains("http://schema.org"))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for(WebElementEx ldJson: ldJsons) {
			String ldJsonContent = ldJson.getAttribute("innerHTML");
			//continue if schema contains publisher
			if(ldJsonContent.contains("\"publisher\": {")) {
				String[] lines = ldJson.getAttribute("innerHTML").split("\n");
				boolean inSidePublisherObject = false;
				for(String line: lines) {
					if(inSidePublisherObject) {
						if (line.contains("\"name\":")) {
							String[] keyValue = line.split(":");
							String publisher = keyValue[1].replaceAll("\\s*\"", "").replaceAll(",", "");
							collector.checkThat("publisher is not " + publisherName + "\nurl: " + url ,
									publisher, is(publisherName));
							return;
						}
					}
					//should check for name value only for publisher object
					if(line.contains("\"publisher\": {")) {
						inSidePublisherObject = true;
					}
				}	
			}
		}
		// If publisher was not found on any schema, fail the test
		collector.addError(new Throwable("publisher field was not found for url: " + url ));
	}

}
