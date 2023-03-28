package com.about.mantle.app;

public final class MantleExternalConfigKeys {

	private MantleExternalConfigKeys() {
	}

	/* NOTE that fields having PASSWORD in their name have been flagged by sonarqube as blocking issues.
	 * That's fine but these variables simply contain the key used to query the sensitive information from consul.
	 * Even then the value returned from consul is encrypted and goes through a decryption process every time
	 * the value is needed. Finally, renaming these fields simply the remove the word password would constitute
	 * a breaking change as these fields are public. As such, we feel this "issue" is not that big of a deal so
	 * we choose to ignore them by using the //NOSONAR comment below where needed.
	 */

	public static final String PREFIX = "com.about.globe";
	public static final String HOMEPAGE_DOC_ID = PREFIX + ".homepage.docId";
	public static final String SESSION_COOKIES_ENABLED = PREFIX + ".cookies.session.enabled";
	public static final String LEGACY_URLS_SUPPORT = PREFIX + ".legacy.urls.support";
	public static final String LEGACY_URL_UPDATER_ENABLED = PREFIX + ".legacy.urls.updater.enabled";
	public static final String LEGACY_URLS_MIN_SIZE = PREFIX + ".legacy.urls.min.size";
	public static final String LEGACY_URLS_PREMISSIVE = PREFIX + ".legacy.urls.permissive.enabled";
	public static final String LEGACY_URLS_SERVICE_BASED_MAP = PREFIX + ".legacy.urls.servicebased.enabled";
	public static final String LEGACY_URL_SERVICE_CACHE_ENABLED = PREFIX + ".cache.legacyUrlService.enabled";
	public static final String SELENE_VERTICAL = PREFIX + ".selene.vertical";

	public static final String TEMPLATE_DEBUGGING_ENABLED = PREFIX + ".templateDebug";
	public static final String CURATEDDOMAIN_CACHE_ENABLED = PREFIX + ".cache.curatedDomain.enabled";
	public static final String CURATEDLIST_CACHE_ENABLED = PREFIX + ".cache.curatedList.enabled";
	public static final String DOCUMENT_CACHE_ENABLED = PREFIX + ".cache.document.enabled";
	public static final String DOCUMENT_ARCHIVE_CACHE_ENABLED = PREFIX + ".cache.documentArchive.enabled";
	public static final String ATTRIBUTION_CACHE_ENABLED = PREFIX + ".cache.attribution.enabled";
	public static final String AUTHOR_CACHE_ENABLED = PREFIX + ".cache.author.enabled";
	public static final String NAVIGATION_LINK_CACHE_ENABLED = PREFIX + ".cache.navigationLink.enabled";
	public static final String NOTIFICATION_CACHE_ENABLED = PREFIX + ".cache.notification.enabled";
	public static final String SEARCH_CACHE_ENABLED = PREFIX + ".cache.search.enabled";
	public static final String RELATED_SEARCH_CACHE_ENABLED = PREFIX + ".cache.relatedSearch.enabled";
	public static final String SOCIAL_LINK_CACHE_ENABLED = PREFIX + ".cache.socialLink.enabled";
	public static final String RELATED_ARTICLE_CACHE_ENABLED = PREFIX + ".cache.relatedArticle.enabled";
	public static final String CHAPTERS_CACHE_ENABLED = PREFIX + ".cache.chapters.enabled";
	public static final String CREDIT_CARD_CACHE_ENABLED = PREFIX + ".cache.creditCard.enabled";
	public static final String DEION_SEARCH_CACHE_ENABLED = PREFIX + ".cache.deionSearch.enabled";
	public static final String LEGACY_URL_MAP_DEION_SEARCH_CACHE_ENABLED = PREFIX + ".cache.legacyURLMapDeionSearch.enabled";
	public static final String DEION_SEARCH_FULL_DOCUMENT_CACHE_ENABLED = PREFIX + ".cache.deionSearchFullDocument.enabled";
	public static final String AMAZON_RSS_CACHE_ENABLED = PREFIX + ".cache.amazonRss.enabled";
	public static final String SITEMAP_CACHE_ENABLED = PREFIX + ".cache.sitemap.enabled";
	public static final String GOOGLE_NEWS_SITEMAP_CACHE_ENABLED = PREFIX + ".cache.google.news.sitemap.enabled";
	public static final String GOOGLE_NEWS_SITEMAP_ENABLED = PREFIX + ".google.news.sitemap.enabled";
	public static final String TESTING_ENABLED = PREFIX + ".testing.enabled";
	public static final String GLOBAL_TESTING_ENABLED = PREFIX + ".global.testing.enabled";
	public static final String TAXENE_CACHE_ENABLED = PREFIX + ".cache.taxene.enabled";
	public static final String TAXENE_RELATION_CACHE_ENABLED = PREFIX + ".cache.taxeneRelation.enabled";
	public static final String TAXENE_JOURNEY_CACHE_ENABLED = PREFIX + ".cache.taxeneJourney.enabled";
	public static final String DOCUMENT_TAXENE_COMPOSITE_CACHED_ENABLED = PREFIX + ".cache.documentTaxeneComposite.enabled";
	public static final String SUGGESTION_CACHE_ENABLED = PREFIX + ".cache.suggestion.enabled";
	public static final String UGC_FEEDBACK_CACHE_ENABLED = PREFIX + ".cache.ugcFeedback.enabled";
	public static final String UGC_USER_CACHE_ENABLED = PREFIX + ".cache.ugcUser.enabled";
	public static final String EMBED_CACHE_ENABLED = PREFIX + ".cache.embed.enabled";
	public static final String AUCTION_FLOOR_CACHE_ENABLED = PREFIX + ".cache.auctionFloor.enabled";
	public static final String PREBID_CONFIG_CACHE_ENABLED = PREFIX + ".cache.prebidConfig.enabled";
	public static final String USE_SELENE_ENDPOINT_FOR_DOC_SCHEMA = PREFIX + ".seleneEndPointForDocSchema.enabled";
	public static final String DOC_SCHEMA_CACHE_ENABLED = PREFIX + ".cache.docSchema.enabled";
	public static final String METADATA_CACHE_ENABLED = PREFIX + ".cache.metadata.enabled";
	public static final String MANTLE_410_CACHE_ENABLED = PREFIX + ".cache.mantle410Service.enabled";
	public static final String VERTICAL_CONFIG_CACHE_ENABLED = PREFIX + ".cache.verticalConfigService.enabled";
	public static final String DESCRIPTIVE_TAXONOMY_TERM_CACHE_ENABLED = PREFIX + ".cache.descriptiveTaxonomyTermService.enabled";

	/*
	 * Enables alternate caches to reduce heap space usage by avoiding caching of duplicate records
	 */
	public static final String RELATED_ARTICLE_CACHE_DEDUP_ENABLED = PREFIX + ".cache.relatedArticle.dedup.enabled";
	public static final String DOCUMENT_TAXENE_COMPOSITE_CACHED_DEDUP_ENABLED = PREFIX + ".cache.documentTaxeneComposite.dedup.enabled";

	/*
	 * Untrusted sponsored content must be vended from a sandboxed action in a separate domain.
	 */
	public static final String SANDBOXED_CONTENT_ENABLED = PREFIX + ".sandboxedContent.enabled";
	public static final String SANDBOXED_CONTENT_BASE_URL = PREFIX + ".sandboxedContent.baseUrl";
	public static final String SANDBOXED_CONTENT_DOMAIN = PREFIX + ".sandboxedContent.domain";

	public static final String SAFELIST_DOMAINS = PREFIX + ".safelist.domains";

	public static final String SELENE_AUTH_USERNAME = PREFIX + ".selene.auth.username";
	public static final String SELENE_AUTH_SECRET = PREFIX + ".selene.auth.secret";
	public static final String SELENE_SERVICE_NAME = PREFIX + ".selene.serviceName";
	public static final String SERVICES_CLIENT_ID = PREFIX + ".selene.id";
	public static final String SAILTHRU_CLIENT_KEY = PREFIX + ".sailthru.key";
	public static final String SAILTHRU_CLIENT_SECRET = PREFIX + ".sailthru.secret";
	public static final String SAILTHRU_CLIENT_SOCKET_TIMEOUT = PREFIX + ".sailthru.socketTimeout";
	public static final String SAILTHRU_CLIENT_CONNECT_TIMEOUT = PREFIX + ".sailthru.connectTimeout";
	public static final String THUMBOR_BASE_URL = PREFIX + ".thumbor.baseUrl";
	public static final String THUMBOR_KEY = PREFIX + ".thumbor.key";
	public static final String THUMBOR_MAC_POOL_ENABLED = PREFIX + ".thumbor.macPool.enabled";
	public static final String THUMBOR_MAC_POOL_MAX_SIZE = PREFIX + ".thumbor.macPool.maxSize";
	public static final String PROCTOR_GIT_PATH = PREFIX + ".testing.proctor.gitPath";
	public static final String PROCTOR_GIT_BRANCH = PREFIX + ".testing.proctor.gitBranch";
	public static final String PROCTOR_GIT_USERNAME = PREFIX + ".testing.proctor.gitUsername";
	public static final String PROCTOR_GIT_PASSWORD = PREFIX + ".testing.proctor.gitPassword"; //NOSONAR
	//global proctor setting
	public static final String GLOBAL_PROCTOR_GIT_PATH = PREFIX + ".global.testing.proctor.gitPath";
	public static final String GLOBAL_PROCTOR_GIT_BRANCH = PREFIX + ".global.testing.proctor.gitBranch";
	public static final String GLOBAL_PROCTOR_GIT_USERNAME = PREFIX + ".global.testing.proctor.gitUsername";
	public static final String GLOBAL_PROCTOR_GIT_PASSWORD = PREFIX + ".global.testing.proctor.gitPassword"; //NOSONAR

	public static final String RESOURCES_GIT_PATH = PREFIX + ".resources.gitPath";
	public static final String RESOURCES_GIT_BRANCH = PREFIX + ".resources.gitBranch";
	public static final String RESOURCES_GIT_ROOT = PREFIX + ".resources.gitRoot";
	public static final String RESOURCES_GIT_USERNAME = PREFIX + ".resources.gitUsername";
	public static final String RESOURCES_GIT_PASSWORD = PREFIX + ".resources.gitPassword";
	public static final String TAXENE_ROOT_DOCUMENT_ID = PREFIX + ".cache.taxene.root.docId";
	public static final String JWT_SECRET = PREFIX + ".jwt.secret";
	public static final String JWT_LIFESPAN = PREFIX + ".jwt.lifespan";
	public static final String PATTERN_LIBRARY_HEADER_NAME = PREFIX + ".pattern.library.header";
	public static final String PATTERN_LIBRARY_USER = PREFIX + ".pattern.library.user";
	public static final String PATTERN_LIBRARY_SECRET = PREFIX + ".pattern.library.secret";
	public static final String SELENE_SMILE_FORMAT = PREFIX + ".client.smile.format.enabled";
	public static final String POST_DOS_REQ_SEC = PREFIX + ".DoS.post.reqs";
	public static final String DOS_REQ_SEC = PREFIX + ".DoS.baseline.reqs";
	public static final String DOS_REQ_SAFELIST = PREFIX + ".DoS.baseline.safelist";
	public static final String DOS_REQ_REJECT = PREFIX + ".DoS.baseline.reject";
	public static final String DOS_ENABLED = PREFIX + ".DoS.enabled";
	public static final String DATA_BASE_URL_KEY = "com.about.data.api.base.url";
	public static final String DOTDASH_CIDRS = PREFIX + ".dotdashSubnets";
	public static final String AMAZON_RSS_INTERVAL_MODE_START = PREFIX + ".amazonRss.intervalModeStart";
	public static final String AMAZON_RSS_INTERVAL_MODE_SPAN = PREFIX + ".amazonRss.intervalModeSpan";
	public static final String RSSFEED_SEARCH_CACHE_ENABLED = PREFIX + ".cache.rssfeed_search.enabled";
	// Remove in GLBE-7092
	public static final String SUBHEADING_PREPROCESSOR_ENABLED = PREFIX + ".subheadingPreprocessor.enabled";

	public static final String PROXY_SERVLET_TIMEOUT = PREFIX + ".proxy.timeout";
	public static final String PROXY_SERVLET_BUFFER_SIZE = PREFIX + ".proxy.bufferSize";

	public static final String SUPPRESS_END_COMMENTS = PREFIX + ".suppressEndComments";

	public static final String WATERMARK_FILTER = PREFIX + ".watermark.filter";

	// The name of the site as specified in the verts news config
	public static final String RSS_NEWS_NAME = PREFIX + ".news.name";
	// The description of the site as specified in the verts news config
	public static final String RSS_NEWS_DESCRIPTION = PREFIX + ".news.description";
	// The link to the site as specified in the verts news config
	public static final String RSS_NEWS_LINK = PREFIX + ".news.link";

	public static final String FLIPBOARD_RSS_FEED_NAME = PREFIX + ".rssfeed.displayname";

	public static final String FLIPBOARD_RSS_FEED_DESCRIPTION = PREFIX + ".rssfeed.description";

	public static final String NEWS_TAXONOMY_DOCID = PREFIX + ".news.taxonomyDocId";
	public static final String TAXONOMY_DOCIDS_WHERE_CONTENT_TO_BE_SORTED_WITH_DEION_SEARCH = PREFIX + ".taxonomyDocIdsWhereContentToBeSortedWithDeionSearch";

	public static final String TOC_FAQ_DEFAULT_HEADING = PREFIX + ".toc.faq.default.heading";

	/**
	 * Email address used as the sender for privacy requests
	 */
	public static final String PRIVACY_REQ_SENDER_EMAIL = PREFIX + ".compliance.privacyReqSenderEmail";
	/**
	 * Email address used as the recipient for privacy requests
	 */
	public static final String PRIVACY_REQ_RECIPIENT_EMAIL = PREFIX + ".compliance.privacyReqRecipientEmail";

	public static final String TAXONOMY_DOCIDS_TO_BE_EXCLUDED_FROM_SITEWIDE_NOTIFICATION = PREFIX + ".taxonomyDocIdsToBeExcludedFromSitewideNotification";

	public static final String DOCIDS_TO_BE_EXCLUDED_FROM_SITEWIDE_NOTIFICATION = PREFIX + ".docIdsToBeExcludedFromSitewideNotification";

	public static final String getSeleneClientSmileFormatEnabled(String clientName) {
		return PREFIX + ".client." + clientName + ".smile.format.enabled";
	}

	public static final String getClientConnectTimeout(String clientName) {
		return PREFIX + ".client." + clientName + ".connectTimeout";
	}

	public static final String getClientReadTimeout(String clientName) {
		return PREFIX + ".client." + clientName + ".readTimeout";
	}

	public static final String getClientConnectionPoolEnabled(String clientName) {
		return PREFIX + ".client." + clientName + ".connectionPoolEnabled";
	}

	public static final String getClientConnectionPoolMax(String clientName) {
		return PREFIX + ".client." + clientName + ".connectionPoolMaxTotal";
	}

	public static final String getClientConnectionPoolDefaultMaxPerRoute(String clientName) {
		return PREFIX + ".client." + clientName + ".connectionPoolDefaultMaxPerRoute";
	}

	// Amazon Web Service Keys
	public static final String AWS_ECOMM_CACHE_ENABLED = PREFIX + ".amazonService.cacheEnabled";
	public static final String AWS_ECOMM_ACCESS_KEY = PREFIX + ".amazonService.accessKey";
	public static final String AWS_ECOMM_SECRET_KEY = PREFIX + ".amazonService.secretKey";
	public static final String AWS_ECOMM_ENDPOINT = PREFIX + ".amazonService.endPoint";
	public static final String AWS_ECOMM_ASSOCIATE_TAG = PREFIX + ".amazonService.associateTag";
	public static final String AWS_ECOMM_PAAPI_VERSION_5_ENABLED = PREFIX + ".amazonService.paapi.v5.enabled";

	//Skimlinks Pricing API
	public static final String SKIMLINKS_ECOMM_CACHE_ENABLED = PREFIX + ".skimlinksService.cacheEnabled";
	public static final String SKIMLINKS_ECOMM_API_KEY = PREFIX + ".skimlinksService.apiKey";
	public static final String SKIMLINKS_ECOMM_ID = PREFIX + ".skimlinksService.id";
	public static final String SKIMLINKS_ECOMM_BASE_URL = PREFIX + ".skimlinksService.base.url";

    // Walmart Product Lookup API
    public static final String WALMART_ECOMM_CACHE_ENABLED = PREFIX + ".walmartEcomService.cacheEnabled";
    public static final String WALMART_ECOMM_API_KEY = PREFIX + ".walmartEcomService.apiKey";
    public static final String WALMART_ECOMM_LINKSHARE_PUB_ID = PREFIX + ".walmartEcomService.linksharePublisherId";

    // Google Maps API
    public static final String GOOGLE_MAPS_API_KEY = PREFIX + ".googlemaps.api.apiKey";

    public static final String LOCALE = PREFIX + ".locale";

    //Cache clearing Kafka consumer
    public static final String KAFKA_BOOTSTRAP_SERVERS = PREFIX + ".kafka.bootstrap.servers";

    //Cache clearance candidate repo
    public static final String CACHE_CLEARANCE_HEADER_ENABLED = PREFIX + ".cacheClearance.header.enabled";
    public static final String CACHE_CLEARANCE_SECONDARY_DURATION = PREFIX + ".cacheClearance.secondary.duration"; // in minutes
    public static final String CACHE_CLEARANCE_REPLICATION_DELAY = PREFIX + ".cacheClearance.replicationDelayMilliseconds";

	//CMP
	public static final String CMP_DOMAIN_DATA_CACHE_ENABLED = PREFIX + ".cmp.domainData.cacheEnabled";
	public static final String CMP_ONETRUST_DOMAIN_ID = PREFIX + ".cmp.oneTrust.domainId";
	public static final String CMP_ONETRUST_LANG = PREFIX + ".cmp.oneTrust.lang";
	public static final String CMP_ONETRUST_TEMPLATES = PREFIX + ".cmp.oneTrust.templates";
	public static final String CMP_ONETRUST_SSR_ENABLED = PREFIX + ".cmp.oneTrust.ssr.enabled";
	public static final String CMP_ONETRUST_VERSION = PREFIX + ".cmp.oneTrust.version";

	//Blurry Thumbnail Keys
	public static final String BLURRY_THUMNBNAIL_ENABLED = PREFIX + ".blurryThumbnail.enabled";
	public static final String BLURRY_THUMBNAIL_GENERATE = PREFIX + ".blurryThumbnail.generateMissingThumbnail";

	// Switch vertical from using author/review to attributions
	public static final String ATTRIBUTION_ENABLED = PREFIX + ".attribution.enabled";

	// Facebook API
	public static final String FACEBOOK_ACCESS_TOKEN = PREFIX + ".facebook.accessToken";
	public static final String FACEBOOK_OPINION_META_ENABLED = PREFIX + ".facebook.opinionMeta.enabled";

	// Looker API
	public static final String LOOKER_URL = PREFIX + ".lookerapi.url";
	public static final String LOOKER_CLIENTID = PREFIX + ".lookerapi.clientId";
	public static final String LOOKER_SECRET = PREFIX + ".lookerapi.secret";
	public static final String LOOKER_VERTICAL = PREFIX + ".lookerapi.vertical";

	// Iframely Service
	public static final String IFRAMELY_ENABLED = PREFIX + ".iframely.enabled";
	public static final String IFRAMELY_CACHE_ENABLED = PREFIX + ".iframely.cache.enabled";
	public static final String IFRAMELY_API_KEY = PREFIX + ".iframely.apikey";

	//Disqus
	public static final String DISQUS_FORUM_NAME = PREFIX +".disqus.forumName";
	public static final String DISQUS_CACHE_ENABLED = PREFIX +".disqus.cache.enabled";
	public static final String DISQUS_API_KEY = PREFIX +".disqus.apiKey";
	public static final String DISQUS_DEFAULT_POPULAR_COMMENT_LIMIT = PREFIX +".disqus.popular.defaultMaxLimitComments";
	public static final String DISQUS_DEFAULT_POPULAR_MAX_AGE = PREFIX +".disqus.popular.defaultMaxAge";
	public static final String DISQUS_AGGREGATE_RATING_ENABLED = PREFIX + ".disqus.aggregateRating.enabled";
	public static final String DISQUS_AGGREGATE_RATING_CACHE_ENABLED = PREFIX + ".cache.disqus.aggregateRating.enabled";

	// Thread dump / diagnostics
	public static final String SHUTDOWN_THREAD_DUMP_PATH = PREFIX + ".shutdownThreadDumpPath";

	// Document preprocessors
	public static final String LINK_PRIMARY_AND_SECONDARY_TAXONOMY_TO_DOCS_ENABLED = PREFIX + ".docPreprocessor.primaryAndSecondaryTaxonomyLinking.enabled";

	//BOVD Json Data service
    public static final String BOVD_ENABLED = PREFIX + ".bovd.enabled";
	public static final String BOVD_JSON_DATA_CACHE_ENABLED = PREFIX + ".cache.bovdJsonData.enabled";

	// Descriptive Taxonomy Term (aka legacy meredith taxonomy)
	public static final String DESCRIPTIVE_TAXONOMY_TERM_BASE_URL = PREFIX + ".descriptiveTaxonomyTerm.endpoint";
	public static final String DESCRIPTIVE_TAXONOMY_TERM_API_KEY = PREFIX + ".descriptiveTaxonomyTerm.endpointAPIKey";

	//Legacy Meredith Shared Services
	public static final String SHARED_SERVICES_BASE_URL = PREFIX + ".sharedServices.baseUrl";
	public static final String SHARED_SERVICES_API_KEY = PREFIX + ".sharedServices.apiKey";
	public static final String SHARED_SERVICES_BRAND = PREFIX + ".sharedServices.brand";

	//RegSources used for Newsletters
	public static final String SHARED_SERVICE_REGSOURCES_CACHE_ENABLED = PREFIX + ".sharedServices.regSources.cache.enabled";

	//Meredith Centralized Affiliate Ecommerce Service
	public static final String CAES_BASE_URL = PREFIX + ".caes.baseUrl";
	public static final String CAES_ENABLED = PREFIX + ".caes.enabled";

	// Orion Ad Metrics Service
	public static final String ORION_BASE_URL = PREFIX + ".orion.baseUrl";
	public static final String ORION_ENABLED = PREFIX + ".orion.enabled";

	public static final String ADS_TXT_BOVD_PATH = PREFIX + ".adsTxt.bovdPath";

	public static final String LEGACY_MEREDITH_ALLOWED_NUTRIENTS = PREFIX + ".legacyMeredithAllowedNutrients.enabled";

	public static final String SELENE_DOC_URLS_WITHOUT_WWW = PREFIX + ".seleneDocUrls.withoutWww";

	// Resound api
	public static final String RESOUND_BASE_URL = PREFIX + ".resound.endpoint";

	//Auth0
	public static final String AUTH0_DOMAIN = PREFIX + ".auth0.domain";

	//Bookmarks service
	public static final String BOOKMARKS_BASE_URL = PREFIX + ".bookmarks.baseUrl";

	// Content Graph Service
	public static final String CONTENT_GRAPH_BASE_URL = PREFIX + ".contentGraph.baseUrl";
	public static final String CONTENT_GRAPH_API_KEY = PREFIX + ".contentGraph.apiKey";

	//Campaigns
	public static final String CAMPAIGNS_CONFIG_PATH = PREFIX + ".campaigns.configPath";

	//Pushly
	public static final String PUSHLY_DOMAIN_KEY = PREFIX + ".pushly.domainKey";

	public static final String UNCAPPED_IMAGE_WIDTHS_ENABLED = PREFIX + ".images.uncappedWidths.enabled";

	//Assets
	public static final String ASSETS_ENABLE_ASYNC_CSS = PREFIX + ".assets.css.async.enabled";

	// Determines whether Keycloak auth should be used - will be removed after migration from Auth0
	public static final String KEYCLOAK_AUTHENTICATION_ENABLED = PREFIX + ".keycloak.authentication.enabled";
}
