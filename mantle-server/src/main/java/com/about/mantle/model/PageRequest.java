package com.about.mantle.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PageRequest {

	private Integer offset;
	private Integer limit;

	private PageRequest(Integer offset, Integer limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public static PageRequest fromPageNumberAndSize(Integer pageNumber, Integer pageSize) {
		return new PageRequest((pageNumber - 1) * pageSize, pageSize);
	}

	@Override
	public int hashCode() {
		// @formatter:off
		return new HashCodeBuilder()
				.append(offset)
				.append(limit)
				.build();
		// @formatter:on
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PageRequest)) return false;

		PageRequest other = (PageRequest) obj;
		// @formatter:off
		return new EqualsBuilder()
				.append(offset, other.offset)
				.append(limit, other.limit)
				.build();
		// @formatter:on
	}

}
