package com.about.mantle.utils.selene.document.api.listsc;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListOptions {
	private String documentType;
	private String markerType;
	private String sortOrder;
	private Boolean supportSearch;
}
