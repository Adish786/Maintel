package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.TopicMapDocumentEx.Coordinate;

public class CardEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;
	private String title;
	private String description;
	private String cardCategory;
	private ImageRenditionsEx imageRenditions = ImageRenditionsEx.EMPTY;
	private Coordinate coordinate;
	private BaseDocumentEx document;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCardCategory() {
		return cardCategory;
	}
	public void setCardCategory(String cardCategory) {
		this.cardCategory = cardCategory;
	}
	public ImageRenditionsEx getImageRenditions() {
		return imageRenditions;
	}
	public void setImageRenditions(ImageRenditionsEx imageRenditions) {
		this.imageRenditions = ImageRenditionsEx.emptyIfNull(imageRenditions);
	}
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public BaseDocumentEx getDocument() {
		return document;
	}
	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}

}
