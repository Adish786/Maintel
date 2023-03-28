package com.about.mantle.model.tasks;

import java.util.Collections;
import java.util.Map;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.EmbedService;
import com.about.mantle.model.services.embeds.EmbedContent;

/**
 * Tasks for handling the /embed endpoint which is requested by the EMBED structured content block.
 */
@Tasks
public class EmbedTask {

	private final EmbedService service;

	public EmbedTask(EmbedService service) {
		this.service = service;
	}

	/**
	 * Intended to be used by the /embed endpoint to serve the embed content inside an iframe.
	 */
	@Task(name = "embed")
	public EmbedContent embed(@TaskParameter(name = "url") String url) {
		return embed(url, Collections.emptyMap());
	}

	/**
	 * Intended to be used by the /embed endpoint to serve the embed content inside an iframe.
	 * Options parameter represent options for all embed providers, e.g. twitter, giphy, etc.
	 */
	@Task(name = "embed")
	public EmbedContent embed(@TaskParameter(name = "url") String url,
			@TaskParameter(name = "options") Map<String, Map<String, String>> options) {
		return service.getContent(url, options);
	}
}
