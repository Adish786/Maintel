package com.about.mantle.model.mock;

import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockDocumentTaxeneCompositeTask {
	private final MockSource<List<DocumentTaxeneComposite<BaseDocumentEx>>> documentTaxeneCompositeListMocks;

	public MockDocumentTaxeneCompositeTask(ObjectMapper objectMapper) {
		this.documentTaxeneCompositeListMocks = new MockSource<List<DocumentTaxeneComposite<BaseDocumentEx>>>("documentTaxeneCompositeList",
				objectMapper.getTypeFactory().constructParametricType(List.class, DocumentTaxeneComposite.class));
	}

	@Task(name = "mockDocumentTaxeneList")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getDocumentTaxeneCompositeList(@TaskParameter(name = "mockName") String mockName) {
		return documentTaxeneCompositeListMocks.get(mockName);
	}
}