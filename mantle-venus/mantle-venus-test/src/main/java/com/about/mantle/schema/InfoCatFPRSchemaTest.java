package com.about.mantle.schema;

import java.util.List;

@Deprecated
public class InfoCatFPRSchemaTest extends ReviewSchemaTest{

    private String category;

    public InfoCatFPRSchemaTest(String category) {

        this.category=category;
    }

    public InfoCatFPRSchemaTest(boolean hasGuestAuthor, String category) {
        super(hasGuestAuthor);
        this.category=category;
    }

    @Override
    protected List<String> getBasicStructure() {
        List<String> fieldsToVerify = super.getBasicStructure();

        fieldsToVerify.add("itemReviewed.url");

        if(!category.toLowerCase().matches("book|media|software")) {
            fieldsToVerify.add("itemReviewed.brand");
        }

        return fieldsToVerify;
    }

}