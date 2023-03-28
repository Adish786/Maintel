package com.about.mantle.model.services.resound;
import static com.about.mantle.endpoint.controllers.AbstractMantleEndpointController.Operation.DELETE;
import static com.about.mantle.endpoint.controllers.AbstractMantleEndpointController.Operation.SAVE;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.endpoint.controllers.AbstractMantleEndpointController.Operation;
import com.about.mantle.model.extended.resound.MantleResoundReviewStatusWrapper;
import com.about.mantle.model.extended.resound.MantleResoundReviewWriteWrapper;
import com.about.mantle.model.extended.resound.ResoundReactResponse;
import com.about.mantle.model.extended.resound.ResoundReactionRequest;
import com.about.mantle.model.extended.resound.ResoundReviewResponse;
import com.about.mantle.model.extended.resound.ResoundWriteReviewRequest;
import com.about.mantle.model.extended.resound.ResoundWriteReviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResoundService extends AbstractHttpServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResoundService.class);

    private static final ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
    private static final String REVIEW_PATH = "/review";
    private static final String REACTION_PATH = "/react";
    private static final String SELENE_PROVIDER = "selene";
    private final String brand;

    public ResoundService(HttpServiceClientConfig httpServiceClientConfig, String brand) {
        super(httpServiceClientConfig);
        this.brand = brand;
    }

    public MantleResoundReviewWriteWrapper submitReview(HttpServletRequest request) {

        String reviewText = extractParameter("reviewText", request.getParameterMap());

        String ratingString = extractParameter("rating", request.getParameterMap());
        Integer rating = null;

        try {
            rating = ratingString != null ? Integer.parseInt(ratingString) : null;
        } catch (NumberFormatException e) {
            throw new GlobeException("error parsing rating for submitReview", e);
        }

        String difficulty = extractParameter("difficulty", request.getParameterMap());
        String providerId = extractParameter("docId", request.getParameterMap());
        String provider = SELENE_PROVIDER;

        String cmsId = extractParameter("cmsId", request.getParameterMap());
        String[] splitcmsId = splitProviderAndProviderId(cmsId);
        if (splitcmsId != null) {
            provider = splitcmsId[0];
            providerId = splitcmsId[1];
        }

        String accessToken = extractParameter("accessToken", request.getParameterMap());

        String[] urlsArray = request.getParameterValues("photoUrl");
        List<String> urls = ArrayUtils.isEmpty(urlsArray) ? null : Arrays.asList(urlsArray);

        return submitReview(RequestContext.get(request), reviewText, rating, difficulty, provider, providerId, accessToken, urls);
    }

    public MantleResoundReviewWriteWrapper submitReview(RequestContext requestContext, String reviewText, Integer rating, String difficulty, String provider,
                                                        String providerId, String accessToken, List<String> urls) {

        if (!hasUserHashId(requestContext)) {
            throw new GlobeException("User not logged, unable to submit review");
        }

        if (rating == null || rating > 5 || rating < 0) {
            throw new GlobeException("Invalid Rating, rating must between 0 to 5");
        }

        return postReview(reviewText, rating, difficulty, provider, providerId, requestContext.getHashId(), accessToken, urls);
    }

    private MantleResoundReviewWriteWrapper postReview(String reviewText, Integer rating, String difficulty, String provider,
                                                       String providerId, String userHash, String accessToken, List<String> urls) {
        WebTarget webTarget = baseTarget.path(REVIEW_PATH);

        ResoundWriteReviewRequest resoundWriteReviewRequest =
                new ResoundWriteReviewRequest.Builder()
                        .provider(provider)
                        .providerId(providerId)
                        .userHash(userHash)
                        .brand(brand)
                        .text(reviewText)
                        .ratingDifficulty(difficulty)
                        .ratingFiveStar(rating)
                        .urls(urls)
                        .build();

        MantleResoundReviewWriteWrapper mantleResoundReviewWriteWrapper = new MantleResoundReviewWriteWrapper();
        Response resoundReviewResponse;
        try {
            /**
             * In all other Dotdash services we would call {@link AbstractHttpServiceClient#readResponse(WebTarget, HttpMethod)} here.
             * We can not do that because merediths API does not follow the json structure with `status` and `data` in the root object
             */
            resoundReviewResponse = sendRequest(webTarget, HttpMethod.POST, Entity.entity(resoundWriteReviewRequest, getConfig().getMediaType()), getConfig().getMediaType(), createHeaders(accessToken));
        } catch (Exception e) {
            LOGGER.error("Request failed: URI {}, HttpMethod {}, Reason {}", webTarget.getUri(), HttpMethod.POST, e.getMessage(), e);
            throw e;
        }
        mantleResoundReviewWriteWrapper.setStatus(resoundReviewResponse.getStatus());
        mantleResoundReviewWriteWrapper.setReview(parseResponse(resoundReviewResponse, ResoundWriteReviewResponse.class));

        String globeErrorMessage = "Unable to make Resound call.";
        String resoundMessage = "";

        if (mantleResoundReviewWriteWrapper.getReview() != null)
            resoundMessage = mantleResoundReviewWriteWrapper.getReview().getMessage();

        if (resoundReviewResponse.getStatus() == 500) {
            globeErrorMessage = "Could not enqueue request to Resound due to server-side issue";
            logErrorResponse(globeErrorMessage, resoundReviewResponse, resoundMessage);
        } else if (resoundReviewResponse.getStatus() != 202) {
            logErrorResponse(globeErrorMessage, resoundReviewResponse, resoundMessage);
        }

        return mantleResoundReviewWriteWrapper;
    }

    public MantleResoundReviewStatusWrapper reviewStatus(HttpServletRequest request) {
        String providerId = extractParameter("docId", request.getParameterMap());
        String provider = SELENE_PROVIDER;

        String cmsId = extractParameter("cmsId", request.getParameterMap());
        String[] splitcmsId = splitProviderAndProviderId(cmsId);
        if (splitcmsId != null) {
            provider = splitcmsId[0];
            providerId = splitcmsId[1];
        }

        String accessToken = extractParameter("accessToken", request.getParameterMap());
        return reviewStatus(RequestContext.get(request), provider, providerId, accessToken);
    }

    public MantleResoundReviewStatusWrapper reviewStatus(RequestContext requestContext, String provider, String providerId, String accessToken) {

        if (!hasUserHashId(requestContext)) {
            return null;
        }

        return getReviewAndStatus(provider, providerId, requestContext.getHashId(), accessToken);
    }

    private MantleResoundReviewStatusWrapper getReviewAndStatus(String provider, String providerId, String userHash, String accessToken) {
        WebTarget webTarget = baseTarget.path(REVIEW_PATH)
                .path(provider)
                .path(providerId)
                .path(brand)
                .path(userHash);

        Response resoundReviewResponse;
        try {
            /**
             * In all other Dotdash services we would call {@link AbstractHttpServiceClient#readResponse(WebTarget, HttpMethod)} here.
             * We can not do that because merediths API does not follow the json structure with `status` and `data` in the root object
             */
            resoundReviewResponse = sendRequest(webTarget, HttpMethod.GET, null, getConfig().getMediaType(), createHeaders(accessToken));
        } catch (Exception e) {
            LOGGER.error("Request failed: URI {}, HttpMethod {}, Reason {}", webTarget.getUri(), HttpMethod.GET, e.getMessage(), e);
            throw e;
        }
        MantleResoundReviewStatusWrapper reviewWrapper = new MantleResoundReviewStatusWrapper();
        reviewWrapper.setStatus(resoundReviewResponse.getStatus());
        reviewWrapper.setReview(parseResponse(resoundReviewResponse, ResoundReviewResponse.class));

        String globeErrorMessage = "Unable to make Resound call.";
        String resoundMessage = "";

        if (reviewWrapper.getReview() != null)
            resoundMessage = reviewWrapper.getReview().getMessage();

        if (resoundReviewResponse.getStatus() == 404) {
            //No review to find, not necessarily an error, user may not have written review for this document
        } else if (resoundReviewResponse.getStatus() != 200) {
            logErrorResponse(globeErrorMessage, resoundReviewResponse, resoundMessage);
        }

        return reviewWrapper;
    }

    public ResoundReactResponse submitReact(HttpServletRequest request, Operation operation) {
        RequestContext requestContext = RequestContext.get(request);

        if (!hasUserHashId(requestContext)) {
            throw new GlobeException("User not logged, unable to submit reaction");
        }

        String type = extractParameter("type", request.getParameterMap());

        String provider = null;
        String providerId = null;
        String legacyFeedbackId = extractParameter("legacyFeedbackId", request.getParameterMap());

        String [] splitcmsId = splitProviderAndProviderId(legacyFeedbackId);
        if (splitcmsId != null) {
            provider = splitcmsId[0];
            providerId = splitcmsId[1];
        }

        String userHash = requestContext.getHashId();
        String accessToken = extractParameter("accessToken", request.getParameterMap());

        if (operation == SAVE)
            return postReact(providerId, provider, brand, type, userHash, accessToken);
        else if (operation == DELETE) {
            return deleteReact(providerId, provider, brand, type, userHash, accessToken);
        } else {
            throw new GlobeException("Unsupported globe operation: " + operation);
        }
    }

    public ResoundReactResponse postReact(String providerId, String provider, String brand, String type, String userHash, String accessToken) {
        ResoundReactionRequest resoundReactionRequest = new ResoundReactionRequest.Builder()
                .setProviderId(providerId)
                .setProvider(provider)
                .setBrand(brand)
                .setType(type)
                .setUserHash(userHash)
                .build();
        WebTarget webTarget = baseTarget.path(REACTION_PATH);
        Response response;
        try {
            response = sendRequest(webTarget, HttpMethod.POST, Entity.entity(resoundReactionRequest, getConfig().getMediaType()), getConfig().getMediaType(), createHeaders(accessToken));
        } catch (Exception e) {
            LOGGER.error("Request failed: URI {}, HttpMethod {}, Reason {}", webTarget.getUri(), HttpMethod.POST, e.getMessage(), e);
            throw e;
        }

        ResoundReactResponse resoundReactResponse = parseResponse(response, ResoundReactResponse.class);

        String resoundMessage = "";

        if (resoundReactResponse != null) {
            resoundMessage = resoundReactResponse.getMessage();

            if (resoundReactResponse.getStatus() == null)
                resoundReactResponse.setStatus(response.getStatus());
        }

        if (response.getStatus() != 202)
            logReactErrorResponse(response, resoundMessage, accessToken);

        return resoundReactResponse;
    }
    public ResoundReactResponse deleteReact(String providerId, String provider, String brand, String type, String userHash, String accessToken) {
        WebTarget webTarget = baseTarget.path(REACTION_PATH)
                                            .path(type)
                                            .path(provider)
                                            .path(providerId)
                                            .path(userHash)
                                            .path(brand);

        Response response;
        try {
            response = sendRequest(webTarget, HttpMethod.DELETE, null, getConfig().getMediaType(), createHeaders(accessToken));
        } catch (Exception e) {
            LOGGER.error("Request failed: URI {}, HttpMethod {}, Reason {}", webTarget.getUri(), HttpMethod.DELETE, e.getMessage(), e);
            throw e;
        }
        ResoundReactResponse resoundReactResponse = parseResponse(response, ResoundReactResponse.class);

        String resoundMessage = "";

        if (resoundReactResponse != null) {
            resoundMessage = resoundReactResponse.getMessage();

            if (resoundReactResponse.getStatus() == null)
                resoundReactResponse.setStatus(response.getStatus());
        }

        if (response.getStatus() != 202)
            logReactErrorResponse(response, resoundMessage, accessToken);

        return resoundReactResponse;
    }

    private void logReactErrorResponse(Response response, String resoundMessage, String accessToken) {
        String globeErrorMessage = "Unable to make Resound call.";

        if (response.getStatus() == 500) {
            globeErrorMessage = "Could not enqueue request to Resound due to server-side issue.";
            logErrorResponse(globeErrorMessage, response, resoundMessage);
        } else if (response.getStatus() != 202) {
            logErrorResponse(globeErrorMessage, response, resoundMessage);
        }
    }

    private boolean hasUserHashId(RequestContext requestContext){
        return requestContext.getHashId() != null;
    }

    private String extractParameter(String parameter, Map<String, String[]> parametersMap) {
        String[] parameterValues = parametersMap.get(parameter);
        return ArrayUtils.isEmpty(parameterValues) ? null : stripToNull(parameterValues[0]);
    }

    private Map<String, Object> createHeaders(String accessToken){

        if(accessToken == null){
            throw new GlobeException("Error access token not set");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("brand", brand);
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);
        return headers;
    }

    /**
     * splitProviderAndProviderId
     *
     * resoundId is a complex string that follows the pattern PROVIDER__providerId
     * We need to split it if it's here and have that be what goes to Resound
     * If resoundId isn't present return null
     */
    private String [] splitProviderAndProviderId(String resoundId){
        String [] splitResoundId = null;

        if (resoundId != null) {
            splitResoundId = resoundId.split("__", 2);

            if (splitResoundId.length != 2) {
                throw new GlobeException("Incorrect format for resoundId " + resoundId);
            }
        }

        return splitResoundId;
    }

    /**
     * Not all resound calls return json. Some 400 responses are text.
     * @param <T>
     * @param response
     * @param responseType
     * @return
     */
    private <T> T parseResponse(Response response, Class<T> responseType) {
        T answer = null;
        String responseString = null;
        try {
            // Entity can only be read once so read it as a string and then
            // try to parse that string as the desired object.
            responseString = response.readEntity(String.class);
            answer = objectMapper.readValue(responseString, responseType);
        } catch (Exception e) {
            LOGGER.error("Failed to parse response: {}", responseString, e);
        }
        return answer;
    }

    private void logErrorResponse(String globeErrorMsg, Response response, String resoundMessage) {
        LOGGER.error(globeErrorMsg + " Http error: " + response.getStatus() + " " + response.getStatusInfo() + " Resound message: " + resoundMessage);
    }
}
