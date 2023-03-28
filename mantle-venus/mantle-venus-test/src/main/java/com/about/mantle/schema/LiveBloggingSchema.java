package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveBloggingSchema extends AbstractMntlSchemaTest{
    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = new ArrayList<>();
        fieldsToVerify.add("@context");
        fieldsToVerify.add("@type");
        fieldsToVerify.add("headline");
        fieldsToVerify.add("url");
        fieldsToVerify.add("datePublished");
        fieldsToVerify.add("coverageStartTime");
        fieldsToVerify.add("dateModified");
        fieldsToVerify.add("coverageEndTime");
        fieldsToVerify.add("description");
        fieldsToVerify.add("author");
        fieldsToVerify.add("publisher");
        fieldsToVerify.add("image");
        fieldsToVerify.add("mainEntityOfPage");
        fieldsToVerify.add("liveBlogUpdate");
        fieldsToVerify.add("liveBlogUpdate.@type");
        fieldsToVerify.add("liveBlogUpdate.headline");
        fieldsToVerify.add("liveBlogUpdate.articleBody");
        fieldsToVerify.add("liveBlogUpdate.dateModified");
        fieldsToVerify.add("liveBlogUpdate.author");
        fieldsToVerify.add("liveBlogUpdate.publisher");
        fieldsToVerify.add("liveBlogUpdate.image");
        fieldsToVerify.add("mainEntityOfPage");
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        HashMap<String, Object> fieldsToVerify = new HashMap<>();
        fieldsToVerify.put("@type", "LiveBlogPosting");
        fieldsToVerify.put("liveBlogUpdate.@type", "BlogPosting");
        return fieldsToVerify;
    }
}
