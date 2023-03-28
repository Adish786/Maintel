package com.about.mantle.app;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.DocumentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Goes directly to selene for urls not in the legacy url map,
 * this is specifically for verticals that have legacy documents
 * being added to the vertical regularly.
 */
public class LegacyUrlMapPermissive implements LegacyUrlMap {

    private final DocumentService documentService;
    private final boolean seleneDocUrlsWithoutWww;
    private final String domain;
    private List<String> listOfExcludedPaths;
    private final AntPathMatcher antPathMatcher;
    private final LegacyUrlMap legacyUrlMap;

    public LegacyUrlMapPermissive(DocumentService documentService, boolean seleneDocUrlsWithoutWww, String domain, LegacyUrlMap legacyUrlMap) {
        this.documentService = documentService;
        this.seleneDocUrlsWithoutWww = seleneDocUrlsWithoutWww;
        this.domain = domain;
        this.legacyUrlMap = legacyUrlMap;
        listOfExcludedPaths = new ArrayList<>();
        antPathMatcher = new AntPathMatcher();
    }

    /**
     * Returns the docId for a legacy document based on its uri path
     */
    @Override
    public Long getDocId(String uriPath) {
        Long answer = legacyUrlMap.getDocId(uriPath);

        if(answer == null){
            answer = checkSeleneForDocId(uriPath);
        }

        return answer;
    }

    private Long checkSeleneForDocId (String uriPath){

        Long answer = null;

        //Check to make sure it's a document path and not meant for one
        //of our controllers
        //Then check we don't identify this as a legacy doc if it's a canonical url
        if(matchesExcludedPath(uriPath) || parseDocId(uriPath) != null){
            return null;
        }

        DocumentService.DocumentReadRequestContext documentRequestContext =
                DocumentService.createDocumentRequestContext(buildURL(uriPath), BaseDocumentEx.State.ACTIVE, null);

        BaseDocumentEx document = null;

        try {
            document = documentService.getDocument(documentRequestContext);
        }catch (GlobeNotFoundException E){
            // This could be any url ever so this shouldn't error
        } catch (IllegalStateException e) {
            // GLBE-9620 Catch urls that are malformed and contain {} which Jersey thinks are path templates
        }

        if(document != null && Boolean.TRUE.equals(document.getLegacyDocument())){
            answer = document.getDocumentId();
            legacyUrlMap.addLegacyDocument(document);
        }

        return answer;
    }

    private Long parseDocId(String stem) {

        if (StringUtils.isBlank(stem) || StringUtils.split(stem,('/')).length !=1) return null;

        int index = stem.lastIndexOf('-');
        if (index > 0 && index < (stem.length() - 1) && (stem.length() - (index+1)) > 4) {
            try {
                return Long.parseLong(stem.substring(index + 1));
            } catch (NumberFormatException e) {

            }
        }
        return null;
    }

    private String buildURL (String uriPath) {

        return new StringBuilder("https://")
                .append(seleneDocUrlsWithoutWww ? StringUtils.EMPTY : "www.")
                .append(domain)
                .append(uriPath)
                .toString();
    }

    @Override
    public void addLegacyDocument(BaseDocumentEx document) {
        legacyUrlMap.addLegacyDocument(document);
    }

    @Override
    public void repopulateMap() {
        legacyUrlMap.repopulateMap();
    }

    @Override
    public void addPathsToExclude(List<String> paths) {
        listOfExcludedPaths.addAll(paths);
    }

    private boolean matchesExcludedPath(String uriPath) {
        for (String path : listOfExcludedPaths) {
            if (antPathMatcher.match(path, uriPath)) {
                return true;
            } else if (StringUtils.endsWith(uriPath, "/") &&
                antPathMatcher.match(path, StringUtils.stripEnd(uriPath, "/"))) {
                /* This logic exists to counteract MantleRedirectHandler::addMissingEndingSlashForLegacyDocument(uri)
                 * because otherwise it will attempt to add a trailing slash to all mantle endpoints to check if it's
                 * a legacy document, which in the context of this class involves a call to selene. Ideally we would
                 * have added logic to MantleRedirectHandler to be sensitive to explicit endpoint URIs and bypass the
                 * call to addMissingEndingSlashForLegacyDocument but doing so would involve breaking changes.
                 */
                return true;
            }
        }
        return false;
    }
}
