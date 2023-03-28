package com.about.mantle.model.services;

import com.about.mantle.model.extended.docv2.MetaDataEx;

import java.util.Objects;

/**
 * Service for retrieving metadata from selene /metadata endpoint. This service is needed because not all endpoints that return
 * back document data include metadata.
 */
public interface MetaDataService {

    MetaDataEx getMetaData(MetaDataRequestContext requestContext);

    public static class MetaDataRequestContext {
        private final String url;
        private final Long docId;
        private final String projection;

        public MetaDataRequestContext(Builder builder) {
            this.url = builder.url;
            this.docId = builder.docId;
            this.projection = builder.projection;
        }

        public String getUrl() {
            return url;
        }

        public Long getDocId() {
            return docId;
        }

        public String getProjection() {
            return projection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MetaDataRequestContext that = (MetaDataRequestContext) o;
            return Objects.equals(url, that.url) && Objects.equals(docId, that.docId) && Objects.equals(projection, that.projection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, docId, projection);
        }

        @Override
        public String toString() {
            return "MetaDataRequestContext{" +
                    "url='" + url + '\'' +
                    ", docId=" + docId +
                    ", projection='" + projection + '\'' +
                    '}';
        }

        public static class Builder {
            private String url;
            private Long docId;
            private String projection;

            public Builder setUrl(String url) {
                if (this.docId != null)
                    throw new IllegalArgumentException("Can't set url. DocId is already set.");

                this.url = url;
                return this;
            }

            public Builder setDocId(Long docId) {
                if (this.url != null)
                    throw new IllegalArgumentException("Can't set docId. Url is already set.");

                this.docId = docId;
                return this;
            }

            public Builder setProjection(String projection) {
                this.projection = projection;
                return this;
            }

            public MetaDataRequestContext build() {
                return new MetaDataRequestContext(this);

            }
        }
    }

    public static MetaDataRequestContext createMetaDataRequestContext(String url, String projection) {
        MetaDataRequestContext.Builder builder = new MetaDataRequestContext.Builder();

        return builder.setUrl(url)
                        .setProjection(projection)
                        .build();
    }

    public static MetaDataRequestContext createMetaDataRequestContext(Long docId, String projection) {
        MetaDataRequestContext.Builder builder = new MetaDataRequestContext.Builder();

        return builder.setDocId(docId)
                        .setProjection(projection)
                        .build();
    }
}
