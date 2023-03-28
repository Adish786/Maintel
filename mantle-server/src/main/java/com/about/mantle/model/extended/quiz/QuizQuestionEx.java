package com.about.mantle.model.extended.quiz;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class QuizQuestionEx implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private ImageEx image = ImageEx.EMPTY;
	private String text;
	private RevealEx reveal;
	private SliceableListEx<QuizAnswersEx> answers = SliceableListEx.emptyList();
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public RevealEx getReveal() {
		return reveal;
	}

	public void setReveal(RevealEx reveal) {
		this.reveal = reveal;
	}
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
	public SliceableListEx<QuizAnswersEx> getAnswers() {
		return answers;
	}
	public void setAnswers(SliceableListEx<QuizAnswersEx> answers) {
		this.answers = SliceableListEx.emptyIfNull(answers);
	}
	
	
}
