package com.about.mantle.model.tasks;

	
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.stream.Collectors;

import com.about.mantle.model.extended.SearchEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.responses.SearchExPageResponse;
import com.about.mantle.model.services.SearchService;
import com.about.mantle.model.services.SearchService.AlgorithmType;
import com.google.common.collect.Sets;

@Tasks
public class SearchTask {

	public static final Logger logger = LoggerFactory.getLogger(SearchTask.class);
	private static final Pattern REMOVEDCHARACTERS = Pattern.compile("[{}]+");
	
	/*
	If allowFuzzy is enabled then selene allows us to fuzzy search terms.
	If ~2 is appended onto a search term selene will search for: that term + any terms within 2 character changes of it
	https://solr.apache.org/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-FuzzySearches
	 */
	private static final Integer DEFAULT_FUZZY_DISTANCE = 2;
	private static final Integer NUMBER_OF_WORDS_TO_APPEND_FUZZY_TO = 4;
	private static final Integer SMALLEST_CHAR_SET_WORD_TO_ADD_FUZZY_TO = 4;
	private static final Integer LARGEST_CHAR_SET_WORD_TO_ADD_FUZZY_TO = 19;
	private static final Integer LARGEST_CHAR_SET_QUERY_TO_ADD_FUZZY_TO = 128;

	protected final SearchService searchService;

	public SearchTask(SearchService searchService) {
		this.searchService = searchService;
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "boost") Integer boost, @TaskParameter(name = "offset") Integer offset,
			@TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "useSolr") Boolean useSolr,
			@TaskParameter(name = "algoType") String algorithmType,
			@TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
			@TaskParameter(name = "SUName") String SUName) {
		return getSearchResults(requestContext, boost, offset, limit, useSolr, algorithmType, projection, q, SUName, null);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
												 @TaskParameter(name = "boost") Integer boost, @TaskParameter(name = "offset") Integer offset,
												 @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "useSolr") Boolean useSolr,
												 @TaskParameter(name = "algoType") String algorithmType,
												 @TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
												 @TaskParameter(name = "SUName") String SUName, @TaskParameter(name = "tags") Set<String> tags) {
		return getSearchResults(requestContext, boost, offset, limit, useSolr, algorithmType, projection, q, SUName, tags, false);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
												 @TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
												 @TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "algoType") String algorithmType,
												 @TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
												 @TaskParameter(name = "SUName") String SUName, @TaskParameter(name = "allowFuzzy") Boolean allowFuzzy) {
		return getSearchResults(requestContext, null, offset, limit, useSolr, algorithmType, projection, q, SUName, null, allowFuzzy, DEFAULT_FUZZY_DISTANCE);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
												 @TaskParameter(name = "boost") Integer boost, @TaskParameter(name = "offset") Integer offset,
												 @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "useSolr") Boolean useSolr,
												 @TaskParameter(name = "algoType") String algorithmType,
												 @TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
												 @TaskParameter(name = "SUName") String SUName, @TaskParameter(name = "tags") Set<String> tags,
												 @TaskParameter(name = "allowFuzzy") Boolean allowFuzzy) {
		return getSearchResults(requestContext, boost, offset, limit, useSolr, algorithmType, projection, q, SUName, tags, allowFuzzy, DEFAULT_FUZZY_DISTANCE);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "boost") Integer boost, @TaskParameter(name = "offset") Integer offset,
			@TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "useSolr") Boolean useSolr,
			@TaskParameter(name = "algoType") String algorithmType,
			@TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
			@TaskParameter(name = "SUName") String SUName, @TaskParameter(name = "tags") Set<String> tags,
		    @TaskParameter(name = "allowFuzzy") Boolean allowFuzzy, @TaskParameter(name = "fuzzyDistance") Integer fuzzyDistance) {
		
		AlgorithmType algoType = null;
		if (algorithmType != null) {
			try {
				algoType = AlgorithmType.valueOf(algorithmType);
			} catch (IllegalArgumentException e) {
				logger.error("Bad algorithm type: " + algorithmType);
			}
		}

		// Remove special characters
		q = REMOVEDCHARACTERS.matcher(q).replaceAll("");
		if (isBlank(q)) return new SearchExPageResponse();

		// if Fuzzy match is turned on and the query is valid, add the number to fuzzy match to
		if (Boolean.TRUE.equals(allowFuzzy) && isQueryEligibleForFuzzySearch(q)) {
			StringBuilder stringBuilder = new StringBuilder();
			String[] words = StringUtils.split(q);
			int wordsWithFuzzySearchOn = 0;
			for (String word : words) {
				stringBuilder.append(word);
				if (isValidWord(word) && wordsWithFuzzySearchOn < NUMBER_OF_WORDS_TO_APPEND_FUZZY_TO) {
					stringBuilder.append("~");
					stringBuilder.append(fuzzyDistance != null ? fuzzyDistance : DEFAULT_FUZZY_DISTANCE);
					wordsWithFuzzySearchOn++;
				}
				stringBuilder.append(" ");
			}
			q = stringBuilder.toString();
		}

		SearchExPageResponse result = searchService.getSearchResults(q, allowFuzzy, offset, limit, algoType, projection, tags);

		if (result.getData() != null) {
			result.setData(
					result.getData().stream().filter((searchResult) -> StringUtils.isNotEmpty(searchResult.getTitle())
							&& StringUtils.isNotEmpty(searchResult.getUrl())).collect(Collectors.toList()));
		}
		return result;
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "algoType") String algorithmType,
			@TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
			@TaskParameter(name = "SUName") String SUName) {
		return getSearchResults(requestContext, null, offset, limit, useSolr, algorithmType, projection, q, SUName);
	}
	
	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "algoType") String algorithmType,
			@TaskParameter(name = "projection") String projection, @TaskParameter(name = "q") String q,
			@TaskParameter(name = "SUName") String SUName, @TaskParameter(name = "tags") String tags) {
		return getSearchResults(requestContext, null, offset, limit, useSolr, algorithmType, projection, q, SUName, createTagsSet(tags));
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
												 @TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
												 @TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "algoType") String algorithmType,
												 @TaskParameter(name = "q") String q, @TaskParameter(name = "SUName") String SUName,
												 @TaskParameter(name = "allowFuzzy") Boolean allowFuzzy) {
		return getSearchResults(requestContext, null, offset, limit, useSolr, algorithmType, null, q, SUName, null, allowFuzzy);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
												 @TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
												 @TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "algoType") String algorithmType,
												 @TaskParameter(name = "q") String q, @TaskParameter(name = "SUName") String SUName) {
		return getSearchResults(requestContext, null, offset, limit, useSolr, algorithmType, null, q, SUName);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "useSolr") Boolean useSolr, @TaskParameter(name = "q") String q,
			@TaskParameter(name = "SUName") String SUName) {

		return getSearchResults(requestContext, offset, limit, useSolr, null, null, q, SUName);
	}

	@Task(name = "searchResults")
	@TimedComponent(category = "task")
	public SearchExPageResponse getSearchResults(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "q") String q, @TaskParameter(name = "SUName") String SUName) {

		return getSearchResults(requestContext, offset, limit, false, q, SUName);
	}

	@Task(name = "convertSearchResultsToDocuments")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> convertSearchResultsToDocuments(
			@TaskParameter(name = "searchResults") SearchExPageResponse searchResults) {

		return Optional.ofNullable(searchResults.getData()).orElse(Collections.emptyList())
				.stream()
				.filter(Objects::nonNull)
				.map(SearchEx::getDocument)
				.collect(Collectors.toList());
	}
	
	protected Set<String> createTagsSet(String tags) {
		return Sets.newHashSet(split(defaultString(tags, ""), ','));
	}

	protected boolean isQueryEligibleForFuzzySearch(String q) {
		return q.length() <= LARGEST_CHAR_SET_QUERY_TO_ADD_FUZZY_TO;
	}

	protected boolean isValidWord(String word) {
		int length = word.length();
		return length >= SMALLEST_CHAR_SET_WORD_TO_ADD_FUZZY_TO && length <= LARGEST_CHAR_SET_WORD_TO_ADD_FUZZY_TO && StringUtils.isAlpha(word);
	}

}
