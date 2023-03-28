package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

/**
 * Only applicable for Commerce and Performance marketing pages.
 */
public class KeywordsSchemaTest extends ArticleSchemaTest{

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("keywords", "affiliate");
        return fieldsToVerify;
    }
}
