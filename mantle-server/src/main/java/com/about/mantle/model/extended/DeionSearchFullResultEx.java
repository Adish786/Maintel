package com.about.mantle.model.extended;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

/**
 * This is different from {@link DeionSearchResultEx}. It contains full documents in the result.
 * It represents results from /deion/search/fulldocument end point.
 */
public class DeionSearchFullResultEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private long numFound;
	private SliceableListEx<BaseDocumentEx> items;

	public SliceableListEx<BaseDocumentEx> getItems() {
		return items;
	}

	public void setItems(SliceableListEx<BaseDocumentEx> items) {
		this.items = items;
	}

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}
}
