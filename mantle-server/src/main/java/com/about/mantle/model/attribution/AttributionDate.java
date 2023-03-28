package com.about.mantle.model.attribution;

import org.joda.time.DateTime;

public class AttributionDate {
    private DateTime date;
    private String attributionType;

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getAttributionType() {
        return attributionType;
    }

    public void setAttributionType(String attributionType) {
        this.attributionType = attributionType;
    }

    @Override
    public String toString() {
        return "AttributionDate{" +
                "date=" + date +
                ", attributionType=" + attributionType +
                '}';
    }
}
