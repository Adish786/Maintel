package com.about.mantle.model.news.sitemap.google;

import com.about.mantle.model.extended.docv2.ImageEx;

public class GoogleNewsItem {

	private String url;
	private GoogleNews news;
	private ImageEx image;

	public GoogleNewsItem(String url, GoogleNews news, ImageEx image) {
		this.url = url;
		this.news = news;
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public GoogleNews getNews() {
		return news;
	}

	public void setNews(GoogleNews news) {
		this.news = news;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "GoogleNewsItem [url=" + url + ", news=" + news + ", image=" + image + "]";
	}

}
