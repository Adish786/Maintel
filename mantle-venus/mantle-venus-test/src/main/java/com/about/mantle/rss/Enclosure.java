package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlAttribute;

public class Enclosure {

    String url;
    String length;
    String type;

    public String getUrl() {
        return url;
    }

    public String getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setUrl(String url) {
        this.url = url;
    }

    @XmlAttribute
    public void setLength(String length) {
        this.length = length;
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

}
