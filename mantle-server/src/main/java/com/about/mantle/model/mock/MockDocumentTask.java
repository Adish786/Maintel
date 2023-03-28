package com.about.mantle.model.mock;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.document.preprocessor.DocumentPreprocessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Tasks
public class MockDocumentTask {
	private final MockSource<BaseDocumentEx> documentMocks;
	private List<DocumentPreprocessor> documentPreprocessors;

	public MockDocumentTask(ObjectMapper objectMapper, List<DocumentPreprocessor> documentPreprocessors) {
		this.documentMocks = new MockSource<BaseDocumentEx>("document",
				objectMapper.getTypeFactory().constructType(BaseDocumentEx.class));
		this.documentPreprocessors = documentPreprocessors;
	}

	@Task(name = "mockDocument")
	public BaseDocumentEx mockDocument(@TaskParameter(name = "template") String template) {
		return processDocument(documentMocks.get(template));
	}

	@Task(name = "mockDocument")
	public BaseDocumentEx mockDocument(@TaskParameter(name = "template") String template,
			@TaskParameter(name = "includeSummaries") Boolean includeSummaries) {
		return mockDocument(template);
	}

	public BaseDocumentEx processDocument(BaseDocumentEx document) {

		for (DocumentPreprocessor documentPreprocessor : documentPreprocessors) {
			document = documentPreprocessor.preProcessDocument(document);
		}
		return document;
	}
}
