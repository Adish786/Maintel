package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PRMProductRoundUpSchema extends AbstractMntlSchemaTest{
    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = new ArrayList<>();
        fieldsToVerify.add("@context");
        fieldsToVerify.add("@type");
        fieldsToVerify.add("headline");
        fieldsToVerify.add("author");
        fieldsToVerify.add("author.type");
        fieldsToVerify.add("author.name");
        fieldsToVerify.add("author.url");
        fieldsToVerify.add("image");
        fieldsToVerify.add("video");
        fieldsToVerify.add("publisher");
        fieldsToVerify.add("citation");
        fieldsToVerify.add("mainEntityOfPage");


        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        return null;
    }
}
