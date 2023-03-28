package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishingSchemaTest extends AbstractMntlSchemaTest {

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = new ArrayList<>();
        fieldsToVerify.add("publisher");
        fieldsToVerify.add("publisher.@type");
        fieldsToVerify.add("publisher.name");
        fieldsToVerify.add("publisher.logo");
        fieldsToVerify.add("publisher.brand");
        fieldsToVerify.add("publisher.publishingPrinciples");
        fieldsToVerify.add("publisher.sameAs");
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        HashMap<String, Object> fieldsToVerify = new HashMap<>();
        fieldsToVerify.put("publisher.logo.@type", "ImageObject");
        return fieldsToVerify;
    }

}
