package com.about.mantle.model.extended;

import com.about.hippodrome.models.response.PageInfo;

public class PageInfoExtended {

	private final Integer limit;
	private final Integer offset;
	private final Integer resultSize;
	private final Integer totalResultSize;

	public PageInfoExtended(PageInfo pageInfo) {
		this.limit = pageInfo.getLimit();
		this.offset = pageInfo.getOffset();
		this.resultSize = pageInfo.getResultSize();
		this.totalResultSize = pageInfo.getTotalResultSize();
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getResultSize() {
		return resultSize;
	}

	public Integer getTotalResultSize() {
		return totalResultSize;
	}

	public Boolean hasNext() {
		if (offset == null || resultSize == null || totalResultSize == null) return null;
		return (offset + resultSize) < totalResultSize;
	}

	public Boolean hasPrevious() {
		if (offset == null) return null;
		return offset > 0;
	}

	public Integer getPageNumber() {
		if (offset == null || limit == null || limit == 0) return null;

		return offset / limit;
	}

	public Integer getPageCount() {
		if (totalResultSize == null || limit == null || limit == 0) return null;

		return (totalResultSize / limit) + (totalResultSize % limit > 0 ? 1 : 0);
	}

}
