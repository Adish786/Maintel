package com.about.mantle.cache.clearance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Some cache clearance requests need to target a series of pages without knowing what
 * they will be in advance (e.g. sitemap pages, rss feeds).
 * This class tracks which urls matching a given prefix have been processed to avoid
 * multiple cache clearances being performed on the same page while retaining the
 * request to handle other paths.
 * If the requested url matches the prefix, and the path is not in the list of processed
 * URLs, cache clearance should be processed for the request.
 */
public class UrlPrefixCacheClearanceRequest {
    private String prefix;
    private List<String> processedUrls = new CopyOnWriteArrayList<>();

    // Needed for deserialization
    public UrlPrefixCacheClearanceRequest() {
    }

    public UrlPrefixCacheClearanceRequest(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getProcessedUrls() {
        return processedUrls;
    }

    public void setProcessedUrls(List<String> processedUrls) {
        this.processedUrls = processedUrls;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UrlPrefixCacheClearanceRequest{");
        sb.append("prefix='").append(prefix).append('\'');
        sb.append(", processedUrls=").append(processedUrls);
        sb.append('}');
        return sb.toString();
    }
}
