package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlAttribute;

public class MetaDataType {
    String formalName;

    public String getFormalName() {
        return formalName;
    }

    @XmlAttribute(name = "FormalName")
    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }
}