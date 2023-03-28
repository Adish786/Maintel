package com.about.mantle.model.extended.curatedlist;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.List;

import org.joda.time.DateTime;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class CuratedListEx<T> {
	private String id;
	private String name;
	private String displayName;
	private String description;
	private DateTime activeDate;
	private Boolean isDraft;
	private Integer itemCount;
	private SliceableListEx<T> items = SliceableListEx.emptyList();

	public SliceableListEx<T> getItems() {
		return items;
	}

	public void setItems(SliceableListEx<T> items) {
		this.items = defaultIfNull(items, null);
	}

	public List<T> getData() {
		return items.getList();
	}

	public void setData(List<T> data) {
		this.items = SliceableListEx.of(data);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(DateTime activeDate) {
		this.activeDate = activeDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getDraft() {
		return isDraft;
	}

	public void setDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	@JsonIgnore
	public <E> CuratedListEx<T> copyFrom(CuratedListEx<E> curatedList) {
		if (curatedList == null) return this;

		this.setDescription(curatedList.getDescription());
		this.setActiveDate(curatedList.getActiveDate());
		this.setDisplayName(curatedList.getDisplayName());
		this.setDraft(curatedList.getDraft());
		this.setId(curatedList.getId());
		this.setName(curatedList.getName());

		return this;
	}
}
