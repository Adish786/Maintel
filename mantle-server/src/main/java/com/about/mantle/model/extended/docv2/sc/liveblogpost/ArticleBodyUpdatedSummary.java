package com.about.mantle.model.extended.docv2.sc.liveblogpost;

import java.io.Serializable;

import org.joda.time.DateTime;

public class ArticleBodyUpdatedSummary implements Serializable {

    private String updatedSummary;
    private DateTime updatedSummaryDate;

    public String getUpdatedSummary() {
        return updatedSummary;
    }

    public void setUpdatedSummary(String updatedSummary) {
        this.updatedSummary = updatedSummary;
    }

    public DateTime getUpdatedSummaryDate() {
        return updatedSummaryDate;
    }

    public void setUpdatedSummaryDate(DateTime updatedSummaryDate) {
        this.updatedSummaryDate = updatedSummaryDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ArticleBodyUpdatedSummary{");
        sb.append("updatedSummary='").append(updatedSummary).append('\'');
        sb.append(", updatedSummaryDate=").append(updatedSummaryDate);
        sb.append('}');
        return sb.toString();
    }
}
