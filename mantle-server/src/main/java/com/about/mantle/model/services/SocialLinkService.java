package com.about.mantle.model.services;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.hippodrome.models.response.BaseResponse;

public interface SocialLinkService {

	SocialLinkResponse getSocialLinks(String url, SocialNetwork network, DeviceCategory device);

	public enum SocialNetwork {
		FACEBOOK,
		TWITTER,
		PINTEREST,
		STUMBLEUPON,
		REDDIT,
		LINKEDIN,
		TUMBLR,
		EMAILSHARE,
		SMS,
		WHATSAPP,
		FLIPBOARD,
		INSTAGRAM,
		WEBSITE
	}

	public enum DeviceCategory {
		PERSONAL_COMPUTER,
		SMARTPHONE,
		TABLET;
	}

	public static class SocialLink implements Serializable {

		private static final long serialVersionUID = 1L;

		private DeviceCategory device;
		private SocialNetwork network;
		private String url;

		public SocialNetwork getNetwork() {
			return network;
		}

		public void setNetwork(SocialNetwork network) {
			this.network = network;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public DeviceCategory getDevice() {
			return device;
		}

		public void setDevice(DeviceCategory device) {
			this.device = device;
		}

		public void setNetworkAsString(String network) {
			this.network = SocialNetwork.valueOf(network.toUpperCase());
		}

		@Override
		public int hashCode() {
			HashCodeBuilder builder = new HashCodeBuilder();
			builder.append(network);
			builder.append(device);
			return builder.build();
		}

		@Override
		public boolean equals(Object o) {
			if ( o == null) {
				return false;
			}

			if (!(o instanceof SocialLink)) {
				return false;
			}

			SocialLink other = (SocialLink) o;

			return other.device.equals(this.device) &&
					other.network.equals(this.network);
		}
	}

	public static class SocialLinkResponse extends BaseResponse<List<SocialLink>> implements Serializable {
		private static final long serialVersionUID = 1L;
	}
}
