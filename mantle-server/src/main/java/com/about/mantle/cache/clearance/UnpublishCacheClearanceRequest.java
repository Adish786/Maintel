package com.about.mantle.cache.clearance;

public class UnpublishCacheClearanceRequest {
    private String serviceName;
    private String url;
    private String app;
    private String environment;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnpublishCacheClearanceRequest{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", app='").append(app).append('\'');
        sb.append(", environment='").append(environment);
        sb.append('}');
        return sb.toString();
    }
}
