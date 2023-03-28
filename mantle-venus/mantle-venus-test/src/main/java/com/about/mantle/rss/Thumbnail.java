package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlAttribute;

public class Thumbnail {

    String url;

    public String getUrl() {
        return url;
    }

    @XmlAttribute(name = "url")
    public void seturl(String url) {
        this.url = url;
    }

}
