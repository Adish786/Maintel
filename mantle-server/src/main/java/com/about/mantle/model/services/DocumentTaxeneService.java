package com.about.mantle.model.services;

import java.util.List;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;

/**
 * Service that provides taxene data given a document or documents
 * @see DocumentTaxeneComposite
 */
public interface DocumentTaxeneService{

	/**
	 * Returns a {@link DocumentTaxeneComposite} for a single document, with a given projection to limit what's
	 * populated in the taxene document
	 */
	<T extends BaseDocumentEx> DocumentTaxeneComposite<T> getDocumentTaxeneComposite(T document, String projection);

	/**
	 * Returns a {@link DocumentTaxeneComposite} for a list of documents, with a given projection to limit what's
	 * populated in the taxene document
	 */
	<T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> getDocumentTaxeneCompositeList(List<T> documentList, String projection);

	/**
	 * Version of {@link #getDocumentTaxeneCompositeList(List, String)} but for curated docs.
	 * See docs for {@link CuratedDocumentTaxeneComposite} for more explanation about why this is special.
	 */
	List<CuratedDocumentTaxeneComposite> getCuratedDocumentTaxeneCompositeList(List<CuratedDocumentEx> documentList, String projection);
}
