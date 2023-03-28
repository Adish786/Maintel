package com.about.mantle.plcomponent;

public class PLComponentMapper {
	private String compId;
	private String plUrl;
	private String previewType;
	
	public PLComponentMapper(String compId, String plUrl, String previewType) {
		this.compId = compId;
		this.plUrl = plUrl;
		this.previewType = previewType;
	}
	
	public String getCompId() {
		return compId;
	}
	public void setCompId(String compId) {
		this.compId = compId;
	}
	public String getPlUrl() {
		return plUrl;
	}
	public String getPreviewType() {
		return previewType;
	}
	public void setPlUrl(String plUrl) {
		this.plUrl = plUrl;
	}
	public void setpreviewType(String previewType) {
		this.previewType = previewType;
	}
	@Override
	public String toString() {
		return "Pattern Library Mantle Component: -> " + "Component= "  + getCompId() +  ", MantleUrl= "
				+ getPlUrl() + "Preview Type: " + getPreviewType();		 
	}
}
