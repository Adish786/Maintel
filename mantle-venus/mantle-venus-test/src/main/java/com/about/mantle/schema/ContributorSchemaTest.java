package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ContributorSchemaTest extends AbstractMntlSchemaTest {

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
        fieldsToVerify.add("description");
        fieldsToVerify.add("author.name");
        fieldsToVerify.add("contributor[0].name");
        fieldsToVerify.add("contributor[0].description");
        fieldsToVerify.add("contributor[0].url");
        fieldsToVerify.add("contributor[0].jobTitle");
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        HashMap<String, Object> fieldsToVerify = new HashMap<>();
        fieldsToVerify.put("contributor[0].@type", "Person");
        return fieldsToVerify;
    }

}