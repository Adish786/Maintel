package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

/*
 * Interface for providing preprocessing for a document 
 * used in DocumentService.processDocument 
 */
public interface DocumentPreprocessor {
	
	public BaseDocumentEx preProcessDocument (BaseDocumentEx document);

}
