package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.about.mantle.model.extended.docv2.Sponsor;

public class SummaryEx implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final SummaryEx EMPTY = new SummaryEx() {

		private static final long serialVersionUID = 1L;

		@Override
		public void setTitle(String title) {
		}

		@Override
		public void setHeading(String heading) {
		}

		@Override
		public void setDescription(String description) {
		}

		@Override
		public void setSocialTitle(String socialTitle) {
		}

	};

	private String title;
	private String socialTitle;
	private String heading;
	private String description;
	/**
	 * @deprecated Use {@link #sponsors} instead
	 */
	@Deprecated
	private Sponsor sponsor;
	private SliceableListEx<Sponsor> sponsors = SliceableListEx.emptyList();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSocialTitle() {
		if (StringUtils.isEmpty(socialTitle)) {
			return title;
		}
		return socialTitle;
	}

	public void setSocialTitle(String socialTitle) {
		this.socialTitle = socialTitle;
	}

	/**
	 * @deprecated Use {@link #getSponsors} instead
	 */
	@Deprecated
	public Sponsor getSponsor() {
		return sponsor;
	}

	/**
	 * @deprecated Use {@link #setSponsors} instead
	 */
	@Deprecated
	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}

	public SliceableListEx<Sponsor> getSponsors() {
		return sponsors;
	}

	public void setSponsors(SliceableListEx<Sponsor> sponsors) {
		this.sponsors = sponsors;
	}

	public static SummaryEx emptyIfNull(SummaryEx summary) {
		return summary != null ? summary : EMPTY;
	}
}
