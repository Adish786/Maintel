package com.about.mantle.cache.clearance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.globe.core.model.EnvironmentConfig;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * This holds all uri candidates for cache clearance for a vertical. Internally,
 * it listens to kafka topic periodically in a background thread and keeps set
 * of candidate uris in memory. Clients of this class can check if given url is
 * a candidate for cache clearance or not. Additionally, once a url is identified
 * as a candidate for cache clearance it becomes subject to secondary cache
 * clearance, i.e. any AJAX calls, deferred requests, external component requests
 * will also have their caches resupplied. See outcome of spike here
 * https://dotdash.atlassian.net/browse/GLBE-6820
 */
public final class CacheClearanceCandidateRepo {

	public static final String UNPUBLISH_TOPIC_NAME = "selene-events-document-unpublish";
	public static final String CACHE_CLEARANCE_HEADER = "X-Cache-Clearance";

	private static final String KEY_PREFIX_SITEMAP = "PREFIX-SITEMAP";
	private static final String KEY_PREFIX_FEEDS = "PREFIX-FEEDS";

	private static final Logger logger = LoggerFactory.getLogger(CacheClearanceCandidateRepo.class);
	private static final String jenkinsServiceName = "Linkman - Jenkins";
	private static final String seleneServiceName = "Linkman - Selene";
	private static final String debugServiceName = "Globe - Debug Endpoint";
	private static final String headerServiceName = "Globe - HTTP Header";
	private static final int RETRY_RECORD_COUNT_THRESHOLD = 50;

	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	private final KafkaConsumer<String, String> verticalCacheTopicConsumer;
	private final EnvironmentConfig envConfig;
	private final boolean isHeaderEnabled;
	private final long replicationDelay;
	private final List<CacheClearanceEventHandler> handlers;
	private final ObjectMapper objectMapper;
	private final Ehcache cacheClearanceCandidatePaths;
	// for tracking secondary requests, e.g. ajax, external components, etc.
	private final Cache<String, SecondaryCacheClearanceContainer> primaryToSecondaryPaths;
	private final UrlDataFactory urlDataFactory;

	private static Cache<String, SecondaryCacheClearanceContainer> initializeSecondaryRepo(int secondaryDuration) {
		return CacheBuilder.newBuilder()
			.maximumSize(10000) // just to make sure this doesn't grow out of hand
			.expireAfterWrite(secondaryDuration, TimeUnit.MINUTES)
			.build();
	}

	public CacheClearanceCandidateRepo(KafkaConsumer<String, String> verticalCacheTopicConsumer,
									   Ehcache cacheClearanceCandidatePaths, EnvironmentConfig envConfig, boolean isHeaderEnabled,
									   int secondaryDuration, long replicationDelay,
									   List<CacheClearanceEventHandler> handlers, UrlDataFactory urlDataFactory) {
		this.verticalCacheTopicConsumer = verticalCacheTopicConsumer;
		this.cacheClearanceCandidatePaths = cacheClearanceCandidatePaths;
		this.envConfig = envConfig;
		this.isHeaderEnabled = isHeaderEnabled;
		this.primaryToSecondaryPaths = initializeSecondaryRepo(secondaryDuration);
		this.replicationDelay = replicationDelay;
		this.handlers = handlers;
		this.objectMapper = new ObjectMapper(new JsonFactory())
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.urlDataFactory = urlDataFactory;

		// allow to continue even if consumer is null, in that case cache-clearing
		// feature will be essentially disabled.
		if (verticalCacheTopicConsumer != null) {
			executor.scheduleWithFixedDelay(this::syncUpWithVerticalCacheQueue, 0, 10, TimeUnit.SECONDS);
		}
	}

	public List<CacheClearanceRequest> getCandidateRequests(HttpServletRequest httpRequest) {
		boolean isCandidate = false;
		List<CacheClearanceRequest> candidateRequests = null;
		String requestUri = httpRequest.getRequestURI();

		if (StringUtils.isNotBlank(requestUri)) {
			isCandidate = isPrimaryCandidate(httpRequest);

			if (isCandidate) {
				candidateRequests = getCacheClearanceRequestList(requestUri);
				// only allow one cache clear per request at a time
				if (candidateRequests != null && !cacheClearanceCandidatePaths.remove(requestUri)) {
					// another request beat us to it
					candidateRequests = null;
				}
			}

			// Add candidate requests for path prefix matches
			if (!isCandidate) {
				List<CacheClearanceRequest> prefixRequests = getPrefixCandidates(httpRequest);
				isCandidate = !prefixRequests.isEmpty();
				if (isCandidate) {
					candidateRequests = prefixRequests;
				}
			}

			// if not a primary candidate, check for cache clearance header
			if (!isCandidate && isHeaderEnabled()) {
				isCandidate = httpRequest.getHeader(CACHE_CLEARANCE_HEADER) != null;
				if (isCandidate) {
					candidateRequests = new ArrayList<>();
					CacheClearanceRequest request = CacheClearanceRequest.createBasicCacheClearanceRequest();
					request.setUrlPath(requestUri);
					request.setServiceName(headerServiceName);
					candidateRequests.add(request);
				}

				// Generally use of the header is expected for lithospheric services (e.g. commerce).
				// This use case does not require secondary cache clearance because secondary requests
				// are routed through the vertical via the mantle proxy which also sets the header.
				// However, QA Automation expressed a desire to be able to run their automated tests
				// while exempt from all caching. In this use case, we would want to enable secondary
				// cache clearance. Since we only expect this to apply outside of production we
				// explicitly check the environment here before triggering secondary cache clearance.
				if (isCandidate && !"prod".equalsIgnoreCase(envConfig.getAccountName())) {
					initialSecondaryPaths(requestUri, candidateRequests);
				}
			}

			// if neither a primary candidate nor the cache clearance header is present,
			// check for secondary cache clearance
			if (!isCandidate) {
				candidateRequests = isSecondaryCandidate(httpRequest);
			}
		}

		return candidateRequests;
	}

	public void requestCompleted(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Exception ex, List<CacheClearanceRequest> cacheClearRequest) {
		String requestUri = httpRequest.getRequestURI();
		boolean isPrimary = !isAJAX(httpRequest) && cacheClearRequest.stream().anyMatch(request -> requestUri.equals(request.getUrlPath()));
		String adjective = isPrimary ? "primary" : "secondary";

		if (httpResponse.getStatus() >= 400 || ex != null) {
			// cache clear failures justify logging an error
			if (ex == null) {
				logger.error("Attempt to clear cache for {} requested uri [{}] failed with response status [{}]", adjective, requestUri, httpResponse.getStatus());
			} else {
				logger.error("Attempt to clear cache for {} requested uri [{}] failed due to exception", adjective, requestUri, ex);
			}

			// make preparations for another attempt
			// cacheClearRequest should contain the generated prefix-based requests so the paths don't need to be
			// removed from the PrefixCacheClearanceRequest
			if (isPrimary) {
				cacheClearanceCandidatePaths.putIfAbsent(new Element(requestUri, cacheClearRequest));
			} else {
				SecondaryCacheClearanceContainer secondaryCacheClearanceContainer = primaryToSecondaryPaths.getIfPresent(getRefererUri(httpRequest));
				if (secondaryCacheClearanceContainer != null) {
					secondaryCacheClearanceContainer.getSecondaries().remove(getExpandedUri(httpRequest));
				}
			}
		} else {
			logger.debug("Cache clear complete for {} requested uri [{}]", adjective, requestUri);

			if (isPrimary) {
				// once a primary candidate is handled successfully we will look out for secondary
				// candidates that are associated with the primary, e.g. ajax, external components, etc.
				initialSecondaryPaths(requestUri, cacheClearRequest);
			}
		}
	}

	private List<CacheClearanceRequest> extractElementValue(Element repoElement) {
		TypeReference<ArrayList<CacheClearanceRequest>> typeRef = new TypeReference<ArrayList<CacheClearanceRequest>>(){};
		return objectMapper.convertValue(repoElement.getObjectValue(), typeRef);
	}

	private UrlPrefixCacheClearanceRequest extractPrefixElementValue(Element repoElement) {
		TypeReference<UrlPrefixCacheClearanceRequest> typeRef = new TypeReference<UrlPrefixCacheClearanceRequest>(){};
		return objectMapper.convertValue(repoElement.getObjectValue(), typeRef);
	}

	private boolean isPrimaryCandidate(HttpServletRequest httpRequest) {
		// we don't want to consider ajax requests as primary candidate. See https://dotdash.atlassian.net/browse/GLBE-8362?focusedCommentId=751819
		if (!isAJAX(httpRequest)) {
			String requestUri = httpRequest.getRequestURI();
			Element repoElement = cacheClearanceCandidatePaths.get(requestUri);
			if (repoElement != null) {
				List<CacheClearanceRequest> candidateRequests = extractElementValue(repoElement);
				boolean isExemptFromReplicationDelay = candidateRequests.stream().noneMatch(request -> seleneServiceName.equals(request.getServiceName()));
				boolean isPastReplicationDelay = System.currentTimeMillis() > repoElement.getLatestOfCreationAndUpdateTime() + replicationDelay;
				return !repoElement.isExpired() && (isExemptFromReplicationDelay || isPastReplicationDelay);
			}
		}
		return false;
	}

	private List<CacheClearanceRequest> getPrefixCandidates(HttpServletRequest httpRequest) {
		List<CacheClearanceRequest> prefixMatchRequests = new ArrayList<>();
		// we don't want to consider ajax requests as primary candidate. See https://dotdash.atlassian.net/browse/GLBE-8362?focusedCommentId=751819
		if (!isAJAX(httpRequest)) {
			getPrefixCandidate(httpRequest, KEY_PREFIX_SITEMAP, prefixMatchRequests);
			getPrefixCandidate(httpRequest, KEY_PREFIX_FEEDS, prefixMatchRequests);
		}
		return prefixMatchRequests;
	}

	private void getPrefixCandidate(HttpServletRequest httpRequest, String key, List<CacheClearanceRequest> prefixMatchRequests) {
		Element sitemapPrefixElement = cacheClearanceCandidatePaths.get(key);
		if (sitemapPrefixElement != null) {
			UrlPrefixCacheClearanceRequest prefixRequest = extractPrefixElementValue(sitemapPrefixElement);
			boolean isPastReplicationDelay = System.currentTimeMillis() > sitemapPrefixElement.getLatestOfCreationAndUpdateTime() + replicationDelay;
			String requestPath = httpRequest.getRequestURI();
			boolean prefixIsValid = !sitemapPrefixElement.isExpired() && isPastReplicationDelay && requestPath.startsWith(prefixRequest.getPrefix());
			if (prefixIsValid) {
				if (!prefixRequest.getProcessedUrls().contains(requestPath)) {
					prefixRequest.getProcessedUrls().add(requestPath);

					// Element changes require updating ehcache
					cacheClearanceCandidatePaths.put(new Element(key, prefixRequest));

					CacheClearanceRequest request = CacheClearanceRequest.createBasicCacheClearanceRequest();
					request.setServiceName(seleneServiceName);
					request.setUrlPath(requestPath);
					prefixMatchRequests.add(request);
				}
			}
		}
	}

	private void initialSecondaryPaths(String primaryPath, List<CacheClearanceRequest> candidateRequests) {
		// initially empty; must be concurrent because once a uri becomes a candidate for
		// cache clearance it will also be subject for secondary cache clearance which could
		// span multiple simultaneous requests. each secondary request will be added to this
		// set so that cache will get cleared only once for a secondary request until
		// secondary cache clearance expires for the primary candidate uri
		primaryToSecondaryPaths.put(primaryPath, new SecondaryCacheClearanceContainer(candidateRequests, Sets.newConcurrentHashSet()));
	}

	private List<CacheClearanceRequest> isSecondaryCandidate(HttpServletRequest httpRequest) {
		// limit to AJAX requests to differentiate from standard link navigation
		if (isAJAX(httpRequest)) {
			// first check if the primary path is subject to secondary cache clearance
			SecondaryCacheClearanceContainer secondaryCacheClearanceContainer = primaryToSecondaryPaths.getIfPresent(getRefererUri(httpRequest));

			if (secondaryCacheClearanceContainer != null) {
				// add the secondary path so that we don't clear cache again for the same request
				boolean isCandidate = secondaryCacheClearanceContainer.getSecondaries().add(getExpandedUri(httpRequest));
				if (isCandidate) {
					return secondaryCacheClearanceContainer.getPrimaryRequests();
				}
			}
		}

		return null;
	}

	// NOTE: this header is contingent on the use of jQuery or the utility function of vanilla js; any AJAX requests to ourselves
	// using native JS will not have this header set by default.
	private static boolean isAJAX(HttpServletRequest httpRequest) {
		return "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));
	}

	private static String getRefererUri(HttpServletRequest httpRequest) {
		String referer = StringUtils.defaultIfBlank(httpRequest.getHeader(HttpHeader.REFERER.asString()), "");
		if (StringUtils.isNotBlank(referer)) {
			// we could try to parse the referer as a URI to get the path but it could throw
			// an exception if the URI is not legal and seeing as how this is coming from the client
			// it could contain anything. therefore it's easier to just split on '/'
			// index 0 is the scheme (https); index 1 is the host (www.lifewire.com), index 2 is the path
			String[] uriParts = StringUtils.split(referer, "/", 3);
			String refererUri = "/";
			if (uriParts.length > 2) {
				// strip query params because their presence will make it impossible to associate with the primary path
				refererUri += StringUtils.split(uriParts[2], "?", 2)[0];
			}
			return refererUri;
		}
		return referer;
	}

	private static final char SEP = '/'; // separator used for expansion
	// exclude parameters that may vary by user
	// CSRFToken is silently attached to every POST
	// loaded_cr/css/js/svg are sent by the client-side deferred library to specify which components have already loaded
	private static final Set<String> EXCLUDED_PARAMS = ImmutableSet.of("CSRFToken", "loaded_cr", "loaded_css", "loaded_js", "loaded_svg");
	private static String getExpandedUri(HttpServletRequest httpRequest) {
		// Using the requestURI is inadequate for capturing the variety of secondary requests, e.g.
		// GET /xyz
		// GET /xyz?foo=bar
		// GET /xyz?foo=bar&baz=bin
		// POST /xyz  (form data: foo=bar)
		// ALL have the same URI /xyz but are distinct requests with varying response.
		// Thus we need to expand the URI to make tracking of secondary requests more specific.
		// The above examples will be expanded as follows:
		// GET/xyz
		// GET/xyz/foo/bar
		// GET/xyz/baz/bin/foo/bar
		// POST/xyz/foo/bar
		StringBuilder sb = new StringBuilder(httpRequest.getMethod()).append(httpRequest.getRequestURI());
		// We can't use the map returned directly because it's unmodifiable and we need to sort the parameter names.
		String[] params = httpRequest.getParameterMap().keySet().toArray(new String[0]);
		Arrays.sort(params);
		for (String param : params) {
			if (!EXCLUDED_PARAMS.contains(param)) {
				sb.append(SEP).append(param);
				for (String value : httpRequest.getParameterValues(param)) {
					sb.append(SEP).append(value);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Cache clearance header allows application to determine candidates by presence of header.
	 * Needed for commerce and performance marketing but also useful for verticals to enable in QA.
	 * Should NOT be enabled for verticals in prod because it could be an attack vector for DDOS.
	 */
	private boolean isHeaderEnabled() {
		return isHeaderEnabled;
	}
	
	private void syncUpWithVerticalCacheQueue() {
		try {
			ConsumerRecords<String, String> records = null;
			do {
				// wait for 100 ms for data and come back anyway with or without it
				records = verticalCacheTopicConsumer.poll(Duration.ofMillis(100));
				if (records != null && !records.isEmpty()) {
					for (ConsumerRecord<String, String> record : records) {
						try {
							processMessage(record);
						} catch (Exception e) {
							logger.error("Error processing cache clearance message", e);
						}
					}
				}

				// If too many records are received, check queue again immediately. This ensures that high volume
				// changes are processed quickly and do not build up. A backlog of records could cause new legacy
				// documents to 404 long after they are published.
			} while (records != null && records.count() > RETRY_RECORD_COUNT_THRESHOLD);
		} catch (Exception e) {
			logger.error("Exception during fetching records from vertical cache clearance topic", e);
		}

	}
	
	/**
	 * This method processes incoming messages on vertical specific cache clearance topics 
	 * (e.g. for brides `realtime-weddings-resupply-cache`). On these topics, CCR messages can come from
	 * ad-hoc cache-clearance job or from Linkman.
	 */
	private void processMessage(ConsumerRecord<String, String> record) {
		String recordValue = record.value();
		if (StringUtils.isNotBlank(recordValue)) {
			recordValue = recordValue.trim();

			try {
				// Selene's events for unpublished documents do not include a vertical field, meaning
				// requests can not be routed to the vertical-specific topics without adding a hostname
				// to application mapping like in hippodrome. To simplify changes in the short term,
				// verticals will monitor the events from selene directly as the volume of events on this
				// topic should be minimal. The url on the event is parsed to determine whether the
				// vertical should handle the event so only relevant documents result in cache clearance
				// being triggered.
				// Selene's events should be updated to facilitate routing in linkman then following a
				// linkman change verticals will no longer need to monitor this topic directly.
				if (UNPUBLISH_TOPIC_NAME.equalsIgnoreCase(record.topic())) {
					UnpublishCacheClearanceRequest unpublishRequest = objectMapper.readValue(recordValue, UnpublishCacheClearanceRequest.class);
					String url = unpublishRequest.getUrl();
					UrlData urlData = urlDataFactory.create(url);
					if (urlData instanceof VerticalUrlData) {
						VerticalUrlData verticalUrlData = (VerticalUrlData) urlData;
						if (envConfig.getApplication().equalsIgnoreCase(verticalUrlData.getApplicationName())) {
							// For unpublish requests, add cache clearance for document url, homepage, sitemap, and rss feeds
							handleSeleneCacheClearanceEvent(verticalUrlData.getPath(), record);
							handleSeleneCacheClearanceEvent("/", record);
							handleSeleneCacheClearanceEvent("/sitemap.xml", record);

							// Numbered sitemap pages and rss feeds are handled by prefix clearance requests as
							// the list of URLs that may need to have cache clearance triggered are not known in
							// advance.
							// Storing fresh prefix requests to reset processed urls
							cacheClearanceCandidatePaths.put(new Element(KEY_PREFIX_SITEMAP, new UrlPrefixCacheClearanceRequest("/sitemap_")));
							cacheClearanceCandidatePaths.put(new Element(KEY_PREFIX_FEEDS, new UrlPrefixCacheClearanceRequest("/feeds/")));
						}
					}
				} else {
					// Normal cache clearance event handler
					CacheClearanceRequest request = objectMapper.readValue(recordValue, CacheClearanceRequest.class);

					if (envConfig.getEnvironment().equalsIgnoreCase(request.getEnvironment()) || !jenkinsServiceName.equals(request.getServiceName())) {
						handleCacheClearanceEvent(request, record);
					}
				}

			} catch (JsonProcessingException e) {
				logger.error("Error reading cache clearance message from "+ recordValue, e);
			}
		}
	}

	private void handleSeleneCacheClearanceEvent(String path, ConsumerRecord<String, String> record) {
		CacheClearanceRequest request = CacheClearanceRequest.createBasicCacheClearanceRequest();
		request.setServiceName(seleneServiceName);
		request.setUrlPath(path);
		handleCacheClearanceEvent(request, record);
	}

	private void handleCacheClearanceEvent(CacheClearanceRequest request, ConsumerRecord<String, String> record) {
		try {
			// Adding try-catch due to ehcache error: java.lang.IllegalStateException: The CacheClearanceCandidateRepo_cacheClearanceCandidatePaths Cache is not alive (STATUS_SHUTDOWN)
			// which occurs in production. If this happens, it would prevent the URL from being processed by the
			// handlers. This could prevent new (legacy) documents from being added to the legacy URL map for
			// legacy meredith verticals.
			// Insert item into candidate repo
			List<CacheClearanceRequest> listOfRequests = new ArrayList<>();

			List<CacheClearanceRequest> listAlreadyStored = getCacheClearanceRequestList(request.getUrlPath());

			if (listAlreadyStored != null) {
				listOfRequests.addAll(listAlreadyStored);
			}

			listOfRequests.add(request);
			cacheClearanceCandidatePaths.put(new Element(request.getUrlPath(), listOfRequests));

			logger.debug(String.format("Received and accepted cache clearance request record from %s with Value: %s and Primary Cache Size: %d",
					request.getServiceName(), record != null ? record.value() : null, cacheClearanceCandidatePaths.getSize()));
		} catch (Exception e) {
			logger.error("Error inserting cache clearance url into candidate repo", e);
		}

		// Notify event handlers
		if (handlers != null) {
			for (CacheClearanceEventHandler handler : handlers) {
				handler.handle(request.getUrlPath());
			}
		}
	}

	private List<CacheClearanceRequest> getCacheClearanceRequestList(String urlPath) {
		List<CacheClearanceRequest> cacheClearanceRequestList = null;
		Element cacheClearanceRequestElement = cacheClearanceCandidatePaths.get(urlPath);

		if (cacheClearanceRequestElement != null) {
			cacheClearanceRequestList = extractElementValue(cacheClearanceRequestElement);
		}

		return cacheClearanceRequestList;
	}

	/**
	 * Adds the provided urlPath to the candidate repo as if it had come from the real-time
	 * kafka topic or the jenkins job. Strictly for use in local and QA environments.
	 * @param urlPath
	 */
	public void handleDebugCacheClearanceEvent(String urlPath) {
		if (!"PROD".equalsIgnoreCase(envConfig.getAccountName())) {
			CacheClearanceRequest request = CacheClearanceRequest.createAllLevelCacheClearanceRequest();
			request.setUrlPath(urlPath);
			request.setServiceName(debugServiceName);
			handleCacheClearanceEvent(request, null);
		}
	}

}
