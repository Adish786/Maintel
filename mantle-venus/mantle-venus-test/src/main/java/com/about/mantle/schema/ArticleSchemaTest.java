package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleSchemaTest extends AbstractMntlSchemaTest {

	private boolean hasGuestAuthor;
	private boolean hasImage;

	public ArticleSchemaTest() {
		this(false);
	}

	public ArticleSchemaTest(boolean  hasGuestAuthor) {
		this.hasGuestAuthor = hasGuestAuthor;
	}

	public ArticleSchemaTest(boolean hasGuestAuthor, boolean hasImage) {
		this.hasGuestAuthor = hasGuestAuthor;
		this.hasImage = hasImage;
	}

	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = new ArrayList<>();

		fieldsToVerify.add("@context");
		fieldsToVerify.add("@type");
		fieldsToVerify.add("mainEntityOfPage.breadcrumb.itemListElement");
		fieldsToVerify.add("mainEntityOfPage");
		fieldsToVerify.add("articleSection");;
		fieldsToVerify.add("keywords");
		fieldsToVerify.add("headline");
		fieldsToVerify.add("datePublished");
		fieldsToVerify.add("dateModified");
		fieldsToVerify.add("description");
		fieldsToVerify.add("publisher");
		fieldsToVerify.add("description");
		fieldsToVerify.add("author.name");

		if(hasGuestAuthor) {
			fieldsToVerify.add("author.description");
			fieldsToVerify.add("author.sameAs");
		}

		if(this.hasImage){
			fieldsToVerify.add("image");
		}

		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		HashMap<String, Object> fieldsToVerify = new HashMap<>();

		fieldsToVerify.put("@type", "Article");
		fieldsToVerify.put("author.'@type'[0]", "Person");

		return fieldsToVerify;
	}
}