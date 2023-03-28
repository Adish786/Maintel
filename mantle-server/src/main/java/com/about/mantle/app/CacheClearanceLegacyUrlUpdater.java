package com.about.mantle.app;

import com.about.globe.core.exception.GlobeNotFoundException;
import org.apache.commons.lang3.StringUtils;

import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.cache.clearance.CacheClearanceEventHandler;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes cache clearance events to identify new URLs to be added to the legacy url map.
 */
public class CacheClearanceLegacyUrlUpdater implements CacheClearanceEventHandler {

	private static final Logger logger = LoggerFactory.getLogger(CacheClearanceLegacyUrlUpdater.class);

	private final DocumentService documentService;
	private final LegacyUrlMap legacyUrlMap;
	private final UrlDataFactory urlDataFactory;
	private final String domain;
	private final boolean seleneDocUrlsWithoutWww;

	public CacheClearanceLegacyUrlUpdater(String domain, DocumentService documentService, LegacyUrlMap legacyUrlMap,
										  UrlDataFactory urlDataFactory, boolean seleneDocUrlsWithoutWww) {
		this.domain = domain;
		this.documentService = documentService;
		this.legacyUrlMap = legacyUrlMap;
		this.urlDataFactory = urlDataFactory;
		this.seleneDocUrlsWithoutWww = seleneDocUrlsWithoutWww;
	}

	/**
	 * Checks whether the provided URL is a legacy URL not currently registered in the legacy URL map, retrieves the
	 * document, and adds it to the legacy URL map.
	 * @param path
	 */
	@Override
	public void handle(String path) {
		if (legacyUrlMap == null) return;

		String url = new StringBuilder("https://").append(seleneDocUrlsWithoutWww ? StringUtils.EMPTY : "www.").append(domain).append(path).toString();
		UrlData urlData = urlDataFactory.create(url);
		if (urlData instanceof VerticalUrlData) {
			VerticalUrlData verticalUrlData = (VerticalUrlData) urlData;
			if (verticalUrlData.getDocId() == null) {
				/* URL is not registered in legacy url map. The above checks should ensure that only legacy
				 * document URLs are requested and that they are only registered once.
				 */
				try{
					BaseDocumentEx legacyDoc = documentService.getDocument(
						DocumentService.createDocumentRequestContext(url, null, null));

					legacyUrlMap.addLegacyDocument(legacyDoc);
				}catch (GlobeNotFoundException E){
					logger.error("Error trying to update legacy url map with new url from kafka "+url);
				}
			}
		}
	}
}
