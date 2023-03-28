package com.about.mantle.model.services.looker;

import java.util.List;
import java.util.Map;

public class LookerQueryRequest {
	private String model;
	private String view;
	private List<String> fields;
	private List<String>pivots;
	private Map<String, String> filters;
	private String row_total;
	private String limit;
	private String query_timezone;
	
	
	public String getQuery_timezone() {
		return query_timezone;
	}
	
	public void setQuery_timezone(String query_timezone) {
		this.query_timezone = query_timezone;
	}
	
	public String getLimit() {
		return limit;
	}
	
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public String getRow_total() {
		return row_total;
	}
	
	public void setRow_total(String row_total) {
		this.row_total = row_total;
	}
	
	public Map<String, String> getFilters() {
		return filters;
	}
	
	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}
	
	public List<String> getPivots() {
		return pivots;
	}
	
	public void setPivots(List<String> pivots) {
		this.pivots = pivots;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
}
