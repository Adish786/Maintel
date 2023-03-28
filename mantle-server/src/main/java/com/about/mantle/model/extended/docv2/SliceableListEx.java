package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;

public class SliceableListEx<T> implements Serializable, Iterable<T> {

	private static final long serialVersionUID = 1L;

	private static final SliceableListEx<?> EMPTY_LIST = new SliceableListEx<Object>() {

		private static final long serialVersionUID = 1L;

		@Override
		public void setLimit(Integer limit) {
		}

		@Override
		public void setOffset(Integer offset) {
		}

		@Override
		public void setTotalSize(Integer totalSize) {
		}

		@Override
		public void setList(List<Object> list) {
		}
	};

	private Integer limit;
	private Integer offset;
	private Integer totalSize = 0;
	private List<T> list = ImmutableList.<T> of();

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = ListUtils.emptyIfNull(list);
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public static <T> SliceableListEx<T> emptyList() {
		return (SliceableListEx<T>) EMPTY_LIST;
	}

	public static <T> SliceableListEx<T> emptyIfNull(SliceableListEx<T> list) {
		return list != null ? list : emptyList();
	}
	
	public static <T> boolean isEmpty(SliceableListEx<T> list) {
		return list == null || list.isEmpty();
	}

	@SafeVarargs
	public static <T> SliceableListEx<T> of(T... ts) {
		if (ts == null) return emptyList();

		SliceableListEx<T> sliceableList = new SliceableListEx<>();

		sliceableList.setLimit(ts.length);
		sliceableList.setOffset(0);
		sliceableList.setTotalSize(ts.length);
		sliceableList.setList(ImmutableList.copyOf(ts));

		return sliceableList;
	}

	public static <T> SliceableListEx<T> of(List<T> list) {
		if (list == null) return emptyList();

		SliceableListEx<T> sliceableList = new SliceableListEx<>();

		sliceableList.setLimit(list.size());
		sliceableList.setOffset(0);
		sliceableList.setTotalSize(list.size());
		sliceableList.setList(ImmutableList.copyOf(list));

		return sliceableList;
	}

	@Override
	public Iterator<T> iterator() {
		return getList().iterator();
	}

	public Stream<T> stream() {
		return getList().stream();
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("SliceableListEx{");
		sb.append("limit=").append(limit);
		sb.append(", offset=").append(offset);
		sb.append(", totalSize=").append(totalSize);
		sb.append(", list=").append(list);
		sb.append('}');
		return sb.toString();
	}
}
