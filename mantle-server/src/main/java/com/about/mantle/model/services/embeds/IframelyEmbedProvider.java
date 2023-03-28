package com.about.mantle.model.services.embeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Map;

public class IframelyEmbedProvider {

    private static final Logger logger = LoggerFactory.getLogger(IframelyEmbedProvider.class);

    private final String iframelyUrl;
    private final String iframelyAPIKey;

    private static final Client client = ClientBuilder.newClient();

    public IframelyEmbedProvider (String iframelyUrl, String iframelyAPIKey){
        this.iframelyUrl = iframelyUrl;
        this.iframelyAPIKey = iframelyAPIKey;
    }

    public EmbedContent getIframelyResult(String url){
        return getIframelyResult(url, null);
    }

    public EmbedContent getIframelyResult(String url, Map<String, String> optionalParameters){

        WebTarget webTarget = client.target(iframelyUrl)
                .queryParam("api_key", iframelyAPIKey).queryParam("url", url);

        if (optionalParameters != null) {
            for (Map.Entry<String, String> option : optionalParameters.entrySet()) {
                webTarget = webTarget.queryParam(option.getKey(), option.getValue());
            }
        }

        Response response = null;
        IframelyResponse iframelyResponse = null;
        try {
            response = webTarget.request().get();
            iframelyResponse = response.readEntity(IframelyResponse.class);
        } catch (Exception e) {
            logger.error("failed to read oEmbed response", e);
            return null;
        } finally {
            if (response != null) response.close();
        }

        EmbedContent embedContent = new EmbedContent();
        embedContent.setProvider("iframely-" + iframelyResponse.getProvider_name().toLowerCase());
        embedContent.setWidth(iframelyResponse.getThumbnail_width()+"");
        embedContent.setHeight(iframelyResponse.getThumbnail_height()+"");

        if (StringUtils.stripToNull(iframelyResponse.getHtml()) != null) {
            embedContent.setContent(iframelyResponse.getHtml());
            embedContent.setType(EmbedContent.Type.HTML);
        } else if (StringUtils.stripToNull(iframelyResponse.getUrl()) != null && "photo".equals(iframelyResponse.getType())) {
            embedContent.setContent(iframelyResponse.getUrl());
            embedContent.setType(EmbedContent.Type.IMG);
        } else {
            // Known type "link" not required at this time.
            logger.error("failed to interpret oEmbed response of type [{}] for url [{}]" , iframelyResponse.getType(), url);
            return null;
        }

        return embedContent;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class IframelyResponse {

        private String type;
        private String url;
        private String html;

        public String getProvider_name() {
            return provider_name;
        }

        public void setProvider_name(String provider_name) {
            this.provider_name = provider_name;
        }

        private String provider_name;
        private String thumbnail_url;
        private int thumbnail_width;
        private int thumbnail_height;

        public String getThumbnail_url() {
            return thumbnail_url;
        }

        public void setThumbnail_url(String thumbnail_url) {
            this.thumbnail_url = thumbnail_url;
        }

        public int getThumbnail_width() {
            return thumbnail_width;
        }

        public void setThumbnail_width(int thumbnail_width) {
            this.thumbnail_width = thumbnail_width;
        }

        public int getThumbnail_height() {
            return thumbnail_height;
        }

        public void setThumbnail_height(int thumbnail_height) {
            this.thumbnail_height = thumbnail_height;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }
    }
}
