package com.about.mantle.amazon.productAdvertisingApi.services.impl;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApi;
import com.about.mantle.amazon.productAdvertisingApi.soap.SignatureHandler;
import com.about.mantle.amazon.productAdvertisingApi.soap.SoapLoggingHandler;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.AWSECommerceService;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.AWSECommerceServicePortType;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Item;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.ItemLookupRequest;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Items;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.OperationRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.xml.ws.Holder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AmazonProductAdvertisingApiImpl implements AmazonProductAdvertisingApi {

    private Logger logger = LoggerFactory.getLogger(AmazonProductAdvertisingApi.class);

    /**
     * Used when requesting (for instance) more than one item at a time.  Maximum number of items to send on a single
     * request.  Amazon allows a max of 10
     */
    private static final int CHUNK_SIZE = 10;
    private static final int RETRY_WAIT_SECONDS = 30; // how long to wait before trying to reconnect to amazon.

    private AWSECommerceService _service; // Do no use directly, use getter
    private long lastServiceRetry = Long.MIN_VALUE; // Min value indicates that we haven't tried at all yet

    private final String awsAccessKeyId;
    private final String awsSecret;
    private final String associateTag;
    private final boolean traceEnabled;

    /**
     *
     * @param awsAccessKeyId
     * @param awsSecret
     * @param associateTag
     * @param traceEnabled if true, enables deep java web service dumping to stdout / err
     */
    public AmazonProductAdvertisingApiImpl(String awsAccessKeyId, String awsSecret, String associateTag,
            boolean traceEnabled) {

        Assert.isTrue(StringUtils.isNotEmpty(awsAccessKeyId));
        Assert.isTrue(StringUtils.isNotEmpty(awsSecret));
        Assert.isTrue(StringUtils.isNotEmpty(associateTag));

        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecret = awsSecret;
        this.associateTag = associateTag;
        this.traceEnabled = traceEnabled;
    }

	public void initialize() {

		if (this.traceEnabled) {
			// see https://stackoverflow.com/a/16338394/295797 and
			// https://stackoverflow.com/a/40274550/295797
			System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "" + Integer.MAX_VALUE);
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold",
					"" + Integer.MAX_VALUE);
		}
	}

    private AWSECommerceService addServiceHandlers(AWSECommerceService svc) {

        svc.setHandlerResolver(portInfo -> ImmutableList.of(
                // gives us hooks into the messages sent and received for logging
                new SoapLoggingHandler(), new SignatureHandler(awsAccessKeyId, awsSecret)));

        return svc;
    }

    @Override
    public Stream<Item> lookupItems(Collection<String> asins, EnumSet<ResponseGroup> responseGroups, Locale locale) {

        logger.debug("Requesting Amazon products `{}` for locale `{}` responseGroups `{}`", asins, locale,
                responseGroups);

        Stream<Item> answer;

        // split asins into chunks of CHUNK_SIZE
        Iterable<List<String>> chunkedAsins = Iterables.partition(asins, CHUNK_SIZE);

        Map<Boolean, List<Item>> goodAndBadItems = StreamSupport.stream(chunkedAsins.spliterator(), false)
                // single chunk -> single ItemLookupRequest
                .map(this::buildItemLookupRequest)
                // perform request
                .map(itemLookupRequest -> lookupItems(locale, itemLookupRequest, responseGroups))
                // Items -> Item stream
                .flatMap(items -> items.getItem().stream())
                // partition by items that have errors and items that do not.  true means item is good
                .collect(Collectors.partitioningBy(item -> item.getErrors() == null));

        // Handle item-level errors
        goodAndBadItems.get(false).forEach(
                item -> logger.error("Could not look up ASIN {}: {}", item.getASIN(), item.getErrors().getError()));

        logMissingItems(asins, goodAndBadItems);

        answer = goodAndBadItems.get(true).stream();

        return answer;
    }

    private void logMissingItems(Collection<String> asins, Map<Boolean, List<Item>> goodAndBadItems) {

        // Some items aren't returned by amazon at all.  If you query for *just* those items, you get a useful message
        // that says "AWS.ECommerceService.ItemNotAccessible: This item is not accessible through the Product Advertising API."
        // However if you query for good and bad items together, Amazon will just omit the bad ones with no message.
        // The following ensures that we log those missing ones.

        if (asins.size() > goodAndBadItems.size()) {

            Set<String> allReturnedAsins= goodAndBadItems.values().stream() // stream of List<Item>
                    .flatMap(items -> items.stream()) // stream of Item
                    .map(Item::getASIN) // stream of ASINs as strings
                    .collect(Collectors.toSet());

            asins.stream() // stream of ASINs as strings
                    .filter(asin -> !(allReturnedAsins.contains(asin))) // remove if we got a response for said asin
                    .forEach(asin -> logger.error("ASIN {} not returned.  " +
                            "Usually because the item is not available through the Amazon API.", asin));
        }
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
     * @return never null.  Empty `Items` object if there were no results.
     */
    private Items lookupItems(Locale locale, ItemLookupRequest itemLookupRequest,
            EnumSet<ResponseGroup> responseGroups) {

        Items answer;

        AWSECommerceServicePortType port = getPortFor(locale);

        Holder<OperationRequest> operationRequestHolder = new Holder<>(new OperationRequest());
        Holder<List<Items>> responseHolder = new Holder<>(new LinkedList<>());

        ItemLookupRequest commonItemLookupRequestFields = new ItemLookupRequest();
        List<String> newResponseGroups = commonItemLookupRequestFields.getResponseGroup();
        responseGroups.forEach(responseGroup -> newResponseGroups.add(responseGroup.getResponseGroup()));

        try {
            port.itemLookup(null, // use default
                    awsAccessKeyId, associateTag, null, // use default
                    null, // use default
                    commonItemLookupRequestFields,
                    ImmutableList.of(itemLookupRequest),
                    operationRequestHolder,
                    responseHolder);
        } catch (Exception e) {
            throw new GlobeException("Could not look up Amazon items", e);
        }

        // handle operation-level errors (Both Items and each Item have their own level of errors (sigh))

        if (operationRequestHolder.value.getErrors() != null) {
            operationRequestHolder.value.getErrors().getError().forEach(error -> logger
                    .error("Error looking up Amazon items.  Code: {}: {}", error.getCode(), error.getMessage()));
        }

        List<Items> response = responseHolder.value;

        int responseSize = response.size();
        if (responseSize == 0) {
            answer = new Items();
        } else if (responseSize == 1) {
            answer = response.get(0);
        } else {
            throw new GlobeException("Excpected <= 1 result but got " + responseSize, null);
        }

        // log 'Items' level errors

        if (answer.getRequest() != null && answer.getRequest().getErrors() != null) {
            answer.getRequest().getErrors().getError().forEach(error -> logger
                    .error("Error looking up Amazon items.  Code: {}: {}", error.getCode(), error.getMessage()));
        }

        return answer;
    }

    /**
     *
     * See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemLookup.html
     * We are using many of the default settings listed there.
     * @param asins
     * @return
     */
    private ItemLookupRequest buildItemLookupRequest(List<String> asins) {

        Assert.isTrue(asins.size() <= CHUNK_SIZE, "Can only process " + CHUNK_SIZE + " asins at a time");

        ItemLookupRequest answer = new ItemLookupRequest();
        List<String> itemIds = answer.getItemId();
        itemIds.addAll(asins);
        // ItemLookupRequest has no setter for itemIds, you instead
        // have to augment the one you get from the getter.
        return answer;
    }

    /**
     * There is a different 'port' per locale.  See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/CHAP_LocaleConsiderations.html
     *
     * @param locale
     * @return
     */
	private AWSECommerceServicePortType getPortFor(Locale locale) {

		if (!(Locale.US.equals(locale))) {
			throw new UnsupportedOperationException("Only US locale currently supported");
		}

        AWSECommerceService amazonService = getAmazonService();

		if (amazonService != null) {
			return amazonService.getAWSECommerceServicePortUS(); // might be slow, should we cache?
		} else {
			throw new GlobeException("Could not connect to Amazon Product Advertising API.  See earlier error messages.");
		}
	}

    /**
     * Returns an instance of the amazon service if available.  Otherwise will log and return null, which the caller
     * will need to handle
     * @return
     */
    private AWSECommerceService getAmazonService() {

        if (_service == null) {
            synchronized (this) {
                if (_service == null) {
                    _tryToPopulateAmazonService();
                }
            }
        }

        return _service; // note - could still be null
    }

    /**
     * Do not call directly, use {@link #getAmazonService()}
     */
    private void _tryToPopulateAmazonService() {

        long now = System.currentTimeMillis();

        if (this.lastServiceRetry == Long.MIN_VALUE || now - this.lastServiceRetry > RETRY_WAIT_SECONDS * 1000) {

            try {
                _service = new AWSECommerceService();
                _service = addServiceHandlers(_service);
            } catch (Exception e) {
                _service = null;
                logger.error("Could not initialize Amazon Product Advertising API, all calls to amazon will fail.  "
                        + "  Will retry in " + RETRY_WAIT_SECONDS + " Seconds.  Continuing", e);
                this.lastServiceRetry = now;
            }
        } else {
            // not ready for a retry yet
        }
    }
}
