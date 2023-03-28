package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Metadata {

    MetaDataType metaDataType;
    List<Property> properties;

    public MetaDataType getMetaDataType() {
        return metaDataType;
    }

    public List<Property> getProperty() {
        return properties;
    }

    @XmlElement(name = "MetaDataType")
    public void setMetaDataType(MetaDataType metaDataType) {
        this.metaDataType = metaDataType;
    }

    @XmlElement(name = "Property")
    public void setProperty(List<Property> properties) {
        this.properties = properties;
    }

}
