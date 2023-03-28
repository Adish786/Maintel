package com.about.mantle.model.mock;

import java.util.Map;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockPrebidTask {

	private final MockSource<Map<String, Object>> mockConfigs;

	public MockPrebidTask(ObjectMapper objectMapper) {
		this.mockConfigs = new MockSource<Map<String, Object>>("prebid",
				objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
	}

	@Task(name = "mockPrebidConfig")
	public Map<String, Object> mockPrebidConfig() {
		return mockConfigs.get("prebid");
	}

	@Task(name = "mockPrebidConfig")
	public Map<String, Object> mockPrebidConfig(@TaskParameter(name = "mockId") String mockId) {
		return mockConfigs.get(mockId);
	}

}