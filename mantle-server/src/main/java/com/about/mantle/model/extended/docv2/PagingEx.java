package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.net.URISyntaxException;

import com.about.globe.core.http.RequestContext;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PagingEx implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 * For image gallery, the meaning of "current" and "total":
	 *
	 * Total: total number of index pages for the image gallery
	 * Current: The index number if it's an index page. If its an individual page, it's the index number of the index page
	 *          where this individual page is indexed.
	 */
	private String pattern;
	private Integer current;
	private Integer total;

	/*
	 * Those fields are currently for image gallery only.
	 *   totalItems: total number of item in the image gallery
	 *   indexPage: to tell if the response is for an index page or individual page request
	 *
	 * The fields "sequence", ""prevUrl" and "nextUrl" are for individual pages:
	 *   sequence: sequence number of the current item in the image gallery
	 *   prevUrl: the url of previous item, or null if the current item is the first one
	 *   nextUrl: the url of next item, or null if the current item is the last one
	 */

	private Integer totalItems;
	private Boolean indexPage;
	private Integer sequence;

	private String prevUrlHint;
	private String nextUrlHint;

	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getCurrent() {
		return current;
	}
	public void setCurrent(Integer current) {
		this.current = current;
	}

	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}

	public Boolean isIndexPage() {
		return indexPage;
	}
	public void setIndexPage(Boolean indexPage) {
		this.indexPage = indexPage;
	}

	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	@JsonProperty("prevUrl")
	public String getPrevUrlHint() {
		return prevUrlHint;
	}
	@JsonProperty("prevUrl")
	public void setPrevUrlHint(String prevUrlHint) {
		this.prevUrlHint = prevUrlHint;
	}
	
	public String getPrevUrl(RequestContext requestContext) {
		try {
			return requestContext.getUrlData().with().path(prevUrlHint).build().getCanonicalUrl();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unsupported encoding", e);
		}
	}
	
	@JsonProperty("nextUrl")
	public String getNextUrlHint() {
		return nextUrlHint;
	}
	
	@JsonProperty("nextUrl")
	public void setNextUrlHint(String nextUrlHint) {
		this.nextUrlHint = nextUrlHint;
	}
	
	public String getNextUrl(RequestContext requestContext) {
		try {
			return requestContext.getUrlData().with().path(nextUrlHint).build().getCanonicalUrl();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unsupported encoding", e);
		}
	}
}
