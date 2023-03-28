package com.about.mantle.model.mock;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockArticlesTask {
	private final MockSource<SliceableListEx<BaseDocumentEx>> recircMocks;

	public MockArticlesTask(ObjectMapper objectMapper) {
		this.recircMocks = new MockSource<SliceableListEx<BaseDocumentEx>>("recirc",
				objectMapper.getTypeFactory().constructParametricType(SliceableListEx.class, BaseDocumentEx.class));
	}
	
	@Task(name = "mockArticles")
	public SliceableListEx<BaseDocumentEx> mockArticlesRecirc(@TaskParameter(name = "recircType") String recircType) {
		return recircMocks.get(recircType);
	}
}
