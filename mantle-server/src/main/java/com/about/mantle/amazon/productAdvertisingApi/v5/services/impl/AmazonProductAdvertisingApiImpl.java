package com.about.mantle.amazon.productAdvertisingApi.v5.services.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApi;
import com.amazon.paapi5.v1.ApiClient;
import com.amazon.paapi5.v1.Condition;
import com.amazon.paapi5.v1.GetItemsRequest;
import com.amazon.paapi5.v1.GetItemsResource;
import com.amazon.paapi5.v1.GetItemsResponse;
import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.PartnerType;
import com.amazon.paapi5.v1.api.DefaultApi;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class AmazonProductAdvertisingApiImpl implements AmazonProductAdvertisingApi {

    private Logger logger = LoggerFactory.getLogger(AmazonProductAdvertisingApi.class);

    /**
     * Used when requesting (for instance) more than one item at a time.  Maximum number of items to send on a single
     * request.  Amazon allows a max of 10
     */
    private static final int CHUNK_SIZE = 10;

    private final String awsAccessKeyId;
    private final String awsSecret;
    private final String associateTag;
    private final String endPoint;
    private DefaultApi _amazonApi; // Do no use directly, use getter

    /**
     *
     * @param awsAccessKeyId
     * @param awsSecret
     * @param associateTag
     * @param endPoint
     * @param traceEnabled if true, enables deep java web service dumping to stdout / err
     */
    public AmazonProductAdvertisingApiImpl(String awsAccessKeyId, String awsSecret, String associateTag, String endPoint) {

        Assert.isTrue(StringUtils.isNotEmpty(awsAccessKeyId));
        Assert.isTrue(StringUtils.isNotEmpty(awsSecret));
        Assert.isTrue(StringUtils.isNotEmpty(associateTag));

        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecret = awsSecret;
        this.associateTag = associateTag;
        this.endPoint = endPoint;
    }

    @Override
    public Stream<Item> getItems(Collection<String> asins, EnumSet<GetItemsResource> itemsResources, Locale locale) {

        logger.debug("Requesting Amazon products `{}` for locale `{}` item resources `{}`", asins, locale,
        		itemsResources);

        Stream<Item> answer;

        // split asins into chunks of CHUNK_SIZE
        Iterable<List<String>> chunkedAsins = Iterables.partition(asins, CHUNK_SIZE);

        //There is no longer a concept of good/bad items and there are no
        //item level errors anymore
        answer = StreamSupport.stream(chunkedAsins.spliterator(), false)
                // single chunk -> single ItemLookupRequest
                .map(this::buildGetItemsRequest)
                // perform request
                .map(itemLookupRequest -> getItems(locale, itemLookupRequest, itemsResources))
                // List<Item> -> Item stream
                .flatMap(itemResult -> itemResult.stream())
        		.collect(Collectors.toList())
        		.stream();

        return answer;
    }

    /**
     * Does the actual lookup.  While the underlying API can actually handle more than one request at once, we're
     * sending one at a time so that we can parallelize if necessary.
     *
     * Issues calling the API will result in an Exception.  If the API call is successful but Amazon reports a problem,
     * those problems will be logged at ERROR level.  If there are errors at the _Item_ level, those items will be
     * propagated back to the caller for them to deal with.
     * @param locale
     * @param itemLookupRequest
     * @return never null.  Amazon may return no 'ItemResult' default to empty list if there were no results.
     */
    private List<Item> getItems(Locale locale, GetItemsRequest itemLookupRequest,
            EnumSet<GetItemsResource> itemResources) {

        GetItemsResponse answer;

        DefaultApi client = getClientFor(locale);

        List<GetItemsResource> newResources = Lists.newArrayList(itemResources);
        itemLookupRequest.setResources(newResources);
        
        try {
        	answer = client.getItems(itemLookupRequest);
        } catch (Exception e) {
            throw new GlobeException("Could not look up Amazon items", e);
        }

        // all item and operation errors are now at the top level
        if (answer.getErrors() != null) {
        	answer.getErrors().forEach(error -> logger
                    .warn("Error looking up Amazon items.  Code: {}: {}", error.getCode(), error.getMessage()));
        }
        
        return answer.getItemsResult() != null ? answer.getItemsResult().getItems() : ImmutableList.of();
    }

    /**
     * Request only new items for provided asins
     * See https://webservices.amazon.com/paapi5/documentation/get-items.html
     * @param asins
     * @return
     */
    private GetItemsRequest buildGetItemsRequest(List<String> asins) {

        Assert.isTrue(asins.size() <= CHUNK_SIZE, "Can only process " + CHUNK_SIZE + " asins at a time");

        return new GetItemsRequest().partnerType(PartnerType.ASSOCIATES).partnerTag(associateTag).condition(Condition.NEW).itemIds(asins);
    }

    /**
     * There is a different 'region' per locale.  See https://webservices.amazon.com/paapi5/documentation/locale-reference.html
     *
     * @param locale
     * @return
     */
	private DefaultApi getClientFor(Locale locale) {

		if (!(Locale.US.equals(locale))) {
			throw new UnsupportedOperationException("Only US locale currently supported");
		}

		DefaultApi amazonApi = _getDefaultAmazonApi();
		amazonApi.getApiClient().setRegion("us-east-1");
		return amazonApi;
	}

    /**
     * Returns an instance of the amazon client. Use {@link #getClientFor()}
     * @return
     */
    private DefaultApi _getDefaultAmazonApi() {

    	if (_amazonApi == null) {
            synchronized (this) {
                if (_amazonApi == null) {
                	ApiClient client = new ApiClient();
            		client.setAwsAccessKey(awsAccessKeyId);
            		client.setAwsSecretKey(awsSecret);

            		/*
            		 * PAAPI Host and Region to which you want to send request. For more
            		 * details refer:
            		 * https://webservices.amazon.com/paapi5/documentation/common-request-
            		 * parameters.html#host-and-region
            		 */
            		client.setHost(endPoint);
            		_amazonApi = new DefaultApi(client);
                }
            }
        }

        return _amazonApi;
    }

}
