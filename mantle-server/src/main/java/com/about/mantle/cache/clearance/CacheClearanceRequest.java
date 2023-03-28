package com.about.mantle.cache.clearance;

import java.util.List;

public class CacheClearanceRequest {
    private String serviceName;
    private String urlPath;
    private String app;
    private String environment;
    private Integer level;
    private List<String> cacheNames;

    //To be used with priority queueing see GLBE-9468
    private boolean priority;

    public static CacheClearanceRequest createBasicCacheClearanceRequest() {
        CacheClearanceRequest ccr = new CacheClearanceRequest();
        ccr.setLevel(CacheClearanceTemplateModifier.CACHE_CLEARANCE_BASE_LEVEL);
        return ccr;
    }

    public static CacheClearanceRequest createAllLevelCacheClearanceRequest() {
        CacheClearanceRequest ccr = new CacheClearanceRequest();
        ccr.setLevel(CacheClearanceTemplateModifier.CACHE_CLEARANCE_ALL_LEVEL);
        return ccr;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CacheClearanceRequest{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", urlPath='").append(urlPath).append('\'');
        sb.append(", app='").append(app).append('\'');
        sb.append(", environment='").append(environment).append('\'');
        sb.append(", level=").append(level);
        sb.append(", cacheNames=").append(cacheNames);
        sb.append(", priority=").append(priority);
        sb.append('}');
        return sb.toString();
    }
}
