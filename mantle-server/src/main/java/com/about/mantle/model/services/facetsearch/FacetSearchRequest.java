package com.about.mantle.model.services.facetsearch;

import java.util.List;
import java.util.Map;

public class FacetSearchRequest {
	
	private Map<String, List<String>> pivots;
	private String query; 
	private String facetSearchKey;
	private Integer offset;
	private Integer limit;
	private String sort; 
	private Integer maxHeight;
	private Integer maxWidth;
	private Boolean forceSize;
	private String cropSetting;
	
	public Map<String, List<String>> getPivots() {
		return pivots;
	}
	public void setPivots(Map<String, List<String>> pivots) {
		this.pivots = pivots;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getFacetSearchKey() {
		return facetSearchKey;
	}
	public void setFacetSearchKey(String facetSearchKey) {
		this.facetSearchKey = facetSearchKey;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public Integer getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}
	public Integer getMaxWidth() {
		return maxWidth;
	}
	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	public Boolean getForceSize() {
		return forceSize;
	}
	public void setForceSize(Boolean forceSize) {
		this.forceSize = forceSize;
	}
	public String getCropSetting() {
		return cropSetting;
	}
	public void setCropSetting(String cropSetting) {
		this.cropSetting = cropSetting;
	}

}
