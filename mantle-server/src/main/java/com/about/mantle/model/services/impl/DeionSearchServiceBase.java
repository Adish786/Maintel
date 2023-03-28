package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.FacetRangeQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class DeionSearchServiceBase extends AbstractHttpServiceClient {

	public DeionSearchServiceBase(HttpServiceClientConfig httpClientConfig) {
	    super(httpClientConfig);
	}

	/**
	 * Creates a webTarget from requestContext from for a specific end point
	 */
	protected WebTarget createWebTarget(DeionSearchService.DeionSearchRequestContext reqCtx, String path) {
		WebTarget webTarget = baseTarget.path(path);
		DeionSearchService.DeionSearchRequestContext.DeionSearchKey searchKey = reqCtx.getDeionSearchKey();

		if (searchKey.getQuery() != null) {
			webTarget = webTarget.queryParam("query", "{q}").resolveTemplate("q", searchKey.getQuery());
		}

		if (searchKey.getBoost() != null) {
			webTarget = webTarget.queryParam("boost", "{b}").resolveTemplate("b", searchKey.getBoost());
		}

		if (reqCtx.getDeionSearchKey().getDomain() != null) {
			webTarget = webTarget.queryParam("domain", reqCtx.getDeionSearchKey().getDomain());
		}

		if (CollectionUtils.isNotEmpty(searchKey.getFilterQueries())) {
			for (String s : searchKey.getFilterQueries()) {
				if (StringUtils.isNotEmpty(s))
					webTarget = webTarget.queryParam("filterQuery", s);
			}
		}

		if (CollectionUtils.isNotEmpty(searchKey.getFields())) {
			for (String s : searchKey.getFields()) {
				if (StringUtils.isNotEmpty(s))
					webTarget = webTarget.queryParam("field", s);
			}
		}

		if (CollectionUtils.isNotEmpty(searchKey.getFacetFields())) {
			for (String s : searchKey.getFacetFields()) {
				if (StringUtils.isNotEmpty(s))
					webTarget = webTarget.queryParam("facetField", s);
			}
		}
		
		if (searchKey.getFacetRangeQueryList() != null) {
			for (FacetRangeQuery s : searchKey.getFacetRangeQueryList()) {
				webTarget = webTarget.queryParam("facet.range", s.getField());
				webTarget = webTarget.queryParam("f."+s.getField()+".facet.range.gap", s.getGap());
				webTarget = webTarget.queryParam("f."+s.getField()+".facet.range.start", s.getStart());
				webTarget = webTarget.queryParam("f."+s.getField()+".facet.range.end", s.getEnd());
			}
		}


		if (searchKey.getOffset() != null) {
			webTarget = webTarget.queryParam("offset", searchKey.getOffset());
		}

		if (searchKey.getLimit() != null) {
			webTarget = webTarget.queryParam("limit", searchKey.getLimit());
		}

		if (searchKey.getSort() != null) {
			webTarget = webTarget.queryParam("sort", searchKey.getSort());
		}

		webTarget = webTarget.queryParam("noCache", searchKey.isNoCache());

		webTarget = webTarget.queryParam("includeDocumentSummaries", reqCtx.getIncludeDocumentSummaries());
		if (reqCtx.getProjection() != null) {
			webTarget = webTarget.queryParam("projection", "{p}").resolveTemplate("p",
					reqCtx.getProjection().toString());
		}
		
		if(searchKey.getTraversalStartDocId() != null){
			webTarget = webTarget.queryParam("traversalStartDocId", searchKey.getTraversalStartDocId().toArray());
		}

		if(searchKey.getTraversalExcludedDocId() != null){
			webTarget = webTarget.queryParam("traversalExcludedDocId", searchKey.getTraversalExcludedDocId().toArray());
		}

		if(searchKey.getTraversalRelationships() != null){
			webTarget = webTarget.queryParam("traversalRelationship", searchKey.getTraversalRelationships().toArray());
		}

		return webTarget;
	}

}
