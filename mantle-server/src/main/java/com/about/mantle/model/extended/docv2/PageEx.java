package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.net.URISyntaxException;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.htmlslicing.HtmlSlice;

public class PageEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private SliceableListEx<String> keywords = SliceableListEx.emptyList();
	private String description;
	private String urlHint;
	private String heading;
	private String subheading;
	private SliceableListEx<HtmlSlice> intro = SliceableListEx.emptyList();
	private SliceableListEx<HtmlSlice> content = SliceableListEx.emptyList();
	private SliceableListEx<ImageEx> images = SliceableListEx.emptyList();
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private SliceableListEx<String> labels = SliceableListEx.emptyList();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SliceableListEx<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(SliceableListEx<String> keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlHint() {
		return urlHint;
	}

	public String getUrl(RequestContext requestContext) {
		try {
			return requestContext.getUrlData().with().path(urlHint).build().getCanonicalUrl();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unsupported encoding", e);
		}
	}

	public void setUrlHint(String urlHint) {
		this.urlHint = urlHint;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public SliceableListEx<HtmlSlice> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<HtmlSlice> intro) {
		this.intro = SliceableListEx.emptyIfNull(intro);
	}

	public SliceableListEx<HtmlSlice> getContent() {
		return content;
	}

	public void setContent(SliceableListEx<HtmlSlice> content) {
		this.content = SliceableListEx.emptyIfNull(content);
	}

	public SliceableListEx<ImageEx> getImages() {
		return images;
	}

	public void setImages(SliceableListEx<ImageEx> images) {
		this.images = SliceableListEx.emptyIfNull(images);
	}

	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
	}

	public SliceableListEx<String> getLabels() {
		return labels;
	}

	public void setLabels(SliceableListEx<String> labels) {
		this.labels = SliceableListEx.emptyIfNull(labels);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
