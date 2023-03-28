package com.about.mantle.infocat.model.product;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class PlantProfile extends Profile {

    private static final long serialVersionUID = 1L;
    private static final List<String> PROPERTY_ORDER = ImmutableList.of(
            "description",
            "genusName",
            "commonName",
            "additionalCommonNames",
            "plantType",
            "light",
            "height",
            "width",
            "flowerColor",
            "foliageColor",
            "seasonFeatures",
            "specialFeatures",
            "zones",
            "propagation",
            "problemSolvers",
            "harvestTips"
        );

    @Override
    public List<String> getPropertyOrder() {
        return PROPERTY_ORDER;
    }
}
