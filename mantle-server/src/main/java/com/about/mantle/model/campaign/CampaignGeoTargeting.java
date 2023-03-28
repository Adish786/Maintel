package com.about.mantle.model.campaign;

import java.util.List;

public class CampaignGeoTargeting {
    private Boolean isInclusive = true;
    private List<String> countries;

    public Boolean getIsInclusive() {
		return isInclusive;
	}

	public void setIsInclusive(Boolean isInclusive) {
		this.isInclusive = isInclusive;
	}

    public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}
}
