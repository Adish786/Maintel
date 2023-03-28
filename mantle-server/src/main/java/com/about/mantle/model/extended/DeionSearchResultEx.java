package com.about.mantle.model.extended;

import java.io.Serializable;
import java.util.List;

public class DeionSearchResultEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private long numFound;
	private List<DeionSearchResultItemEx> items;
	private List<DeionFacet> facets;

	private List<DeionRangeFacet> rangeFacets;


	public DeionSearchResultEx (){
		
	}
	
	public DeionSearchResultEx (DeionSearchResultEx that){
		this.numFound = that.numFound;
		this.items = that.items;
		this.facets = that.facets;
	}
	
	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public List<DeionSearchResultItemEx> getItems() {
		return items;
	}

	public void setItems(List<DeionSearchResultItemEx> items) {
		this.items = items;
	}

	public List<DeionFacet> getFacets() {
		return facets;
	}

	public void setFacets(List<DeionFacet> facets) {
		this.facets = facets;
	}

	public List<DeionRangeFacet> getRangeFacets() {
		return rangeFacets;
	}

	public void setRangeFacets(List<DeionRangeFacet> ranges) {
		this.rangeFacets = ranges;
	}

	//TODO consider moving into a standalone class
	public static class DeionFacetCount implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String value;
		private String displayName; //added for mantle use see TS-3704
		private Long count;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}

	//TODO consider moving into a standalone class as this class is being more heavily used in faceted search
	public static class DeionFacet implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String field;
		private String displayName; //added for mantle use see TS-3704
		private List<DeionFacetCount> counts;
		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public List<DeionFacetCount> getCounts() {
			return counts;
		}

		public void setCounts(List<DeionFacetCount> counts) {
			this.counts = counts;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
}
