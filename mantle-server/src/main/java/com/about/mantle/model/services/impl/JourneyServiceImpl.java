package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.responses.TaxeneExResponse;
import com.about.mantle.model.journey.JourneyRelationshipType;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.services.JourneyService;
import org.apache.commons.lang3.tuple.Pair;

public class JourneyServiceImpl extends AbstractHttpServiceClient implements JourneyService {

	public static final String SELENE_TAXENE_PATH = "taxene";
	public static final String SELENE_TAXENE_JOURNEY_PATH = "journey";
	private static final String LINKED_JOURNEY_TAXENE_RELATIONSHIP = "linkedJourney";

	public JourneyServiceImpl(HttpServiceClientConfig httpClientConfig) {
		super(httpClientConfig);
	}

	public JourneyServiceImpl(WebTarget target, HttpServiceClientConfig httpClientConfig) {
		super(httpClientConfig);
		this.baseTarget = target;
	}

    @Override
    public Pair<JourneyRoot, JourneyRelationshipType> getJourneyRootAndRelationship(JourneyRequestContext reqCtx) {

		JourneyRelationshipType relationshipType = null;
		JourneyRoot root = null;

		// First we see if the relationship is a 'linkedJourney'.  If not, then we check to see if it's a regular
		// journey relationship

        TaxeneExResponse response = getJourney(reqCtx, LINKED_JOURNEY_TAXENE_RELATIONSHIP, TaxeneExResponse.class);
		TaxeneNodeEx rootTaxeneNode = response == null ? null : response.getData();
        root = rootTaxeneNode == null ? null : new JourneyRoot(rootTaxeneNode);

        if (root != null) {
            relationshipType = JourneyRelationshipType.LINKED;
		} else {
			// no root found for linked, try 'regular' journey relationship.
			response = getJourney(reqCtx, null, TaxeneExResponse.class);
			rootTaxeneNode = response == null ? null : response.getData();
			root = rootTaxeneNode == null ? null : new JourneyRoot(rootTaxeneNode);
			relationshipType = JourneyRelationshipType.MEMBER;
		}

		if (root == null) {
		    relationshipType = JourneyRelationshipType.NONE;
		}

        return Pair.of(root, relationshipType);

    }

	/**
	 *
	 * @param reqCtx
	 * @param linkedType If null, document in reqCtx must be _part of_ a journey.  If not-null, document in reqCtx will
	 *                   be _linked_ to the journey using `linkedType` as the relationship type.
	 * @param bindToTarget
	 * @param <T>
	 * @return
	 */
	private <T extends BaseResponse<?>> T getJourney(JourneyRequestContext reqCtx, String linkedType, Class<T> bindToTarget) {
		if (reqCtx == null) throw new NullPointerException("JourneyRequestContext can not be null");

		WebTarget webTarget = baseTarget.path(SELENE_TAXENE_PATH).path(SELENE_TAXENE_JOURNEY_PATH)
				.path(reqCtx.getDocId().toString());

		if (linkedType != null) {
			webTarget = webTarget.queryParam("linkedType", linkedType);
		}
		webTarget = webTarget.queryParam("includeDocumentSummaries", reqCtx.getIncludeDocumentSummaries());

		if (reqCtx.getProjection() != null) {
			webTarget = webTarget.queryParam("projection", "{p}").resolveTemplate("p", reqCtx.getProjection());
		}

		return readResponse(webTarget, bindToTarget, HttpMethod.GET, super.getConfig().getMediaType());
	}
}
