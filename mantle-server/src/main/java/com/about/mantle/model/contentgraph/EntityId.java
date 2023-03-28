package com.about.mantle.model.contentgraph;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;

/**
 * Maps to a content graph "document", e.g.
 *   provider: cms, id: onecms_posts_alrcom_279754
 */
public class EntityId {
	private final String provider;
	private final String id;

	private EntityId(Builder builder) {
		this.provider = builder.provider;
		this.id = builder.id;
	}

	public String getProvider() {
		return provider;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "EntityId [id=" + id + ", provider=" + provider + "]";
	}

	public static class Builder {
		private String provider;
		private String id;

		public Builder provider(String provider) {
			this.provider = provider;
			return this;
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public EntityId build() {
			if (StringUtils.isBlank(provider)) {
				throw new GlobeException("provider is not set");
			}
			if (StringUtils.isBlank(id)) {
				throw new GlobeException("id is not set");
			}
			return new EntityId(this);
		}
	}
}
