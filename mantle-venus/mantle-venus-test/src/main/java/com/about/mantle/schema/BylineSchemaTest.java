package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class BylineSchemaTest extends ArticleSchemaTest {

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();
        fieldsToVerify.add("contributor");
        fieldsToVerify.add("editor");
        fieldsToVerify.add("author");
        fieldsToVerify.remove("author.name");

        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.remove("author.@type", "Person");
        fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "MedicalWebPage");
        fieldsToVerify.put("author[0].@type", "Person");
        fieldsToVerify.put("author[1].@type", "Person");
        fieldsToVerify.put("contributor[0].@type", "Person");
        fieldsToVerify.put("contributor[1].@type", "Person");
        fieldsToVerify.put("editor[0].@type", "Person");
        fieldsToVerify.put("mainEntityOfPage.reviewedBy[0].@type", "Person");
        fieldsToVerify.put("mainEntityOfPage.reviewedBy[1].@type", "Person");

        return fieldsToVerify;
    }
}
