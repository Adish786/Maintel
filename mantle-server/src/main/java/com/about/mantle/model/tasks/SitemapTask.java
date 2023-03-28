package com.about.mantle.model.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.google.common.collect.ImmutableList;

@Tasks
public class SitemapTask {
	protected final DeionSearchService deionSearchService;

	public SitemapTask(DeionSearchService deionSearchService) {
		this.deionSearchService = deionSearchService;
	}

	/**
	 * Invoke all the task methods outside the context of a rendered template to ensure that
	 * the underlying services are hydrated and primed for serving the rendered template request.
	 * @param sitemapTask
	 * @param domain
	 */
	public void preloadSitemap(String domain) {
		if (deionSearchService != null) {
			int numPages = numSiteMapIndexes(domain);
			for (int page = 1; page <= numPages; ++page) {
				sitemapPage(domain, page);
			}
		}
	}

	@Task(name = "numSiteMapIndexes")
	public Integer numSiteMapIndexes(@RequestContextTaskParameter RequestContext requestContext) {
		return numSiteMapIndexes(requestContext.getUrlData().getDomain());
	}

	@Task(name = "numSiteMapIndexes")
	@TimedComponent(category = "task")
	public Integer numSiteMapIndexes(@TaskParameter(name = "domain") String domain) {
		// @formatter:off
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setNoCache(true)
			   .setDomain(domain)
			   .setQuery(getSitemapSearchQuery())
			   .setLimit(0);
		// @formatter:on
		DeionSearchResultEx indexResult = deionSearchService.search(builder.build());
		int entriesPerFile = getEntriesPerFile();
		int numSitemaps = 0;
		if (indexResult != null) {
			numSitemaps = Math.toIntExact(indexResult.getNumFound() / entriesPerFile);
			if (indexResult.getNumFound() % entriesPerFile > 0) {
				numSitemaps += 1;
			}
		}
		return numSitemaps;
	}

	@Task(name = "sitemapPage")
	public DeionSearchResultEx sitemapPage(@RequestContextTaskParameter RequestContext requestContext) {
		return sitemapPage(requestContext.getUrlData().getDomain(), getPageNum(requestContext.getUrlData().getPath()));
	}

	@Task(name = "sitemapPage")
	@TimedComponent(category = "task")
	public DeionSearchResultEx sitemapPage(@TaskParameter(name = "domain") String domain,
			@TaskParameter(name = "page") int page) {
		int entriesPerFile = getEntriesPerFile();
		// @formatter:off
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setNoCache(true)
			   .setDomain(domain)
			   .setQuery(getSitemapSearchQuery())
			   .setFields(ImmutableList.of("url", "displayed"))
			   .setSort("displayed DESC")
			   .setOffset((page - 1) * entriesPerFile)
			   .setLimit(entriesPerFile);
		// @formatter:on
		return deionSearchService.search(builder.build());
	}

	@Task(name = "sitemapUrlPrefix")
	@TimedComponent(category = "task")
	public String sitemapUrlPrefix(@RequestContextTaskParameter RequestContext requestContext)
			throws UnsupportedEncodingException, URISyntaxException {
		return requestContext.getUrlData().with().path("/sitemap").query(null).fragment(null).build().getUrl();
	}

	private int getPageNum(String path) {
		int extension = path.lastIndexOf(".");
		int underscore = path.lastIndexOf("_", extension);
		return Integer.parseInt(path.substring(underscore + 1, extension));
	}

	private String getSitemapSearchQuery() {
		return DeionSearchRequestContext.getDefaultTemplateTypesFilterOutQuery() + " AND -noIndex:true AND state:ACTIVE";
	}

	private int getEntriesPerFile() {
		return DeionSearchService.SUGGESTED_MAX_LIMIT;
	}
}