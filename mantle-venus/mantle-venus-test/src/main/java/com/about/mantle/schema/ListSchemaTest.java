package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

@Deprecated
// will be deleted once all the verticals merged in schema refactoring PRs https://dotdash.atlassian.net/browse/AXIS-2944
public class ListSchemaTest extends ArticleSchemaTest {

    private int starRatingindex = -1;

    public ListSchemaTest() {

    }

    /*
     * This constructor is used for star rating verification on the schema
     * @starRatingindex is used for the index of star rating block on the document
     */
    public ListSchemaTest(int starRatingindex ) {
        this.starRatingindex = starRatingindex;
    }


    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();
        fieldsToVerify.add("about[0].@context");
        fieldsToVerify.add("about[0].@type");
        fieldsToVerify.add("about[0].description");
        fieldsToVerify.add("about[0].itemListOrder");
        fieldsToVerify.add("about[0].itemListElement");
        if (starRatingindex >= 0) {
            fieldsToVerify.add("about[0].itemListElement[" + starRatingindex + "].review.reviewRating");
        }

        return fieldsToVerify;
    }

    @Override
    protected Map<String, Object> getAdditionalFieldsToVerify() {
        Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
        fieldsToVerify.put("about[0].@type", "ItemList");
        if (starRatingindex >= 0) {
            fieldsToVerify.remove("'@type'[0]", "Article");
        }
        return fieldsToVerify;
    }

}