package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.SuggestionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.SuggestionService;
import com.about.mantle.model.services.SuggestionService.SuggestionSearchRequestContext;
import com.about.mantle.utils.MantleSolrClientUtils;

@Tasks
public class SuggestionTask {

	protected final SuggestionService suggestionService;

	public SuggestionTask(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	@Task(name = "suggestionSearch")
	@TimedComponent(category = "task")
	public SliceableListEx<SuggestionSearchResultItemEx> suggestionSearch(@TaskParameter(name = "query") String query,
			@TaskParameter(name = "filters") List<String> filters, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit) {

		if (isBlank(query)) return SliceableListEx.emptyList();
		
		SuggestionSearchRequestContext requestContext = new SuggestionSearchRequestContext.Builder()
				.setQuery('*'+query+'*')
				.setFilterQueries(filters)
				.setLimit(limit)
				.setOffset(offset)
				.setSort(sort)
				.build();
		return suggestionService.search(requestContext);
	}

}
