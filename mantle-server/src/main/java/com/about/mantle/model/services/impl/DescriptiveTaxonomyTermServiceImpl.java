package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermList;
import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermParsedData;
import com.about.mantle.model.services.DescriptiveTaxonomyTermService;
import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DescriptiveTaxonomyTermServiceImpl extends AbstractHttpServiceClient implements DescriptiveTaxonomyTermService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptiveTaxonomyTermServiceImpl.class);
	private static final String TAXONOMY_GROUP_PATH = "/taxonomy/light/groups";
	private static final String GROUP_ID_PATH = "/udt";
	private final Map<String, Object> header;


	public DescriptiveTaxonomyTermServiceImpl(HttpServiceClientConfig httpServiceClientConfig, String apiKey) {
		super(httpServiceClientConfig);
		this.baseTarget = baseTarget.path(TAXONOMY_GROUP_PATH);
		header = ImmutableMap.of("x-ssst", apiKey);
	}

	@Override
	public DescriptiveTaxonomyTermParsedData generateDescriptionTaxonomyTermParsedData() {
		return mapTaxonomy(getLegacyTaxonomy());
	}

	private List<DescriptiveTaxonomyTermList.Term> getLegacyTaxonomy() {
		WebTarget webTarget = baseTarget.path(GROUP_ID_PATH);

		/**
		 * In all other Dotdash services we would call {@link AbstractHttpServiceClient#readResponse(WebTarget, HttpMethod)} here.
		 * We can not do that because merediths API does not follow the json structure with `status` and `data` in the root object
		 */
		Response descriptiveTaxonomyTermsResponse = sendRequest(webTarget, HttpMethod.GET, null, getConfig().getMediaType(), header);
		if (descriptiveTaxonomyTermsResponse.getStatus() == 424) {
			LOGGER.error("Unable to make Legacy Meredith call. Meredith Error: " + descriptiveTaxonomyTermsResponse.readEntity(String.class) );
			return null;
		} else if (descriptiveTaxonomyTermsResponse.getStatus() != 200) {
			LOGGER.error("Unable to make Legacy Meredith call. Http error: " + descriptiveTaxonomyTermsResponse.getStatus() + " " + descriptiveTaxonomyTermsResponse.getStatusInfo() );
			return null;
		}

		DescriptiveTaxonomyTermList taxonomyTerms;
		try {
			taxonomyTerms = descriptiveTaxonomyTermsResponse.readEntity(DescriptiveTaxonomyTermList.class);
		} catch (MessageBodyProviderNotFoundException e) {
			LOGGER.error("Unable to make Legacy Meredith call. Meredith API has specified that they are giving us plain text when they should be specifying json content.");
			return null;
		}
		return taxonomyTerms.getTerms();
	}

	private DescriptiveTaxonomyTermParsedData mapTaxonomy(List<DescriptiveTaxonomyTermList.Term> legacyTaxonomy) {
		Map<String, List<String>> descriptiveTaxonomyTermMap = new HashMap<>();
		HashSet<String> adTaxonomyIds = new HashSet<>();


		if (legacyTaxonomy == null) return null;
		for (DescriptiveTaxonomyTermList.Term term : legacyTaxonomy) {
			if (term.getParents() != null) {
				descriptiveTaxonomyTermMap.put(term.getId(), term.getParents());
			}
			if (term.getGroups().contains("adg")) {
				adTaxonomyIds.add(term.getId());
			}
		}

		return new DescriptiveTaxonomyTermParsedData(descriptiveTaxonomyTermMap, adTaxonomyIds);
	}
}
