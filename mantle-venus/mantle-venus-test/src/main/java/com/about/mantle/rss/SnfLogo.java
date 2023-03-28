package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;

public class SnfLogo {

    @XmlElement(name = "url")
    String url;

    public String getUrl() {
        return url;
    }

}
