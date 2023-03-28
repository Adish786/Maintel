package com.about.mantle.schema;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.venus.core.driver.WebDriverExtended;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public abstract class AbstractMntlSchemaTest extends MntlSchemaCompareTest {
	String SCHEMA_SELECTOR = "script[type=\"application/ld+json\"]";

	/**
	 * Parses Schema from the page
	 */
	public JsonPath getSchema(WebDriverExtended driver, String url) {
		driver.get(url);
		MntlPage page = new MntlPage(driver);
		page.waitFor().exactMoment(1, TimeUnit.SECONDS);
		SchemaComponent schemaComp = page.head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
		if (schemaComp.getElement().getAttributes().containsKey("data-defer") || schemaComp.getElement().getAttributes().containsKey("data-defer-params"))
			throw new RuntimeException("The schema component has either `data-defer` or `data-defer-params`");
		try {
			new JSONArray(schemaComp.schemaContent());
		} catch (JSONException e) {
			fail("The schema component has syntax error or a duplicated key. JSONException:"+e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return new JsonPath(schemaComp.schemaContent());
	}

	public JsonPath getSpecificSchema(WebDriverExtended driver, String url, String schemaType) {
		driver.get(url);
		MntlPage page = new MntlPage(driver);
		page.waitFor().exactMoment(1, TimeUnit.SECONDS);
		SchemaComponent schemaComp = page.head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
		if (schemaComp.getElement().getAttributes().containsKey("data-defer") || schemaComp.getElement().getAttributes().containsKey("data-defer-params")) {
			throw new RuntimeException("The schema component has either `data-defer` or `data-defer-params`");
		}
		try {
			JSONArray schemaArray = new JSONArray(schemaComp.schemaContent());
			for (Object schema : schemaArray) {
				if(schema.toString().contains(schemaType)) return new JsonPath(schema.toString());
			}
			fail("Requested schemaType is not found on the page");
		} catch (JSONException e) {
			fail("The schema component has syntax error or a duplicated key. JSONException:"+e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return null;
	}

	public String getSchemaType() {
		String schemaType = getAdditionalFieldsToVerify().get("@type").toString();
		if(schemaType.isEmpty())throw new RuntimeException("Schema type not found on the template.");
		return schemaType;
	}

	public void testSchemaRules(JsonPath schema, ErrorCollector errorCollector) {
		testBasicStructure(getBasicStructure(), schema, errorCollector);
		if (getAdditionalFieldsToVerify() != null) {
			testSpecificFieldsInSchema(getAdditionalFieldsToVerify(), schema, errorCollector);
		}
	}

	/**
	 * Verifies fields in Json schema with the value provided in map. Field is represented by key and the value is test
	 * data. All classes extending this (AbstractMntlSchemaTest) class should decide what they want to check beyond
	 * basic structure by implementing {@link this#getAdditionalFieldsToVerify}
	 */
	protected void testSpecificFieldsInSchema(Map<String, Object> fields, JsonPath schema, ErrorCollector errorCollector) {
		for (String key : fields.keySet()) {
			Object source = schema.get(key);
			Object target = fields.get(key);
			errorCollector.checkThat("Target value must be non-null for field: " + key, target, notNullValue());
			errorCollector.checkThat("Source value found null for field: " + key, source, notNullValue());
			if(source instanceof List<?>){
				errorCollector.checkThat("Source and target values do not match for field: " + key, source.toString(), Matchers.containsString(target.toString()));
			}else {
				errorCollector.checkThat("Source and target values do not match for field: " + key, source, is(target));
			}
		}
	}

	/**
	 * Verifies basic structure of schema for a document. All classes extending this (AbstractMntlSchemaTest) class
	 * should decide how their basic structure should look like by implementing {@link this#getBasicStructure} It not
	 * only checks if basic structure is present but for certain types of value it will also do some additional checks.
	 * e.g. For String values it makes sure it's not empty, same for list and map.
	 */
	protected void testBasicStructure(List<String> basicStructureCandidates, JsonPath schema, ErrorCollector collector) {
		for (String candidate : basicStructureCandidates) {
			Object obj = schema.get(candidate);

			if (isNotNull.test(obj)) {
				String validation = "Expected non-empty value for key: ";
				if (isList.test(obj)) {
					collector.checkThat(validation + candidate,
										((ArrayList<?>) obj).size(), greaterThan(0));
				} else if (isString.test(obj)) {
					collector.checkThat(validation + candidate,
										(String) obj, not(isEmptyOrNullString()));
				} else if (isMap.test(obj)) {
					collector.checkThat(validation + candidate,
										((HashMap<?, ?>) obj).size(), greaterThan(0));
				}

			} else {
				collector.checkThat("Expected non-null value for key: " + candidate, obj, notNullValue());
			}

		}
	}

	protected Predicate<Object> isNotNull = (obj) -> obj != null;
	protected Predicate<Object> isBoolean = (obj) -> obj.getClass().equals(Boolean.class);
	protected Predicate<Object> isString = (obj) -> obj.getClass().equals(String.class);
	protected Predicate<Object> isList = (obj) -> obj.getClass().equals(ArrayList.class);
	protected Predicate<Object> isFloat = (obj) -> obj.getClass().equals(Float.class);
	protected Predicate<Object> isInteger = (obj) -> obj.getClass().equals(Integer.class);
	protected Predicate<Object> isMap = (obj) -> obj.getClass().equals(HashMap.class);

	abstract protected List<String> getBasicStructure();

	abstract protected Map<String, Object> getAdditionalFieldsToVerify();



}
