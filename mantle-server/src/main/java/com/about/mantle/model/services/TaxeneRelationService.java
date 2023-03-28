package com.about.mantle.model.services;

import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.google.common.collect.ImmutableSet;

public interface TaxeneRelationService {

	public TaxeneNodeEx traverse(TaxeneTraverseRequestContext reqCtx);
	
	public static class TaxeneTraverseRequestContext {
		private final Long docId;
		private final Direction direction;
		private final TraverseStrategy traverseStrategy;
		private final Set<String> relationships;
		private final boolean displayDefaultParentRelationship;
		private final NodeType nodeType;
		private final Integer limit;
		private final boolean includeDocumentSummaries;
		private final String projection;
		private final Integer maxDocPopulation;
		private final boolean activeOnly;
		private final boolean includeConfigs;
		private final boolean isPreview; // used only for cache key generation

		private static final int MAX_LIMIT = 1000;

		private TaxeneTraverseRequestContext(Builder builder) {

			if (builder.docId == null) {
				throw new IllegalArgumentException("docId not provided.");
			} else {
				docId = builder.docId;
			}

			if (builder.direction == null) {
				throw new IllegalArgumentException("direction not provided.");
			} else {
				direction = builder.direction;
			}

			traverseStrategy = (builder.traverseStrategy == null) ? TraverseStrategy.BREADTH_FIRST
					: builder.traverseStrategy;

			if (builder.relationships == null) {
				relationships = ImmutableSet.of();
			} else {
				relationships = builder.relationships;
			}

			displayDefaultParentRelationship = BooleanUtils.isTrue(builder.displayDefaultParentRelationship);

			if (direction == Direction.OUT && NodeType.LEAF_NODE == builder.nodeType) {
				throw new IllegalArgumentException("for OUT request, nodeType cannot be LEAF.");
			} else {
				nodeType = builder.nodeType;
			}

			limit = (builder.limit == null) ? MAX_LIMIT : builder.limit;
			
			includeDocumentSummaries = (builder.includeDocumentSummaries == null) ? false
					: builder.includeDocumentSummaries;
			projection = builder.projection;
			maxDocPopulation = builder.maxDocPopulation;
			activeOnly = (builder.activeOnly == null) ? true : builder.activeOnly;
			includeConfigs = (builder.includeConfigs == null) ? false : builder.includeConfigs;
			isPreview = (builder.isPreview == null) ? false : builder.isPreview;
		}

		public Long getDocId() {
			return docId;
		}

		public Direction getDirection() {
			return direction;
		}

		public TraverseStrategy getTraverseStrategy() {
			return traverseStrategy;
		}

		public Set<String> getRelationships() {
			return relationships;
		}

		public boolean getDisplayDefaultParentRelationship() {
			return displayDefaultParentRelationship;
		}

		public NodeType getNodeType() {
			return nodeType;
		}

		public Integer getLimit() {
			return limit;
		}
		
		public Boolean getIncludeDocumentSummaries() {
			return includeDocumentSummaries;
		}

		public String getProjection() {
			return projection;
		}

		public Integer getMaxDocPopulation() {
			return maxDocPopulation;
		}

		public Boolean getActiveOnly() {
			return activeOnly;
		}

		public Boolean getIncludeConfigs() {
			return includeConfigs;
		}

		public Boolean getIsPreview() {
			return isPreview;
		}

		@Override
		public String toString() {
			// @formatter:off
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("docId", docId)
					.append("direction", direction)
					.append("traverseStrategy", traverseStrategy)
					.append("relationships", relationships)
					.append("nodeType", nodeType)
					.append("limit", limit)
					.append("displayDefaultParentRelationship",displayDefaultParentRelationship)
					.append("includeDocumentSummaries", includeDocumentSummaries)
					.append("projection", projection)
					.append("maxDocPopulation", maxDocPopulation)
					.append("activeOnly", activeOnly)
					.append("includeConfigs", includeConfigs)
					.append("isPreview", isPreview)
					.appendSuper(super.toString())
					.build();
			// @formatter:on
		}

		public static class Builder {
			private Long docId;
			private Direction direction;
			private TraverseStrategy traverseStrategy;
			private Set<String> relationships;
			private Boolean displayDefaultParentRelationship;
			private NodeType nodeType;
			private Integer limit;
			private Boolean includeDocumentSummaries;
			private String projection;
			private Integer maxDocPopulation;
			private Boolean activeOnly;
			private Boolean includeConfigs;
			private Boolean isPreview;

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public Builder setDirection(Direction direction) {
				this.direction = direction;
				return this;
			}

			public Builder setTraverseStrategy(TraverseStrategy traverseStrategy) {
				this.traverseStrategy = traverseStrategy;
				return this;
			}

			public Builder setRelationships(Set<String> relationships) {
				this.relationships = relationships;
				return this;
			}

			public Builder setNodeType(NodeType nodeType) {
				this.nodeType = nodeType;
				return this;
			}

			public Builder setLimit(Integer limit) {
				this.limit = limit;
				return this;
			}

			public Builder setDisplayDefaultParentRelationship(Boolean displayDefaultParentRelationship) {

				this.displayDefaultParentRelationship = displayDefaultParentRelationship;
				return this;
			}
			
			public Builder setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
				this.includeDocumentSummaries = includeDocumentSummaries;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public Builder setMaxDocPopulation(Integer maxDocPopulation) {
				this.maxDocPopulation = maxDocPopulation;
				return this;
			}

			public Builder setActiveOnly(Boolean activeOnly) {
				this.activeOnly = activeOnly;
				return this;
			}

			public Builder setIncludeConfigs(Boolean includeConfigs) {
				this.includeConfigs = includeConfigs;
				return this;
			}

			public Builder setIsPreview(Boolean isPreview) {
				this.isPreview = isPreview;
				return this;
			}

			public TaxeneTraverseRequestContext build() {
				return new TaxeneTraverseRequestContext(this);
			}

		}

		public enum Direction {
			IN,
			OUT
		}

		public enum TraverseStrategy {
			ONE_LEVEL,
			BREADTH_FIRST
		}

		public enum NodeType {
			INTERNAL_NODE,
			LEAF_NODE
		}
	}

}
