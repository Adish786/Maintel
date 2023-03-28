package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentQuestionAndAnswerEx extends AbstractStructuredContentContentEx<StructuredContentQuestionAndAnswerEx.StructuredContentQuestionAndAnswerDataEx> {

	public static class StructuredContentQuestionAndAnswerDataEx extends AbstractStructuredContentDataEx {

		private String question;
		private String shortText;
		private String answer;
		private Boolean hideOnTOC;

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public String getShortText() {
			return shortText;
		}

		public void setShortText(String shortText) {
			this.shortText = shortText;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public Boolean getHideOnTOC() {
			return hideOnTOC;
		}

		public void setHideOnTOC(Boolean hideOnTOC) {
			this.hideOnTOC = hideOnTOC;
		}

		@Override
		public String toString() {
			return "StructuredContentQuestionAndAnswerDataEx{" +
					"question='" + question + '\'' +
					", shortText='" + shortText + '\'' +
					", answer='" + answer + '\'' +
					", hideOnTOC=" + hideOnTOC +
					'}';
		}
	}
}
