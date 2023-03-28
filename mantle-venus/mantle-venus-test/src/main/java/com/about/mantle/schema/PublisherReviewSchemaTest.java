package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class PublisherReviewSchemaTest extends ArticleSchemaTest {

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();
        fieldsToVerify.add("image");
        fieldsToVerify.add("image.@type");
        fieldsToVerify.add("image.url");
        fieldsToVerify.add("image.height");
        fieldsToVerify.add("image.width");
        fieldsToVerify.add("publisher.@type");
        fieldsToVerify.add("publisher.name");
        fieldsToVerify.add("publisher.url");
        fieldsToVerify.add("publisher.logo");
        fieldsToVerify.add("publisher.brand");
        fieldsToVerify.add("publisher.publishingPrinciples");
        fieldsToVerify.add("publisher.sameAs");
        fieldsToVerify.add("mainEntityOfPage.@type");
        fieldsToVerify.add("mainEntityOfPage.@id");
        fieldsToVerify.add("mainEntityOfPage.breadcrumb");
        fieldsToVerify.add("mainEntityOfPage.reviewedBy.name");
        fieldsToVerify.add("mainEntityOfPage.lastReviewed");
        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "WebPage");
        fieldsToVerify.put("author.'@type'[0]", "Person");
        fieldsToVerify.put("mainEntityOfPage.reviewedBy.'@type'[0]", "Person");
        return fieldsToVerify;
    }
}