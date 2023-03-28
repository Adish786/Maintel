package com.about.mantle.model.mock;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.AuthorEx;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockAuthorTask {
    private final MockSource<AuthorEx> authorMock;

    public MockAuthorTask(ObjectMapper objectMapper) {
        this.authorMock = new MockSource<AuthorEx>("author",
                objectMapper.getTypeFactory().constructType(AuthorEx.class));
    }

    @Task(name = "mockAuthor")
    public AuthorEx mockAuthor(@TaskParameter(name = "template") String template) {
        return authorMock.get(template);
    }
}