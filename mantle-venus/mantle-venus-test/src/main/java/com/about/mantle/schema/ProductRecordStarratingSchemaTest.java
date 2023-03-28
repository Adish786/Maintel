package com.about.mantle.schema;

import java.util.*;
import java.util.function.Consumer;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.neko.NekoDocumentContext;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.DocumentContext;

import io.restassured.path.json.JsonPath;

import static org.hamcrest.Matchers.is;

public abstract class ProductRecordStarratingSchemaTest extends MntlVenusTest
		implements MntlCommonTestMethods<MntlCommonTestMethods.MntlRunner> {

	protected abstract String getDocId();
	protected abstract String getNeko();

	static List<String> addFieldsToVerify = new ArrayList<>();
	static Map<String, Object> additionalFields = new HashMap<>();

	/*
	 * DocId and neko url should be provided
	 * Verifying that there should be only one star rating value on the schema
	 * first product rating which has star rating value should be displayed on the schema 
	 */
	protected Consumer<MntlRunner> productRecordStarRatingSchemaTest = runner -> {

		JsonPath schema = mntlSchema().getSchema(runner.driver(), runner.url());
		String nekoUrl = getNeko();
		String docId = getDocId();

		DocumentContext seleneData = SeleneUtils.getDocuments(docId);
		JsonArray seleneArray = jsonObject(seleneData).get("data").getAsJsonObject().get("items").getAsJsonObject()
				.get("list").getAsJsonArray();

		String productId;
		int flag = 0;
		for (int product = 0; product < seleneArray.size(); product++) {
			productId = seleneData.read("$.data.items.list[" + product + "].contents.list[0].data.productId")
					.toString();
			DocumentContext nekoContext = nekoData(nekoUrl, productId);
			double rating = Double.parseDouble(nekoContext.read("data.rating"));

			if (rating > 0 && flag == 0) {
				String ratingSchema = schema
						.get("about[0].itemListElement[" + product + "].review.reviewRating.ratingValue").toString().replaceAll("\\[|\\]","");
				double formattedRating = Double.parseDouble(ratingSchema);
				collector.checkThat(product + ". product schema does not have correct star rating value", formattedRating,
						is(rating));
				additionalFields.put("about[0].itemListElement[" + product + "].review.reviewRating.worstRating", 0);
				additionalFields.put("about[0].itemListElement[" + product + "].review.reviewRating.bestRating", 5);
				flag++;
				continue;
			}
			if (rating <= 0 || flag > 0) {
				Optional<String> ratingSchema = Optional.ofNullable(
						schema.get("about[0].itemListElement[" + product + "].review.reviewRating.ratingValue"));
				collector.checkThat("There is more than 1 star rating value in schema", ratingSchema.isPresent(),
						is(false));
			}
		}
		mntlSchema().testSchemaRules(schema, collector);
	};

	private DocumentContext nekoData(String nekoUrl, String productId) {
		DocumentContext nekoData = NekoDocumentContext.getNekoDocument(nekoUrl, productId);
		return nekoData;
	}

	private JsonObject jsonObject(DocumentContext context) {
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(context.jsonString());
		JsonObject jsonObject = jsonTree.getAsJsonObject();

		return jsonObject;
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
