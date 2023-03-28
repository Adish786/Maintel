package com.about.mantle.app;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.mantle.model.extended.LegacyUrlResultEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.LegacyUrlService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DedicatedServiceBasedLegacyUrlMap implements LegacyUrlMap {
    private static final Logger logger = LoggerFactory.getLogger(DedicatedServiceBasedLegacyUrlMap.class);
    // Must use ConcurrentHashMap as multiple threads may read map while update is being made
    private static Map<String, Long> map = new ConcurrentHashMap<>();

    private Long minimumLegacyDocuments;
    private final LegacyUrlService legacyUrlService;
    private final BaseDocumentEx.Vertical vertical;
    private final EnvironmentConfig envConfig;

    //This is used so we only grab legacy urls we haven't seen before as cursor is a timestamp that is used
    //to get results after that time.
    private long currentCursor;


    public DedicatedServiceBasedLegacyUrlMap(LegacyUrlService legacyUrlService, BaseDocumentEx.Vertical vertical, Long minimumLegacyDocuments,
                                             EnvironmentConfig envConfig) {
        this.legacyUrlService = legacyUrlService;
        this.minimumLegacyDocuments = minimumLegacyDocuments;
        this.envConfig = envConfig;
        if (vertical == null) {
            throw new GlobeException("Vertical name can not be null, failed to create legacy url map.");
        }
        this.vertical = vertical;
        this.currentCursor = 0;

        // Populate map here by calling Legacy Url service at startup and get all
        // legacy urls up-front.
        fillLegacyUrlMap(false);
    }

    private void fillLegacyUrlMap(boolean repopulate) {

        long cursor = currentCursor;
        int limitBatchSize = LegacyUrlService.SUGGESTED_MAX_LIMIT;

        LegacyUrlResultEx legacyUrlResult = null;

        try {

            do{

                legacyUrlResult = legacyUrlResult == null ? legacyUrlService.getUrlList(cursor, limitBatchSize) : legacyUrlService.getUrlList(legacyUrlResult.getNextCursor(), limitBatchSize);


                if(legacyUrlResult == null || legacyUrlResult.getUrlList() == null){
                    throw new GlobeException("Error unable to get list of documents in call to Legacyurls");
                }

                // first pass, get total number of documents available in legacyurl service
                if(!repopulate) {
                    logger.debug("Querying {} legacy documents in legacyUrlService: limit={}, totalRemain={}", vertical,
                            limitBatchSize, legacyUrlResult.getTotalRemain());
                }



                for(LegacyUrlResultEx.LegacyUrlToDocId entry :legacyUrlResult.getUrlList()){
                    addLegacyDocument(entry.getUrl(), entry.getDocId(), repopulate);
                }

            } while(legacyUrlResult.getNextCursor() != null);

            if(!repopulate) {
                logger.info("Populated in-memory map from Legacy Url Service with {} legacy documents:{}", vertical, map.size());
            }

            if (minimumLegacyDocuments != null && minimumLegacyDocuments > 0L) {
                checkForLegacyMapDeprivationAndHaltApp(minimumLegacyDocuments, map.size());
            }

            // Adding prod check to simplify qa environment legacy url configuration during data conversions
            // Verticals using deprecated constructor (no env config) will be treated as prod for backwards
            // compatibility.
            boolean isProd = (envConfig == null || "prod".equalsIgnoreCase(envConfig.getAccountName()));
            if (isProd && map.size() != 0) {
                throw new GlobeException("Legacy documents are not found in SOLR");
            }

            //Make this thread safe in case timers pile on top of each other.
            synchronized (this){
                currentCursor = System.currentTimeMillis();
            }


        } catch (Exception ex) {
            throw new GlobeException("Error while getting results back from Selene Legacy Url Service", ex);
        }
    }


    /**
     * Adds a legacy document entry into the map
     * @param url
     * @param docId
     */
    private void addLegacyDocument(String url, Long docId, boolean repopulate) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            logger.error("Failed to parse url:{} with docId:{}", url, docId);
        }

        if (uri != null && StringUtils.isNotBlank(uri.getPath()) && !"/".equals(uri.getPath())
                && docId != null) {
            /**
             * If the legacy url is already in the map then there must have been some docs added and
             * the deion search sort is out of order. This will cause errors for ops. Logging an error to keep track.
             */
            String mapKey = uri.getPath();
            if (map.containsKey(mapKey)) {

                //If we are repopulating then we should expect there to be duplicates, this only happens on the timer than re-runs the search once
                //Also if for some reason we get the same path with the same docId we should not report the redundant error
                if(!repopulate && !docId.equals(map.get(mapKey))) {
                    logger.warn("Duplicate legacy url in map. Potential overwriting doc that is ignored - docId: {} and url: {} " +
                                    "Already recorded with this docId: {}",
                            docId, url, map.get(mapKey));
                }
            } else {
                map.put(mapKey, docId);
            }
        } else {
            logger.warn("Found an illegal legacy document:{} with docId:{}", uri, docId);
        }
    }

    private void checkForLegacyMapDeprivationAndHaltApp(long expectedMinimumSize, long actualSize) {
        if (actualSize < expectedMinimumSize) {
            throw new GlobeException("Legacy map is deprived of legacy documents. Expected minimum " + expectedMinimumSize
                    + " legacy documents but fulfilled only " + actualSize);
        }
    }

    /**
     * Returns the docId for a legacy document based on its uri path
     */
    @Override
    public Long getDocId(String uriPath) {
        return map.get(preProcessUriPath(uriPath));
    }

    /**
     * Adds an additional document to the list of legacy documents
     * @param document
     */
    @Override
    public void addLegacyDocument(BaseDocumentEx document) {
        if (document != null) {
            addLegacyDocument(document.getUrl(), document.getDocumentId(), true);
        }
    }

    @Override
    public void repopulateMap(){
       fillLegacyUrlMap(true);
    }

    @Override
    public void addPathsToExclude(List<String> paths) {
        //Do nothing this is for the Permissive Class
    }

    /**
     * Provide this hook method for verticals to do some pre-processing on uri paths
     * before the map look-up. For example: Beauty vertical validate and normalize
     * uri paths by removing trailing '/'
     */
    protected String preProcessUriPath(String uriPath) {
        return uriPath;
    }
}
