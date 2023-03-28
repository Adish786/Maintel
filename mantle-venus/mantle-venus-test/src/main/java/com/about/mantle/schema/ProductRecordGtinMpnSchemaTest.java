package com.about.mantle.schema;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.neko.NekoDocumentContext;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Option;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class ProductRecordGtinMpnSchemaTest extends MntlVenusTest
		implements MntlCommonTestMethods<MntlCommonTestMethods.MntlRunner> {

	static List<String> addFieldsToVerify = new ArrayList<>();
	static Map<String, Object> additionalFields = new HashMap<>();

	/*
	 * Reading the gtin and mpn values from neko for each products and comparing and verifying values on the schema
	 * neko service url and document id must be provided 
	 * if there is both amazonGtin and gtin values for the product then the first amazonGtin should be on the schema from the amazonGtin array
	 * if there is only gtin value for the product then the first gtin value should be on the schema from the gtin array
	 * if there is none amazonGtin and gtin value for the product then nothing will be on the schema
	 * if there is mpn for the product then the first value from the array should be on the schema
	 * if there is no mpn for the product then there should not be a mpn on the schema
	 * 
	 */
	protected Consumer<MntlRunner> productRecordGtinMpnSchemaTest = runner -> {

		String schemaType = new SchemaArray().getSchemaType();
		io.restassured.path.json.JsonPath schema = new SchemaArray().getSpecificSchema(runner.driver(), runner.url(), schemaType);
		String nekoUrl = (String) runner.valuesMap().get("nekoUrl");
		String docId = (String) runner.valuesMap().get("docId");
		
		DocumentContext seleneData = SeleneUtils.getDocuments(docId);
		Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL); 
		JsonArray seleneArray = jsonObject(seleneData).get("data").getAsJsonObject().get("items").getAsJsonObject()
				.get("list").getAsJsonArray();
		
		String productId;

		for (int product = 0; product < seleneArray.size(); product++) {
			productId = seleneData.read("$.data.items.list[" + product + "].contents.list[0].data.productId")
					.toString();
			DocumentContext nekoContext = nekoData(nekoUrl, productId);
			JsonArray nekoArray = jsonObject(nekoContext).get("data").getAsJsonObject().get("properties")
					.getAsJsonArray();

			String amazonGtinValue = null;
			String gtinValue = null;
			String mpnValue = null;
			for (int i = 0; i < nekoArray.size(); i++) {
				String gtinType = JsonPath.using(conf).parse(nekoContext.jsonString())
						.read("data.properties[" + i + "].propertyDefinitionMetadata.definition.name");
					
				if (gtinType.equalsIgnoreCase("gtin")) {
					gtinValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");
				}
				if (gtinType.equalsIgnoreCase("amazonGtin")) {
					amazonGtinValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");
				}			
				if (gtinType.equalsIgnoreCase("mpn")) {
					mpnValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");			
				}
			}
			if (amazonGtinValue != null) {
				String gtinSchema = schema.get("about[0].itemListElement[" + product + "].gtin").toString();
				collector.checkThat(product + ". product schema does not have correct gtin value", amazonGtinValue, is(gtinSchema));
				additionalFields.put("about[0].itemListElement[" + product + "].gtin", amazonGtinValue);
			}
			if (amazonGtinValue == null && gtinValue != null) {
				String gtinSchema = schema.get("about[0].itemListElement[" + product + "].gtin").toString();
				collector.checkThat(product + ". product schema does not have correct gtin value", gtinValue, is(gtinSchema));
				additionalFields.put("about[0].itemListElement[" + product + "].gtin", gtinValue);
			}
			
			if (amazonGtinValue == null && gtinValue == null) {
				Optional<String> gtinSchema = Optional.ofNullable(schema.get("about[0].itemListElement[" + product + "].gtin"));
				collector.checkThat(product + ". product schema should not have  gtin value", gtinSchema.isPresent(), is(false));
			}
			
			if (mpnValue != null) {
				String mpnSchema = schema.get("about[0].itemListElement[" + product + "].mpn");
				collector.checkThat(product + ". product schema does not have correct mpn value", mpnValue, is(mpnSchema));
				additionalFields.put("about[0].itemListElement[" + product + "].mpn", mpnValue);
			} else {
				Optional<String> mpnSchema = Optional.ofNullable(schema.get("about[0].itemListElement[" + product + "].mpn"));
				collector.checkThat(product + ". product schema should not have  mpn value", mpnSchema.isPresent(), is(false));
			}
		}	
		mntlSchemaGtin().testSchemaRules(schema, collector);
	};

	protected Consumer<MntlRunner> productRecordFPRGtinMpnSchemaTest = runner -> {
		String schemaType = new SchemaArray().getSchemaType();
		io.restassured.path.json.JsonPath schema = new SchemaArray().getSpecificSchema(runner.driver(), runner.url(), schemaType);
		String nekoUrl = (String) runner.valuesMap().get("nekoUrl");
		String docId = (String) runner.valuesMap().get("docId");

		DocumentContext seleneData = SeleneUtils.getDocuments(docId);
		Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		String entityType = seleneData.read("$.data.entity.data.type");
		if(entityType.equalsIgnoreCase("PRODUCTRECORD")){
			String productId = seleneData.read("$.data.entity.data.id");
			DocumentContext nekoContext = nekoData(nekoUrl, productId);
			JsonArray nekoArray = jsonObject(nekoContext).get("data").getAsJsonObject().get("properties")
					.getAsJsonArray();

			String amazonGtinValue = null;
			String gtinValue = null;
			String mpnValue = null;

			for (int i = 0; i < nekoArray.size(); i++) {
				String gtinType = JsonPath.using(conf).parse(nekoContext.jsonString())
						.read("data.properties[" + i + "].propertyDefinitionMetadata.definition.name");

				if (gtinType.equalsIgnoreCase("gtin")) {
					gtinValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");
				}
				if (gtinType.equalsIgnoreCase("amazonGtin")) {
					amazonGtinValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");
				}
				if (gtinType.equalsIgnoreCase("mpn")) {
					mpnValue = JsonPath.using(conf).parse(nekoContext.jsonString())
							.read("data.properties[" + i + "].value.stringValues[0]");
				}
			}
			if (amazonGtinValue != null) {
				String gtinSchema = schema.get("itemReviewed.GTIN").toString();
				collector.checkThat( "Schema does not have correct gtin value", amazonGtinValue, is(gtinSchema));
				additionalFields.put("itemReviewed.GTIN", amazonGtinValue);
			}
			if (amazonGtinValue == null && gtinValue != null) {
				String gtinSchema = schema.get("").toString();
				collector.checkThat("Schema does not have correct gtin value", gtinValue, is(gtinSchema));
				additionalFields.put("itemReviewed.GTIN", gtinValue);
			}

			if (amazonGtinValue == null && gtinValue == null) {
				Optional<String> gtinSchema = Optional.ofNullable(schema.get(""));
				collector.checkThat("Schema should not have  gtin value", gtinSchema.isPresent(), is(false));
			}

		}else {
			Assert.fail("A document should be provided with a product record");
		}

		mntlSchemaGtin().testSchemaRules(schema, collector);
	};
	public DocumentContext nekoData(String nekoUrl, String productId) {
		DocumentContext nekoData = NekoDocumentContext.getNekoDocument(nekoUrl, productId);
		return nekoData;
	}

	private JsonObject jsonObject(DocumentContext context) {
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(context.jsonString());
		JsonObject jsonObject = jsonTree.getAsJsonObject();

		return jsonObject;
	}

	public AbstractMntlSchemaTest mntlSchemaGtin() {
		AbstractMntlSchemaTest mntlSchemaTest = new AbstractMntlSchemaTest() {
			@Override
			protected List<String> getBasicStructure() {
				List<String> fieldsToVerify = new ArrayList<>();
				fieldsToVerify.add("@context");
				fieldsToVerify.add("@type");
				fieldsToVerify.add("mainEntityOfPage");
				fieldsToVerify.add("headline");
				fieldsToVerify.add("datePublished");
				fieldsToVerify.add("dateModified");
				fieldsToVerify.add("description");
				fieldsToVerify.add("publisher");
				fieldsToVerify.add("author.name");
				fieldsToVerify.addAll(addFieldsToVerify);
				return fieldsToVerify;
			}

			@Override
			protected Map<String, Object> getAdditionalFieldsToVerify() {
				return null;
			}
		};

		return mntlSchemaTest;
	}

	static class SchemaArray extends MntlListSchemaTest {
		@Override
		protected Map<String, Object> getAdditionalFieldsToVerify() {
			Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
			fieldsToVerify.put("@type[0]", null);
			return fieldsToVerify;
		}
	}
}
