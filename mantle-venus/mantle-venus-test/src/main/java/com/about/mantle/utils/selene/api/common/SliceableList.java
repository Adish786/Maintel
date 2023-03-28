package com.about.mantle.utils.selene.api.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SliceableList<T> {
	private List<T> list;
	private int totalSize;
}
