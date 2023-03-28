package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Channel {

    String title;
    String link;
    String description;
    String copyright;
    String language;
    String lastBuildDate;
    String generator;
    String ttl;
    @XmlElement(name = "image")
    Image image;

    @XmlElement(name = "logo", namespace = "http://www.smartnews.be/snf")
    SnfLogo snfLogo;

    @XmlElement(name = "darkModeLogo", namespace = "http://www.smartnews.be/snf")
    SnfLogo darkModeLogo;

    @XmlElement(name = "item")
    private List<Item> items = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getLanguage() {
        return language;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public String getGenerator() {
        return generator;
    }

    public String getTtl() {
        return ttl;
    }

    public Image getImage() {
        return image;
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
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    @XmlElement
    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlElement
    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    @XmlElement
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    @XmlElement
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public SnfLogo getSnFLogo() { return this.snfLogo; }

    public SnfLogo getDarkModeLogo() { return this.darkModeLogo; }

}
