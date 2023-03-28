package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.AuthorService;

@Tasks
public class AuthorTask {

	protected final AuthorService authorService;
	protected final ArticlesTask articlesTask;

	public AuthorTask(AuthorService authorService, ArticlesTask articlesTask) {
		this.authorService = authorService;
		this.articlesTask = articlesTask;
	}

	@Task(name = "AUTHOR")
	@TimedComponent(category = "task")
	public AuthorEx fetchAuthorById(@TaskParameter(name = "id") String id) {
		if (isBlank(id)) return null;
		
		return authorService.getAuthorById(id);
	}

	@Task(name = "AUTHOR")
	@Deprecated
	// Authors are no longer unique per document.
	// Uses of this task should be removed / replaced with calls to AttributionTask
	public AuthorEx fetchAuthorByDocument(@TaskParameter(name = "document") BaseDocumentEx document) {
		if (document == null) return null;

		return fetchAuthorById(document.getAuthorKey());
	}
}
