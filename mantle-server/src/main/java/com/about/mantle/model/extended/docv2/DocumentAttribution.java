package com.about.mantle.model.extended.docv2;

import com.about.mantle.model.extended.BaseAuthor;
import com.about.mantle.model.extended.attribution.Attribution;

import org.joda.time.DateTime;

/**
 * Represents an author's association with a document
 */
public class DocumentAttribution extends Attribution {

    private DateTime lastModified;
    private BaseAuthor author;

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public BaseAuthor getAuthor() {
        return author;
    }

    public void setAuthor(BaseAuthor author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "DocumentAttribution{" +
                "lastModified=" + lastModified +
                ", author=" + author +
                "} " + super.toString();
    }

}