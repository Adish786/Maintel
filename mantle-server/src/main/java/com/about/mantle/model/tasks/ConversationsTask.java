package com.about.mantle.model.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class ConversationsTask {

    private static final Logger logger = LoggerFactory.getLogger(ConversationsTask.class);

    private final BusinessOwnedVerticalDataService bovdService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConversationsTask(BusinessOwnedVerticalDataService bovdService) {
        this.bovdService = bovdService;
    }

    /**
     * Create a task to provide data for conversations feature (e.g. 'Money Talks' for TheBalance, 'Healthy Conversations' for Verywell).
     * This data is stored in BOVD repository as a JSON file.
     *
     * @param filePath Absolute file path in the BOVD repository. Must be a JSON file.
     *                 e.g. 'money/money-talks/retirement-savings.json'
     */
    @Task(name = "conversations")
    public List<Map<String, Object>> conversationsData(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "filePath") String filePath) {
        List<Map<String, Object>> conversationItems = new ArrayList<>();

        if (StringUtils.isBlank(filePath)) {
            logger.warn("An empty JSON filepath is provided from XML component for request {}", requestContext.getRequestUri());
        } else if (!StringUtils.endsWithIgnoreCase(filePath, ".json")) {
            logger.warn("An invalid JSON file {} is provided from XML component for request {}", filePath, requestContext.getRequestUri());
        } else {
            try {
                byte[] bytes = bovdService.getResource(filePath);
                TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<List<HashMap<String, Object>>>() {};
                conversationItems = (List<Map<String, Object>>) (List<? extends Map<String, Object>>) objectMapper.readValue(bytes, typeRef);
            } catch (GlobeException | IOException ex) {
                logger.error("Could not read/parse JSON data from BOVD service.", ex);
            }
        }
        return conversationItems;
    }
}