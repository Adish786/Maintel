package com.about.mantle.model.campaign;

import java.util.List;

public class Campaign {
    private String name;
    private CampaignDialog dialog;
    private CampaignTrigger trigger;
	private CampaignGeoTargeting geoTargeting;
    private List<CampaignTargetingRule> targeting;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public CampaignDialog getDialog() {
		return dialog;
	}

	public void setDialog(CampaignDialog dialog) {
		this.dialog = dialog;
	}

    public CampaignTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(CampaignTrigger trigger) {
		this.trigger = trigger;
	}

    public CampaignGeoTargeting getGeoTargeting() {
		return geoTargeting;
	}

	public void setGeoTargeting(CampaignGeoTargeting geoTargeting) {
		this.geoTargeting = geoTargeting;
	}

    public List<CampaignTargetingRule> getTargeting() {
		return targeting;
	}

	public void setTargeting(List<CampaignTargetingRule> targeting) {
		this.targeting = targeting;
	}
}
