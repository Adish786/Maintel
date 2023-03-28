package com.about.mantle.model.services.impl;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.DocumentArchiveService;
import com.about.mantle.model.services.Mantle410Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Combines BOVD and Selene data to verify if URI is supposed to 410
 */
public class Mantle410ServiceImpl implements Mantle410Service {
    private final BusinessOwnedVerticalDataService bovdService;
    private final DocumentArchiveService documentArchiveService;
    private String globeDomain;
    private String filepath;
    private final boolean seleneDocUrlsWithoutWww;

    public Mantle410ServiceImpl(String globeDomain, String filepath, BusinessOwnedVerticalDataService bovdService,
                                DocumentArchiveService documentArchiveService, boolean seleneDocUrlsWithoutWww){
        this.globeDomain = globeDomain;
        this.filepath = filepath;
        this.bovdService = bovdService;
        this.documentArchiveService = documentArchiveService;
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

        Set<String> bvodMap = new HashSet<String>();
        byte[] bytes = bovdService.getResource(filepath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
            while(reader.ready()){
                bvodMap.add(reader.readLine());
            }

        }catch (Exception e) {
            throw new GlobeException("building Mantle 410 Service Map from BVOD", e);
        }
        return bvodMap;
    }


}
