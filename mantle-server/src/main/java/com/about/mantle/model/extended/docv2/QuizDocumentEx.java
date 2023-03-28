package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.mantle.model.extended.quiz.QuizQuestionEx;
import com.about.mantle.model.extended.quiz.QuizResultEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class QuizDocumentEx extends BaseDocumentEx implements Linkboxed, Serializable {
	
	public enum ResultsCalc {
		PERCENT_CORRECT,
		BEST_FIT,
		WINNER_TAKE_ALL;
	}

	private static final long serialVersionUID = 1L;

	private ResultsCalc resultsCalc;
	private Integer resultsShown;
	private SliceableListEx<QuizResultEx> results = SliceableListEx.emptyList();
	private SliceableListEx<QuizQuestionEx> questions = SliceableListEx.emptyList();;
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private List<LinkboxLinkEx> linkboxList = Collections.emptyList();
	private String content;

	public ResultsCalc getResultsCalc() {
		return resultsCalc;
	}

	public void setResultsCalc(ResultsCalc resultsCalc) {
		this.resultsCalc = resultsCalc;
	}

	public Integer getResultsShown() {
		return resultsShown;
	}

	public void setResultsShown(Integer resultsShown) {
		this.resultsShown = resultsShown;
	}

	public SliceableListEx<QuizResultEx> getResults() {
		return results;
	}

	public void setResults(SliceableListEx<QuizResultEx> results) {
		this.results = results;
	}

	public SliceableListEx<QuizQuestionEx> getQuestions() {
		return questions;
	}

	public void setQuestions(SliceableListEx<QuizQuestionEx> questions) {
		this.questions = questions;
	}

	@Override
	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
		this.linkboxList = transformLinkboxes();
	}

	@Override
	@JsonIgnore
	public List<LinkboxLinkEx> getLinkboxesAsList() {
		return linkboxList;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
}
