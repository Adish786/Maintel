package com.about.mantle.model.extended.quiz;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class QuizAnswersEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private ImageEx image = ImageEx.EMPTY;
	private String text;
	private SliceableListEx<Value> values = SliceableListEx.emptyList();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = ImageEx.emptyIfNull(image);
	}

	public SliceableListEx<Value> getValues() {
		return values;
	}

	public void setValues(SliceableListEx<Value> values) {
		this.values = SliceableListEx.emptyIfNull(values);
	}
	
	public static class Value implements Serializable {
		private static final long serialVersionUID = 1L;

		private String key;
		private Integer score;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Integer getScore() {
			return score;
		}

		public void setScore(Integer score) {
			this.score = score;
		}

	}
}
