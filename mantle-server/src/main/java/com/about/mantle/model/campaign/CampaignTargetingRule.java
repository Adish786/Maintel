package com.about.mantle.model.campaign;

import java.util.List;

public class CampaignTargetingRule {
    private List<String> templates;
    private List<String> taxonomies;
	private List<String> viewTypes;
	private List<String> revenueGroups;

    public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

    public List<String> getTaxonomies() {
		return taxonomies;
	}

	public void setTaxonomies(List<String> taxonomies) {
		this.taxonomies = taxonomies;
	}

    public List<String> getViewTypes() {
		return viewTypes;
	}

	public void setViewTypes(List<String> viewTypes) {
		this.viewTypes = viewTypes;
	}

    public List<String> getRevenueGroups() {
		return revenueGroups;
	}

	public void setRevenueGroups(List<String> revenueGroups) {
		this.revenueGroups = revenueGroups;
	}
}
