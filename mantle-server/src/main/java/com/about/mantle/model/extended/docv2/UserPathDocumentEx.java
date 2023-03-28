package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;

public class UserPathDocumentEx extends BaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private String tabName;
	private String markerType;
	private SliceableListEx<SectionEx> sections = SliceableListEx.emptyList();
	private SliceableListEx<String> eCourses = SliceableListEx.emptyList();
	private List<ImageEx> images = Collections.emptyList();

	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return images.size();
	}

	@Override
	public String getSubheading() {
		return getDescription();
	}

	public SliceableListEx<SectionEx> getSections() {
		return sections;
	}

	public void setSections(SliceableListEx<SectionEx> sections) {
		this.sections = SliceableListEx.emptyIfNull(sections);

		ImmutableList.Builder<ImageEx> builder = ImmutableList.builder();

		for (SectionEx section : this.sections.getList()) {
			if (section != null && isNotBlank(section.getImage().getUrl())) {
				builder.add(section.getImage());
			}
		}

		this.images = builder.build();
	}

	public SliceableListEx<String> geteCourses() {
		return eCourses;
	}

	public void seteCourses(SliceableListEx<String> eCourses) {
		this.eCourses = SliceableListEx.emptyIfNull(eCourses);
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

}
