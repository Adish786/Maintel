package com.about.mantle.model.mock;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.journey.JourneyRoot;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockJourneyTask {
    private final MockSource<JourneyRoot> journeyRootMock;

    public MockJourneyTask(ObjectMapper objectMapper) {
        this.journeyRootMock = new MockSource<>("journey",
                objectMapper.getTypeFactory().constructType(JourneyRoot.class));
    }

    @Task(name = "mockJourney")
    public JourneyRoot mockJourney(@TaskParameter(name = "template") String template) {
        return journeyRootMock.get(template);
    }
}
