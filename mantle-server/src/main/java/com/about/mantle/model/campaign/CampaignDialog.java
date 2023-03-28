package com.about.mantle.model.campaign;

public class CampaignDialog {
    private String type;
    private String regSource;
	private Boolean isSubscriptionListHidden;

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getRegSource() {
		return regSource;
	}

	public void setRegSource(String regSource) {
		this.regSource = regSource;
	}

    public Boolean getIsSubscriptionListHidden() {
		return isSubscriptionListHidden;
	}

	public void setIsSubscriptionListHidden(Boolean isSubscriptionListHidden) {
		this.isSubscriptionListHidden = isSubscriptionListHidden;
	}
}
