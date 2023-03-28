package com.about.mantle.model.extended.docv2;

import java.util.List;

import com.about.mantle.model.extended.curatedlist.CuratedListEx;

public class DocumentTaxeneCuratedListEx<T extends BaseDocumentEx> extends CuratedListEx<CuratedDocumentTaxeneComposite> {

	@Override
	public void setData(List<CuratedDocumentTaxeneComposite> data) {
		this.setItems(SliceableListEx.of(data));
		this.setItemCount(data == null ? 0 : data.size());
	}
}
