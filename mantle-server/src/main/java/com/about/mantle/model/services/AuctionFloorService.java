package com.about.mantle.model.services;

import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfo;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.extended.docv2.SliceableListEx;

/**
 * Used to provide floor values for ad slots.
 */
public interface AuctionFloorService {

	public static final String SELENE_AUCTION_FLOOR_MAPPING_PATH = "/auction/floor/search";

	// returns null if exempt from auction floor pricing
	AuctionFloorInfo getAuctionFloorInfo(AuctionFloorRequestContext ctx);
	public static final SliceableListEx<AuctionFloorInfoListItem> DEFAULT_AUCTION_FLOOR_INFO_LIST = defaultAuctionFloorInfoList();

	SliceableListEx<AuctionFloorInfoListItem> getAuctionFloorInfoList(AuctionFloorListRequestContext ctx, AuctionFloorConfig config);
	private static SliceableListEx<AuctionFloorInfoListItem> defaultAuctionFloorInfoList() {
		SliceableListEx<AuctionFloorInfoListItem> defaultAuctionFloorInfoList = new SliceableListEx();
		return defaultAuctionFloorInfoList;
	}
	public static class AuctionFloorRequestContext {

		private String deviceCategory;
		private String geoLocation;
		private String operatingSystem;
		private String slot;
		private String tier;

		public String getDeviceCategory() {
			return deviceCategory;
		}

		public String getGeoLocation() {
			return geoLocation;
		}

		public String getOperatingSystem() {
			return operatingSystem;
		}

		public String getSlot() {
			return slot;
		}

		public String getTier() {
			return tier;
		}

		@Override
		public String toString() {
			return new StringBuilder("AuctionFloorContext [")
					.append("deviceCategory=").append(deviceCategory)
					.append(", geoLocation=").append(geoLocation)
					.append(", operatingSystem=").append(operatingSystem)
					.append(", slot=").append(slot)
					.append(", tier=").append(tier)
					.append("]").toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((deviceCategory == null) ? 0 : deviceCategory.hashCode());
			result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
			result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
			result = prime * result + ((slot == null) ? 0 : slot.hashCode());
			result = prime * result + ((tier == null) ? 0 : tier.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AuctionFloorRequestContext other = (AuctionFloorRequestContext) obj;
			if (deviceCategory == null) {
				if (other.deviceCategory != null)
					return false;
			} else if (!deviceCategory.equals(other.deviceCategory))
				return false;
			if (geoLocation == null) {
				if (other.geoLocation != null)
					return false;
			} else if (!geoLocation.equals(other.geoLocation))
				return false;
			if (operatingSystem == null) {
				if (other.operatingSystem != null)
					return false;
			} else if (!operatingSystem.equals(other.operatingSystem))
				return false;
			if (slot == null) {
				if (other.slot != null)
					return false;
			} else if (!slot.equals(other.slot))
				return false;
			if (tier == null) {
				if (other.tier != null)
					return false;
			} else if (!tier.equals(other.tier))
				return false;
			return true;
		}

		private AuctionFloorRequestContext(Builder b) {
			this.deviceCategory = b.deviceCategory;
			this.geoLocation = b.geoLocation;
			this.operatingSystem = b.operatingSystem;
			this.slot = b.slot;
			this.tier = b.tier;
		}

		public static class Builder {

			private String deviceCategory;
			private String geoLocation;
			private String operatingSystem;
			private String slot;
			private String tier;

			public Builder deviceCategory(String deviceCategory) {
				this.deviceCategory = deviceCategory;
				return this;
			}

			public Builder geoLocation(String geoLocation) {
				this.geoLocation = geoLocation;
				return this;
			}

			public Builder operatingSystem(String operatingSystem) {
				this.operatingSystem = operatingSystem;
				return this;
			}

			public Builder slot(String slot) {
				this.slot = slot;
				return this;
			}

			public Builder tier(String tier) {
				this.tier = tier;
				return this;
			}

			public AuctionFloorRequestContext build() {
				return new AuctionFloorRequestContext(this);
			}

		}

	}


	public static class AuctionFloorListRequestContext {

		private String deviceCategory;
		private String geoLocation;
		private String operatingSystem;
		private String tier;
		private String vertical;

		public String getDeviceCategory() {
			return deviceCategory;
		}

		public String getGeoLocation() {
			return geoLocation;
		}

		public String getOperatingSystem() {
			return operatingSystem;
		}

		public String getTier() {
			return tier;
		}

		public String getVertical() {
			return vertical;
		}

		@Override
		public String toString() {
			return new StringBuilder("AuctionFloorContext [")
					.append("deviceCategory=").append(deviceCategory)
					.append(", geoLocation=").append(geoLocation)
					.append(", operatingSystem=").append(operatingSystem)
					.append(", tier=").append(tier)
					.append(", vertical=").append(vertical)
					.append("]").toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((deviceCategory == null) ? 0 : deviceCategory.hashCode());
			result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
			result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
			result = prime * result + ((tier == null) ? 0 : tier.hashCode());
			result = prime * result + ((vertical == null) ? 0 : vertical.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AuctionFloorListRequestContext other = (AuctionFloorListRequestContext) obj;
			if (deviceCategory == null) {
				if (other.deviceCategory != null)
					return false;
			} else if (!deviceCategory.equals(other.deviceCategory))
				return false;
			if (geoLocation == null) {
				if (other.geoLocation != null)
					return false;
			} else if (!geoLocation.equals(other.geoLocation))
				return false;
			if (operatingSystem == null) {
				if (other.operatingSystem != null)
					return false;
			} else if (!operatingSystem.equals(other.operatingSystem))
				return false;
			if (tier == null) {
				if (other.tier != null)
					return false;
			} else if (!tier.equals(other.tier))
				return false;
			if (vertical == null) {
				if (other.vertical != null)
					return false;
			} else if (!vertical.equals(other.vertical))
				return false;
			return true;
		}

		private AuctionFloorListRequestContext(Builder b) {
			this.deviceCategory = b.deviceCategory;
			this.geoLocation = b.geoLocation;
			this.operatingSystem = b.operatingSystem;
			this.tier = b.tier;
			this.vertical = b.vertical;
		}

		public static class Builder {

			private String deviceCategory;
			private String geoLocation;
			private String operatingSystem;
			private String tier;
			private String vertical;

			public Builder deviceCategory(String deviceCategory) {
				this.deviceCategory = deviceCategory;
				return this;
			}

			public Builder geoLocation(String geoLocation) {
				this.geoLocation = geoLocation;
				return this;
			}

			public Builder operatingSystem(String operatingSystem) {
				this.operatingSystem = operatingSystem;
				return this;
			}

			public Builder tier(String tier) {
				this.tier = tier;
				return this;
			}

			public Builder vertical(String vertical) {
				this.vertical = vertical;
				return this;
			}

			public AuctionFloorListRequestContext build() {
				return new AuctionFloorListRequestContext(this);
			}

		}

	}

}
