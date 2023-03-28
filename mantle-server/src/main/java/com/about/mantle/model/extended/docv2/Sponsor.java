package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Sponsor implements Serializable {
	private static final long serialVersionUID = 1L;

	private ImageEx sponsorImage;
	private String pixelTrackingUrl;
	private String description;
	private String url;
	private String title;
	private String id;
	private String brandColor;
	private SliceableListEx<TrackingCode> trackingCodes;

	public String getBrandColor() {
		return brandColor;
	}

	public void setBrandColor(String brandColor) {
		this.brandColor = brandColor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ImageEx getSponsorImage() {
		return sponsorImage;
	}

	public void setSponsorImage(ImageEx sponsorshipImage) {
		this.sponsorImage = sponsorshipImage;
	}

	public String getPixelTrackingUrl() {
		return pixelTrackingUrl;
	}

	public void setPixelTrackingUrl(String pixelTrackingUrl) {
		this.pixelTrackingUrl = pixelTrackingUrl;
	}

	@JsonProperty("trackingCode")
	public SliceableListEx<TrackingCode> getTrackingCodes() {
		return trackingCodes;
	}

	@JsonProperty("trackingCode")
	public void setTrackingCodes(SliceableListEx<TrackingCode> trackingCodes) {
		this.trackingCodes = trackingCodes;
	}

	@JsonIgnore
	public List<String> getTrustedTrackingCodes() {
		List<String> answer = Collections.emptyList();
		if (trackingCodes != null) {
			answer = trackingCodes.getList().stream().filter(t -> t.getTrusted())
			                      .map(t -> t.getCode()).collect(Collectors.toList());
		}
		return answer;
	}

	@JsonIgnore
	public List<String> getUntrustedTrackingCodes() {
		List<String> answer = Collections.emptyList();
		if (trackingCodes != null) {
			answer = trackingCodes.getList().stream().filter(t -> !t.getTrusted())
			                      .map(t -> t.getCode()).collect(Collectors.toList());
		}
		return answer;
	}

	@JsonIgnore
	public boolean hasUntrustedTrackingCodes() {
		return trackingCodes != null && trackingCodes.getList().stream().anyMatch(t -> !t.getTrusted());
	}

	public static class TrackingCode implements Serializable {
		private static final long serialVersionUID = 1L;
		private boolean trusted = false;
		private String code;

		public Boolean getTrusted() {
			return trusted;
		}

		public void setTrusted(Boolean trusted) {
			this.trusted = Boolean.TRUE.equals(trusted);
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
