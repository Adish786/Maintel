package com.about.mantle.cache.hash;

import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

public class DocumentCollectionHasher extends CollectionHasher {
	public DocumentCollectionHasher(){
		super(i -> i == null ? 0 : ((BaseDocumentEx)i).getDocumentId().hashCode());
	}
}
