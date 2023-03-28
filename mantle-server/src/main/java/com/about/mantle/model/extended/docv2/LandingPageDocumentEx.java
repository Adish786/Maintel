package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class LandingPageDocumentEx extends BaseDocumentEx {

	private static final long serialVersionUID = 1L;
	private SpotlightUnitEx spotlightUnit;
	private MustReadsEx mustReads;

	@Override
	public int calculateImageCount() {
		return (spotlightUnit != null && isNotBlank(spotlightUnit.getImage().getUrl())) ? 1 : 0;
	}

	public SpotlightUnitEx getSpotlightUnit() {
		return spotlightUnit;
	}

	public void setSpotlightUnit(SpotlightUnitEx spotlightUnit) {
		this.spotlightUnit = spotlightUnit;
	}

	public MustReadsEx getMustReads() {
		return mustReads;
	}

	public void setMustReads(MustReadsEx mustReads) {
		this.mustReads = mustReads;
	}
}
