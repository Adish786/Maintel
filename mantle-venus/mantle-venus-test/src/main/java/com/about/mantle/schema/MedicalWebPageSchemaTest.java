package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class MedicalWebPageSchemaTest extends ArticleSchemaTest {

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();

        fieldsToVerify.add("mainEntityOfPage.reviewedBy");
        fieldsToVerify.add("mainEntityOfPage.lastReviewed");

        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();

        fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "MedicalWebPage");
        fieldsToVerify.put("mainEntityOfPage.reviewedBy.'@type'[0]", "Person");

        return fieldsToVerify;
    }
}