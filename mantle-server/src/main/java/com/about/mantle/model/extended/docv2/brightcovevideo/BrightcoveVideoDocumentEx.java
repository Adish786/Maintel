package com.about.mantle.model.extended.docv2.brightcovevideo;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;

import java.util.List;

public class BrightcoveVideoDocumentEx extends BaseDocumentEx {
	private static final long serialVersionUID = 1L;

	private String videoId;
	private String accountId;
	private String referenceId;
	private String state;
	private String name;
	private String longDescription;
	private SliceableListEx<String> tags;
	private Long duration;
	private String economics;
	private SliceableListEx<CuePointEx> cuePoints;
	private Boolean textTracks;
	private ScheduleEx schedule;
	private String franchise;
	private String syndicate;
	private String season;
	private String episode;
	private String nativePlayer;
	private String contentLanguage;
	private String thumbnailUrl;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public SliceableListEx<String> getTags() {
		return tags;
	}

	public void setTags(SliceableListEx<String> tags) {
		this.tags = tags;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getEconomics() {
		return economics;
	}

	public void setEconomics(String economics) {
		this.economics = economics;
	}

	public SliceableListEx<CuePointEx> getCuePoints() {
		return cuePoints;
	}

	public void setCuePoints(SliceableListEx<CuePointEx> cuePoints) {
		this.cuePoints = cuePoints;
	}

	public Boolean getTextTracks() {
		return textTracks;
	}

	public void setTextTracks(Boolean textTracks) {
		this.textTracks = textTracks;
	}

	public ScheduleEx getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduleEx schedule) {
		this.schedule = schedule;
	}

	public String getFranchise() {
		return franchise;
	}

	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}

	public String getSyndicate() {
		return syndicate;
	}

	public void setSyndicate(String syndicate) {
		this.syndicate = syndicate;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	public String getNativePlayer() {
		return nativePlayer;
	}

	public void setNativePlayer(String nativePlayer) {
		this.nativePlayer = nativePlayer;
	}

	public String getContentLanguage() {
		return contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	private class VideoMetaData {

		private String accountId;
		private SliceableListEx<CuePointEx> cuePoints;
		private Dates dates;
		private String description;
		private DocumentState documentState;
		private Long duration;
		private String economics;
		private String franchise;
		private String longDescription;
		private String name;
		private String nativePlayer;
		private ScheduleEx schedule;
		private String syndicate;
		private List<String> tags;
		private Boolean textTracks;
		private String videoId;
		private Vertical vertical;

		private class Dates {
			private long created;
			private long updated;
			private long firstPublished;

			private Dates() {
				if(BrightcoveVideoDocumentEx.super.getDates() != null) {
					this.created = BrightcoveVideoDocumentEx.super.getDates().getCreated() != null ? BrightcoveVideoDocumentEx.super.getDates().getCreated().getMillis() : null;
					this.updated = BrightcoveVideoDocumentEx.super.getDates().getUpdated() != null ? BrightcoveVideoDocumentEx.super.getDates().getUpdated().getMillis() : null;
					this.firstPublished = BrightcoveVideoDocumentEx.super.getDates().getFirstPublished() != null ? BrightcoveVideoDocumentEx.super.getDates().getFirstPublished().getMillis() : null;
				}
			}
		}

		private class DocumentState {
			private State state;

			private DocumentState() {
				if(BrightcoveVideoDocumentEx.super.getDocumentState() != null) {
					this.state = BrightcoveVideoDocumentEx.super.getDocumentState().getState();
				}
			}
		}

		public VideoMetaData() {
			this.accountId = BrightcoveVideoDocumentEx.this.accountId;
			this.cuePoints = BrightcoveVideoDocumentEx.this.cuePoints;
			this.dates = new BrightcoveVideoDocumentEx.VideoMetaData.Dates();
			this.description = BrightcoveVideoDocumentEx.super.getDescription();
			this.documentState = new BrightcoveVideoDocumentEx.VideoMetaData.DocumentState();
			this.duration = BrightcoveVideoDocumentEx.this.duration;
			this.economics = BrightcoveVideoDocumentEx.this.economics;
			this.franchise = BrightcoveVideoDocumentEx.this.franchise;
			this.longDescription = BrightcoveVideoDocumentEx.this.longDescription;
			this.name = BrightcoveVideoDocumentEx.this.name;
			this.nativePlayer = BrightcoveVideoDocumentEx.this.nativePlayer;
			this.schedule = BrightcoveVideoDocumentEx.this.schedule;
			this.syndicate = BrightcoveVideoDocumentEx.this.syndicate;
			this.tags = BrightcoveVideoDocumentEx.this.tags != null ? BrightcoveVideoDocumentEx.this.tags.getList() : null;
			this.textTracks = BrightcoveVideoDocumentEx.this.textTracks;
			this.vertical = BrightcoveVideoDocumentEx.super.getVertical();
			this.videoId = BrightcoveVideoDocumentEx.this.videoId;
		}

	}

	/**
	 * @return Json string used to populate data-metadata attribute on frontend
	 */
	@JsonIgnore
	public String getVideoMetaData() {
		return new Gson().toJson(new BrightcoveVideoDocumentEx.VideoMetaData());
	}

}
