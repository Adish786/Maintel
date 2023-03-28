package com.about.mantle.model.campaign;

import java.util.Map;

public class CampaignTrigger {
    private String type;
    private Map<String, Integer> timer;

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public Map<String, Integer> getTimer() {
		return timer;
	}

	public void setTimer(Map<String, Integer> timer) {
		this.timer = timer;
	}
}
