package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;

public class ListDocumentEx extends BaseDocumentEx implements Linkboxed {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<ItemEx> items = SliceableListEx.emptyList();
	private UserFeedback userFeedback;
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private SliceableListEx<String> labels = SliceableListEx.emptyList();
	private ListOptions listOptions;

	private List<ImageEx> images = Collections.emptyList();
	private List<LinkboxLinkEx> linkboxList = Collections.emptyList();

	public ListDocumentEx() throws GlobeInvalidTaskParameterException {
		super();
	}

	@Override
	@JsonIgnore
	public int getCharacterCount() throws GlobeInvalidTaskParameterException {

		int length = 0;
		// Count characters of the document description
		length += length(getDescription());
		// Count characters of the description and the link's text of each item
		length += getItems().getList().stream().mapToInt(item -> {
			return length(item.getDescription()) + (item.getLink() != null ? length(item.getLink().getText()) : 0);
		}).sum();

		return length;
	}

	@Override
	@JsonIgnore
	public List<LinkboxLinkEx> getLinkboxesAsList() {
		return linkboxList;
	}

	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return images.size();
	}

	public SliceableListEx<ItemEx> getItems() {
		return items;
	}

	public void setItems(SliceableListEx<ItemEx> items) {
		this.items = SliceableListEx.emptyIfNull(items);

		ImmutableList.Builder<ImageEx> builder = ImmutableList.builder();
		for (ItemEx item : getItems().getList()) {
			if (item != null && isNotBlank(item.getImage().getUrl())) {
				builder.add(item.getImage());
			}
		}
		this.images = builder.build();
	}

	public UserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(UserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	@Override
	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
		this.linkboxList = transformLinkboxes();
	}

	public SliceableListEx<String> getLabels() {
		return labels;
	}

	public void setLabels(SliceableListEx<String> labels) {
		this.labels = SliceableListEx.emptyIfNull(labels);
	}

	public ListOptions getListOptions() {
		return listOptions;
	}

	public void setListOptions(ListOptions listOptions) {
		this.listOptions = listOptions;
	}

	@Override
	public boolean hasCommerceInfo() {
		return items.getList().stream()
				.filter(item -> item.getCommerceInfo() != null && item.getCommerceInfo().getTotalSize() > 0).findAny()
				.isPresent();
	}
	
	public static class ListOptions implements Serializable {
		private static final long serialVersionUID = 1L;

		private String documentType;
		private String markerType;
		private String sortOrder;
		private Boolean supportSearch;

		public String getDocumentType() {
			return documentType;
		}
		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}
		
		public String getMarkerType() {
			return markerType;
		}
		public void setMarkerType(String markerType) {
			this.markerType = markerType;
		}
		
		public String getSortOrder() {
			return sortOrder;
		}
		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}
		
		public Boolean getSupportSearch() {
			return supportSearch;
		}
		public void setSupportSearch(Boolean supportSearch) {
			this.supportSearch = supportSearch;
		}
	}
	
	public static class UserFeedback implements Serializable {
		private static final long serialVersionUID = 1L;

		private LinkEx link;
		private String annotation;

		public LinkEx getLink() {
			return link;
		}

		public void setLink(LinkEx link) {
			this.link = link;
		}

		public String getAnnotation() {
			return annotation;
		}

		public void setAnnotation(String annotation) {
			this.annotation = annotation;
		}
	}
}
