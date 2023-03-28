package com.about.mantle.model.services.document;

import java.util.Set;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.document.preprocessor.Element;

/**
 * Allows for rewriting of document inline or component-generated HTML content.
 */
public interface ElementRewriter {
	void processElement(BaseDocumentEx document, Element element);

	Set<String> getTargetElementNames();
}
