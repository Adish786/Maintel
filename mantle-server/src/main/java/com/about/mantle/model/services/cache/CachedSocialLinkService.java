package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.SocialLinkService;

public class CachedSocialLinkService implements SocialLinkService {

	private final SocialLinkService socialLinkService;
	private final CacheTemplate<SocialLinkResponse> socialLinksCache;

	public CachedSocialLinkService(SocialLinkService socialLinkService, CacheTemplate<SocialLinkResponse> socialLinksCache) {
		this.socialLinkService = socialLinkService;
		this.socialLinksCache = socialLinksCache;
	}

	@Override
	public SocialLinkResponse getSocialLinks(String url, SocialNetwork network, DeviceCategory device) {
		CacheKey key = new CacheKey(url, network, device);

		return socialLinksCache.get(key, () -> socialLinkService.getSocialLinks(url, network, device));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final String url;
		private final SocialNetwork network;
		private final DeviceCategory device;

		public CacheKey(String url, SocialNetwork network, DeviceCategory device) {
			this.url = url;
			this.network = network;
			this.device = device;
		}

		@Override
		public String getUniqueKey() {
			return RedisCacheKey.objectsToUniqueKey(null,url,network,device).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + EnumHasher.INSTANCE.hash(device);
			result = prime * result + EnumHasher.INSTANCE.hash(network);
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CacheKey other = (CacheKey) obj;
			if (device != other.device) return false;
			if (network != other.network) return false;
			if (url == null) {
				if (other.url != null) return false;
			} else if (!url.equals(other.url)) return false;
			return true;
		}
	}
}
