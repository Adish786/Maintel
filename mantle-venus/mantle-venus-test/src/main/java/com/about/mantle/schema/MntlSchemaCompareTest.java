package com.about.mantle.schema;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * We compare PROD and QA schemas in this test
 */
public class MntlSchemaCompareTest extends MntlVenusTest {

	
	protected static ImmutableMap<String, String> publisherValues;
	protected static int noOfpublisherSameAsValues;

	public List<String> getSchemas(WebDriverExtended driver , String url) {
		driver.get(url);
		MntlPage page = new MntlPage(driver);
		page.waitFor().exactMoment(1, TimeUnit.SECONDS);
		List<String> schemas = new ArrayList<String>();
		try {
			//get all schema tags
			List<WebElementEx> ldJsons = page.head().findElements(By.tagName("script")).stream()
					.filter(js -> js.getAttribute("type").equals("application/ld+json"))
					.filter(script -> script.getAttribute("innerHTML").contains("http://schema.org"))
					.collect(Collectors.toList());
			for(WebElementEx ldJson: ldJsons) {
				schemas.add(ldJson.getAttribute("innerHTML"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return schemas;
	}

	public void testSchemaQaProdCompare(WebDriverExtended driver ,String url) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		TypeReference<HashMap<String, Object>> type =
		    new TypeReference<HashMap<String, Object>>() {};
		String prodUrl = new MntlUrl(url).getProdUrl();
		List<String> prodSchemaElements = getSchemas(driver ,prodUrl);
		List<String> qaSchemaElements = getSchemas(driver ,url);
		collector.checkThat("QA page doesn't contain any schemas", qaSchemaElements.size(), greaterThan(0));
		collector.checkThat("PROD pages don't contain any schemas", prodSchemaElements.size(), greaterThan(0));
		assertThat("number of schema tags in QA is different from PROD", qaSchemaElements.size(), is(prodSchemaElements.size()));

		for(int i = 0; i < prodSchemaElements.size(); i++) {
			try {
				String prodSchemaText = prodSchemaElements.get(i).replaceAll("https:\\/\\/.+\\.com\\/", "").replaceAll("\"dateModified\": \".+\"", "");
				String qaSchemaText = qaSchemaElements.get(i).replaceAll("https:\\/\\/.+\\.com\\/", "").replaceAll("\"dateModified\": \".+\"", "");
				collector.checkThat("QA schema is different from PROD: " + StringUtils.difference(prodSchemaText, qaSchemaText), prodSchemaText.equals(qaSchemaText), is(true));
				Map<String, Object> prodSchema = mapper.readValue(prodSchemaText, type);
				Map<String, Object> qaSchema = mapper.readValue(qaSchemaText, type);
				MapDifference<String, Object> difference = Maps.difference(prodSchema, qaSchema);
				collector.checkThat("QA schema is different from PROD: " + difference.entriesDiffering(), difference.areEqual(), is(true));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	protected void publisherSchemaTest(WebDriverExtended driver , String url) {
		PublishingSchemaTest publishingSchema = new PublishingSchemaTest();
		JsonPath pubSchema =  new JsonPath(getSchemas(driver ,url).get(0));
		publishingSchema.testSchemaRules(pubSchema ,collector);
		collector.checkThat("Publisher Brand is not " + publisherValues.get("brand"), pubSchema.get("publisher.brand"),
				is(publisherValues.get("brand")));
		collector.checkThat("Publishing principles is not about-us#EditorialGuidelines ",
				pubSchema.get("publisher.publishingPrinciples"), is(publisherValues.get("publishingPrinciples")));
		for (int num = 0; num < noOfpublisherSameAsValues; num++) {
			collector.checkThat("Publisher same as field is not " + publisherValues.get("publisherSameAs[" + num + "]"),
					pubSchema.get("publisher.sameAs[" + num + "]"),
					is(publisherValues.get("publisherSameAs[" + num + "]")));

		}
		collector.checkThat("Address type is not correct postal address ", pubSchema.get("publisher.address.@type"),
				is("PostalAddress"));
		collector.checkThat("Address is not correct street address ", pubSchema.get("publisher.address.streetAddress"),
				is("225 Liberty Street, 4th Floor"));
		collector.checkThat("Address locality is not new york ", pubSchema.get("publisher.address.addressLocality"),
				is("New York"));
		collector.checkThat("Address region is not NY ", pubSchema.get("publisher.address.addressRegion"), is("NY"));
		collector.checkThat("Address zip code is not 10281 ", pubSchema.get("publisher.address.postalCode"), is("10281"));
		collector.checkThat("Address county is not USA ", pubSchema.get("publisher.address.addressCountry"), is("USA"));
		collector.checkThat("Address url is not correct ", pubSchema.get("publisher.parentOrganization.url"),
				is("https://www.dotdashmeredith.com"));
		collector.checkThat("Address brand is not correct ", pubSchema.get("publisher.parentOrganization.brand"),
				is("Dotdash Meredith"));
		collector.checkThat("Address name is not correct ", pubSchema.get("publisher.parentOrganization.name"), is("Dotdash Meredith"));
		collector.checkThat("Parent Organization Address type is not correct postal address ",
				pubSchema.get("publisher.parentOrganization.address.@type"), is("PostalAddress"));
		collector.checkThat("Parent Organization Address is not correct street address ",
				pubSchema.get("publisher.parentOrganization.address.streetAddress"), is("225 Liberty Street, 4th Floor"));
		collector.checkThat("Parent Organization Address locality is not new york ",
				pubSchema.get("publisher.parentOrganization.address.addressLocality"), is("New York"));
		collector.checkThat("Parent Organization Address region is not NY ",
				pubSchema.get("publisher.parentOrganization.address.addressRegion"), is("NY"));
		collector.checkThat("Parent Organization Address zip code is not 10281 ",
				pubSchema.get("publisher.parentOrganization.address.postalCode"), is("10281"));
		collector.checkThat("Parent Organization Address county is not USA ",
				pubSchema.get("publisher.parentOrganization.address.addressCountry"), is("USA"));
		collector.checkThat("Logo type is not correct ", pubSchema.get("publisher.parentOrganization.logo.@type"),
				is("ImageObject"));
		collector.checkThat("Logo url is not correct ", pubSchema.get("publisher.parentOrganization.logo.url"),
				containsString("dotdash-logo-e9cde67f713a45c68ce5def51d3ca409.jpg"));
		collector.checkThat("parentOrganization link 1 is not correct ",
				pubSchema.get("publisher.parentOrganization.sameAs[0]"), is("https://en.wikipedia.org/wiki/Dotdash_Meredith"));
		collector.checkThat("parentOrganization link 2 is not correct ",
				pubSchema.get("publisher.parentOrganization.sameAs[1]"), is("https://www.instagram.com/dotdashmeredith/"));
		collector.checkThat("parentOrganization link 3 is not correct ",
				pubSchema.get("publisher.parentOrganization.sameAs[2]"),
				is("https://www.linkedin.com/company/dotdashmeredith/"));
		collector.checkThat("parentOrganization link 4 is not correct ",
				pubSchema.get("publisher.parentOrganization.sameAs[3]"), is("https://www.facebook.com/dotdashmeredith/"));
	}
}
