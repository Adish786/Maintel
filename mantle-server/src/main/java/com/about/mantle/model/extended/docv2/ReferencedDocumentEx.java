package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

/**
 * Mirrors selene's ReferencedDocument.
 * https://bitbucket.prod.aws.about.com/projects/SER/repos/selene/browse/selene-internal-api/src/main/java/com/dotdash/selene/internal/api/document/v2/model/ReferencedDocument.java
 * 
 * A referenced document is populated from a docId.
 * @see BaseDocumentEx#getPrimaryVideo() for an example of its use.
 */
public class ReferencedDocumentEx implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long docId;
	private BaseDocumentEx document;
	
	public Long getDocId() {
		return docId;
	}
	
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	public BaseDocumentEx getDocument() {
		return document;
	}
	
	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}

}
