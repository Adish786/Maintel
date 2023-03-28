package com.about.mantle.model.social;

import java.io.Serializable;
import java.util.List;

import com.about.mantle.model.services.SocialLinkService.SocialNetwork;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkShare implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<Network> visible;
	private List<Network> extended;
	
	public List<Network> getVisible() {
		return visible;
	}

	public void setVisible(List<Network> visible) {
		this.visible = visible;
	}

	public List<Network> getExtended() {
		return extended;
	}
	
	public void setExtended(List<Network> extended) {
		this.extended = extended;
	}

	public static class Network{
		private final SocialNetwork socialNetwork;
		private final String longShare;
		private final String shortShare;

		@JsonCreator
		public Network(@JsonProperty("socialNetwork") SocialNetwork socialNetwork, 
				@JsonProperty("longShare") String longShare,
				@JsonProperty("shortShare") String shortShare){
			this.socialNetwork=socialNetwork;
			this.longShare=longShare;
			this.shortShare=shortShare;
		}
		
		public SocialNetwork getSocialNetwork() {
			return socialNetwork;
		}
		public String getLongShare() {
			return longShare;
		}
		public String getShortShare() {
			return shortShare;
		}
	}
}
