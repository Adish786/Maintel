package com.about.mantle.schema;

import java.util.Map;

public class InlineVideoSchemaTest extends ArticleSchemaTest {

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("video.@type", "VideoObject");
        return fieldsToVerify;
    }
}
