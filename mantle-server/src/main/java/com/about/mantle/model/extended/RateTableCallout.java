package com.about.mantle.model.extended;

public enum RateTableCallout {
		
	MORTGAGE("mortgage", "mortgage", "mortgages", "mortgage"),
	MMA("mm", "mm,sav,cd", "deposits", "deposits"),
	SAVINGS("sav", "mm,sav,cd", "deposits", "deposits"),
	CD("cd", "mm,sav,cd", "deposits", "deposits"),
	NO_TABLE("", "", "", "");
	
	private String tab;
	private String visibleTabs;
	private String redirect;
	private String keyword;
	
	private RateTableCallout(String tab, String visibleTabs, String redirect, String keyword) {
		this.tab = tab;
		this.visibleTabs = visibleTabs;
		this.redirect = redirect;
		this.keyword = keyword;
	}
	
	public String getTab() {
		return tab;
	}
	public String getVisibleTabs() {
		return visibleTabs;
	}
	public String getRedirect() {
		return redirect;
	}
	public String getKeyword() {
		return keyword;
	}
	
};
