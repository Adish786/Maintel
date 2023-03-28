package com.about.mantle.model.services.news;

import java.util.List;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

/** 
 * 
 * Returns google news items for sitemap
 * News documents are picked-up. For more details on
 * which documents are the candidates please ref- GLBE-7371
 *
 */
public interface GoogleNewsSitemapService {

	public List<BaseDocumentEx> getGoogleNewsDocuments(String domain);
}
