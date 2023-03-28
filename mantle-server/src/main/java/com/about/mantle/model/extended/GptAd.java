package com.about.mantle.model.extended;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder=GptAd.Builder.class)
public class GptAd {
	private final String id;
	// Deprecated. Use sizesList
	@Deprecated
	private final String sizes;
	// TODO: Rename back to sizes in 3.13 as a breaking change.
	private final List<?> sizesList;
	private final String type;
	private final String pos;
	private final Integer priority;
	private final Boolean isDynamic;
	private final Integer timedRefresh;
	private final String refreshAfterSlotRenderedElement;
	private final Boolean timeoutRefreshOnceOnly;
	private final Boolean rtb;
	private final Boolean waitForThirdParty;
	private final Map<String, Object> targeting;
	private final AuctionFloorInfo auctionFloorInfo;

	private GptAd(Builder builder) {
		this.id = builder.id;
		this.type = builder.type;
		// @deprecated. Use sizesList
		this.sizes = builder.sizes;
		// TODO: Rename back to sizes in 3.13 as a breaking change.
		this.sizesList = builder.sizesList;
		this.pos = builder.pos;
		this.priority = builder.priority;
		this.isDynamic = builder.isDynamic;
		this.timedRefresh = builder.timedRefresh;
		this.refreshAfterSlotRenderedElement = builder.refreshAfterSlotRenderedElement;
		this.timeoutRefreshOnceOnly = builder.timeoutRefreshOnceOnly;
		this.rtb = builder.rtb;
		this.waitForThirdParty = builder.waitForThirdParty;
		this.targeting = builder.targeting;
		this.auctionFloorInfo = builder.auctionFloorInfo;
	}

	public String getId() {
		return id;
	}

	@Deprecated // Use getSizesList
	public String getSizes() {
		return sizes;
	}

	// TODO: Rename back to sizes in 3.13 as a breaking change.
	public List<?> getSizesList() {
		return sizesList;
	}

	public String getType() {
		return type;
	}

	public String getPos() {
		return pos;
	}

	public Integer getPriority() {
		return priority;
	}

	public Boolean getIsDynamic() {
		return isDynamic;
	}

	public Integer getTimedRefresh() {
		return timedRefresh;
	}

	public String getRefreshAfterSlotRenderedElement() {
		return refreshAfterSlotRenderedElement;
	}

	public Boolean getTimeoutRefreshOnceOnly() {
		return timeoutRefreshOnceOnly;
	}

	public Boolean getRtb() {
		return rtb;
	}

	public Boolean getWaitForThirdParty() {
		return waitForThirdParty;
	}

	public Map<String, Object> getTargeting() {
		return targeting;
	}

	public AuctionFloorInfo getAuctionFloorInfo() {
		return auctionFloorInfo;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		private String id;
		@Deprecated // Use sizesLis
		private String sizes;
		// TODO: Rename back to sizes in 3.13 as a breaking change.
		private List<?> sizesList;
		private String type;
		private String pos;
		private Integer priority;
		private Boolean isDynamic;
		private Integer timedRefresh;
		private String refreshAfterSlotRenderedElement;
		private Boolean timeoutRefreshOnceOnly;
		private Boolean rtb;
		private Boolean waitForThirdParty;
		private Map<String, Object> targeting;
		private AuctionFloorInfo auctionFloorInfo;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		@Deprecated // Use getSizesList
		public Builder sizes(String sizes) {
			this.sizes = sizes;
			return this;
		}

		// TODO: Rename back to sizes in 3.13 as a breaking change.
		public Builder sizesList(List<?> sizesList) {
			this.sizesList = sizesList;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder pos(String pos) {
			this.pos = pos;
			return this;
		}

		public Builder priority(Integer priority) {
			this.priority = priority;
			return this;
		}

		public Builder isDynamic(Boolean isDynamic) {
			this.isDynamic = isDynamic;
			return this;
		}

		public Builder timedRefresh(Integer timedRefresh) {
			this.timedRefresh = timedRefresh;
			return this;
		}

		public Builder refreshAfterSlotRenderedElement(String refreshAfterSlotRenderedElement) {
			this.refreshAfterSlotRenderedElement = refreshAfterSlotRenderedElement;
			return this;
		}

		public Builder timeoutRefreshOnceOnly(Boolean timeoutRefreshOnceOnly) {
			this.timeoutRefreshOnceOnly = timeoutRefreshOnceOnly;
			return this;
		}

		public Builder rtb(Boolean rtb) {
			this.rtb = rtb;
			return this;
		}

		public Builder waitForThirdParty(Boolean waitForThirdParty) {
			this.waitForThirdParty = waitForThirdParty;
			return this;
		}

		public Builder targeting(Map<String, Object> targeting) {
			this.targeting = targeting;
			return this;
		}

		public Builder auctionFloorInfo(AuctionFloorInfo auctionFloorInfo) {
			this.auctionFloorInfo = auctionFloorInfo;
			return this;
		}

		public GptAd build() {
			return new GptAd(this);
		}
	}
}