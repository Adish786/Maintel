package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Item {

    String title;
    String link;
    String description;
    Enclosure enclosure;
    String guid;
    String pubDate;
    String content;
    String subTitle;
    Metadata metadata;
    Thumbnail thumbnail;

    @XmlElement(name = "heroImage", namespace = "https://amazon.com/ospublishing/1.0/")
    String amznHeroImage;

    @XmlElement(name = "introText", namespace = "https://amazon.com/ospublishing/1.0/")
    String amznIntroText;

    @XmlElement(name = "products", namespace = "https://amazon.com/ospublishing/1.0/")
	private List<AmazonProducts> amazonProducts = new ArrayList<>();

	public List<AmazonProducts> getAmazonProducts() {
		return this.amazonProducts;
	}

    public String getAmazonHeroImage() { return amznHeroImage; }

    public String getAmazonIntroText() { return amznIntroText; }

    public String getTitle() { return title; }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public String getGuid() {
        return guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getContent() {
        return content;
    }
    
    public String getSubTitle() { return subTitle; }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    @XmlElement
    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public void setLink(String link) {
        this.link = link;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    @XmlElement(name = "thumbnail", namespace="http://search.yahoo.com/mrss/")
    public void setThumbnail(Thumbnail thumbnail) { this.thumbnail = thumbnail; }

    @XmlElement
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @XmlElement
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @XmlElement(name = "content:encoded", namespace="http://www.w3.org/XML/1998/namespace")
    public void setContent(String content) { this.content = content; }

    public Metadata getMetadata() {
        return metadata;
    }

    @XmlElement(name = "Metadata", required = false)
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    @XmlElement(name = "amznsubtitle")
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

}
