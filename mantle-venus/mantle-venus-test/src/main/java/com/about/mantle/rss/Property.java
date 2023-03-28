package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlAttribute;

public class Property {
    String formalName;
    String value;

    public String getFormalName() {
        return formalName;
    }

    @XmlAttribute(name = "FormalName")
    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute(name = "Value")
    public void setValue(String value) {
        this.value = value;
    }
}
