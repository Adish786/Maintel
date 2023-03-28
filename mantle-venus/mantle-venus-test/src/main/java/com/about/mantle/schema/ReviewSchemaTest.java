package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

@Deprecated
// will be deleted once all the verticals merged in schema refactoring PRs https://dotdash.atlassian.net/browse/AXIS-2944
public class ReviewSchemaTest extends ArticleSchemaTest {

    private boolean hasGuestAuthor;

    public ReviewSchemaTest() {
        this.hasGuestAuthor=true;
    }

    public ReviewSchemaTest(boolean hasGuestAuthor) {
        this.hasGuestAuthor=hasGuestAuthor;
    }

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();

        fieldsToVerify.add("image");
        fieldsToVerify.add("author.name");
        if(hasGuestAuthor) {
            fieldsToVerify.add("author.@type");
            fieldsToVerify.add("author.name");
        }
        fieldsToVerify.add("itemReviewed.@type");
        fieldsToVerify.add("itemReviewed.name");
        fieldsToVerify.add("itemReviewed.review");
        fieldsToVerify.add("itemReviewed.offers");
        fieldsToVerify.add("itemReviewed.review.author.name");
        fieldsToVerify.add("itemReviewed.image");
        fieldsToVerify.add("reviewRating.@type");
        fieldsToVerify.add("reviewRating.ratingValue");

        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("author.@type", "Person");
        fieldsToVerify.put("'@type'[0]", "Review");
        fieldsToVerify.put("itemReviewed.review.author.@type", "Person");
        fieldsToVerify.put("reviewRating.@type", "Rating");

        return fieldsToVerify;
    }
}
