package com.about.mantle.model.services.commerce;

import java.util.List;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

/**
 * This service is responsible for getting list of documents which is used
 * to expose in RSS feed for amazon. It also filter documents and does some transformation 
 * required in RSS feed.
 * PRD: https://docs.google.com/document/d/1kItDVfGa62vspqXIkfvVMK6RS_YfTtrfh9fE2m2uExw/edit
 * Ticket: https://iacpublishing.atlassian.net/browse/GLBE-6018 
 */
public interface AmazonRssService {
	
	public List<BaseDocumentEx> getDocumentsForRssFeed(String domain, int showLastPublishedInNHours);

}
