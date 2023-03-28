package com.about.mantle.model.extended;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.tasks.SocialTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

public class AuthorEx extends BaseAuthor implements Serializable {

	private static final long serialVersionUID = 1L;

	private String status;
	private String displayNamePossessive;
	@Deprecated
	private boolean contributingWriter;
	private String language;
	@Deprecated
	private DateTime promotionDate;
	@Deprecated
	private DateTime liveDate;
	@Deprecated
	private DateTime terminationDate;
	@Deprecated
	private Integer numberOfExperts;
	private List<SocialPresence> socialPresence = Collections.emptyList();
	private Map<String, String> socialPresenceMap = ImmutableMap.of();
	private String imageUrl;
	private ImageEx image = ImageEx.EMPTY;
	private String shortBio;
	// note: the activity, audit, createdBy, and createdDate attributes are present on the V2 Author object
	// but are being left off AuthorEx for now to avoid caching extra data as they currently aren't being used

	@Override
	public String toString() {
		return "AuthorEx [bioUrl=" + this.getBioUrl() + ", id=" + this.getId() + "]";
	}

	@Override
	public String getType() {
		return BaseAuthor.AUTHOR;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDisplayNamePossessive() {
		return displayNamePossessive;
	}

	public void setDisplayNamePossessive(String displayNamePossessive) {
		this.displayNamePossessive = displayNamePossessive;
	}

	@Deprecated
	public boolean isContributingWriter() {
		return contributingWriter;
	}

	@Deprecated
	public void setContributingWriter(boolean contributingWriter) {
		this.contributingWriter = contributingWriter;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Deprecated
	public Integer getNumberOfExperts() {
		return numberOfExperts;
	}

	@Deprecated
	public void setNumberOfExperts(Integer numberOfExperts) {
		this.numberOfExperts = numberOfExperts;
	}

	@Deprecated
	public DateTime getTerminationDate() {
		return terminationDate;
	}

	@Deprecated
	public void setTerminationDate(DateTime terminationDate) {
		this.terminationDate = terminationDate;
	}

	@Deprecated
	public DateTime getPromotionDate() {
		return promotionDate;
	}

	@Deprecated
	public void setPromotionDate(DateTime promotionDate) {
		this.promotionDate = promotionDate;
	}

	@Deprecated
	public DateTime getLiveDate() {
		return liveDate;
	}

	@Deprecated
	public void setLiveDate(DateTime liveDate) {
		this.liveDate = liveDate;
	}

	public List<SocialPresence> getSocialPresence() {
		return socialPresence;
	}

	public void setSocialPresence(List<SocialPresence> socialPresence) {
		this.socialPresence = emptyIfNull(socialPresence);
		Function<SocialPresence,String> getNetwork = p -> SocialTask.getSocialNetwork(p.getNetwork());
		Predicate<SocialPresence> onNetworkNotNull = p -> getNetwork.apply(p) != null;
		Function<SocialPresence, String> getSocialNetworkUrl = p -> p.getUrl().contains("://") ? p.getUrl() : "https://" + p.getUrl();
		this.socialPresenceMap = this.socialPresence.stream()
				.filter(onNetworkNotNull)
				.collect(Collectors.toMap(getNetwork, getSocialNetworkUrl));
	}

	@JsonIgnore
	public Map<String, String> getSocialPresenceMap() {
		return socialPresenceMap;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		if (image == null || image.getWidth(0) <= 0 || image.getHeight(0) <= 0) {
			image = ImageEx.EMPTY;
		}
		this.image = image;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}

	public static class SocialPresence implements Serializable {
		private static final long serialVersionUID = 1L;

		// Note: in selene this is an enum, not a string.
		// We are keeping it as a string for further flexibility if selene adds new
		// network enum values on their end in the future.
		private String network;
		private String url;

		public String getNetwork() {
			return network;
		}
		public void setNetwork(String network) {
			if (network != null) {
				network = network.toLowerCase();
			}
			this.network = network;
		}

		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}

}