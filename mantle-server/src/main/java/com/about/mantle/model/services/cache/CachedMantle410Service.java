package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.DocumentArchiveService;
import com.about.mantle.model.services.Mantle410Service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CachedMantle410Service implements Mantle410Service {

    private DocumentArchiveService documentArchiveService;
    private Mantle410Service mantle410Service;
    private String globeDomain;
    private CacheTemplate<Set<String>> cacheTemplate;
    private final boolean seleneDocUrlsWithoutWww;
    private static final StaticCacheKey key = new StaticCacheKey("Mantle410Service");

    public CachedMantle410Service(Mantle410Service mantle410Service, String globeDomain, CacheTemplate<Set<String>> cacheTemplate, DocumentArchiveService documentArchiveService, boolean seleneDocUrlsWithoutWww){
        this.documentArchiveService = documentArchiveService;
        this.mantle410Service = mantle410Service;
        this.cacheTemplate = cacheTemplate;
        this.globeDomain = globeDomain;
        this.seleneDocUrlsWithoutWww = seleneDocUrlsWithoutWww;
    }

    /**
     * Checks a uri against BVOD to see if the page is gone
     *
     * @param uri
     * @return
     */
    @Override
    public boolean is410(String uri) {
        String url = new StringBuilder("https://").append(seleneDocUrlsWithoutWww ? StringUtils.EMPTY : "www.").append(globeDomain).append(uri).toString();

        return getBVODMap().contains(url) || documentArchiveService.getArchivedDocuments().contains(url);
    }

    @Override
    public Set<String> getBVODMap() {
        return this.cacheTemplate.get(key, () -> mantle410Service.getBVODMap());
    }
}
