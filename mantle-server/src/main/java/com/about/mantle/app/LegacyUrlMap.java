package com.about.mantle.app;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

import java.util.List;

/**
 * Implementation of this interface is supposed to store uriPath to docId mapping which
 * is used in handling of legacy urls - which doesn't conform to 
 * Dotdash's canonical url format of slug-docid.
 *
 */
public interface LegacyUrlMap {
	
	/**
	 * gives back docId for a given uriPath
	 */
	Long getDocId(String uriPath);

	/**
	 * Adds an additional document to the list of legacy documents.
	 * @param document
	 */
	void addLegacyDocument(BaseDocumentEx document);

	/**
	 * For repopulating the map after start up
	 */
	void repopulateMap();
	
	/**
	 * Static method returns {@link LegacyUrlMap} which returns null for any given uriPath.
	 * This comes handy when you need to create impl just for the sake of populating empty
	 * inner mapping (hashmap) of uriPah to docId. 
	 */
	static LegacyUrlMap emptyLegacyUrlMap() {
		return new LegacyUrlMap() {
			@Override
			public Long getDocId(String uriPath) {
				return null;
			}

			@Override
			public void addLegacyDocument(BaseDocumentEx document) {
				// Do nothing
			}

			@Override
			public void repopulateMap() {
				// Do nothing
			}

			@Override
			public void addPathsToExclude(List<String> paths) {
				// Do nothing
			}
		};
	}

	/**
	 * Adds paths to exclude from being looked up in the map
	 * This is primarily intended to add the paths from non-document
	 * controllers
	 * @param paths
	 */
	void addPathsToExclude(List<String> paths);
}
