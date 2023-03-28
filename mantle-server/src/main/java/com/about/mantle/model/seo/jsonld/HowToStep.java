package com.about.mantle.model.seo.jsonld;

import java.util.List;

import com.about.mantle.model.extended.docv2.ImageEx;

public class HowToStep {
	private String name;
	private List<ImageEx> images;
	private String text;
	
	public HowToStep(String name, List<ImageEx> images, String text){
		this.name = name;
		this.images = images;
		this.text = text;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ImageEx> getImages() {
		return images;
	}
	public void setImages(List<ImageEx> images) {
		this.images = images;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public static class Builder {
		private String name;
		private List<ImageEx> images;
		private StringBuilder text;

		public HowToStep.Builder name(String name) {
			if(this.name == null){
				this.name = name;
			}else{
				appendText(name); //handles when steps have multiple h4s 
			}
			return this;
		}
		
		public HowToStep.Builder images(List<ImageEx> images) {
			this.images = images;
			return this;
		}
		
		public HowToStep.Builder text(String text) {
			appendText(text);
			return this;
		}

		public void appendText(String text) {
			if(this.text == null){
				this.text = new StringBuilder ();
			}
			this.text.append(text);
		}

		public static HowToStep.Builder fromName(String name) {
			HowToStep.Builder builder = new Builder();
			return builder.name(name);
		}
		
		public static HowToStep.Builder fromImages(List<ImageEx> images) {
			HowToStep.Builder builder = new Builder();
			return builder.images(images);
		}
		
		public static HowToStep.Builder fromText(String text) {
			HowToStep.Builder builder = new Builder();
			return builder.text(text);
		}

		public HowToStep build() {
			return new HowToStep(name, images, text != null ? text.toString() : null);
		}
	}
}
