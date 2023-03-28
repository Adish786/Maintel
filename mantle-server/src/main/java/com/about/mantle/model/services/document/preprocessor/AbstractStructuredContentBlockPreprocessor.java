package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;

/**
 * Handles iterating over each content block in different document types. This avoids duplication of
 * this logic in multiple preprocessors.
 */
public abstract class AbstractStructuredContentBlockPreprocessor implements DocumentPreprocessor {

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		if (document instanceof StructuredContentBaseDocumentEx) {
			((StructuredContentBaseDocumentEx) document).getContentsStream().forEach(block -> processContentBlock(document, block));
		}

		return document;
	}

	/**
	 * This function is called for each group of content block encountered.
	 * @param doc
	 * @param content
	 */
	protected abstract void processContentBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> content);
}
