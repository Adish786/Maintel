package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;

public class Image {

    @XmlElement(name = "title")
    String title;
    @XmlElement(name = "url")
    String url;
    @XmlElement(name = "link")
    String link;
    @XmlElement(name = "width")
    String width;
    @XmlElement(name = "height")
    String height;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getLink() {
        return link;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }
}
