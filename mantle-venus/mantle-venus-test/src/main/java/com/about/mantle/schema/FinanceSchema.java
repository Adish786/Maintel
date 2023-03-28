package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceSchema extends AbstractMntlSchemaTest{

    private String templateType;

    public FinanceSchema() {}

    public FinanceSchema(String templateType) {
        this.templateType = templateType;
    }

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = new ArrayList<>();
        fieldsToVerify.add("@context");
        fieldsToVerify.add("mainEntityOfPage");
        fieldsToVerify.add("articleSection");;
        fieldsToVerify.add("keywords");
        fieldsToVerify.add("headline");
        fieldsToVerify.add("datePublished");
        fieldsToVerify.add("dateModified");
        fieldsToVerify.add("description");
        fieldsToVerify.add("publisher");
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        HashMap<String, Object> fieldsToVerify = new HashMap<>();
        if(templateType.equalsIgnoreCase("RoundUp"))
        fieldsToVerify.put("about.itemListElement.@type", "FinancialProduct");
        return fieldsToVerify;
    }
}