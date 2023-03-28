package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class ItemReviewSchemaTest extends ArticleSchemaTest {

    private boolean hasImage;

    public ItemReviewSchemaTest(boolean hasGuestAuthor, boolean hasImage) {
        super(hasGuestAuthor, hasImage);
    }

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();

        if(this.hasImage){
            fieldsToVerify.add("image");
            fieldsToVerify.add("image.@type");
            fieldsToVerify.add("image.url");
            fieldsToVerify.add("image.height");
            fieldsToVerify.add("image.width");
        }
        fieldsToVerify.add("itemReviewed.@type");
        fieldsToVerify.add("itemReviewed.name");
        fieldsToVerify.add("itemReviewed.review");
        fieldsToVerify.add("itemReviewed.review.author.name");
        fieldsToVerify.add("itemReviewed.offers");
        fieldsToVerify.add("itemReviewed.url");
        fieldsToVerify.add("itemReviewed.image");
        fieldsToVerify.add("reviewRating.@type");
        fieldsToVerify.add("reviewRating.ratingValue");

        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();

        fieldsToVerify.put("author.@type", "Person");
        fieldsToVerify.put("@type", "Review");
        fieldsToVerify.put("itemReviewed.review.author.@type", "Person");
        fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "WebPage");
        fieldsToVerify.put("reviewRating.@type", "Rating");

        return fieldsToVerify;
    }
}