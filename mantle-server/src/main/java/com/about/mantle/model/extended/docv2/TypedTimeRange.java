package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class TypedTimeRange implements Serializable {
    private static final long serialVersionUID = 1L;

    private TimeRangeEx timeRange;
    private RecipeTimeType recipeTimeType;

    public TypedTimeRange() {
    }

    public TimeRangeEx getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRangeEx timeRange) {
        this.timeRange = timeRange;
    }

    public RecipeTimeType getRecipeTimeType() {
        return recipeTimeType;
    }

    public void setRecipeTimeType(RecipeTimeType recipeTimeType) {
        this.recipeTimeType = recipeTimeType;
    }

    public static enum RecipeTimeType {
        RISE,
        MARINATE,
        BAKE,
        FREEZE,
        ROAST,
        STAND,
        START_TO_FINISH,
        OVEN,
        GRILL,
        BROIL,
        COOL,
        SLOW_COOK,
        CHILL,
        COOK,
        PREP,
        MICROWAVE,
        PROCESS,
        SMOKE,
        DECORATE,
        SOAK,
        HEAT,
        FRY,
        TOTAL,
        HANDS_ON,
        READY_IN,
        ACTIVE,
        ASSEMBLE,
        REFRIGERATE,
        REST,
        BRING_TO_PRESSURE,
        RELEASE_PRESSURE,
        STEEP,
        TOAST,
        STEAM,
        SAUTE,
        TIME_TO_PRESSURE,
        PRESSURE_COOK,
        ADDITIONAL,
        OTHER_TIME
    }
}