package com.about.mantle.model.contentgraph;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;

/**
 * Maps to an asset (e.g. photo) and pins it to an entity (e.g. recipe).
 */
public class AssetMetadata {
	private final EntityId entityId;
	private final String contentType;
	private final String fileName;

	private AssetMetadata(Builder builder) {
		this.entityId = builder.entityId;
		this.contentType = builder.contentType;
		this.fileName = builder.fileName;
	}

	public EntityId getEntityId() {
		return entityId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "AssetMetadata [contentType=" + contentType + ", entityId=" + entityId + ", fileName=" + fileName + "]";
	}

	public static class Builder {
		private EntityId entityId;
		private String contentType;
		private String fileName;

		public Builder entityId(EntityId entityId) {
			this.entityId = entityId;
			return this;
		}

		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder fileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public AssetMetadata build() {
			if (entityId == null) {
				throw new GlobeException("entityId is not set");
			}
			if (StringUtils.isBlank(contentType)) {
				throw new GlobeException("contentType is not set");
			}
			if (StringUtils.isBlank(fileName)) {
				throw new GlobeException("fileName is not set");
			}
			return new AssetMetadata(this);
		}
	}
}
