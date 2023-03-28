package com.about.mantle.model.tasks;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.apache.commons.lang3.tuple.Pair;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.mantle.infocat.model.InfoCatRecordPair;
import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.model.extended.docv2.ItemEx;
import com.about.mantle.model.extended.docv2.ListDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCommerceInfoEx.StructuredContentCommerceInfoDataEx;
import com.about.mantle.model.services.commerce.CommerceService;
import com.about.mantle.spring.interceptor.MantleMdcPopulator;
import com.google.common.collect.ImmutableList;

@Tasks
public class CommerceTask {
    public static final Logger logger = LoggerFactory.getLogger(CommerceTask.class);

    private final CommerceService commerceService;
    private final DocumentTask documentTask;
    private final UrlDataFactory urlDataFactory;

    public CommerceTask(CommerceService commerceService, DocumentTask documentTask, UrlDataFactory urlDataFactory) {

        this.commerceService = commerceService;
        this.documentTask = documentTask;
        this.urlDataFactory = urlDataFactory;

    }

    /**
     * Use {@link #CommerceTask(CommerceService, DocumentTask, UrlDataFactory)}
     * 
     * @param commerceService
     * @param documentTask
     */
    @Deprecated // Remove this as a part of GLBE-5916
    public CommerceTask(CommerceService commerceService, DocumentTask documentTask) {
        this(commerceService, documentTask, null);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state) {

        return gatherCommerceInfoFromListDocument(requestContext, url, state, null);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, null);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "configurationString") String configurationString) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url,
                createConfigurationFromString(configurationString));
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "configuration") Map<String, String> config) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, State.ACTIVE, null, config);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config) {

        populateMdc(url);

        BaseDocumentEx document = documentTask.fetchDocument(requestContext, url, state, activeDate);

        // Check to make sure if document has commerce info in it
        if (!document.hasCommerceInfo())
            return null;

        return commerceService.getCommerceModel(getListOfListOfCommerceInfo(document, null, null), config);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, null, priorityDomains,
                safelistDomains);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, State.ACTIVE, null, config,
                priorityDomains, safelistDomains);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherCommerceInfoFromListDocument(requestContext, url, state, null, priorityDomains, safelistDomains);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocument(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null,
                priorityDomains, safelistDomains);
    }

    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        populateMdc(url);

        BaseDocumentEx document = documentTask.fetchDocument(requestContext, url, state, activeDate);

        // Check to make sure if document has commerce info in it
        if (!document.hasCommerceInfo())
            return null;

        List<String> priorityList = priorityDomains == null ? null
                : Arrays.asList(StringUtils.split(priorityDomains, " ,"));
        List<String> safelistList = safelistDomains == null ? null
                : Arrays.asList(StringUtils.split(safelistDomains, " ,"));

        return commerceService.getCommerceModel(getListOfListOfCommerceInfo(document, priorityList, safelistList),
                config);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, null, null);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, null, null, priorityDomains,
                safelistDomains);
    }
    
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, null, null, priorityDomains,
                safelistDomains, filterOOS);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "limit") Integer limit) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, null, null, priorityDomains,
                safelistDomains, limit);
    }
    
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "limit") Integer limit,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, null, null, priorityDomains,
                safelistDomains, limit, filterOOS);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null,
                priorityDomains, safelistDomains);
    }
    
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null,
                priorityDomains, safelistDomains, 3, filterOOS);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "limit") Integer limit) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null,
                priorityDomains, safelistDomains, limit, true);
    }
    
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "limit") Integer limit,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, null,
                priorityDomains, safelistDomains, limit, filterOOS);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, config,
                priorityDomains, safelistDomains, 3, true);
    }

    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfig(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "safelistRetailerDomains") String safelistDomains,
            @TaskParameter(name = "limit") Integer limit,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        populateMdc(url);

        BaseDocumentEx document = documentTask.fetchDocument(requestContext, url, state, activeDate);

        // Check to make sure if document has product records in it
        if (isEmpty(document.getInfoCatProductRecords()))
            return null;

        List<String> priorityList = priorityDomains == null ? null
                : Arrays.asList(StringUtils.split(priorityDomains, " ,"));
        List<String> safelistList = safelistDomains == null ? null
                : Arrays.asList(StringUtils.split(safelistDomains, " ,"));

        return commerceService.getCommerceModelForRetailers(
                getListOfListOfRetailers(document, priorityList, safelistList, limit, filterOOS), config);
    }

    /*
     * Will return a sorted and filtered List of commerceInfo which will include the
     * priorityDomains first in the provided order and then followed by the safelist
     * domains in whatever order they were encountered in the original list The
     * number of commerceInfo in the List will be limited to MAX_RETAILERS
     */
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<CommerceInfoEx> sortCommerceInfo(
            @TaskParameter(name = "commerceInfo") List<CommerceInfoEx> commerceInfo,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains) {

        final int MAX_RETAILERS = 3;

        if (isEmpty(priorityDomains) && isEmpty(safelistDomains)) {
            return commerceInfo;
        }

        /*
         * Create domain map, e.g. amazon.com -> 0
         */
        Map<String, Integer> domainMap = IntStream.range(0, priorityDomains.size()).boxed()
                .collect(Collectors.toMap(priorityDomains::get, Function.identity()));

        /*
         * Create commerceInfo map, e.g. 0 -> CommerceInfo {}
         */
        AtomicInteger whitelistPriorityIndex = new AtomicInteger(priorityDomains.size());

        Map<String, List<CommerceInfoEx>> infoMap = commerceInfo.stream().filter(info -> {
            String host = getDomain(info.getId());
            return host != null;
        }).collect(Collectors.groupingBy(info -> {
            String host = getDomain(info.getId());
            Integer priorityIndex = domainMap.get(host);
            if (priorityIndex != null) {
                return priorityIndex.toString();
            } else if (safelistDomains.contains(host)) {
                return String.valueOf(whitelistPriorityIndex.getAndIncrement());
            }
            return host;
        }));

        /*
         * Sort the retailers and set a limit of MAX_RETAILERS
         */
        List<CommerceInfoEx> sortedRetailers = infoMap.entrySet().stream()
                .sorted(Map.Entry.<String, List<CommerceInfoEx>>comparingByKey())
                .flatMap(entry -> entry.getValue().stream()).limit(MAX_RETAILERS).collect(Collectors.toList());

        return sortedRetailers;
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<CommerceInfoEx> sortCommerceInfo(
            @TaskParameter(name = "commerceInfo") List<CommerceInfoEx> commerceInfo) {

        return sortCommerceInfo(commerceInfo, null, null);
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains) {
        return sortRetailers(retailers, priorityDomains, safelistDomains, 3);
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {
    	return sortRetailers(retailers, priorityDomains, safelistDomains, 3, filterOOS); 
    }
    
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "limit") Integer limit) {
    	return sortRetailers(retailers, priorityDomains, safelistDomains, limit, true); 
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "campaignUrls") List<Retailer> campaignUrls,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "limit") Integer limit) {
    	return sortRetailers(retailers, campaignUrls, priorityDomains, safelistDomains, limit, true); 
    }
    
    /*
     * Will return a sorted and filtered List of Retailer which will include the
     * priorityDomains first in the provided order and then followed by the safelist
     * domains in whatever order they were encountered in the original list The
     * number of retailers in the List will be limited to limit.
     */
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "limit") Integer limit,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {
        return sortRetailers(retailers, null, priorityDomains, safelistDomains, limit, filterOOS);
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "campaignUrls") List<Retailer> campaignUrls,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {
        return sortRetailers(retailers, campaignUrls, priorityDomains, safelistDomains, 3, filterOOS);
    }

    /*
     * Will return a sorted and filtered List of Retailer which will include the
     * campaignUrls first in the provided order and then followed by 
     * the priorityDomains and then the safelistDomains
     * in whatever order they were encountered in the original list.
     * The number of retailers in the List will be limited to limit.
     */
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "campaignUrls") List<Retailer> campaignUrls, 
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "safelistDomains") List<String> safelistDomains,
            @TaskParameter(name = "limit") Integer limit,
            @TaskParameter(name = "filterOOS") Boolean filterOOS) {

        // Default to 3 as that is the typical max amount of buttons for commerce
        // retailers
        final int MAX_RETAILERS = limit == null || limit <= 0 ? 3 : limit;

        if (isEmpty(priorityDomains) && isEmpty(safelistDomains) && isEmpty(campaignUrls)) {
            return retailers;
        }

        /*
         * Create domain map, e.g. amazon.com -> 0
         */
        Map<String, Integer> domainMap = IntStream.range(0, priorityDomains.size()).boxed()
                .collect(Collectors.toMap(priorityDomains::get, Function.identity()));

        AtomicInteger domainPriorityIndex = new AtomicInteger(priorityDomains.size());

        boolean allOutOfStock = isEmpty(campaignUrls) && retailers.stream().allMatch(retailer -> retailer.isOutOfStock());

        Map<String, List<Retailer>> infoMap = retailers.stream().filter(retailer -> {
            String host = getDomain(retailer.getUrl());
            return host != null;
        }).filter(retailer -> {
        	// If we are explicitly turning off OOS filtering AND its an amazon link, continue as normal
        	// or
            // If all products are out of stock, continue as normal for a better UX
            // Otherwise, filter out out of stock retailers
            return (BooleanUtils.isFalse(filterOOS) && "amazon".equalsIgnoreCase(retailer.getType())) || allOutOfStock ? true : !retailer.isOutOfStock();
        }).collect(Collectors.groupingBy(retailer -> {
            String host = getDomain(retailer.getUrl());
            Integer priorityIndex = domainMap.get(host);
            if (priorityIndex != null) {
                return priorityIndex.toString();
            } else if (safelistDomains.contains(host)) {
                return String.valueOf(domainPriorityIndex.getAndIncrement());
            }
            return host;
        }));

        /*
         * Create priorityRetailers stream
         */
        Stream<Retailer> priorityRetailerStream = infoMap.entrySet().stream()
            .sorted(Map.Entry.<String, List<Retailer>>comparingByKey()).flatMap(entry -> entry.getValue().stream());

        /*
         * Create campaignUrls stream
         */
        Stream<Retailer> campaignUrlStream = emptyIfNull(campaignUrls).stream();

        /*
         * Concat campaignUrls and priorityRetailers into one stream
         * then sort the retailers and set a limit of MAX_RETAILERS
         */
        List<Retailer> sortedRetailers = Stream.concat(campaignUrlStream, priorityRetailerStream)
            .limit(MAX_RETAILERS).collect(Collectors.toList());

        return sortedRetailers;
    }

    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailers(@TaskParameter(name = "retailers") List<Retailer> retailers) {

        return sortRetailers(retailers, null, null);
    }

    //
    // Stopgap alternate versions to fix bug
    //
    @Deprecated
    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherCommerceInfoFromListDocument(requestContext, url, priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, config, priorityDomains,
                whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherCommerceInfoFromListDocument(requestContext, url, state, priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherCommerceInfoFromListDocument(requestContext, url, state, activeDate, priorityDomains,
                whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListCommerceInfo")
    @TimedComponent(category = "task")
    public List<List<CommerceInfoEx>> gatherCommerceInfoFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherCommerceInfoFromListDocumentWithMapConfig(requestContext, url, state, activeDate, config,
                priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains,
            @TaskParameter(name = "limit") Integer limit) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, priorityDomains, whitelistedDomains,
                limit);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, priorityDomains,
                whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains,
            @TaskParameter(name = "limit") Integer limit) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, priorityDomains,
                whitelistedDomains, limit);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, config,
                priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "gatherListOfListRetailers")
    @TimedComponent(category = "task")
    public List<List<Retailer>> gatherRetailersFromListDocumentWithMapConfigWhitelist(
            @RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "url") String url,
            @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
            @TaskParameter(name = "configuration") Map<String, String> config,
            @TaskParameter(name = "priorityRetailerDomains") String priorityDomains,
            @TaskParameter(name = "whitelistedRetailerDomains") String whitelistedDomains,
            @TaskParameter(name = "limit") Integer limit) {

        return gatherRetailersFromListDocumentWithMapConfig(requestContext, url, state, activeDate, config,
                priorityDomains, whitelistedDomains, limit, true);
    }

    @Deprecated
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<CommerceInfoEx> sortCommerceInfoWhitelist(
            @TaskParameter(name = "commerceInfo") List<CommerceInfoEx> commerceInfo,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "whitelistedDomains") List<String> whitelistedDomains) {

        return sortCommerceInfo(commerceInfo, priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailersWhitelist(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "whitelistedDomains") List<String> whitelistedDomains) {
        return sortRetailers(retailers, priorityDomains, whitelistedDomains);
    }

    @Deprecated
    @Task(name = "retailerSort")
    @TimedComponent(category = "task")
    public List<Retailer> sortRetailersWhitelist(@TaskParameter(name = "retailers") List<Retailer> retailers,
            @TaskParameter(name = "priorityDomains") List<String> priorityDomains,
            @TaskParameter(name = "whitelistedDomains") List<String> whitelistedDomains,
            @TaskParameter(name = "limit") Integer limit) {
        return sortRetailers(retailers, priorityDomains, whitelistedDomains, limit);
    }

    /**
     * Generally the MDC is populated by the
     * {@link com.about.mantle.spring.interceptor.MdcPopulatorInterceptor}, but
     * calls to the commerce tasks don't have parsable Urls so they won't be
     * populated there. So populating here manually.
     * 
     * @param url
     */
    private void populateMdc(String url) {

        MDC.clear();
        MDC.put("url", url);

        // Null check should be removed once {@link #CommerceTask(CommerceService,
        // DocumentTask)} is removed.
        if (urlDataFactory != null) {
            UrlData urlData = urlDataFactory.create(url);
            MantleMdcPopulator.populateMdcFromUrlData(urlData);
        }
    }

    /**
     * Returns commerce info for a given document. If listTemplate then extracts
     * commerce info from each of the items and returns list of list of commerce
     * info. else if document is any of SC type then extracts commerce info from
     * each of the commerce SC blocks from document and returns list of list of
     * commerce info. If any other kind of document, then returns empty list.
     * 
     */
    private List<List<CommerceInfoEx>> getListOfListOfCommerceInfo(BaseDocumentEx document,
            List<String> priorityDomains, List<String> safelistDomains) {

        // final result
        List<List<CommerceInfoEx>> answer;
        // @formatter:off
		//Mapper function for removing commerce info which doesn't have type in it, from list of commerce info
		UnaryOperator<List<CommerceInfoEx>> removeBlankCommerceType = (commerceInfoList) -> commerceInfoList.stream()
				.filter(c -> StringUtils.isNotBlank(c.getType()))
				.collect(toList());
		// @formatter:on
        if (document instanceof ListDocumentEx) {
            // Handy predicate to filter nulls
            Predicate<ItemEx> onNonNull = item -> Objects.nonNull(item.getCommerceInfo());
            // @formatter:off
			answer = ((ListDocumentEx) document).getItems().getList().stream()
					.filter(onNonNull)
					.map(item -> item.getCommerceInfo().getList())
					.map(info -> sortCommerceInfo(info,priorityDomains,safelistDomains))
					.map(removeBlankCommerceType)
					.collect(toList());
			// @formatter:on

        } else if (document instanceof StructuredContentBaseDocumentEx) {
            Predicate<StructuredContentCommerceInfoDataEx> onNonNull = item -> Objects.nonNull(item.getCommerceInfo());

            @SuppressWarnings("rawtypes")
            List<? extends AbstractStructuredContentContentEx> commerceScBlocks = ((StructuredContentBaseDocumentEx) document)
                    .getContentsStreamOfType("commerce").collect(toList());
            // @formatter:off
			answer = commerceScBlocks.stream()
					.map(csb -> (StructuredContentCommerceInfoDataEx) csb.getData())
					.filter(onNonNull)
					.map(data -> data.getCommerceInfo().getList())
					.map(info -> sortCommerceInfo(info,priorityDomains,safelistDomains))
					.map(removeBlankCommerceType)
					.collect(toList());
			// @formatter:on
        } else {
            answer = Collections.emptyList();
        }

        return answer;
    }

    /**
     * Returns retailers for a given document. If listTemplate then extracts
     * commerce info from each of the items and returns list of list of commerce
     * info. else if document is any of SC type then extracts commerce info from
     * each of the commerce SC blocks from document and returns list of list of
     * commerce info. If any other kind of document, then returns empty list.
     * 
     */
    private List<List<Retailer>> getListOfListOfRetailers(BaseDocumentEx document, List<String> priorityDomains,
            List<String> safelistDomains, Integer limit, Boolean filterOOS) {

        // final result
        List<List<Retailer>> answer;
        // @formatter:off
		//Mapper function for removing commerce info which doesn't have type in it, from list of commerce info
		UnaryOperator<List<Retailer>> removeBlankCommerceType = (retailerList) -> retailerList.stream()
				.filter(r -> StringUtils.isNotBlank(r.getType()))
				.collect(toList());
		// @formatter:on
        if (document instanceof StructuredContentBaseDocumentEx) {
            List<InfoCatRecordPair> recordPairs = ((StructuredContentBaseDocumentEx) document)
                    .getInfoCatProductRecords();
            // @formatter:off
            answer = recordPairs.stream()
                .map(pair -> {
                    List<Retailer> retailerList = pair.getProduct().getRetailerList();
                    Pair<String, String> campaignUrlValue = pair.getProduct().getCampaignUrlValue();
                    List<Retailer> campaignUrls = null;

                    if (campaignUrlValue != null) {
                        Retailer campaignUrl = new Retailer(campaignUrlValue.getLeft(), campaignUrlValue.getRight());
                        campaignUrls = ImmutableList.of(campaignUrl);
                    }

                    return sortRetailers(retailerList, campaignUrls, priorityDomains, safelistDomains, filterOOS);
            })
                .map(removeBlankCommerceType)
                .collect(toList());
            // @formatter:on
        } else {
            answer = Collections.emptyList();
        }

        return answer;
    }

    private Map<String, String> createConfigurationFromString(String config) {
        Map<String, String> configuration = new HashMap<>();

        String[] array = StringUtils.split(config, ',');

        for (int i = 0; i < array.length - 1; i += 2) {
            configuration.put(array[i], array[i + 1]);
        }

        return configuration;
    }

    private String getDomain(String uri) {
        String host = null;
        try {
            host = new URI(uri).getHost();
        } catch (URISyntaxException e) {
            logger.error("Could not parse uri: " + uri);
            return null;
        }

        Integer lastDot = host.lastIndexOf(".");
        Integer secondToLastDot = host.substring(0, lastDot).lastIndexOf(".") + 1;
        return host.substring(secondToLastDot);
    }

}
