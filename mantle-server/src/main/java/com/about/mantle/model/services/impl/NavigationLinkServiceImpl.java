package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.CategoryLinkPageResponse;
import com.about.mantle.model.services.NavigationLinkService;

/*
 * Can be removed as this service does not exist anymore
 */
public class NavigationLinkServiceImpl extends AbstractHttpServiceClient implements NavigationLinkService {

	private static final String SELENE_NAVIGATIONLINKS_PATH = "/navigationlink";
	
	public NavigationLinkServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
		baseTarget = baseTarget.path(SELENE_NAVIGATIONLINKS_PATH);
	}
	
	@Override
	public SliceableListEx<CategoryLinkEx> getNavigationLinks(String url, PageRequest pageRequest) {

		CategoryLinkPageResponse response = getNavigationLinks(url, pageRequest, null, CategoryLinkPageResponse.class);

		SliceableListEx<CategoryLinkEx> data = response.getData();
		
		if (data == null) {
			return data;
		}
		data.setLimit(pageRequest.getLimit());
		data.setOffset(pageRequest.getOffset());
		
		return data;

	}
	
	private <T extends BaseResponse<?>> T getNavigationLinks(String url, PageRequest pageRequest, String projection, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.queryParam("url", url);

		if (pageRequest != null && pageRequest.getLimit() != null) webTarget = webTarget.queryParam("limit",
				pageRequest.getLimit());

		if (pageRequest != null && pageRequest.getOffset() != null) webTarget = webTarget.queryParam("offset",
				pageRequest.getOffset());
		
		if (projection != null) {
			webTarget = webTarget.queryParam("projection", "{p}").resolveTemplate("p", projection);
		}
		return readResponse(webTarget, bindToTarget);
	}

}
