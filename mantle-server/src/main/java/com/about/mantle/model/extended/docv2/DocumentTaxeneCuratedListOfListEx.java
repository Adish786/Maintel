package com.about.mantle.model.extended.docv2;

import java.util.List;

import com.about.mantle.model.extended.curatedlist.CuratedListEx;

public class DocumentTaxeneCuratedListOfListEx<T extends BaseDocumentEx>
		extends CuratedListEx<DocumentTaxeneCuratedListEx<T>> {

	@Override
	public void setData(List<DocumentTaxeneCuratedListEx<T>> data) {
		this.setItems(SliceableListEx.of(data));
		this.setItemCount(data == null ? 0 : data.size());
	}
}
