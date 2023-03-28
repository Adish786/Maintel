package com.about.mantle.schema;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.neko.NekoDocumentContext;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Configuration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Option;

public abstract class ProductRecordOffersSchemaTest extends MntlVenusTest
		implements MntlCommonTestMethods<MntlCommonTestMethods.MntlRunner> {

	/*
	 * document Id needs to be provided by the sub-class
	 */
	abstract String getDocId();

	/*
	 * Neko service url needs to be provided by the sub-class 
	 * 		we can implement service discovery to get the neko url from the project in the future, 
	 * 		for now we need to pass from sub-class
	 */
	abstract String getNeko();

	static List<String> addFieldsToVerify = new ArrayList<>();
	static Map<String, Object> additionalFields = new HashMap<>();

	@SuppressWarnings("deprecation")
	protected Consumer<MntlRunner> productOffersSchemaTest = runner -> {

		io.restassured.path.json.JsonPath schema = mntlSchema().getSchema(runner.driver(), runner.url());
		String nekoUrl = getNeko();

		DocumentContext seleneData = SeleneUtils.getDocuments(getDocId());

		Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		JsonArray seleneArray = jsonObject(seleneData).get("data").getAsJsonObject().get("items").getAsJsonObject()
				.get("list").getAsJsonArray();

		int index = 0;
		int flag = 0;
		String productId;
		String currencyCode = null;
		int currencyValue = 0;

		for (int product = 0; product < seleneArray.size(); product++) {
			productId = seleneData.read("$.data.items.list[" + product + "].contents.list[0].data.productId")
					.toString();
			DocumentContext nekoContext = nekoData(nekoUrl, productId);
			JsonArray nekoArray = jsonObject(nekoContext).get("data").getAsJsonObject().get("properties")
					.getAsJsonArray();

			for (int i = 0; i < nekoArray.size(); i++) {

				String currency = JsonPath.using(conf).parse(nekoContext.jsonString())
						.read("data.properties[" + i + "].value.currencyCode");
				if (currency != null) {
					currencyCode = currency;
					currencyValue = nekoContext.read("data.properties[" + i + "].value.currencyValue");
					flag++;
					break;
				}
			}
			if (flag > 0) {
				index = product;
				break;
			}
		}
		if (flag > 0) {
			DecimalFormat df = new DecimalFormat("#.00");
			double formattedValue = (double) currencyValue / 100;
			addFieldsToVerify.add("about[0].itemListElement[" + index + "].offers");
			additionalFields.put("about[0].itemListElement[" + index + "].offers.@type", "Offer");
			additionalFields.put("about[0].itemListElement[" + index + "].offers.priceCurrency", currencyCode);
			additionalFields.put("about[0].itemListElement[" + index + "].offers.price", df.format(formattedValue));
			mntlSchema().testSchemaRules(schema, collector);
		} else {
			Assert.fail("None of the product has offer...");
		}
	};

	private JsonObject jsonObject(DocumentContext context) {
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(context.jsonString());
		JsonObject jsonObject = jsonTree.getAsJsonObject();

		return jsonObject;
	}

	private DocumentContext nekoData(String nekoUrl, String productId) {
		DocumentContext nekoData = NekoDocumentContext.getNekoDocument(nekoUrl, productId);
		return nekoData;
	}

	public AbstractMntlSchemaTest mntlSchema() {
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
				return additionalFields;
			}
		};

		return mntlSchemaTest;
	}

}
