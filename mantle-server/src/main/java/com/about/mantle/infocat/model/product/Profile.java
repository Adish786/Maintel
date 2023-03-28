package com.about.mantle.infocat.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Objects;

public abstract class Profile extends Product {

    @Override
    public String getProductName() {
        return Objects.toString(getPropertyValue("fullTitle").getPrimaryValue(), null);
    }

    @Override
    public String getShortTitle() {
        return Objects.toString(getPropertyValue("shortTitle").getPrimaryValue(), null);
    }

    @Override
    public String getLongTitle() {
        return Objects.toString(getPropertyValue("fullTitle").getPrimaryValue(), null);
    }

    @JsonIgnore
	public abstract List<String> getPropertyOrder();
}
