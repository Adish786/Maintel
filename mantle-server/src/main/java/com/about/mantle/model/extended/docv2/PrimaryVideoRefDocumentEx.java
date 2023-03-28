package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

/**
 * Mirrors selene's PrimaryVideoRefDocument.
 * https://bitbucket.prod.aws.about.com/projects/SER/repos/selene/browse/selene-internal-api/src/main/java/com/dotdash/selene/internal/api/document/v2/model/PrimaryVideoRefDocument.java
 *
 * @see BaseDocumentEx#getPrimaryVideo() for an example of its use.
 */
public class PrimaryVideoRefDocumentEx extends ReferencedDocumentEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean isPremium = true;

	public Boolean getIsPremium() {
		return isPremium;
	}

	public void setIsPremium(Boolean isPremium) {
		this.isPremium = isPremium;
	}
}
