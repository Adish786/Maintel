package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.cache.hash.JourneyRequestContextHasher;
import com.about.mantle.model.journey.JourneyRelationshipType;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.services.JourneyService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;

public class CachedJourneyService implements JourneyService {
	private final JourneyService journeyService;
	// maps rootId to JourneyRoot
	private final CacheTemplate<JourneyRoot> journeyRootCache;
	// maps docId to its associated journey if one exists.  Includes the relationship type between the doc and its journey
	private final CacheTemplate<JourneyRootIdAndRelationshipType> rootIdCache;

	public CachedJourneyService(JourneyService journeyService,
			CacheTemplate<JourneyRoot> journeyStructureCache, CacheTemplate<JourneyRootIdAndRelationshipType> rootIdCache) {
		this.journeyService = journeyService;
		this.journeyRootCache = journeyStructureCache;
		this.rootIdCache = rootIdCache;
	}

	@Override
	public Pair<JourneyRoot, JourneyRelationshipType> getJourneyRootAndRelationship(JourneyRequestContext reqCtx) {

		Pair<JourneyRoot, JourneyRelationshipType> answer;

		/* Selene will return the same journey structure for every document in a journey.
		 * To avoid caching multiple copies of the same object we employ two layers of cache.
		 *
		 *  docId -> journeyRootDocId -> journeyStructure
		 * |----Layer 1--------------|
		 *          |---------------Layer 2--------------|
		 *
		 * Layer 1 caches the journey rootId docId for a document, along with the relationship type between that journey
		 * and that docId
		 * Since the journey rootId docId is a small object (Long), the size of Layer 1 can be large enough
		 * to contain all the documents on the site without much impact to the size of the heap.
		 *
		 * Layer 2 caches the journey structure for a journey rootId docId.
		 * Since the journey structure is a large object (JourneyRoot), the size of Layer 2 should be just
		 * large enough to contain all the journeys on the site. We certainly want to avoid caching redundant
		 * copies because too many of these could negatively impact the size of the heap.
		 */
		Long docId = reqCtx.getDocId();
		// Layer 1: map the docId to its journey rootId docId
		JourneyRootIdAndRelationshipType rootIdAndRelationship = rootIdCache.get(docId, () -> {
			Pair<JourneyRoot, JourneyRelationshipType> resp = journeyService.getJourneyRootAndRelationship(reqCtx);
			JourneyRoot root = resp.getLeft();
			Long id = root == null || root.getSections() == null || root.getSections().isEmpty()
					? null : root.getDocument().getDocumentId();
			if (id != null) {
				/* Getting the journey rootId docId and the journey structure requires making the same Selene call.
				 * Recall that Selene will return the same journey structure for every document in a journey (including the rootId).
				 * Therefore we can avoid the second call by using the response of the first call to pre-populate Layer 2 (below).
				 */
				JourneyRequestContext rootCtx = getRootJourneyContext(id, reqCtx);
				journeyRootCache.update(new CacheKey(rootCtx), root);
			}
			return new JourneyRootIdAndRelationshipType(id, resp.getRight());
		});

		if (rootIdAndRelationship == null || rootIdAndRelationship.getRootId() == null) {
			answer = Pair.of(NULL_JOURNEY, JourneyRelationshipType.NONE);
		} else {

			// Layer 2: map the journey rootId docId to its journey structure
			JourneyRequestContext rootCtx = getRootJourneyContext(rootIdAndRelationship.getRootId(), reqCtx);
			JourneyRoot journeyRoot = journeyRootCache.get(new CacheKey(rootCtx), () -> journeyService.getJourneyRootAndRelationship(rootCtx).getLeft());

			answer = Pair.of(journeyRoot, rootIdAndRelationship.getRelationshipType());
		}
		return answer;
	}

	private static JourneyRequestContext getRootJourneyContext(Long rootId, JourneyRequestContext reqCtx) {
		if (rootId.equals(reqCtx.getDocId())) {
			return reqCtx;
		}
		JourneyRequestContext rootCtx = new JourneyRequestContext.Builder()
			.setDocId(rootId)
			.setIncludeDocumentSummaries(reqCtx.getIncludeDocumentSummaries())
			.setProjection(reqCtx.getProjection())
			.build();
		return rootCtx;
	}

	private static class CacheKey implements Serializable, RedisCacheKey {
		private static final long serialVersionUID = 1L;

		private final JourneyRequestContext reqCtx;

		public CacheKey(JourneyRequestContext reqCtx) {
			this.reqCtx = reqCtx;
		}

		@Override
		public String getUniqueKey() {
			if (reqCtx != null){
				return reqCtx.toString();
			}
			return "null";
		}

		@Override
		public int hashCode() {
			return JourneyRequestContextHasher.INSTANCE.hash(reqCtx);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof CachedJourneyService.CacheKey)) return false;

			CachedJourneyService.CacheKey other = (CachedJourneyService.CacheKey) obj;
			// @formatter:off
			return new EqualsBuilder()
					.append(reqCtx.getDocId(), other.reqCtx.getDocId())
					.append(reqCtx.getIncludeDocumentSummaries(), other.reqCtx.getIncludeDocumentSummaries())
					.append(reqCtx.getProjection(), other.reqCtx.getProjection())
					.build();
			// @formatter:on
		}
	}

	/**
	 * Tried to use a Pair for this but it didn't place nice with the cache serialization.  So here ya go.  Just used
	 * for caching.
	 */
	public static class JourneyRootIdAndRelationshipType {

		private Long rootId;
		private JourneyRelationshipType relationshipType;

		/**
		 * Required for BinaryCacheTemplateModifier deserialization
		 */
		public JourneyRootIdAndRelationshipType() {
		}

		public JourneyRootIdAndRelationshipType(Long rootId, JourneyRelationshipType relationshipType) {
			this.rootId = rootId;
			this.relationshipType = relationshipType;
		}

		public Long getRootId() {
			return rootId;
		}

		public JourneyRelationshipType getRelationshipType() {
			return relationshipType;
		}
	}
}

