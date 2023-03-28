package com.about.mantle.model.services;

import com.about.mantle.model.extended.LegacyUrlResultEx;

public interface LegacyUrlService {
    public static final int SUGGESTED_MAX_LIMIT = 100000;

    /**
     * getUrlList
     *
     * @param cursor - Unix Timestamp that the service uses to get number of urls specified by the limit afterwards,
     *               if 0 will get them from the beginning of time
     * @param limit - How many results are returned after the specified time
     * @return list of legacy url results
     */
    public LegacyUrlResultEx getUrlList(long cursor, int limit);
}
