package com.about.mantle.model.services.contentgraph;

import com.about.mantle.model.contentgraph.AssetMetadata;
import com.about.mantle.model.contentgraph.PresignedUrl;

/**
 * Globe's integration points with Legacy Meredith's content graph.
 */
public interface ContentGraphService {

	/**
	 * Used for uploading a publically accessible asset, e.g. user photos.
	 * @param assetMetadata
	 * @return
	 */
	PresignedUrl generatePresignedUrl(AssetMetadata assetMetadata);

}
