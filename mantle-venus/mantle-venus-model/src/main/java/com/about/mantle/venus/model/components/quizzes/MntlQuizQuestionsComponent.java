package com.about.mantle.venus.model.components.quizzes;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-quiz-questions")
public class MntlQuizQuestionsComponent extends MntlComponent {

	private final Lazy<List<QuestionComponent>> questions;
	public MntlQuizQuestionsComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.questions = lazy(() -> findElements(By.className("question"), QuestionComponent::new));
	}

	public List<QuestionComponent> questions() {
		return questions.get();
	}
	
	@Validate
	public WebElementEx getQuestionImage() {
		return questions.get().get(0).image();
	}
	
	public static class QuestionComponent extends Component {

		private final Lazy<WebElementEx> questionText;
		private final Lazy<WebElementEx> image;
		private final Lazy<List<AnswerComponent>> answers;
		private final Lazy<WebElementEx> questionNumber;
		
		public QuestionComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.questionText = lazy(() -> findElement(By.className("question-text")));
			this.answers = lazy(() -> findElements(By.className("answer"), AnswerComponent::new));
			this.image = lazy(() -> findElement(By.tagName("img")));
			this.questionNumber = lazy(() -> findElement(By.tagName("span")));
		}
		
		@Override
		public String text() {
			return questionText.get().text();
		}

		public List<AnswerComponent> answers() {
			return answers.get();
		}
		
		public WebElementEx image() {
			return image.get();
		}
		
		public int number() {
			return  Integer.parseInt(questionNumber.get().text());
		}
	}

	public static class AnswerComponent extends Component {

		private final Lazy<WebElementEx> checkBox;
		private final Lazy<WebElementEx> answerText;
		public AnswerComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.checkBox = lazy(() -> findElement(By.className("checkbox")));
			this.answerText = lazy(() -> findElement(By.className("answer-text")));
		}
		
		public WebElementEx checkBox() {
			return checkBox.get();
		}
		
		public WebElementEx answerText() {
			return answerText.get();
		}
	}

}