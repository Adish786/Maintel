package com.about.mantle.model.extended.docv2.sc.liveblogpost;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class LiveBlogPost implements Serializable {

    private SliceableListEx<ArticleBodyUpdatedSummary> articleBodyUpdatedSummaries;
    private DateTime coverageStartDate;
    private DateTime coverageEndDate;

    public SliceableListEx<ArticleBodyUpdatedSummary> getArticleBodyUpdatedSummaries() {
        return articleBodyUpdatedSummaries;
    }

    public void setArticleBodyUpdatedSummaries(SliceableListEx<ArticleBodyUpdatedSummary> articleBodyUpdatedSummaries) {
        this.articleBodyUpdatedSummaries = articleBodyUpdatedSummaries;
    }

    public DateTime getCoverageStartDate() {
        return coverageStartDate;
    }

    public void setCoverageStartDate(DateTime coverageStartDate) {
        this.coverageStartDate = coverageStartDate;
    }

    public DateTime getCoverageEndDate() {
        return coverageEndDate;
    }

    public void setCoverageEndDate(DateTime coverageEndDate) {
        this.coverageEndDate = coverageEndDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LiveBlogPost{");
        sb.append("articleBodyUpdatedSummaries=").append(articleBodyUpdatedSummaries);
        sb.append(", coverageStartDate=").append(coverageStartDate);
        sb.append(", coverageEndDate=").append(coverageEndDate);
        sb.append('}');
        return sb.toString();
    }
}
