package com.about.mantle.schema;

import java.util.Map;

public class NewsSchemaTest extends ArticleSchemaTest {
	
	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
		fieldsToVerify.put("@type", "NewsArticle");
		return fieldsToVerify;
	}
}