package com.about.mantle.model.extended.attribution;

import com.about.globe.core.exception.GlobeException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum AttributionTypeEnum {
    AUTHOR,
    ADDITIONAL_REPORTER,
    ADDITIONAL_RESEARCHER,
    BEAUTY_WELLNESS_REVIEWER,
    CLEANING_REVIEWER,
    COMPLIANCE_REVIEWER,
    EDITOR,
    FACT_CHECKER,
    FINANCE_REVIEWER,
    GARDENING_REVIEWER,
    MEDICAL_REVIEWER,
    NUTRITION_REVIEWER,
    ORIGINAL_WRITER,
    RECIPE_TESTER,
    RENOVATIONS_REPAIR_REVIEWER,
    RESEARCH_ANALYSIS,
    TECH_REVIEWER,
    TESTER,
    UGC_RECIPE,
    UPDATER,
    VET_REVIEWER,
    WELLNESS_REVIEWER;

    @JsonCreator
    public static AttributionTypeEnum create(String value) {
        for (AttributionTypeEnum typeEnum : values()) {
            if (typeEnum.name().equals(value)) {
                return typeEnum;
            }
        }
        throw new GlobeException("Invalid ATTRIBUTION ENUM value found:" + value);
    }
}
