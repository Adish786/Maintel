package com.about.mantle.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HowToSchemaTest extends ArticleSchemaTest {

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();
        fieldsToVerify.add("about[0].supply");
        fieldsToVerify.add("about[0].tool");
        fieldsToVerify.add("about[0].description");
        fieldsToVerify.add("about[0].@type"); // Dynamic Value. Order can be different
        fieldsToVerify.add("about[0].step[0].@type"); // Dynamic Value. Order can be different
        fieldsToVerify.add("about[0].image");// Optional field, need add "FINALPROJECT" into taggedImages/tags/list in custom data

        return fieldsToVerify;
    }


    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("about[0].tool[0].@type","HowToTool");
        return fieldsToVerify;

    }
}