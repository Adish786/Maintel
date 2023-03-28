package com.about.mantle.model.extended;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.List;

import com.about.hippodrome.models.response.PageInfo;

public class PageExtended<T> {

	private final PageInfoExtended pageInfo;
	private final List<T> list;

	public PageExtended(PageInfo pageInfo, List<T> list) {
		this.pageInfo = pageInfo == null ? null : new PageInfoExtended(pageInfo);
		this.list = isEmpty(list) ? Collections.<T> emptyList() : list;
	}

	public PageInfoExtended getPageInfo() {
		return pageInfo;
	}

	public List<T> getList() {
		return list;
	}

}
