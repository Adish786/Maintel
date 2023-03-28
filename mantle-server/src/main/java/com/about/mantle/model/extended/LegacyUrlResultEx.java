package com.about.mantle.model.extended;

import java.util.List;

public class LegacyUrlResultEx {
    private List<LegacyUrlToDocId> urlList;
    private Long totalRemain;

    //Note while this is used for pagination, the pagination is a result of this being a time stamp which gets the first
    //x results after it.
    private Long nextCursor;

    public List<LegacyUrlToDocId>  getUrlList() {
        return urlList;
    }

    public void setUrlList(List<LegacyUrlToDocId>  urlList) {
        this.urlList = urlList;
    }

    public Long getTotalRemain() {
        return totalRemain;
    }

    public void setTotalRemain(Long totalRemain) {
        this.totalRemain = totalRemain;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public static class LegacyUrlToDocId{
        private Long docId;
        private String url;

        public Long getDocId() {
            return docId;
        }

        public void setDocId(Long docId) {
            this.docId = docId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

