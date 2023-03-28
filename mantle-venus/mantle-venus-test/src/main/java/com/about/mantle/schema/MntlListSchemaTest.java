package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class MntlListSchemaTest extends ArticleSchemaTest {

	private int starRatingindex = -1;
	private boolean hasImage;

	public MntlListSchemaTest() {

	}

	/*
	 * This constructor is used for star rating verification on the schema
	 * @starRatingindex is used for the index of star rating block on the document
	 */
	public MntlListSchemaTest(int starRatingindex ) {
		this.starRatingindex = starRatingindex;
	}

	public MntlListSchemaTest(boolean  hasImage) {
		this.hasImage = hasImage;
	}


	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = super.getBasicStructure();

		fieldsToVerify.add("about[0].@context");
		fieldsToVerify.add("about[0].@type");
		fieldsToVerify.add("about[0].description");
		fieldsToVerify.add("about[0].itemListOrder");
		fieldsToVerify.add("about[0].itemListElement");
		fieldsToVerify.add("about[0].name");
		if(this.hasImage) {
			fieldsToVerify.add("image[0].@type");
			fieldsToVerify.add("image[0].url");
			fieldsToVerify.add("image[0].height");
			fieldsToVerify.add("image[0].width");
			fieldsToVerify.add("image");
			fieldsToVerify.add("image.@type");
			fieldsToVerify.add("image.url");
			fieldsToVerify.add("image.height");
			fieldsToVerify.add("image.width");
		}
		fieldsToVerify.add("publisher.logo");
		fieldsToVerify.add("publisher.logo.@type");
		fieldsToVerify.add("publisher.logo.url");
		fieldsToVerify.add("publisher.logo.width");
		fieldsToVerify.add("publisher.logo.height");
		fieldsToVerify.add("publisher.publishingPrinciples");
		fieldsToVerify.add("mainEntityOfPage.@type");
		fieldsToVerify.add("mainEntityOfPage.@id");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.@type");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.itemListElement[0].@type");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.itemListElement[0].position");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.itemListElement[0].item.@id");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.itemListElement[0].item.name");
		fieldsToVerify.add("about.@type");
		fieldsToVerify.add("about.name");
		fieldsToVerify.add("about.description");
		fieldsToVerify.add("about.url");

		if (starRatingindex >= 0) {
			fieldsToVerify.add("about[0].itemListElement[0][" + starRatingindex + "].review.reviewRating");
		}

		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();

		fieldsToVerify.put("about[0].@type", "ItemList");
		fieldsToVerify.put("publisher.@type", "Organization");
		fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "WebPage");

		if (starRatingindex >= 0) {
			fieldsToVerify.remove("@type", "Article");
		}

		return fieldsToVerify;
	}

}