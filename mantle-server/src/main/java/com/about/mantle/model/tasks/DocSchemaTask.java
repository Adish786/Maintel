package com.about.mantle.model.tasks;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.DocSchemaService;

@Tasks
public class DocSchemaTask {

	protected final DocSchemaService docSchemaService;

	public DocSchemaTask(DocSchemaService docSchemaService) {
		this.docSchemaService = docSchemaService;
	}

	@Task(name = "docSchema")
	public String getDocSchemaByDocId(@TaskParameter(name = "docId") Long docId) {
		if (docSchemaService == null) {
			return null;
		}
		return docSchemaService.getDocSchemaByDocId(docId);
	}
}
