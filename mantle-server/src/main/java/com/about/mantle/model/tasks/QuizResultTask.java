package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.quiz.QuizResultEx;

@Tasks
public class QuizResultTask {

	@Task(name = "QUIZRESULT")
	public QuizResultEx getQuizResult(@TaskParameter(name = "quizResults") SliceableListEx<QuizResultEx> quizResults,
			@RequestContextTaskParameter RequestContext requestContext) {

		String quizResultParam = requestContext.getParameterSingle("quizResult");

		if (quizResultParam != null) {
			for (QuizResultEx result : quizResults.getList()) {
				String id = result.getId();
				if (id.startsWith(quizResultParam)) {
					return result;
				}
			}
		}

		return null;
	}
}
