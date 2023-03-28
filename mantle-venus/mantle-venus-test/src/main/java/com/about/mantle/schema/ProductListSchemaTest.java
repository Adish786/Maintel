package com.about.mantle.schema;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.pages.MntlListScPage;
import com.about.venus.core.driver.WebDriverExtended;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.greaterThan;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProductListSchemaTest extends MntlListSchemaTest implements MntlCommonTestMethods<MntlRunner> {

	static  Map<String, Object> addAdditionalFieldsToVerify = new HashMap<>();
	static List<String> addFieldsToVerify = new ArrayList<>();

	protected Consumer<MntlRunner> productSchemaListTest = runner -> {
		
		MntlListScPage page = new MntlListScPage(runner.driver(), MntlListScItemComponent.class);
		List<MntlListScItemComponent> mntlListScBlocks = page.listScBlocks();
		
		collector.checkThat("ListSc blocks are missing ", mntlListScBlocks.size(), greaterThan(0));
		
		for (int listItem = 0; listItem < mntlListScBlocks.size(); listItem++) {

			addAdditionalFieldsToVerify.put("about[0][0].itemListElement[" + listItem + "].@type", "Product");
			addAdditionalFieldsToVerify.put("about[0][0].itemListElement[" + listItem + "].review.author[0].@type", "Person");
			addAdditionalFieldsToVerify.put("about[0][0].itemListElement[" + listItem + "].brand.@type", "Brand");
			addFieldsToVerify.add("about[0][0].itemListElement[" + listItem + "].url");
			addFieldsToVerify.add("about[0][0].itemListElement[" + listItem + "].name");
			addFieldsToVerify.add("about[0][0].itemListElement[" + listItem + "].description");
			addFieldsToVerify.add("about[0][0].itemListElement[" + listItem + "].award");
			addFieldsToVerify.add("about[0][0].itemListElement[" + listItem + "].brand.name");
		}//we are not verifying image field due to the inconsistency

			schemaListMethod(runner.driver(), runner.url());		 
	};
	
	private void schemaListMethod(WebDriverExtended driver, String url) {
		AbstractMntlSchemaTest mntlSchemaTest = new ProductListSchemaTest();
		JsonPath schema = mntlSchemaTest.getSchema(driver, url);
		mntlSchemaTest.testSchemaRules(schema, collector);
	}

	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = super.getBasicStructure();
		fieldsToVerify.addAll(addFieldsToVerify);
		fieldsToVerify.add("about[0].name");
		fieldsToVerify.remove("image");
		
		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
		fieldsToVerify.put("about[0].@type", "ItemList");
		fieldsToVerify.putAll(addAdditionalFieldsToVerify);
		fieldsToVerify.remove("@type", "Article");
		fieldsToVerify.remove("mainEntityOfPage.'@type'[0]", "WebPage");
		
		return fieldsToVerify;
	}

}
