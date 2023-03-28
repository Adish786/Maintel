package com.about.mantle.infocat.model.product;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class CatProfile extends AnimalProfile {

    private static final long serialVersionUID = 1L;
    private static final List<String> PROPERTY_ORDER = ImmutableList.of(
            "description",
            "officialName",
            "commonName",
            "petHeight",
            "petWeight",
            "lifespan",
            "goodWith",
            "temperament",
            "intelligence",
            "sheddingAmount",
            "playfulness",
            "energyLevel",
            "vocalLevel",
            "coatLength",
            "colors",
            "patterns",
            "otherTraits"
        );

    @Override
    public List<String> getPropertyOrder() {
        return PROPERTY_ORDER;
    }
}
