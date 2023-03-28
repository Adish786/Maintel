package com.about.mantle.spring;

import static com.about.mantle.model.feeds.FlipboardFeedBuilder.FLIPBOARD_FEED_NAME;
import static com.about.mantle.model.feeds.GoogleNewsFeedBuilder.GOOGLE_NEWS_FEED_NAME;
import static com.about.mantle.model.feeds.GoogleNewsTaxonomyFeedBuilder.GOOGLE_NEWS_TAXONOMY_FEED_NAME;
import static com.about.mantle.model.feeds.PinterestTaxonomyFeedBuilder.PINTEREST_TAXONOMY_FEED_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;


import com.about.mantle.app.LegacyUrlMapPermissive;
import com.about.mantle.app.DedicatedServiceBasedLegacyUrlMap;
import com.about.mantle.model.extended.LegacyUrlResultEx;
import com.about.mantle.model.services.LegacyUrlService;
import com.about.mantle.exception.MissingServiceException;
import com.about.mantle.model.services.auth.Auth0Verifier;
import com.about.mantle.model.services.auth.DDMAccountAuthService;
import com.about.mantle.model.services.bookmarks.BookmarksService;
import com.about.mantle.model.services.bookmarks.impl.BookmarksServiceImpl;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.extended.ugc.PhotoGalleryItem;
import com.about.mantle.model.services.cache.CachedLegacyUrlService;
import com.about.mantle.model.services.impl.LegacyUrlServiceImpl;
import com.about.mantle.model.services.resound.ResoundService;
import com.about.mantle.model.services.ugc.UGCUserService;
import com.about.mantle.model.services.ugc.cache.CachedUGCUserService;
import com.about.mantle.model.services.ugc.dto.UGCUserDto;
import com.about.mantle.model.services.ugc.impl.UGCUserServiceImpl;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jmx.support.ConnectorServerFactoryBean;

import com.about.globe.core.app.GlobeExternalConfigKeys;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.RequestContextContributorException;
import com.about.globe.core.expression.ExpressionEvaluator;
import com.about.globe.core.http.LinkHeaderRequestContextContributor;
import com.about.globe.core.http.LinkProviderFactory;
import com.about.globe.core.http.MessagesRequestContextContributor;
import com.about.globe.core.http.RequestContext.Builder;
import com.about.globe.core.http.RequestContextContributor;
import com.about.globe.core.http.RequestContextSource;
import com.about.globe.core.http.RequestContextSourceImpl;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.globe.core.spring.AbstractGlobeSpringConfiguration;
import com.about.globe.core.spring.GlobeThreadFactory;
import com.about.globe.core.testing.GlobeTestFramework;
import com.about.globe.core.web.filter.AccessLogInjector;
import com.about.globe.core.web.filter.MdcPopulator;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.hippodrome.config.servicediscovery.Service;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.hippodrome.jwt.exception.UnsupportedAlgorithmException;
import com.about.hippodrome.models.media.VersionedMediaTypes;
import com.about.hippodrome.restclient.AuthTokenProvider;
import com.about.hippodrome.restclient.CredentialsProvider;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig.HttpConnectionConfig;
import com.about.hippodrome.restclient.jwt.JwtAuthTokenProvider;
import com.about.hippodrome.url.DefaultingBaseUrlDocumentUrlDataFactory;
import com.about.hippodrome.url.DefaultingUrlDataFactory;
import com.about.hippodrome.url.PlatformUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.amazon.productAdvertisingApi.v5.tasks.AmazonProductTask;
import com.about.mantle.app.LegacyUrlMap;
import com.about.mantle.app.LegacyUrlMapImpl;
import com.about.mantle.app.CacheClearanceLegacyUrlUpdater;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.cache.clearance.CacheClearanceEventHandler;
import com.about.mantle.cache.clearance.CacheClearanceTemplateModifier;
import com.about.mantle.definition.action.DebugActionTasks;
import com.about.mantle.definition.action.NewsletterSignupService;
import com.about.mantle.definition.action.TemporaryComponentsActionTasks;
import com.about.mantle.expression.spring.MantleSpringExpressionEvaluator;
import com.about.mantle.handlers.methods.CommonTemplateNames;
import com.about.mantle.handlers.methods.MantleLinkHeaderHandlerMethods;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import com.about.mantle.handlers.methods.MantleResourceHandlerMethods;
import com.about.mantle.htmlslicing.ConfigurableHtmlSlicer;
import com.about.mantle.htmlslicing.HtmlSlicer;
import com.about.mantle.http.HttpRequestContextContributor;
import com.about.mantle.http.util.MantleIpDetectorUtils;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.infocat.services.cache.CachedProductService;
import com.about.mantle.infocat.services.impl.ProductServiceImpl;
import com.about.mantle.jmx.ArticleServiceJmxBean;
import com.about.mantle.jmx.DocumentTaxeneServiceJmxBean;
import com.about.mantle.logging.JsonFormatAccessLogInjector;
import com.about.mantle.logging.SafeListParamaterFailedLogger;
import com.about.mantle.model.commerce.AmazonCommerceModel;
import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.about.mantle.model.commerce.SkimlinksCommerceModel;
import com.about.mantle.model.commerce.WalmartCommerceModel;
import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermParsedData;
import com.about.mantle.model.disqus.DisqusPost;
import com.about.mantle.model.disqus.DisqusThreadDetails;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.extended.AuctionFloorInfo;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.SuggestionSearchResultItemEx;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.attribution.AttributionType;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.curatedlist.ImageCuratedListEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.ratings.DisqusAggregateRating;
import com.about.mantle.model.extended.responses.SearchExPageResponse;
import com.about.mantle.model.feeds.FeedBuilder;
import com.about.mantle.model.feeds.FeedBuilderFactory;
import com.about.mantle.model.feeds.FlipboardFeedBuilder;
import com.about.mantle.model.feeds.GoogleNewsFeedBuilder;
import com.about.mantle.model.feeds.GoogleNewsTaxonomyFeedBuilder;
import com.about.mantle.model.feeds.PinterestTaxonomyFeedBuilder;
import com.about.mantle.model.feeds.SmartNewsFeedBuilder;
import com.about.mantle.model.googleplace.GooglePlaceModel;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.json.JsonTask;
import com.about.mantle.model.mock.MockArticlesTask;
import com.about.mantle.model.mock.MockAuthorTask;
import com.about.mantle.model.mock.MockDocumentTask;
import com.about.mantle.model.mock.MockDocumentTaxeneCompositeTask;
import com.about.mantle.model.mock.MockJourneyTask;
import com.about.mantle.model.mock.MockPrebidTask;
import com.about.mantle.model.mock.MockTaxeneRelationTask;
import com.about.mantle.model.seo.QueryParamSafelist;
import com.about.mantle.model.services.ArticleService;
import com.about.mantle.model.services.AttributionService;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.services.AuctionFloorService;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.CampaignService;
import com.about.mantle.model.services.ConsentBannerService;
import com.about.mantle.model.services.CuratedDomainService;
import com.about.mantle.model.services.CuratedListService;
import com.about.mantle.model.services.DeionSearchFullDocumentService;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DisqusRatingService;
import com.about.mantle.model.services.DisqusService;
import com.about.mantle.model.services.DocSchemaService;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.DocumentArchiveService;
import com.about.mantle.model.services.DocumentTaxeneService;
import com.about.mantle.model.services.EmbedService;
import com.about.mantle.model.services.ExternalComponentService;
import com.about.mantle.model.services.JourneyService;
import com.about.mantle.model.services.Mantle410Service;
import com.about.mantle.model.services.DescriptiveTaxonomyTermService;
import com.about.mantle.model.services.MetaDataService;
import com.about.mantle.model.services.NavigationLinkService;
import com.about.mantle.model.services.NewsletterValidationService;
import com.about.mantle.model.services.NotificationService;
import com.about.mantle.model.services.PrebidConfigurationService;
import com.about.mantle.model.services.RtbService;
import com.about.mantle.model.services.SearchService;
import com.about.mantle.model.services.SocialLinkService;
import com.about.mantle.model.services.SocialLinkService.SocialLinkResponse;
import com.about.mantle.model.services.SuggestionService;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.VerticalConfigService;
import com.about.mantle.model.services.cache.CachedAmazonRssService;
import com.about.mantle.model.services.cache.CachedArticleService;
import com.about.mantle.model.services.cache.CachedAttributionService;
import com.about.mantle.model.services.cache.CachedAuctionFloorMappingConfigService;
import com.about.mantle.model.services.cache.CachedAuctionFloorService;
import com.about.mantle.model.services.cache.CachedAuthorService;
import com.about.mantle.model.services.cache.CachedConsentBannerService;
import com.about.mantle.model.services.cache.CachedCuratedDomainService;
import com.about.mantle.model.services.cache.CachedCuratedListService;
import com.about.mantle.model.services.cache.CachedDeionSearchFullDocumentService;
import com.about.mantle.model.services.cache.CachedDeionSearchService;
import com.about.mantle.model.services.cache.CachedDisqusRatingService;
import com.about.mantle.model.services.cache.CachedDisqusService;
import com.about.mantle.model.services.cache.CachedDocSchemaService;
import com.about.mantle.model.services.cache.CachedDocumentService;
import com.about.mantle.model.services.cache.CachedDocumentArchiveService;
import com.about.mantle.model.services.cache.CachedDocumentTaxeneService;
import com.about.mantle.model.services.cache.CachedEmbedService;
import com.about.mantle.model.services.cache.CachedGoogleNewsSitemapService;
import com.about.mantle.model.services.cache.CachedIframelyEmbedService;
import com.about.mantle.model.services.cache.CachedJourneyService;
import com.about.mantle.model.services.cache.CachedLocationService;
import com.about.mantle.model.services.cache.CachedMantle410Service;
import com.about.mantle.model.services.cache.CachedDescriptiveTaxonomyTermService;
import com.about.mantle.model.services.cache.CachedMetaDataService;
import com.about.mantle.model.services.cache.CachedNavigationLinksService;
import com.about.mantle.model.services.cache.CachedNotificationService;
import com.about.mantle.model.services.cache.CachedPrebidConfigurationService;
import com.about.mantle.model.services.cache.CachedSearchService;
import com.about.mantle.model.services.cache.CachedSocialLinkService;
import com.about.mantle.model.services.cache.CachedSuggestionService;
import com.about.mantle.model.services.cache.CachedTaxeneRelationService;
import com.about.mantle.model.services.cache.CachedVerticalConfigService;
import com.about.mantle.model.services.client.ServiceClientUtils;
import com.about.mantle.model.services.client.ServiceClientUtils.ClientMediaType;
import com.about.mantle.model.services.client.selene.CredentialsProviderImpl;
import com.about.mantle.model.services.client.selene.JwtAuthClientImpl;
import com.about.mantle.model.services.client.selene.SeleneAuthServiceHttpClient;
import com.about.mantle.model.services.commerce.AmazonCommerceApi;
import com.about.mantle.model.services.commerce.AmazonCommerceApiV5;
import com.about.mantle.model.services.commerce.AmazonRssService;
import com.about.mantle.model.services.commerce.AmazonRssServiceImpl;
import com.about.mantle.model.services.commerce.CachedWalmartProductLookupApiServiceFacade;
import com.about.mantle.model.services.commerce.CommerceApi;
import com.about.mantle.model.services.commerce.CommerceMappingService;
import com.about.mantle.model.services.commerce.CommerceMappingServiceImpl;
import com.about.mantle.model.services.commerce.CommerceService;
import com.about.mantle.model.services.commerce.CommerceServiceImpl;
import com.about.mantle.model.services.commerce.GenericCommerceApi;
import com.about.mantle.model.services.commerce.SkimlinksCommerceApi;
import com.about.mantle.model.services.commerce.VendorLookupService;
import com.about.mantle.model.services.commerce.WalmartCommerceApi;
import com.about.mantle.model.services.commerce.WalmartProductLookupApiServiceFacade;
import com.about.mantle.model.services.commerce.WalmartProductLookupApiServiceFacadeImpl;
import com.about.mantle.model.services.contentgraph.ContentGraphService;
import com.about.mantle.model.services.contentgraph.impl.ContentGraphServiceImpl;
import com.about.mantle.model.services.document.ElementRewriter;
import com.about.mantle.model.services.document.preprocessor.AmazonIdPreprocessor;
import com.about.mantle.model.services.document.preprocessor.DocumentPreprocessor;
import com.about.mantle.model.services.document.preprocessor.ElementRewriterPreprocessor;
import com.about.mantle.model.services.document.preprocessor.NLPPreprocessor;
import com.about.mantle.model.services.document.preprocessor.DescriptiveTaxonomyTermPreprocessor;
import com.about.mantle.model.services.document.preprocessor.PrimaryAndSecondaryTaxonomyPreprocessor;
import com.about.mantle.model.services.document.preprocessor.ProductRecordPreprocessor;
import com.about.mantle.model.services.document.preprocessor.ProfilePreprocessor;
import com.about.mantle.model.services.embeds.EmbedContent;
import com.about.mantle.model.services.embeds.EmbedProvider;
import com.about.mantle.model.services.embeds.IframelyEmbedProvider;
import com.about.mantle.model.services.embeds.OEmbedProvider;
import com.about.mantle.model.services.embeds.OEmbedProvider.OEmbedEndpoint;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.impl.CachedRssFeedService;
import com.about.mantle.model.services.feeds.impl.RssFeedServiceImpl;
import com.about.mantle.model.services.feeds.response.RssFeedSearchResponse;
import com.about.mantle.model.services.impl.ArticleServiceImpl;
import com.about.mantle.model.services.impl.AttributionServiceImpl;
import com.about.mantle.model.services.impl.AuctionFloorMappingConfigServiceImpl;
import com.about.mantle.model.services.impl.AuctionFloorServiceImpl;
import com.about.mantle.model.services.impl.AuthorServiceImpl;
import com.about.mantle.model.services.impl.BusinessOwnedVerticalDataServiceImpl;
import com.about.mantle.model.services.impl.CampaignServiceImpl;
import com.about.mantle.model.services.impl.CsvBasedPrebidConfigurationService;
import com.about.mantle.model.services.impl.CuratedDomainServiceImpl;
import com.about.mantle.model.services.impl.CuratedListServiceImpl;
import com.about.mantle.model.services.impl.DeionSearchFullDocumentServiceImpl;
import com.about.mantle.model.services.impl.DeionSearchServiceImpl;
import com.about.mantle.model.services.impl.DisqusRatingServiceImpl;
import com.about.mantle.model.services.impl.DisqusServiceImpl;
import com.about.mantle.model.services.impl.DocSchemaServiceImpl;
import com.about.mantle.model.services.impl.DocumentServiceImpl;
import com.about.mantle.model.services.impl.DocumentArchiveServiceImpl;
import com.about.mantle.model.services.impl.DocumentTaxeneServiceImpl;
import com.about.mantle.model.services.impl.EmbedServiceImpl;
import com.about.mantle.model.services.impl.IframelyEmbedService;
import com.about.mantle.model.services.impl.JourneyServiceImpl;
import com.about.mantle.model.services.impl.Mantle410ServiceImpl;
import com.about.mantle.model.services.impl.DescriptiveTaxonomyTermServiceImpl;
import com.about.mantle.model.services.impl.MetaDataServiceImpl;
import com.about.mantle.model.services.impl.NavigationLinkServiceImpl;
import com.about.mantle.model.services.impl.NewsletterValidationServiceImpl;
import com.about.mantle.model.services.impl.NotificationServiceImpl;
import com.about.mantle.model.services.impl.OneTrustConsentBannerService;
import com.about.mantle.model.services.impl.RtbServiceImpl;
import com.about.mantle.model.services.impl.SearchServiceImpl;
import com.about.mantle.model.services.impl.SocialLinkServiceImpl;
import com.about.mantle.model.services.impl.SuggestionServiceImpl;
import com.about.mantle.model.services.impl.TaxeneRelationServiceImpl;
import com.about.mantle.model.services.impl.VerticalConfigServiceImpl;
import com.about.mantle.model.services.location.LocationService;
import com.about.mantle.model.services.location.LocationServiceImpl;
import com.about.mantle.model.services.location.MockLocationServiceImpl;
import com.about.mantle.model.services.news.GoogleNewsSitemapService;
import com.about.mantle.model.services.news.GoogleNewsSitemapServiceImpl;
import com.about.mantle.model.services.prebid.PrebidConfiguration;
import com.about.mantle.model.services.prebid.bidders.BidderCreator;
import com.about.mantle.model.services.prebid.bidders.CriteoCreator;
import com.about.mantle.model.services.prebid.bidders.CriteoPGCreator;
import com.about.mantle.model.services.prebid.bidders.IndexExchangeCreator;
import com.about.mantle.model.services.prebid.bidders.RoundelCreator;
import com.about.mantle.model.services.prebid.bidders.PubmaticCreator;
import com.about.mantle.model.services.prebid.bidders.RubiconPGCreator;
import com.about.mantle.model.services.prebid.bidders.TrustXCreator;
import com.about.mantle.model.services.prebid.bidders.AppNexusCreator;
import com.about.mantle.model.services.prebid.bidders.OneMobileCreator;
import com.about.mantle.model.services.prebid.bidders.RubiconProjectCreator;
import com.about.mantle.model.services.prebid.bidders.TtdCreator;
import com.about.mantle.model.services.prebid.csv.DomainSpecificBeanVerifier;
import com.about.mantle.model.services.rtb.RtbPartner;
import com.about.mantle.model.services.ugc.UGCFeedbackService;
import com.about.mantle.model.services.ugc.cache.CachedUGCFeedbackService;
import com.about.mantle.model.services.ugc.impl.UGCFeedbackServiceImpl;
import com.about.mantle.model.shared_services.cache.CachedRegSourcesServiceImpl;
import com.about.mantle.model.shared_services.registration.impl.RegistrationServiceImpl;
import com.about.mantle.model.shared_services.registration.RegistrationService;
import com.about.mantle.model.shared_services.regsources.RegSourcesService;
import com.about.mantle.model.shared_services.regsources.impl.RegSourcesServiceImpl;
import com.about.mantle.model.shared_services.regsources.response.RegSource;
import com.about.mantle.model.tasks.AggregateRatingTask;
import com.about.mantle.model.tasks.AmazonRssTask;
import com.about.mantle.model.tasks.ArticlesTask;
import com.about.mantle.model.tasks.AttributionTask;
import com.about.mantle.model.tasks.AuctionFloorTask;
import com.about.mantle.model.tasks.AuthorTask;
import com.about.mantle.model.tasks.BuildResourcesTask;
import com.about.mantle.model.tasks.BusinessOwnedVerticalDataTask;
import com.about.mantle.model.tasks.CampaignTask;
import com.about.mantle.model.tasks.CommerceTask;
import com.about.mantle.model.tasks.ComplianceTask;
import com.about.mantle.model.tasks.ComponentTask;
import com.about.mantle.model.tasks.ConversationsTask;
import com.about.mantle.model.tasks.CuratedListTask;
import com.about.mantle.model.tasks.DeionSearchServiceTask;
import com.about.mantle.model.tasks.DigitalIssueTask;
import com.about.mantle.model.tasks.DisqusTask;
import com.about.mantle.model.tasks.DocSchemaTask;
import com.about.mantle.model.tasks.DocumentTask;
import com.about.mantle.model.tasks.DocumentTaxeneCompositeTask;
import com.about.mantle.model.tasks.DocumentTaxeneCuratedListTask;
import com.about.mantle.model.tasks.EmbedTask;
import com.about.mantle.model.tasks.EntityReferenceTask;
import com.about.mantle.model.tasks.ExternalComponentTask;
import com.about.mantle.model.tasks.ExternalConfigurationTask;
import com.about.mantle.model.tasks.FeedTask;
import com.about.mantle.model.tasks.GeoLocationTask;
import com.about.mantle.model.tasks.GoogleNewsSitemapTask;
import com.about.mantle.model.tasks.GptTask;
import com.about.mantle.model.tasks.IconsTask;
import com.about.mantle.model.tasks.ImageTask;
import com.about.mantle.model.tasks.JourneyTask;
import com.about.mantle.model.tasks.JourneyTaxeneRelationTask;
import com.about.mantle.model.tasks.JsonLdSchemaTask;
import com.about.mantle.model.tasks.LocationTask;
import com.about.mantle.model.tasks.MantleGTMPageViewTask;
import com.about.mantle.model.tasks.NavigationLinkTask;
import com.about.mantle.model.tasks.NewsTask;
import com.about.mantle.model.tasks.NotificationTask;
import com.about.mantle.model.tasks.PatternLibraryTask;
import com.about.mantle.model.tasks.PrebidTask;
import com.about.mantle.model.tasks.PrintReadyTemplateNameResolveTask;
import com.about.mantle.model.tasks.ProductRecordTask;
import com.about.mantle.model.tasks.QuizResultTask;
import com.about.mantle.model.tasks.RegistrationTask;
import com.about.mantle.model.tasks.RegSourcesTask;
import com.about.mantle.model.tasks.ResourceTasks;
import com.about.mantle.model.tasks.RtbTask;
import com.about.mantle.model.tasks.SailthruTagsTask;
import com.about.mantle.model.tasks.ParselyTagsTask;
import com.about.mantle.model.tasks.SearchTask;
import com.about.mantle.model.tasks.SitemapTask;
import com.about.mantle.model.tasks.SocialTask;
import com.about.mantle.model.tasks.StructuredContentDocumentProcessor;
import com.about.mantle.model.tasks.StructuredContentDocumentProcessor.StructuredContentAdInsertionStrategy;
import com.about.mantle.model.tasks.StructuredContentDocumentProcessor.StructuredContentAdSlotStrategy;
import com.about.mantle.model.tasks.StructuredNutritionTask;
import com.about.mantle.model.tasks.SuggestionTask;
import com.about.mantle.model.tasks.TableOfContentsTask;
import com.about.mantle.model.tasks.TaxeneConfigurationTask;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.model.tasks.TemplateNameResolveTask;
import com.about.mantle.model.tasks.ToolsTask;
import com.about.mantle.model.tasks.UGCFeedbackTask;
import com.about.mantle.model.tasks.UGCRatingsTask;
import com.about.mantle.model.transformers.BulletedListTransformer;
import com.about.mantle.model.transformers.RSS2Transformer;
import com.about.mantle.model.transformers.TopicPageTransformer;
import com.about.mantle.render.ImageResizer;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.render.freemarker.MantleFreeMarkerRenderingEngine;
import com.about.mantle.render.image.MantleImageRenderUtils;
import com.about.mantle.render.image.PollexorImageResizer;
import com.about.mantle.render.image.ThumborImageRenderUtils;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApiFacade;
import com.about.mantle.skimlinks.pricingApi.services.impl.CachedSkimlinksPricingApiFacade;
import com.about.mantle.skimlinks.pricingApi.services.impl.SkimlinksPricingApiFacadeImpl;
import com.about.mantle.skimlinks.pricingApi.services.impl.SkimlinksPricingApiImpl;
import com.about.mantle.spring.interceptor.MantleMdcPopulator;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;
import com.about.mantle.spring.interceptor.RedirectHandler;
import com.about.mantle.testing.SomethingMagicalService;
import com.about.mantle.testing.TestingRequestContextContributor;
import com.about.mantle.testing.proctor.DefaultIdentifiersExtractor;
import com.about.mantle.testing.proctor.IdentifiersExtractor;
import com.about.mantle.testing.proctor.MantleProctorSupplier;
import com.about.mantle.testing.proctor.ProctorGlobeTestFramework;
import com.about.mantle.testing.proctor.ProctorSupplier;
import com.about.mantle.web.filter.ExternalServiceProxyHandler;
import com.about.mantle.web.filter.Mantle404Handler;
import com.about.mantle.web.filter.MantleExternalComponentServiceProxyHandler;
import com.about.mantle.web.filter.MantleRedirectHandler;
import com.about.mantle.web.filter.redirect.RedirectRuleResolver;
import com.about.mantle.web.filter.redirect.RedirectRuleResolverImpl;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.dotdash.walmart.productlookup.DefaultProductLookupApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.sailthru.client.DefaultSailthruHttpClientConfiguration;
import com.sailthru.client.SailthruClient;
import com.sailthru.client.SailthruHttpClientConfiguration;

import net.sf.ehcache.Ehcache;

@Configuration
public abstract class MantleSpringConfiguration extends AbstractGlobeSpringConfiguration {

	public final static String THUMBOR_PROXY_PREFIX = "/thmb";
	private static final Logger logger = LoggerFactory.getLogger(MantleSpringConfiguration.class);
	// Using separate logger for thumbor pool size messages to avoid all logger info messages being logged
	private static final Logger thumborPoolLogger = LoggerFactory.getLogger(MantleSpringConfiguration.class.getCanonicalName()
			+ ".ThumborPool");
	private static final Timer proctorTimer = new Timer("Timer-Proctor", true);
	private static final Timer bovdGitTimer = new Timer("Timer-Git", true);
	private static final Timer sitemapTimer = new Timer("Timer-Sitemap", true);
	private static final Timer legacyUrlMapTimer = new Timer("Timer-legacyUrlMapDeion", true);
	private static final String SELENE_CLIENTID_HEADER = "Selene-Client-Id";
	private static final String SERVICES_UGC_CLIENTID_HEADER = "Services-UGC-Client-Id";
	private static final String SHARED_SERVICES_AUTH_HEADER_NAME = "x-api-key";
	private static final int ONE_MIN_IN_SECONDS = 60;
	private static final int ONE_HOUR_IN_SECONDS = ONE_MIN_IN_SECONDS * 60;
	private static final int ONE_DAY_IN_SECONDS = ONE_HOUR_IN_SECONDS * 24;
	private static final int TEN_MIN_IN_SECONDS = 600;
	private static final String CACHE_SITEMAP_DEION_SEARCH = "CachedDeionSearchService_sitemap";
	private static final String CACHE_LEGACY_DEION_SEARCH = "CachedLegacyUrlMapDeionSearchService_search";
	private static final String CACHE_LEGACY_URL_GETURLLIST = "CachedLegacyUrlService_getUrlList";

	//This is temprorary to avoid breaking changes
	//Should be moved into the documentService() constructor
	@Autowired
	private List<DocumentPreprocessor> documentPreprocessors;

	private HttpServiceClientConfig.Builder defaultServicesClientConfig(String clientName, Service service, boolean injectAuthTokenProvider) {
		return defaultServicesClientConfig(clientName, service, injectAuthTokenProvider, null, null);
	}

	private HttpServiceClientConfig.Builder defaultServicesClientConfig(String clientName, Service service, boolean injectAuthTokenProvider, String clientIdHeader, String clientId) {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultBaseClientConfig(clientName);
		httpConfigBuilder.setBaseUrl(determineServiceUrl(service));
		httpConfigBuilder.setMediaType(ClientMediaType.JSON_V1.getMediaType());

		if (StringUtils.isNotEmpty(clientIdHeader)){
			HttpServiceClientConfig.ClientId httpClientId;

			if (StringUtils.isNotEmpty(clientId)) {
				httpClientId = new HttpServiceClientConfig.ClientId(clientIdHeader, clientId);
			} else {
				String defaultClientId = propertyFactory.getProperty(MantleExternalConfigKeys.SERVICES_CLIENT_ID).asString(null).get();
				httpClientId = new HttpServiceClientConfig.ClientId(clientIdHeader, defaultClientId);
			}

			if (StringUtils.isNotEmpty(httpClientId.getValue())) {
				httpConfigBuilder.setClientId(httpClientId);
			}
		}

		if (injectAuthTokenProvider) {
			//this indicates if we want to throw RunTimeException if app can't authenticate
			httpConfigBuilder.setAuthenticationRequired(true);
			httpConfigBuilder.setAuthTokenProvider(authTokenProvider());
		}

		return httpConfigBuilder;
	}

	protected HttpServiceClientConfig.Builder defaultBaseClientConfig(String clientName) {
		//connection pool config
		Boolean connectionPoolEnabled = propertyFactory
				.getProperty(MantleExternalConfigKeys.getClientConnectionPoolEnabled(clientName)).asBoolean(true).get();
		Integer connectionPoolMaxTotal = propertyFactory
				.getProperty(MantleExternalConfigKeys.getClientConnectionPoolMax(clientName)).asInteger(200).get();
		Integer connectionPoolDefaultMaxPerRoute = propertyFactory
				.getProperty(MantleExternalConfigKeys.getClientConnectionPoolDefaultMaxPerRoute(clientName)).asInteger(20)
				.get();

		HttpConnectionConfig.Builder httpConnectionConfigBuilder = new HttpConnectionConfig.Builder();
		httpConnectionConfigBuilder.setConnectionPoolEnabled(connectionPoolEnabled);
		httpConnectionConfigBuilder.setConnectionPoolMaxTotal(connectionPoolMaxTotal);
		httpConnectionConfigBuilder.setConnectionPoolDefaultMaxPerRoute(connectionPoolDefaultMaxPerRoute);

		Integer readTimeout = propertyFactory.getProperty(MantleExternalConfigKeys.getClientReadTimeout(clientName))
				.asInteger(5000).get();

		Integer connectTimeout = propertyFactory.getProperty(MantleExternalConfigKeys.getClientConnectTimeout(clientName))
				.asInteger(5000).get();

		httpConnectionConfigBuilder.setReadTimeout(readTimeout);
		httpConnectionConfigBuilder.setConnectTimeout(connectTimeout);

		HttpServiceClientConfig.Builder httpServiceClientConfigBuilder = new HttpServiceClientConfig.Builder();

		httpServiceClientConfigBuilder.setHttpConnectionConfig(httpConnectionConfigBuilder.build());

		return httpServiceClientConfigBuilder;
	}

	protected boolean isUseSmile(String serviceName) {
		return propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_SMILE_FORMAT).asBoolean(false).get() || propertyFactory
				.getProperty(MantleExternalConfigKeys.getSeleneClientSmileFormatEnabled(serviceName)).asBoolean(false).get();
	}

	private HttpServiceClientConfig.Builder defaultSeleneClientConfig(String clientName, boolean injectAuthTokenProvider) {
		return defaultServicesClientConfig(clientName, seleneService(), injectAuthTokenProvider, SELENE_CLIENTID_HEADER, null);
	}

	protected HttpServiceClientConfig.Builder defaultSeleneClientConfig(String clientName) {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig(clientName, true);
		return httpConfigBuilder;
	}

	protected HttpServiceClientConfig.Builder defaultSeleneClientConfigWithoutAuth(String clientName) {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig(clientName, false);
		httpConfigBuilder.setMediaType(ClientMediaType.SMILE_V2.getMediaType());
		return httpConfigBuilder;
	}

	@Bean
	public AuthTokenProvider authTokenProvider() {
		String seleneAuthUsername = propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_AUTH_USERNAME).asString(null)
				.get();
		String seleneAuthSecret = propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_AUTH_SECRET).asString(null).get();
		if (seleneAuthUsername != null && seleneAuthSecret != null) {
			SeleneAuthServiceHttpClient authClient = new SeleneAuthServiceHttpClient(defaultSeleneClientConfigWithoutAuth(SELENE_CLIENTID_HEADER).build());
			CredentialsProvider credentialsProvider = new CredentialsProviderImpl(MantleExternalConfigKeys.SELENE_AUTH_USERNAME, MantleExternalConfigKeys.SELENE_AUTH_SECRET);
			return new JwtAuthTokenProvider(new JwtAuthClientImpl(authClient), credentialsProvider, null);
		}
		return null;
	}

	@Override
	@Bean
	public ProjectInfo projectInfo() {
		return new SpringBootManifestProjectInfoFinder(MantleSpringConfiguration.class).find();
	}

	@Override
	@Bean
	public AccessLogInjector accessLogInjector() {
		return new JsonFormatAccessLogInjector();
	}

	@Override
	@Bean
	public MantleFreeMarkerRenderingEngine renderingEngine() {
		Boolean debugEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.TEMPLATE_DEBUGGING_ENABLED).asBoolean(true)
				.get();

		return new MantleFreeMarkerRenderingEngine(templateLoader(), renderUtils(), debugEnabled, environmentConfig(),
				projectInfo(), resourceAggregator(), linkProviderFactory(), resourceTasks());
	}

	@Override
	@Bean
	protected ExpressionEvaluator expressionEvaluator() {
		return new MantleSpringExpressionEvaluator(expressionParser(), renderUtils(), projectInfo());
	}

	@Bean
	public RequestContextContributor overrideRequestContextContributor() {
		return new RequestContextContributor() {
			@Override
			public void contribute(HttpServletRequest request, Builder builder)
					throws RequestContextContributorException {
				// NO-OP
			}
		};
	}

	public Locale locale() {
		return propertyFactory.getProperty(MantleExternalConfigKeys.LOCALE).asType(Locale.class, Locale.ENGLISH).get();
	}

	@Bean
	public HttpRequestContextContributor httpRequestContextContributor() {
		return new HttpRequestContextContributor(userAgentParser(), urlDataFactory(), locale());
	}

	@Bean
	public Map<String, String> messageProperties() {
		return resourceBundleResolver().resolve(locale());
	}

	@Bean
	public MessagesRequestContextContributor messagesRequestContextContributor() {
		return new MessagesRequestContextContributor(globe());
	}

	@Bean
	public abstract RequestContextContributor domainRequestContextContributor();

	@Bean
	public abstract TemplateNameResolveTask defaultTemplateNameResolveTask();

	@Bean
	public BusinessOwnedVerticalDataTask bovdTask() {
		return new BusinessOwnedVerticalDataTask(bovdService());
	}

	@Bean
	public RequestContextContributor proctorRequestContextContributor() {
		return new TestingRequestContextContributor(globeTestFramework());
	}

	/**
	 * Override this method in implementing classes to return a Contributor instantiated with a set of strings that should be
	 * included alongside resources as part of the "Link" response header in {@link MantleRequestHandlerMethods}' render methods
	 *
	 * See http://www.rfc-editor.org/rfc/rfc5988.txt or https://www.w3.org/wiki/LinkHeader for details regarding the nature of
	 * well-formed Link header values.  In addition to these specifications, the strings specified here can optionally contain
	 * the special pattern "{HOST}", which will be populated with the value of the request host at render time.
	 *
	 * @return
	 */
	@Bean
	public LinkHeaderRequestContextContributor linkHeaderRequestContextContributor() {
		ArrayList<String> linkHeaders = new ArrayList<String>();
		linkHeaders.add("<//www.googletagmanager.com/gtm.js?id="+ propertyFactory.getProperty(GlobeExternalConfigKeys.GTM_ACCOUNT_ID).asString("GTM-FAKEBAD").get() + ">; rel=preload; as=script; nopush");
		linkHeaders.add("<//www.google-analytics.com/analytics.js>; rel=preload; as=script; nopush");
		linkHeaders.add("<//securepubads.g.doubleclick.net/tag/js/gpt.js>; rel=preload; as=script; nopush");
		return new LinkHeaderRequestContextContributor(linkHeaders);
	}

	@Bean
	public RequestContextSource requestContextSource() {
		ImmutableList.Builder<RequestContextContributor> contributors = ImmutableList.builder();

		contributors.add(httpRequestContextContributor());
		contributors.add(deferRequestContextContributor());
		contributors.add(debugRequestContextContributor());
		contributors.add(domainRequestContextContributor());
		contributors.add(overrideRequestContextContributor());
		contributors.add(messagesRequestContextContributor());
		contributors.add(proctorRequestContextContributor());
		contributors.add(linkHeaderRequestContextContributor());

		return new RequestContextSourceImpl(contributors.build());
	}

	@Bean
	public LinkProviderFactory linkProviderFactory() {
		return requestContext -> path -> path;
	}

	@Bean
	public CuratedListService curatedListService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("curatedList");
		String mediaType = isUseSmile("curatedList") ? ClientMediaType.SMILE_V3.getMediaType() : ClientMediaType.JSON_V3.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		CuratedListServiceImpl service = new CuratedListServiceImpl(httpConfigBuilder.build());


		if (propertyFactory.getProperty(MantleExternalConfigKeys.CURATEDLIST_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<SliceableListEx<ImageCuratedListEx>> imageListByNameCache = getPersistentCache(
					"CachedCuratedListService_imageListByName",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, ImageCuratedListEx.class),
					1000, false, 300);

			CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListByNameCache = getPersistentCache(
					"CachedCuratedListService_documentSummaryListByName",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, DocumentCuratedListEx.class),
					1000, false, 300);

			CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListHistoryByNameCache = getPersistentCache(
					"CachedCuratedListService_documentSummaryListHistoryByName",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, DocumentCuratedListEx.class),
					1000, false, 300);

			CacheTemplate<SliceableListEx<DocumentCuratedListOfListEx>> documentSummaryListOfListByNameCache = getPersistentCache(
					"CachedCuratedListService_documentSummaryListOfListByName",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, DocumentCuratedListOfListEx.class),
					1000, false, 300);

			return new CachedCuratedListService(service, imageListByNameCache, documentSummaryListByNameCache,
					documentSummaryListHistoryByNameCache, documentSummaryListOfListByNameCache);
		}

		return service;
	}

	@Bean
	public DocSchemaService docSchemaService() {
		if (!propertyFactory.getProperty(MantleExternalConfigKeys.USE_SELENE_ENDPOINT_FOR_DOC_SCHEMA).asBoolean(false).get()) {
			return null;
		}

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("docSchema");
		httpConfigBuilder.setMediaType("application/json");
		DocSchemaServiceImpl service = new DocSchemaServiceImpl(httpConfigBuilder.build());


		if (propertyFactory.getProperty(MantleExternalConfigKeys.DOC_SCHEMA_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<String> docSchemaByDocIdCache = getPersistentCache(
					"CachedDocSchemaService", objectMapper().getTypeFactory().constructType(String.class),
					1000, false, (long) 60 * 60 * 12); // 12 hour cache

			return new CachedDocSchemaService(service, docSchemaByDocIdCache);
		}

		return service;
	}

	@Bean
	public AttributionService attributionService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("attribution");
		String mediaType = isUseSmile("attribution") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		AttributionService service = new AttributionServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.ATTRIBUTION_CACHE_ENABLED).asBoolean(true).get()) {

			CacheTemplate<Attribution> attributionByIdCache = getPersistentCache(
					"CachedAttributionService_attributionById",
					objectMapper().getTypeFactory().constructType(Attribution.class),
					1000, false, 300);

			CacheTemplate<Map<String, AttributionType>> attributionTypesCache = getPersistentCache(
					"CachedAttributionService_attributionTypes",
					objectMapper().getTypeFactory().constructType(Map.class),
					1, false, 3600);

			CacheTemplate<SliceableListEx<Attribution>> attributionListByAuthorIdCache = getPersistentCache(
					"CachedAttributionService_attributionListByAuthorId",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, Attribution.class),
					1000, false, 3600);

			return new CachedAttributionService(service, attributionByIdCache, attributionTypesCache, attributionListByAuthorIdCache);
		}

		return service;
	}

	@Bean
	public AuthorService authorService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("author");
		String mediaType = isUseSmile("author") ? ClientMediaType.SMILE_V2.getMediaType() : ClientMediaType.JSON_V2.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		AuthorServiceImpl service = new AuthorServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AUTHOR_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<AuthorEx> authorByIdCache = getPersistentCache(
					"CachedAuthorService_authorById",
					objectMapper().getTypeFactory().constructType(AuthorEx.class),
					1000, false, 300);

			return new CachedAuthorService(service, authorByIdCache);
		}

		return service;
	}

	@Bean
	public MetaDataService metaDataService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("metaData");
		httpConfigBuilder.setMediaType(ClientMediaType.SMILE_V1.getMediaType());

		MetaDataServiceImpl metaDataService = new MetaDataServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.METADATA_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<MetaDataEx> metaDataCache = getPersistentCache("CachedMetaDataService",
														objectMapper().getTypeFactory().constructType(MetaDataEx.class),
														1000, false, 300);

			return new CachedMetaDataService(metaDataService, metaDataCache);
		}

		return metaDataService;
	}

	@Bean
	public DocumentService documentService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("document");
		String mediaType = isUseSmile("document") ? ClientMediaType.SMILE_V2.getMediaType() : ClientMediaType.JSON_V2.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		DocumentServiceImpl service = new DocumentServiceImpl(httpConfigBuilder.build(),documentPreprocessors);

		DocumentService serviceWrapper = documentServiceWrapper(service);

		if (propertyFactory.getProperty(MantleExternalConfigKeys.DOCUMENT_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<BaseDocumentEx> documentResponseCache = getPersistentCache("CachedDocumentService_documentResponse",
					objectMapper().getTypeFactory().constructType(BaseDocumentEx.class),
					1000, false, 300);

			// Permissive LegacyUrlMap causes circular reference to DocumentService
			PlatformUrlDataFactory urlDataFactory = isLegacyUrlMapPermissiveEnabled() ? null : urlDataFactory();
			return new CachedDocumentService(serviceWrapper, documentResponseCache, urlDataFactory);
		}

		return serviceWrapper;
	}

	protected DocumentService documentServiceWrapper(DocumentService service) {
		return service; // don't wrap it--return the actual service by default
	}

	@Bean
	public DocumentArchiveService documentArchiveService() {
		Vertical vertical = null;
		try {
			vertical = getSeleneVertical();
			logger.debug("Document Archive Service enabled for vertical [{}]", vertical.toString());
		} catch (GlobeException e) {
			logger.warn("Document Archive Service disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return DocumentArchiveService.emptyArchiveService();
		}

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("documentArchive");
		httpConfigBuilder.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON);
		DocumentArchiveServiceImpl service = new DocumentArchiveServiceImpl(httpConfigBuilder.build(), vertical.toString());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.DOCUMENT_ARCHIVE_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<Set<String>> archivedDocumentCache = getPersistentCache("CachedDocumentArchiveService_documentArchiveResponse",
					objectMapper().getTypeFactory().constructParametricType(Set.class, String.class),
					1, false, 3600);

			return new CachedDocumentArchiveService(service, archivedDocumentCache);
		}

		return service;
	}

	@Bean
	public DeionSearchService deionSearchService() {
		DeionSearchService service = uncachedDeionSearchService();

		if (propertyFactory.getProperty(MantleExternalConfigKeys.DEION_SEARCH_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<DeionSearchResultEx> searchCache = getPersistentCache(
					"CachedDeionSearchService_search",
					objectMapper().getTypeFactory().constructType(DeionSearchResultEx.class),
					1000, false,
					TEN_MIN_IN_SECONDS);
			return new CachedDeionSearchService(service, searchCache);
		}
		return service;
	}

	@Bean
	public DeionSearchService sitemapDeionSearchService() {
		DeionSearchService service = uncachedDeionSearchService();

		if (propertyFactory.getProperty(MantleExternalConfigKeys.SITEMAP_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<DeionSearchResultEx> searchCache = getPersistentCache(
					CACHE_SITEMAP_DEION_SEARCH,
					objectMapper().getTypeFactory().constructType(DeionSearchResultEx.class),
					1000, false,
					TEN_MIN_IN_SECONDS);
			return new CachedDeionSearchService(service, searchCache);
		}
		return service;
	}

	/**
	 * Service for performing deion searches for legacy url map where the map has to be continuously updated.
	 * As we need to set separate configs for this service it is a separate service form the default deion search service.
	 * See https://dotdash.atlassian.net/browse/GLBE-9508
	 */
	@Bean
	public DeionSearchService legacyUrlMapDeionSearchService() {

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("legacyUrlMapDeionSearch");
		String mediaType =  isUseSmile("deionSearch") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		DeionSearchService service = new DeionSearchServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URL_MAP_DEION_SEARCH_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<DeionSearchResultEx> searchCache = getPersistentCache(
					CACHE_LEGACY_DEION_SEARCH,
					objectMapper().getTypeFactory().constructType(DeionSearchResultEx.class),
					1000, false,
					TEN_MIN_IN_SECONDS);
			return new CachedDeionSearchService(service, searchCache);
		}
		return service;
	}

	@Bean
	public LegacyUrlService legacyUrlService(){

		if(!isLegacyDocumentsSupported()) {
			return null;
		}

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("legacyUrlService");
		String mediaType =  isUseSmile("legacyurl") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		Vertical seleneVertical = getSeleneVertical();

		LegacyUrlService answer = new LegacyUrlServiceImpl(httpConfigBuilder.build(), seleneVertical.toString());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URL_SERVICE_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<LegacyUrlResultEx> legacyUrlResultCache = getPersistentCache(
					CACHE_LEGACY_URL_GETURLLIST,
					objectMapper().getTypeFactory().constructType(LegacyUrlResultEx.class),
					1000, false,
					TEN_MIN_IN_SECONDS);
			return new CachedLegacyUrlService(answer, legacyUrlResultCache,seleneVertical.toString());
		}

		return answer;

	}

	/**
	 * Service for performing deion searches but bypassing caching of requests. The need to prevent caching of this
	 * service may need to be done selectively in each dependent service. The regular deion search service bean is
	 * unmodified.
	 * @return
	 */
	@Bean
	public DeionSearchService uncachedDeionSearchService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("deionSearch");
		String mediaType = isUseSmile("deionSearch") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		return new DeionSearchServiceImpl(httpConfigBuilder.build());
	}

	@Bean
	public DeionSearchFullDocumentService deionSearchFullDocumentService() {

		HttpServiceClientConfig.Builder config = defaultSeleneClientConfig("deionSearchFullDocument");

		config.setMediaType(isUseSmile("deionSearchFullDocument") ?
				ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType());

		DeionSearchFullDocumentServiceImpl service = new DeionSearchFullDocumentServiceImpl(config.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.DEION_SEARCH_FULL_DOCUMENT_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<DeionSearchFullResultEx> searchCache = getPersistentCache(
					"CachedDeionSearchFullDocumentService_searchFullResults",
					objectMapper().getTypeFactory().constructType(DeionSearchFullResultEx.class),
					1000, false,
					3600);
			return new CachedDeionSearchFullDocumentService(service, searchCache);
		}
		return service;
	}

	@Bean
	public AmazonRssService amazonRssService() {
		AmazonRssService service = new AmazonRssServiceImpl(deionSearchFullDocumentService(), documentService());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AMAZON_RSS_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<List<BaseDocumentEx>> amazonRssCache = getPersistentCache(
					"CachedAmazonRssService_getDocumentsForRssFeed",
					objectMapper().getTypeFactory().constructParametricType(List.class, BaseDocumentEx.class),
					1, false,
					3600);
			return new CachedAmazonRssService(service, amazonRssCache);
		}
		return service;
	}


	public GoogleNewsSitemapService googleNewsSitemapService() {
		GoogleNewsSitemapService service = new GoogleNewsSitemapServiceImpl(uncachedDeionSearchService());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.GOOGLE_NEWS_SITEMAP_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<List<BaseDocumentEx>> googleNewsSitemapServiceCache = getPersistentCache (
					"CachedGoogleNewsSitemapService_getGoogleNewsDocuments",
					objectMapper().getTypeFactory().constructParametricType(List.class, BaseDocumentEx.class),
					1, false,
					3600);

			return new CachedGoogleNewsSitemapService(service, googleNewsSitemapServiceCache);
		}
		return service;
	}

	@Bean
	public Service seleneService() {
		final String serviceName = propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_SERVICE_NAME).asString("selene").get();
		logger.warn("Configured selene service cluster: [{}]", serviceName);
		return getService(serviceName, false);
	}

	protected String determineServiceUrl(@NotNull Service service) {
		String answer = null;
		if (service == null || service.getUris() == null) {
			answer = null;
		} else {
			Optional<String> maybeHost = service.getUris().stream()
					.filter(uri -> "https".equals(uri.getScheme()))
					.filter(uri -> StringUtils.isNotBlank(uri.getHost()))
					.sorted((u1, u2) -> u1.getHost().contains(".dotdash.com")? -1 : 1)
					.map(URI::toASCIIString)
					.findFirst();

			if (maybeHost.isPresent()) {
				answer = maybeHost.get();
			}
		}
		if (answer == null) {
			throw new GlobeException("Could not determine host from Service instance: " + service);
		}
		return answer;
	}

	private Service getService(String serviceName, boolean isOptional) {
		Service answer = propertyFactory.getService(serviceName, GlobeExternalConfigKeys.getServiceDiscoveryFallbackUrlKey(serviceName));
		if (answer == null) {
			if (isOptional) {
				logger.warn("Could not find {} during service discovery.", serviceName);
			} else {
				throw new MissingServiceException("Could not find " + serviceName + " during service discovery.", serviceName);
			}
		}
		return answer;
	}

	@Bean
	public Service infocatService() {
		return getService("neko", true);
	}

	@Bean
	public Service ugcService() {
		return getService("services-ugc", false);
	}

	@Bean
	public NavigationLinkService navigationLinkService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("navigationLink");
		String mediaType = isUseSmile("navigationLink") ? ClientMediaType.SMILE_V2.getMediaType() : ClientMediaType.JSON_V2.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		NavigationLinkServiceImpl service = new NavigationLinkServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.NAVIGATION_LINK_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<SliceableListEx<CategoryLinkEx>> navigationLinksCache = getPersistentCache(
					"CachedNavigationLinksService_navigationLinks",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, CategoryLinkEx.class),
					1000, false, 300);

			return new CachedNavigationLinksService(service, navigationLinksCache);
		}

		return service;
	}

	@Bean
	public NotificationService notificationService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("notification");
		String mediaType = isUseSmile("notification") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		NotificationServiceImpl service = new NotificationServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.NOTIFICATION_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<SliceableListEx<NotificationEx>> notificationCache = getPersistentCache("CachedNotificationService_notificationList",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, NotificationEx.class),
					1000, false, 3600);

			return new CachedNotificationService(service, notificationCache);
		}

		return service;
	}

	@Bean
	public ProductService productService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = null;
		try {
			/* Environemnts may not have infocat service. Catching error configuring client avoid errors on other
			 * verticals.
			 */
			httpConfigBuilder = defaultServicesClientConfig("product", infocatService(), false);
		} catch (GlobeException e) {
			logger.warn("ProductService disabled. Error encountered configuring infocat client.", e);
			return null;
		}

		ProductServiceImpl service = new ProductServiceImpl(httpConfigBuilder.build(), threadPoolExecutor());
		if (propertyFactory.getProperty(GlobeExternalConfigKeys.getCacheEnabled("product")).asBoolean(true).get()) {

			CacheTemplate<Product> productByIdCache = getPersistentCache("CachedProductService_productResponse",
					objectMapper().getTypeFactory().constructType(Product.class), 1000, false, 300);

			return new CachedProductService(service, productByIdCache);
		}

		return service;
	}

	@Bean
	public SearchService searchService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("search");
		String mediaType = isUseSmile("search") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		SearchServiceImpl service = new SearchServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.SEARCH_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<SearchExPageResponse> searchCache = getPersistentCache("CachedSearchService_search",
					objectMapper().getTypeFactory().constructType(SearchExPageResponse.class),
					1000, false, 300);

			return new CachedSearchService(service, searchCache);
		}
		return service;
	}

	@Bean
	public SocialLinkService socialLinkService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("socialLink");
		String mediaType = isUseSmile("socialLink") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		SocialLinkServiceImpl service = new SocialLinkServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.SOCIAL_LINK_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<SocialLinkResponse> socialLinksCache = getPersistentCache("CachedSocialLinkService_socialLinks",
					objectMapper().getTypeFactory().constructType(SocialLinkResponse.class),
					1000,
					false, 300);

			return new CachedSocialLinkService(service, socialLinksCache);
		}

		return service;
	}

	@Bean
	public SuggestionService suggestionService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("suggestion");
		String mediaType = isUseSmile("suggestion") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		SuggestionServiceImpl service = new SuggestionServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.SUGGESTION_CACHE_ENABLED).asBoolean(false).get()) {

			CacheTemplate<SliceableListEx<SuggestionSearchResultItemEx>> searchCache = getPersistentCache(
					"CachedSuggestionService_search",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, SuggestionSearchResultItemEx.class),
					1000, false, 300);

			return new CachedSuggestionService(service, searchCache);
		}
		return service;
	}

	// Services

	@Bean
	public ArticleService articleService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("relatedArticle");
		boolean useSmile = propertyFactory.getProperty(MantleExternalConfigKeys.getSeleneClientSmileFormatEnabled("relatedArticle")).asBoolean(false).get();
		String mediaType = useSmile ? ServiceClientUtils.ClientMediaType.SMILE_V2.getMediaType() : ServiceClientUtils.ClientMediaType.JSON_V2.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);

		ArticleServiceImpl service = new ArticleServiceImpl(httpConfigBuilder.build());
		CacheTemplate<SliceableListEx<BaseDocumentEx>> listRelatedCache = null;
		CacheTemplate<BaseDocumentEx> dedupRelatedCache = null;
		CacheTemplate<List<Long>> relatedDocIdCache = null;

		if (propertyFactory.getProperty(MantleExternalConfigKeys.RELATED_ARTICLE_CACHE_ENABLED).asBoolean(false).get()) {
			JavaType listType = objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, BaseDocumentEx.class);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.RELATED_ARTICLE_CACHE_DEDUP_ENABLED).asBoolean(false).get()) {
				/*
				 * When using the deduplicated cache for ArticleService, related documents are stored individually. For performance reasons, some caches must be
				 * set as redis-only and some as ehcache-only.
				 */

				//cache of selene call directly
				listRelatedCache = getPersistentCache("CachedRelatedArticleService_related", listType, 1000, false, 300, false, true, false, true);

				//deduped list of documents used in related articles for all pages
				dedupRelatedCache = getCache("CachedRelatedArticleService_relatedDedup", 20000, false, 3000);

				//Cache for the list of deduped documents for a page
				relatedDocIdCache = getCache("CachedRelatedArticleService_relatedDocIds", 20000, false, 3000);
			} else {
				listRelatedCache = getPersistentCache("CachedRelatedArticleService_related", listType, 1000, false, 300);
			}
		}

		return new CachedArticleService(service, listRelatedCache, dedupRelatedCache, relatedDocIdCache, threadPoolExecutor());
	}

	@Bean
	public ArticleServiceJmxBean articleServiceJmxBean() {
		return new ArticleServiceJmxBean(articleService());
	}

	@Bean
	public DocumentTaxeneServiceJmxBean documentTaxeneServiceJmxBean() {
		return new DocumentTaxeneServiceJmxBean(documentTaxeneService());
	}

	@Bean
	public AuctionFloorService auctionFloorService() {
		Vertical vertical = null;
		try {
			vertical = getSeleneVertical();
			logger.debug("Auction Floor Pricing enabled for vertical [{}]", vertical.toString());
		} catch (GlobeException e) {
			logger.warn("Auction Floor Pricing disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("auctionFloor");
		String mediaType = isUseSmile("auctionFloor") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		AuctionFloorServiceImpl service = new AuctionFloorServiceImpl(httpConfigBuilder.build(), vertical.toString());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AUCTION_FLOOR_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<AuctionFloorInfo> auctionFloorInfoCache = getPersistentCache(
					"AuctionFloorService_auctionFloorInfo",
					objectMapper().getTypeFactory().constructType(AuctionFloorInfo.class),
					1000, false, 300);

			CacheTemplate<SliceableListEx<AuctionFloorInfoListItem>> auctionFloorListInfoCache = getPersistentCache(
				"AuctionFloorService_auctionFloorListInfo",
				objectMapper().getTypeFactory().constructType(SliceableListEx.class),
				1000, false, 300);

			return new CachedAuctionFloorService(service, auctionFloorInfoCache, auctionFloorListInfoCache);
		}

		return service;
	}

	@Bean
	public AuctionFloorMappingConfigService auctionFloorListService() {
		Vertical vertical = null;
		try {
			vertical = getSeleneVertical();
			logger.debug("Auction Floor Pricing enabled for vertical [{}]", vertical.toString());
		} catch (GlobeException e) {
			logger.warn("Auction Floor Pricing disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("auctionFloor");
		String mediaType = isUseSmile("auctionFloor") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		AuctionFloorMappingConfigServiceImpl service = new AuctionFloorMappingConfigServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AUCTION_FLOOR_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<AuctionFloorConfig> auctionFloorConfigCache = getPersistentCache(
					"AuctionFloorMappingConfigServiceImpl_auctionFloorConfig",
					objectMapper().getTypeFactory().constructType(AuctionFloorConfig.class),
					1, false, 300);

			return new CachedAuctionFloorMappingConfigService(service, auctionFloorConfigCache);
		}

		return service;
	}

	protected Boolean isDescriptiveTaxonomyTermSupported() {
		return StringUtils.isNotEmpty(propertyFactory.getProperty(MantleExternalConfigKeys.DESCRIPTIVE_TAXONOMY_TERM_BASE_URL).asString(null).get());
	}

	private HttpServiceClientConfig.Builder descriptiveTaxonomyTermClientConfig(String clientName) {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultBaseClientConfig(clientName);
		httpConfigBuilder.setBaseUrl(propertyFactory.getProperty(MantleExternalConfigKeys.DESCRIPTIVE_TAXONOMY_TERM_BASE_URL).asString(null).get());
		httpConfigBuilder.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON);

		return httpConfigBuilder;
	}

	@Bean
	public DescriptiveTaxonomyTermService descriptiveTaxonomyTermService() {
		if (isDescriptiveTaxonomyTermSupported()) {
			HttpServiceClientConfig.Builder httpConfigBuilder = descriptiveTaxonomyTermClientConfig("descriptiveTaxonomyTerm");

			String apiKey = getSecureProperty(MantleExternalConfigKeys.DESCRIPTIVE_TAXONOMY_TERM_API_KEY);
			DescriptiveTaxonomyTermServiceImpl service = new DescriptiveTaxonomyTermServiceImpl(httpConfigBuilder.build(), apiKey);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.DESCRIPTIVE_TAXONOMY_TERM_CACHE_ENABLED).asBoolean(true).get()) {
				CacheTemplate<DescriptiveTaxonomyTermParsedData> generateDescriptionTaxonomyTermParsedDataCache = getPersistentCache(
						"CachedDescriptiveTaxonomyTermService_generateDescriptionTaxonomyTermParsedData",
						objectMapper().getTypeFactory().constructType(DescriptiveTaxonomyTermParsedData.class),
						1, false, ONE_HOUR_IN_SECONDS * 3);

				return new CachedDescriptiveTaxonomyTermService(service, generateDescriptionTaxonomyTermParsedDataCache);
			}

			return service;
		}
		return null;
	}

	protected Boolean isResoundSupported() {
		return StringUtils.isNotEmpty(propertyFactory.getProperty(MantleExternalConfigKeys.RESOUND_BASE_URL).asString(null).get());
	}

	private HttpServiceClientConfig.Builder resoundClientConfig(String clientName) {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultBaseClientConfig(clientName);
		String baseURL =propertyFactory.getProperty(MantleExternalConfigKeys.RESOUND_BASE_URL).asString(null).get();

		if(!baseURL.contains("/v2")){
			baseURL = baseURL+"/v2";
		}

		httpConfigBuilder.setBaseUrl(baseURL);
		httpConfigBuilder.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON);

		return httpConfigBuilder;
	}

	@Bean
	public ResoundService resoundService() {
		if (isResoundSupported()) {
			HttpServiceClientConfig.Builder httpConfigBuilder = resoundClientConfig("resound");
			String brand = propertyFactory.getProperty(MantleExternalConfigKeys.SHARED_SERVICES_BRAND).asString(null).get();

			if(brand == null){
				throw new GlobeException("brand has not been set with consul config: "+MantleExternalConfigKeys.SHARED_SERVICES_BRAND);
			}
			ResoundService service = new ResoundService(httpConfigBuilder.build(), brand);

			return service;
		}
		return null;
	}

	@Bean
	public CuratedDomainService curatedDomainService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("curatedDomain");
		String mediaType = isUseSmile("curatedDomain") ? ServiceClientUtils.ClientMediaType.SMILE_V1.getMediaType() : ServiceClientUtils.ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);

		CuratedDomainServiceImpl service = new CuratedDomainServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.CURATEDDOMAIN_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<Set<String>> curatedDomainCache = getPersistentCache("CachedCuratedDomainService_curatedDomain",
					objectMapper().getTypeFactory().constructCollectionType(Set.class, String.class),
					1000, false, 600);
			return new CachedCuratedDomainService(service, curatedDomainCache);
		}

		return service;
	}


	@Bean
	public QueryParamSafelist queryParamSafelist() {
		QueryParamSafelist safelist;
		try {
			safelist = new StandardObjectMapperProvider().getContext(null).readValue(
					IOUtils.toString(getClass().getResourceAsStream("/safelist/queryParamSafelist.json")),
					new TypeReference<QueryParamSafelist>() {
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return safelist;
	}


	@Bean
	public CacheTemplate<String> renderCache() {
		return getCache("CachedRenderedTemplate", 100, false, 3600 * 2);
	}

	// Action Tasks
	@Bean
	public MantleRequestHandlerMethods requestHandlerMethods() {

		return new MantleRequestHandlerMethods(environmentConfig(), urlDataFactory(), dependencyRequestManager(), renderUtils(), tasksRegistry(),
				pageNotFoundHandler(), resourceAggregator(), renderCache(),
				printReadyTemplateNameResolveTask(), newsletterActionTasks(), defaultTemplateNameResolveTask(),
				ratingTask(), submitFeedback(), debugActionTasks(), bovdTask(), somethingMagicalService(renderUtils()),
				templateNames(), requestContextSource(), complianceTask(), linkHeaderHandlerMethods(), documentService(),
				ddmAccountAuthService(), buildLegacyUrlMap(), resoundService());
	}

	@Bean
	public MantleLinkHeaderHandlerMethods linkHeaderHandlerMethods(){
		return new MantleLinkHeaderHandlerMethods(resourceTasks());
	}

	@Bean
	public MantleResourceHandlerMethods resourceHandlerMethods() {
		return new MantleResourceHandlerMethods(aggregateKeyGenerator(), projectInfo(), module(), resourceCache());
	}

	@Bean
	public CommonTemplateNames templateNames() {
		return new CommonTemplateNames();
	}

	@Bean
	public NewsletterSignupService newsletterActionTasks() {
		return new NewsletterSignupService(sailthruClient(), newsletterValidationService());
	}

	@Bean
	public PageNotFoundHandler pageNotFoundHandler() {
		return new Mantle404Handler();
	}

	@Bean
	public RedirectHandler redirectHandler() {
		return new MantleRedirectHandler(urlDataFactory(), documentService(), redirectRuleResolver(),
				taxeneConfigurationTask(), buildLegacyUrlMap(), seleneDocUrlsWithoutWww());
	}

	@Bean
	public boolean seleneDocUrlsWithoutWww() {
		return propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_DOC_URLS_WITHOUT_WWW).asBoolean(false).get();
	}

	@Bean
	public ExternalServiceProxyHandler externalServiceProxyHandler(List<ExternalComponentService> externalComponentServices) {
		return new MantleExternalComponentServiceProxyHandler(this.createMapOfExternalServices(externalComponentServices));
	}

	@Bean
	public SailthruClient sailthruClient() {
		final String apiKey = propertyFactory.getProperty(MantleExternalConfigKeys.SAILTHRU_CLIENT_KEY).asString(null).get();
		final String apiSecret = getSecureProperty(MantleExternalConfigKeys.SAILTHRU_CLIENT_SECRET);
		final Integer socketTimeout = propertyFactory.getProperty(MantleExternalConfigKeys.SAILTHRU_CLIENT_SOCKET_TIMEOUT)
				.asInteger(2000).get();
		final Integer connectTimeout = propertyFactory.getProperty(MantleExternalConfigKeys.SAILTHRU_CLIENT_CONNECT_TIMEOUT)
				.asInteger(2000).get();
		SailthruHttpClientConfiguration configuration = new DefaultSailthruHttpClientConfiguration() {

			@Override
			public int getSoTimeout() {
				return socketTimeout;
			}

			@Override
			public int getConnectionTimeout() {
				return connectTimeout;
			}
		};
		return new SailthruClient(apiKey, apiSecret, configuration);
	}

	@Bean
	public DisqusRatingService disqusRatingService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("disqusRating");
		String mediaType = isUseSmile("disqusRating") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		DisqusRatingService service = new DisqusRatingServiceImpl(httpConfigBuilder.build());

		//By default cache will be enabled if Disqus rating feature is enabled; disabled otherwise.
		if (propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_AGGREGATE_RATING_CACHE_ENABLED).asBoolean(isDisqusAggregateRatingEnabled()).get()) {
			CacheTemplate<DisqusAggregateRating> disqusAggregateRatingCache = getPersistentCache(
					"CachedDisqusRatingService_aggregateRating",
					objectMapper().getTypeFactory().constructType(DisqusAggregateRating.class),
					1000, false, ONE_HOUR_IN_SECONDS * 3); //Setting the defaultTtl longer than usual 1hr because Disqus Aggregate Rating data refresh rate is long i.e. 24hrs

			service = new CachedDisqusRatingService(service, disqusAggregateRatingCache);
		}

		return service;
	}

	@Bean
	public UGCFeedbackService ugcFeedbackService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultServicesClientConfig("ugcFeedback", ugcService(), true, SERVICES_UGC_CLIENTID_HEADER, null);
		String mediaType = isUseSmile("ugcFeedback") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);

		UGCFeedbackServiceImpl ugcFeedbackService = new UGCFeedbackServiceImpl(httpConfigBuilder.build(), messageProperties());

		if(propertyFactory.getProperty(MantleExternalConfigKeys.UGC_FEEDBACK_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<AggregatedFeedbacks> ugcAggregatedFeedbacksCache = getPersistentCache("CachedUGCFeedbackService_aggregatedFeedbacks",
					objectMapper().getTypeFactory().constructType(AggregatedFeedbacks.class), 1000,
					false, 300);

			CacheTemplate<SliceableListEx<PhotoGalleryItem>> ugcPhotoGalleryCache = getPersistentCache("CachedUGCFeedbackService_photoGallery",
					objectMapper().getTypeFactory().constructParametricType(SliceableListEx.class, PhotoGalleryItem.class),
					1000, false, 300);

			return new CachedUGCFeedbackService(ugcFeedbackService, ugcAggregatedFeedbacksCache, ugcPhotoGalleryCache);
		}

		return ugcFeedbackService;
	}

	@Bean
	public UGCUserService ugcUserService() {
		HttpServiceClientConfig httpServiceClientConfig = defaultServicesClientConfig("ugcUser", ugcService(), true, SERVICES_UGC_CLIENTID_HEADER, null).build();

		UGCUserServiceImpl ugcUserService = new UGCUserServiceImpl(httpServiceClientConfig);
		if(propertyFactory.getProperty(MantleExternalConfigKeys.UGC_USER_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<UGCUserDto> ugcUserCache = getPersistentCache(
					"CachedUgcUserService_ugcUserById",
					objectMapper().getTypeFactory().constructType(UGCUserDto.class),
					1000, false, 300);

			return new CachedUGCUserService(ugcUserService, ugcUserCache);
		}
		return ugcUserService;
	}

	@Bean
	public UGCFeedbackTask submitFeedback() {
		return new UGCFeedbackTask(ugcFeedbackService());
	}

	@Bean
	public DebugActionTasks debugActionTasks() {
		return new DebugActionTasks(dependencyRequestManager(), applicationContext, safeListParamaterFailedLogger());
	}

	// TaskModel Tasks

	@Bean
	public ResourceTasks resourceTasks() {
		return new ResourceTasks(resourceAggregator(), module());
	}

	@Bean
	public BuildResourcesTask buildResourcesTask() {
		return new BuildResourcesTask(projectInfo());
	}

	@Bean
	public QuizResultTask quizResultTask() {
		return new QuizResultTask();
	}

	@Bean
	public ArticlesTask articlesTask() {
		return new ArticlesTask(articleService(), documentTask(), urlDataFactory());
	}

	@Bean
	public AttributionTask attributionTask() {
		return new AttributionTask(authorService(), attributionService(), documentService(), ugcUserService(), isAttributionEnabled());
	}

	@Bean
	public UGCRatingsTask ratingTask() {
		return new UGCRatingsTask(ugcFeedbackService());
	}

	@Bean
	public AggregateRatingTask aggregateRatingTask() {
		return new AggregateRatingTask(ugcFeedbackService(), disqusRatingService(), isDisqusAggregateRatingEnabled());
	}

	private boolean isDisqusAggregateRatingEnabled() {
		return propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_AGGREGATE_RATING_ENABLED).asBoolean(false).get();
	}

	@Bean
	public AuthorTask authorTask() {
		return new AuthorTask(authorService(), articlesTask());
	}

	@Bean
	public CuratedListTask curatedListTask() {
		return new CuratedListTask(curatedListService());
	}

	@Bean
	public DocumentTaxeneCuratedListTask documentTaxeneCuratedListTask() {
		return new DocumentTaxeneCuratedListTask(curatedListTask(), documentTaxeneService(),threadPoolExecutor());
	}

	@Bean
	public DocumentTask documentTask() {
		return new DocumentTask(documentService(), configurableHtmlSlicer());
	}

	@Bean
	public ProductRecordTask productRecordTask() {
		Vertical seleneVertical = null;
		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("No vertical filter set on ProductRecord task");
		}
		return new ProductRecordTask(deionSearchFullDocumentService(), seleneVertical);
	}

	@Bean
	public DocSchemaTask docSchemaTask() {
		return new DocSchemaTask(docSchemaService());
	}

	@Bean
	public ConfigurableHtmlSlicer configurableHtmlSlicer() {
		return new ConfigurableHtmlSlicer(objectMapper());
	}

	@Bean
	public NavigationLinkTask navigationLinkTask() {
		return new NavigationLinkTask(navigationLinkService());
	}

	@Bean
	public NotificationTask notificationTask() {
		return new NotificationTask(notificationService(),
				getDocIdListFromConfig(MantleExternalConfigKeys.TAXONOMY_DOCIDS_TO_BE_EXCLUDED_FROM_SITEWIDE_NOTIFICATION),
				getDocIdListFromConfig(MantleExternalConfigKeys.DOCIDS_TO_BE_EXCLUDED_FROM_SITEWIDE_NOTIFICATION));
	}

	@Bean
	public SearchTask searchTask() {
		return new SearchTask(searchService());
	}

	@Bean
	public GeoLocationTask geoLocationTask() {
		return new GeoLocationTask();
	}

	@Bean
	public GptTask gptTask() {
		return new GptTask(auctionFloorService());
	}

	@Bean
	public AuctionFloorTask auctionFloorTask() {
		return new AuctionFloorTask(auctionFloorListService(), auctionFloorService());
	}

	@Bean
	@Autowired
	public RtbService rtbService(Optional<List<RtbPartner>> partners) {
		if (!partners.isPresent()) {
			logger.warn("No server-side RTB partners detected");
		}
		return new RtbServiceImpl(threadPoolExecutor(), partners.orElse(Collections.emptyList()));
	}

	@Bean
	@Autowired
	public RtbTask rtbTask(RtbService rtbService) {
		return new RtbTask(rtbService);
	}

	@Bean
	public TaxeneConfigurationTask taxeneConfigurationTask() {
		return new TaxeneConfigurationTask(taxeneRelationService(), urlDataFactory());
	}

	@Bean
	public SuggestionTask suggestionTask() {
		return new SuggestionTask(suggestionService());
	}

	@Bean
	public DocumentTaxeneCompositeTask documentTaxeneCompositeTask() {
		return new DocumentTaxeneCompositeTask(documentTaxeneService());
	}

	@Bean
	public DocumentTaxeneService documentTaxeneService() {
		DocumentTaxeneService documentTaxeneService = new DocumentTaxeneServiceImpl(taxeneRelationService(),threadPoolExecutor());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.DOCUMENT_TAXENE_COMPOSITE_CACHED_ENABLED).asBoolean(false).get()) {

			//Caching for non-curatedlists
			TypeReference<List<DocumentTaxeneComposite<? extends BaseDocumentEx>>> type = new TypeReference<List<DocumentTaxeneComposite<? extends BaseDocumentEx>>>(){};
			CacheTemplate baseDocumentTaxeneCompositeCache = null;
			CacheTemplate dedupDocumentTaxeneCompositeCache = null;

			//Caching for curatedlists
			JavaType inner = objectMapper().getTypeFactory().constructParametricType(List.class, CuratedDocumentTaxeneComposite.class);
			CacheTemplate<List<CuratedDocumentTaxeneComposite>> curatedDocumentTaxeneCompositeCache = getPersistentCache("curatedTaxeneComposite_cache", inner, 1000, false, 3000);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.DOCUMENT_TAXENE_COMPOSITE_CACHED_DEDUP_ENABLED).asBoolean(false).get()) {
				/*
				 * When using the deduplicated cache for DocumentTaxeneService, records are stored individually. For performance reasons, some caches must be
				 * set as redis-only and some as ehcache-only.
				 */

				baseDocumentTaxeneCompositeCache = getPersistentCache("baseDocumentTaxeneComposite_cache", objectMapper().constructType(type.getType()), 1000, false, 3000, false, true, false, true);

				dedupDocumentTaxeneCompositeCache = getCache("baseDocumentTaxeneComposite_cacheDedup", 40000, false, 3000);
			} else {
				baseDocumentTaxeneCompositeCache = getPersistentCache("baseDocumentTaxeneComposite_cache", objectMapper().constructType(type.getType()), 1000, false, 3000);
			}

			return new CachedDocumentTaxeneService(documentTaxeneService, baseDocumentTaxeneCompositeCache, curatedDocumentTaxeneCompositeCache, dedupDocumentTaxeneCompositeCache, threadPoolExecutor());
		}

		return documentTaxeneService;
	}

	@Bean
	public SocialTask socialTask() {
		String seleneVertical = null;
		try {
			seleneVertical = getSeleneVertical().toString();
		} catch (Exception e) {
			logger.warn("No vertical set on SocialTask - Facebook App Id in meta tag will default an old app ID " +
					"that was configured to be used by about.com only to avoid a validation error by Facebook." +
					"See AXIS-3203 for more details.");
		}
		return new SocialTask(socialLinkService(), renderUtils(), verticalConfigService(), seleneVertical);
	}

	@Bean
	public MantleGTMPageViewTask gtmPageViewTask() {
		return new MantleGTMPageViewTask(environmentConfig(), projectInfo(), renderUtils(), taxeneRelationTask(), getArticleTemplates(), journeyTask());
	}

	@Bean
	public TopicPageTransformer topicPageTransformer() {
		return new TopicPageTransformer();
	}

	@Bean
	public BulletedListTransformer bulletedListTransformer() {
		return new BulletedListTransformer();
	}

	@Bean
	public JsonTask jsonTask() {
		return new JsonTask();
	}

	@Bean
	public MockDocumentTask mockDocumentTask() {
		return new MockDocumentTask(objectMapper(), documentPreprocessors);
	}

	@Bean
	public MockArticlesTask mockarticlesTask() {
		return new MockArticlesTask(objectMapper());
	}

	@Bean
	public MockDocumentTaxeneCompositeTask mockDocumentTaxeneCompositeTask() {
		return new MockDocumentTaxeneCompositeTask(objectMapper());
	}

	@Bean
	public MockTaxeneRelationTask mockTaxeneRelationTask() {
		return new MockTaxeneRelationTask(objectMapper());
	}

	@Bean
	public MockJourneyTask mockJourneyTask() {
		return new MockJourneyTask(objectMapper());
	}

	@Bean
	public MockAuthorTask mockAuthorTask() { return new MockAuthorTask(objectMapper()); }

	/* To avoid breaking changes, this autowired property is added temporarily.
	 * This property should be moved into the renderUtils() constructor.
	 */
	@Autowired
	private Optional<List<ElementRewriter>> elementRewriters;

	@Override
	@Bean
	public MantleRenderUtils renderUtils() {
		final String DOMAIN = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("about.com").get();
		boolean useBlurryPlaceholder = propertyFactory.getProperty(MantleExternalConfigKeys.BLURRY_THUMNBNAIL_ENABLED).asBoolean(false).get();
		boolean generateBlurryPlaceholderIfMissingFromSelene = propertyFactory.getProperty(MantleExternalConfigKeys.BLURRY_THUMBNAIL_GENERATE).asBoolean(false).get();

		return new MantleRenderUtils(DOMAIN, imageRenderUtils(), urlDataFactory(), projectInfo(),
				ipDetectorUtils(), configurableHtmlSlicer(), vendorLookupService(), propertyFactory, environmentConfig(),
				curatedDomainService(), useBlurryPlaceholder, generateBlurryPlaceholderIfMissingFromSelene,
				elementRewriters.orElse(null));
	}

	@Override
	@Bean
	public MantleImageRenderUtils imageRenderUtils() {
		final String IMAGE_SERVER_HOST = propertyFactory.getProperty(GlobeExternalConfigKeys.IMAGE_SERVER_HOST)
				.asString("f.tqn.com").get();
		final Double IMAGE_SIZE_DIFFERENTIAL_THRESHOLD = propertyFactory
				.getProperty(GlobeExternalConfigKeys.IMAGE_RESIZE_DIFFERENTIAL_THRESHOLD).asDouble(0.7).get();
		return new ThumborImageRenderUtils(IMAGE_SERVER_HOST, IMAGE_SIZE_DIFFERENTIAL_THRESHOLD.doubleValue(),
				imageResizer());
	}

	@Bean
	public ExternalConfigurationTask externalConfigurationTask() {
		ExternalConfigurationTask ciaTask = new ExternalConfigurationTask(propertyFactory);

		return ciaTask;
	}

	@Bean
	public DeionSearchServiceTask deionSearchServiceTask() {
		String seleneVertical = null;
		try {
			seleneVertical = getSeleneVertical().toString();
		} catch (Exception e) {
			logger.warn("deionSearchServiceTask.articlesByAuthorId is unavailable. Set selene vertical in consul: " + MantleExternalConfigKeys.SELENE_VERTICAL);
		}
		return new DeionSearchServiceTask(deionSearchService(), seleneVertical, documentTaxeneCompositeTask(),
				attributionTask());
	}

	@Bean
	public NewsTask newsTask() {

		String seleneVertical = null;
		NewsTask newsTask = null;
		Long newsTaxonomyDocId = propertyFactory.getProperty(MantleExternalConfigKeys.NEWS_TAXONOMY_DOCID).asLong(null).get();
		try {
			seleneVertical = getSeleneVertical().toString();
		} catch (Exception e) {
			logger.warn("NewsTask is disabled until the selene vertical is set in consul: ",
					MantleExternalConfigKeys.SELENE_VERTICAL);
		}
		newsTask = new NewsTask(deionSearchService(), seleneVertical, newsTaxonomyDocId);
		return newsTask;
	}

	@Bean
	public IconsTask iconsTask() {
		return new IconsTask();
	}

	@Bean
	public JourneyTask journeyTask() {
		return new JourneyTask(journeyService());
	}

	@Bean
	public TaxeneRelationService taxeneRelationService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("taxeneRelation");
		httpConfigBuilder.setMediaType(VersionedMediaTypes.V2_JSON_WITH_REDUCED_WEIGHT);
		TaxeneRelationServiceImpl serviceImpl = new TaxeneRelationServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.TAXENE_RELATION_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<TaxeneNodeEx> taxeneCache = getPersistentCache("CachedTaxeneService_relation",
					objectMapper().constructType(TaxeneNodeEx.class), 1000, false, 3600);
			return new CachedTaxeneRelationService(serviceImpl, taxeneCache);
		}
		return serviceImpl;
	}

	@Bean
	public JourneyService journeyService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("taxeneJourney");
		httpConfigBuilder.setMediaType(VersionedMediaTypes.V2_JSON_WITH_REDUCED_WEIGHT);
		JourneyServiceImpl serviceImpl = new JourneyServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.TAXENE_JOURNEY_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<TaxeneNodeEx> taxeneCache = getPersistentCache("CachedTaxeneService_journey",
					objectMapper().constructType(TaxeneNodeEx.class),
					1000, false, 3600);
			CacheTemplate<JourneyRoot> journeyRootCache = getPersistentCache("CachedTaxeneService_journeyStructure",
					objectMapper().constructType(JourneyRoot.class),
					1000, false,
					3600);

			CacheTemplate<CachedJourneyService.JourneyRootIdAndRelationshipType> rootIdCache = getPersistentCache("CachedTaxeneService_journeyRootId",
					objectMapper().getTypeFactory().constructType(CachedJourneyService.JourneyRootIdAndRelationshipType.class),
					1000000, false,
					3600);
			return new CachedJourneyService(serviceImpl, journeyRootCache, rootIdCache);
		}
		return serviceImpl;
	}

	@Bean
	public TaxeneRelationTask taxeneRelationTask() {
		return new TaxeneRelationTask(taxeneRelationService(), getTaxonomyDocIdsToSortWithDeionSearchConfig(), deionSearchServiceTask());
	}

	protected List<Long> getDocIdListFromConfig(String configKey) {
		String docIds = propertyFactory.getProperty(configKey).asString(null).get();
		if (docIds == null) return null;
		List<Long> answer = null;
		try {
			answer = Arrays.stream(StringUtils.split(docIds, ",")).map(docId -> Long.parseLong(docId)).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			logger.warn("{} config is set incorrectly. These need to be docIds separated by a comma like: 4781567,4781519", configKey);
		}
		return answer;
	}

	/**
	 * This method is deprecated in favor of using more generic method
	 * {@link #getDocIdListFromConfig(String)} directly
	 */
	@Deprecated
	protected List<Long> getTaxonomyDocIdsToSortWithDeionSearchConfig() {
		return getDocIdListFromConfig(MantleExternalConfigKeys.TAXONOMY_DOCIDS_WHERE_CONTENT_TO_BE_SORTED_WITH_DEION_SEARCH);
	}

	@Bean
	public JourneyTaxeneRelationTask journeyTaxeneRelationTask() {
		return new JourneyTaxeneRelationTask(taxeneRelationTask());
	}

	@Bean
	public SitemapTask sitemapTask() {
		SitemapTask sitemapTask = new SitemapTask(sitemapDeionSearchService());
		scheduleSitemapRefresh(sitemapTask);
		return sitemapTask;
	}

	@Bean
	public ImageResizer imageResizer() {
		final String thumborKey = getSecureProperty(MantleExternalConfigKeys.THUMBOR_KEY);
		final String watermarkFilter = propertyFactory.getProperty(MantleExternalConfigKeys.WATERMARK_FILTER).asString(null).get();

        GenericObjectPool<Mac> pool = buildPollexorHashPool(thumborKey);
		return new PollexorImageResizer(MantleSpringConfiguration.THUMBOR_PROXY_PREFIX + "/", thumborKey, pool, watermarkFilter);
	}

	@Bean
	public ImageTask imageTask() {
		return new ImageTask(imageResizer(), renderUtils());
	}

	@Bean
	public PatternLibraryTask patternLibraryTask()
			throws UnsupportedAlgorithmException, ClientProtocolException, URISyntaxException, IOException {
		return new PatternLibraryTask(dependencyRequestManager(), module(), expressionEvaluator(), renderUtils());
	}

	@Bean
	public ComponentTask componentTask() {
		return new ComponentTask();
	}

	@Bean
	public PrintReadyTemplateNameResolveTask printReadyTemplateNameResolveTask() {
		return new PrintReadyTemplateNameResolveTask(defaultTemplateNameResolveTask());
	}

	@Override
	@Bean
	public GlobeTestFramework globeTestFramework() {
		return new ProctorGlobeTestFramework(proctorSupplier(), globalProctorSupplier(), identifiersExtractor(),
				defaultTemplateNameResolveTask(), taxeneConfigurationTask(), documentTask());
	}

	@Bean
	public IdentifiersExtractor identifiersExtractor() {
		return new DefaultIdentifiersExtractor();
	}

	@Bean
	public ProctorSupplier proctorSupplier() {

		ImmutableTriple<String, String, String> credentials = getRepositoryCredentials(
				MantleExternalConfigKeys.PROCTOR_GIT_PATH, MantleExternalConfigKeys.PROCTOR_GIT_USERNAME,
				MantleExternalConfigKeys.PROCTOR_GIT_PASSWORD);

		final boolean isTestingEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.TESTING_ENABLED)
				.asBoolean(true).get();

		final String gitBranch = propertyFactory.getProperty(MantleExternalConfigKeys.PROCTOR_GIT_BRANCH).asString("trunk").get();

		MantleProctorSupplier supplier = getProctorSupplier(credentials, gitBranch, "verticalProctorSupplier", isTestingEnabled);

		scheduleProctorSupplierRefresh(supplier);

		return supplier;
	}

	@Bean
	public ProctorSupplier globalProctorSupplier() {

		ImmutableTriple<String, String, String> credentials = getRepositoryCredentials(
				MantleExternalConfigKeys.GLOBAL_PROCTOR_GIT_PATH, MantleExternalConfigKeys.GLOBAL_PROCTOR_GIT_USERNAME,
				MantleExternalConfigKeys.GLOBAL_PROCTOR_GIT_PASSWORD);

		final boolean isGlobalTestingEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.GLOBAL_TESTING_ENABLED)
				.asBoolean(true).get();

		//use vertical proctor's branch as default branch for global if not provided
		final String defaultGitBranch = propertyFactory.getProperty(MantleExternalConfigKeys.PROCTOR_GIT_BRANCH).asString("trunk").get();
		final String gitBranch = propertyFactory.getProperty(MantleExternalConfigKeys.GLOBAL_PROCTOR_GIT_BRANCH).asString(defaultGitBranch).get();

		if(!gitBranch.equalsIgnoreCase(defaultGitBranch)) {
			throw new GlobeException("Global proctor branch doesn't match with this vertical's proctor branch");
		}
		MantleProctorSupplier supplier = getProctorSupplier(credentials, gitBranch, "globalProctorSupplier", isGlobalTestingEnabled);

		scheduleProctorSupplierRefresh(supplier);

		return supplier;
	}

	private void scheduleProctorSupplierRefresh(MantleProctorSupplier supplier) {
		supplier.refresh();
		scheduleProctorPolling(supplier);
	}

	private MantleProctorSupplier getProctorSupplier(ImmutableTriple<String, String, String> credentials, String branchName, String name, boolean isTestingEnabled) {
		MantleProctorSupplier supplier = MantleProctorSupplier.getEmptyProctorSupplierWithName(name);

		if (isTestingEnabled && credentials != null) {
			supplier = MantleProctorSupplier.fromGit(credentials.left, branchName, credentials.middle, credentials.right, name);
		} else {
			logger.warn("A/B testing via [{}] is disabled.", name);
		}

		return supplier;
	}

	@Bean
	public BusinessOwnedVerticalDataServiceImpl bovdService() {
		/* Service is not currently wrapped in caching layer to focus current sprint on service implementation.
		 * Following repo cloning on server startup, resources will be pulled from local drive and testing shows
		 * minimal overhead. Ads.txt vended in ~3ms locally so caching deemed not necessary for now.
		 */
		BusinessOwnedVerticalDataServiceImpl service = null;
		ImmutableTriple<String, String, String> credentials = null;
		if (service == null &&
				(credentials = getRepositoryCredentials(MantleExternalConfigKeys.RESOURCES_GIT_PATH, MantleExternalConfigKeys.RESOURCES_GIT_USERNAME, MantleExternalConfigKeys.RESOURCES_GIT_PASSWORD)) != null) {
			final String gitBranch = propertyFactory.getProperty(MantleExternalConfigKeys.RESOURCES_GIT_BRANCH).asString("master").get();
			final String gitRoot = propertyFactory.getProperty(MantleExternalConfigKeys.RESOURCES_GIT_ROOT).asString(null).get();
			final Boolean bovdEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.BOVD_ENABLED).asBoolean(true).get();
			service = new BusinessOwnedVerticalDataServiceImpl(credentials.left, gitBranch, gitRoot, credentials.middle, credentials.right, bovdEnabled);

		}

		if (service == null) {
			throw new GlobeException("Failed to create BusinessOwnedVerticalData service.");
		}

		service.refresh();
		scheduleBovdRefresh(service);
		return service;
	}

	@Override
	@Bean
	public PlatformUrlDataFactory urlDataFactory() {
		PlatformUrlDataFactory answer = null;

		String homeDocId = propertyFactory.getProperty(MantleExternalConfigKeys.HOMEPAGE_DOC_ID).asString(null).get();
		if (StringUtils.isBlank(homeDocId)) {
			throw new GlobeException("Home page docId is not found or empty.");
		}
		String appName = environmentConfig().getApplication();

		if (StringUtils.isBlank(appName)) {
			throw new GlobeException("App name is not found or empty.");
		}

		answer = getUrlDataFactory(homeDocId, appName);
		return answer;
	}


	@Bean
	public InputStream redirectRuleDataSource() {
		return null;
	}

	@Bean
	public RedirectRuleResolver redirectRuleResolver() {
		return new RedirectRuleResolverImpl(redirectRuleDataSource(), urlDataFactory());
	}

	@Bean
	public RSS2Transformer RSS2Transformer() {
		return new RSS2Transformer(imageRenderUtils());
	}

	@Bean
	public InputStream mailboxBlacklistDataSource() {
		return MantleSpringConfiguration.class.getClassLoader()
				.getResourceAsStream("newsletterValidation/mailboxBlocklist.txt");
	}

	@Bean
	public NewsletterValidationService newsletterValidationService() {
		return new NewsletterValidationServiceImpl(mailboxBlacklistDataSource());
	}

	/**
	 * Get credentials needed to connect to a repository store
	 * @param repositoryKey     the value associated with this key is expected to be unencrypted
	 * @param usernameKey       the value associated with this key is expected to be encrypted
	 * @param passwordKey       the value associated with this key is expected to be encrypted
	 * @return Triple (repositoryValue, usernameValue, passwordValue); otherwise null if any value in the triple would be null
	 */
	private ImmutableTriple<String, String, String> getRepositoryCredentials(String repositoryKey, String usernameKey, String passwordKey) {
		final String repositoryValue = propertyFactory.getProperty(repositoryKey).asString(null).get();
		final String usernameValue = getSecureProperty(usernameKey, false);
		final String passwordValue = getSecureProperty(passwordKey, false);
		return (repositoryValue == null || usernameValue == null || passwordValue == null) ? null :
			ImmutableTriple.of(repositoryValue, usernameValue, passwordValue);
	}

	private void scheduleProctorPolling(MantleProctorSupplier supplier) {
		schedulePolling(proctorTimer, supplier::refresh, 5);
	}

	private void scheduleBovdRefresh(BusinessOwnedVerticalDataServiceImpl service) {
		schedulePolling(bovdGitTimer, service::refresh, 5);
	}

	private void scheduleSitemapRefresh(SitemapTask sitemapTask) {
		final String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("about.com").get();
		final int ttlSeconds = propertyFactory.getProperty(GlobeExternalConfigKeys.getCacheTimeToLiveSeconds(CACHE_SITEMAP_DEION_SEARCH)).asInteger(TEN_MIN_IN_SECONDS).get();
		final int actualTtlSeconds = ttlSeconds / 2; // see actualTtl in getPersistentCache
		final int refreshMinutes = Math.max(2, actualTtlSeconds / 60); // anything shorter than 2 minutes feels excessive
		schedulePolling(sitemapTimer, () -> sitemapTask.preloadSitemap(domain), refreshMinutes);
	}

	private void scheduleLegacyMapRepopulate(LegacyUrlMap legacyUrlMap) {
		int timeInSeconds = propertyFactory.getProperty(GlobeExternalConfigKeys.getCacheTimeToLiveSeconds(CACHE_LEGACY_DEION_SEARCH)).asInteger(TEN_MIN_IN_SECONDS).get();

		boolean mutexEnabled = propertyFactory.getProperty(GlobeExternalConfigKeys.REDIS_SERVER_TRANSACTIONS_ENABLED).asBoolean(false).get();

		if(mutexEnabled){
			final int actualTtlSeconds = timeInSeconds / 2; // see actualTtl in getPersistentCache
			final int refreshMinutes = Math.max(2, actualTtlSeconds / 60); // anything shorter than 2 minutes feels excessive
			schedulePolling(legacyUrlMapTimer, legacyUrlMap::repopulateMap, refreshMinutes);
		}else {
			scheduleSingleCall(legacyUrlMapTimer, legacyUrlMap::repopulateMap, timeInSeconds / 60);
		}
	}

	private void schedulePolling(Timer timer, Runnable task, int minutes) {
		long currentTime = System.currentTimeMillis();
		long interval = TimeUnit.MINUTES.toMillis(minutes);

		long delay = interval - (currentTime % interval);

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, delay, interval);
	}

	private void scheduleSingleCall(Timer timer, Runnable task, int minutes) {
		long interval = TimeUnit.MINUTES.toMillis(minutes);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, interval);
	}

	protected Set<TemplateTypeEx> getArticleTemplates() {
		return Sets.newHashSet();
	}

	@Bean
	public MantleIpDetectorUtils ipDetectorUtils() {
		String dotdashCidrs = propertyFactory.getProperty(MantleExternalConfigKeys.DOTDASH_CIDRS)
				.asString("64.125.212.0/23, 216.223.12.0/23, 209.66.78.0/24, 207.241.144.0/21, 127.0.0.0/8, 10.0.0.0/8")
				.get();
		String[] dotdashCidrArr = StringUtils.split(dotdashCidrs, ", ");
		MantleIpDetectorUtils detector = new MantleIpDetectorUtils(dotdashCidrArr);
		return detector;
	}

	/*
	 * Supporting both the new and old version of the amazonProductTask is not easily done via configuration as their interfaces are different
	 * and since we need the task names to be the same. I am removing the old one as there are only a couple of days left before the old api is
	 * obsolete. If any issues arise, note that setting the paapi v5 config to false will not turn off this portion of functionality. However, this
	 * piece is only used on legacy list templates. Listsc will work as expected.
	 *
	 * All unnecessary non-v5 amazon code to be removed as a part of CMRC-764
	 */
	@Bean
	@Autowired
	public AmazonProductTask amazonProductTask(com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade amazonProductAdvertisingApiV5) {
		return new AmazonProductTask(amazonProductAdvertisingApiV5);
	}

	@Bean
	public com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade amazonProductAdvertisingApiFacade() {

		com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade answer;

		String awsAccessKeyId = propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_ACCESS_KEY).asString(null).get();
		String awsSecretKey = getSecureProperty(MantleExternalConfigKeys.AWS_ECOMM_SECRET_KEY);
		String associateTag = propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_ASSOCIATE_TAG).asString(null).get();
		// enables HTTP content logging to stdout. See https://stackoverflow.com/a/16338394/295797 for details.
		// Use during development only
		boolean isTraceEnabled = false;

		com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiImpl svc = new com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiImpl(awsAccessKeyId, awsSecretKey,
				associateTag, isTraceEnabled);

		svc.initialize();

		com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiFacadeImpl impl = new com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiFacadeImpl(svc);

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_CACHE_ENABLED).asBoolean(true).get()) {

			CacheTemplate<Collection<AmazonCommerceModel>> amazonServiceCache = getPersistentCache(
					"AmazonProductAdvertisingApi_ItemLookup",
					objectMapper().getTypeFactory().constructCollectionType(List.class, AmazonCommerceModel.class),
					100, false, 3600);

			answer = new com.about.mantle.amazon.productAdvertisingApi.services.impl.CachedAmazonProductAdvertisingApiFacade(impl, amazonServiceCache);

		} else {
			answer = impl;
		}

		return answer;

	}

	@Bean
	public com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade amazonProductAdvertisingApiFacadeV5() {

		com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade answer;

		String awsAccessKeyId = propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_ACCESS_KEY).asString(null).get();
		String awsSecretKey = getSecureProperty(MantleExternalConfigKeys.AWS_ECOMM_SECRET_KEY);
		String associateTag = propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_ASSOCIATE_TAG).asString(null).get();
		String endPoint = propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_ENDPOINT).asString(null).get();

		com.about.mantle.amazon.productAdvertisingApi.v5.services.impl.AmazonProductAdvertisingApiImpl svc = new com.about.mantle.amazon.productAdvertisingApi.v5.services.impl.AmazonProductAdvertisingApiImpl(awsAccessKeyId, awsSecretKey,
				associateTag, endPoint);

		com.about.mantle.amazon.productAdvertisingApi.v5.services.impl.AmazonProductAdvertisingApiFacadeImpl impl = new com.about.mantle.amazon.productAdvertisingApi.v5.services.impl.AmazonProductAdvertisingApiFacadeImpl(svc);

		if (propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_CACHE_ENABLED).asBoolean(true).get()) {

			CacheTemplate<Collection<AmazonCommerceModelV5>> amazonServiceCache = getPersistentCache(
					"AmazonProductAdvertisingApiV5_ItemLookup",
					objectMapper().getTypeFactory().constructCollectionType(List.class, AmazonCommerceModelV5.class),
					100, false, 3600);

			answer = new com.about.mantle.amazon.productAdvertisingApi.v5.services.impl.CachedAmazonProductAdvertisingApiFacade(impl, amazonServiceCache);

		} else {
			answer = impl;
		}

		return answer;

	}

	@Bean
	public SkimlinksPricingApiFacade skimlinksPricingApiFacade() {

		SkimlinksPricingApiFacade answer;

		String skimlinksApiKey = propertyFactory.getProperty(MantleExternalConfigKeys.SKIMLINKS_ECOMM_API_KEY).asString(null).get();
		String skimlinksId = propertyFactory.getProperty(MantleExternalConfigKeys.SKIMLINKS_ECOMM_ID).asString(null).get();
		String skimlinksBaseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.SKIMLINKS_ECOMM_BASE_URL).asString(null).get();

		if(skimlinksApiKey == null || skimlinksId == null || skimlinksBaseUrl == null) {
			logger.info("Not creating instance of Skimlinks Pricing API because property `{}`, `{}`, or `{}` is not set.  Skimlinks items " +
					"on your page will not have sale prices ", MantleExternalConfigKeys.SKIMLINKS_ECOMM_API_KEY, MantleExternalConfigKeys.SKIMLINKS_ECOMM_ID, MantleExternalConfigKeys.SKIMLINKS_ECOMM_BASE_URL);
	        answer = null;
		} else {
			SkimlinksPricingApiImpl svc = new SkimlinksPricingApiImpl(skimlinksBaseUrl, skimlinksApiKey, skimlinksId);

			SkimlinksPricingApiFacadeImpl facade = new SkimlinksPricingApiFacadeImpl(svc);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.SKIMLINKS_ECOMM_CACHE_ENABLED).asBoolean(true).get()) {

				CacheTemplate<Map<String, SkimlinksCommerceModel>> skimlinksServiceCache = getPersistentCache(
						"SkimlinksPricingApi_ItemLookup",
						objectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, SkimlinksCommerceModel.class),
						100, false, 3600);

				answer = new CachedSkimlinksPricingApiFacade(facade, skimlinksServiceCache);

			} else {
				answer = facade;
			}
		}

		return answer;
	}


	/**
	 * Get a _required_ secure property.
	 * See {@link #getSecureProperty(String, boolean)}
	 */
	protected String getSecureProperty(String propertyName) {
		return getSecureProperty(propertyName, true);
	}

	/**
	 * Get the decrypted value of a secure property.
	 *
	 * Throws `IllegalStateException` when failing to decrypt the value.
	 * Throws `GlobeException` when the property does not exist but it is required.
	 *
	 * @param propertyName name of a secure property having an encrypted value
	 * @param isRequired determins whether absence of the property will raise an exception or return `null`
	 * @return decrypted value of the property on success; `null` if the property does not exist and it's not required
	 */
	protected String getSecureProperty(String propertyName, boolean isRequired) {
		String answer;

		try {
			answer = ((HippodromePropertyFactory) propertyFactory).getEncryptedProperty(propertyName, null)
					.asString(null).get();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load key '" + propertyName + "'", e);
		}

		if (answer == null) {
			if (isRequired) {
				throw new GlobeException("missing required secure property [" + propertyName + "]");
			} else {
				logger.warn("missing non-required secure property [{}]", propertyName);
			}
		}

		return answer;
	}

	@Bean
	public CommerceMappingService commerceMappingService() {
		Map<String, CommerceApi> mapOfTypeToCommerceApi = new HashMap<String, CommerceApi>();

		if(propertyFactory.getProperty(MantleExternalConfigKeys.CAES_ENABLED).asBoolean(false).get()) {
			mapOfTypeToCommerceApi.put("skimlinks", genericCommerceApi());
		} else {
			mapOfTypeToCommerceApi.put("skimlinks", skimlinksCommerceApi());
		}

		mapOfTypeToCommerceApi.put("other", genericCommerceApi());
		mapOfTypeToCommerceApi.put("none", genericCommerceApi());
		if(propertyFactory.getProperty(MantleExternalConfigKeys.AWS_ECOMM_PAAPI_VERSION_5_ENABLED).asBoolean(false).get()) {
	 		mapOfTypeToCommerceApi.put("amazon", amazonCommerceApiV5());
		} else {
			mapOfTypeToCommerceApi.put("amazon", amazonCommerceApi());
		}
		mapOfTypeToCommerceApi.put("walmart", walmartCommerceApi());
		return new CommerceMappingServiceImpl(mapOfTypeToCommerceApi);
	}

	@Bean
	public VendorLookupService vendorLookupService() {
		return new VendorLookupService(urlDataFactory());
	}

	@Bean
	public GenericCommerceApi genericCommerceApi() {
		return new GenericCommerceApi(vendorLookupService());
	}

	@Bean
	public SkimlinksCommerceApi skimlinksCommerceApi() {
		return new SkimlinksCommerceApi(skimlinksPricingApiFacade(), vendorLookupService());
	}

	@Bean
	public AmazonCommerceApi amazonCommerceApi() {
		return new AmazonCommerceApi(amazonProductAdvertisingApiFacade());
    }
	@Bean
	public AmazonCommerceApiV5 amazonCommerceApiV5() {
		return new AmazonCommerceApiV5(amazonProductAdvertisingApiFacadeV5());
    }

    @Bean
    public WalmartProductLookupApiServiceFacade walmartProductLookupApiService() {

		WalmartProductLookupApiServiceFacade answer;

        String apiKey =
                propertyFactory.getProperty(MantleExternalConfigKeys.WALMART_ECOMM_API_KEY).asString(null).get();
        String linksharePublisherId =
                propertyFactory.getProperty(MantleExternalConfigKeys.WALMART_ECOMM_LINKSHARE_PUB_ID).asString(null).get();

        if (apiKey == null) {
            logger.info("Not creating instance of Walmart API because property `{}` not set.  Walmart items " +
				"on your page will fail", MantleExternalConfigKeys.WALMART_ECOMM_API_KEY);
        	answer = null;
		} else {
			JerseyClient restClient = new JerseyClientBuilder().register(JacksonJsonProvider.class).build();

			DefaultProductLookupApiService coreSvc =
					new DefaultProductLookupApiService(restClient, apiKey, linksharePublisherId);

			WalmartProductLookupApiServiceFacade facade = new WalmartProductLookupApiServiceFacadeImpl(coreSvc);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.WALMART_ECOMM_CACHE_ENABLED).asBoolean(true).get()) {

				CacheTemplate<Map<Integer, WalmartCommerceModel>> walmartCache = getPersistentCache(
						"WalmartProductLookupApiService",
						objectMapper().getTypeFactory().constructMapType(
								HashMap.class, Integer.class, WalmartCommerceModel.class),
						100,
						false,
						60 * 60);

				answer = new CachedWalmartProductLookupApiServiceFacade(facade, walmartCache);

			} else {
				answer = facade;
			}

		}

		return answer;
    }

    @Bean
    @Autowired
    public WalmartCommerceApi walmartCommerceApi() {
        return new WalmartCommerceApi(walmartProductLookupApiService());
	}

	@Bean
	public CommerceService commerceService() {
		return new CommerceServiceImpl(commerceMappingService(),threadPoolExecutor());
	}

	@Bean
	public CommerceTask commerceTask() {
		return new CommerceTask(commerceService(), documentTask(), urlDataFactory());
	}

	@Bean
	public TemporaryComponentsActionTasks timedBannerActionTasks() {
		return new TemporaryComponentsActionTasks();
	}

	@Bean
	public StructuredContentDocumentProcessor.StructuredContentAdInsertionStrategy noAdsStructuredContentAdInsertionStrategy() {
		return new StructuredContentDocumentProcessor.NoAdsStructuredContentAdInsertionStrategy();
	}

	@Bean
	public StructuredContentDocumentProcessor.StructuredContentAdInsertionStrategy defaultStructuredContentAdInsertionStrategy() {
		return new StructuredContentDocumentProcessor.DefaultStructuredContentAdInsertionStrategy();
	}

	@Bean
	@Autowired
	public StructuredContentDocumentProcessor structuredContentDocumentProcessor(
			 HtmlSlicer cbFormatter,
			 Map<String, StructuredContentAdSlotStrategy> adSlotStrategyMap,
			 Map<String, StructuredContentAdInsertionStrategy> adInsertionStrategyMap) {
		// Remove parameter in GLBE-7092
		boolean subheadingPreprocessorEnabled = propertyFactory.getProperty(
				MantleExternalConfigKeys.SUBHEADING_PREPROCESSOR_ENABLED).asBoolean(false).get();
		return new StructuredContentDocumentProcessor(cbFormatter, subheadingPreprocessorEnabled, adSlotStrategyMap,
				adInsertionStrategyMap, journeyTask());
	}

	@Bean
	public TableOfContentsTask tableOfContentsTask() {
		String tocFaqDefaultHeadingFromConfig = propertyFactory.getProperty(MantleExternalConfigKeys.TOC_FAQ_DEFAULT_HEADING).asString(null).get();
		return new TableOfContentsTask(tocFaqDefaultHeadingFromConfig);
	}

	@Bean
	public MdcPopulator mdcPopulator() {
		return new MantleMdcPopulator();
	}

	@Bean
	@Autowired
	public SomethingMagicalService somethingMagicalService(MantleRenderUtils renderUtils) {
		return new SomethingMagicalService(renderUtils);
	}

    @Bean
    public LocationService locationService() {
        CacheTemplate<GooglePlaceModel> googlePlaceModelCache = getPersistentCache(
                "CachedLocationService_googlePlaceModel",
                objectMapper().getTypeFactory().constructType(GooglePlaceModel.class),
                1000, false, 60 * 60 * 24 * 30); // cache GooglePlaceModel for 30 days
        String apiKey = getSecureProperty(MantleExternalConfigKeys.GOOGLE_MAPS_API_KEY, false);
        LocationService service = apiKey == null ? new MockLocationServiceImpl() : new LocationServiceImpl(apiKey);

        return new CachedLocationService(service, googlePlaceModelCache);
    }

	@Bean
	public LocationTask locationTask() {
		return new LocationTask(locationService());
	}

	@Bean
	public AmazonRssTask amazonRssTask() {
		final String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("").get();
		final int intervalModeStart = propertyFactory.getProperty(MantleExternalConfigKeys.AMAZON_RSS_INTERVAL_MODE_START).asInteger(0).get();
		final int intervalModeSpan = propertyFactory.getProperty(MantleExternalConfigKeys.AMAZON_RSS_INTERVAL_MODE_SPAN).asInteger(24).get();
		final boolean useAttributions = propertyFactory.getProperty(MantleExternalConfigKeys.ATTRIBUTION_ENABLED).asBoolean(false).get();
		return new AmazonRssTask(amazonRssService(), authorTask(), domain, intervalModeStart, intervalModeSpan, useAttributions ? attributionTask() : null, renderUtils());
	}

	@Bean
	public GoogleNewsSitemapTask GoogleNewsSitemapTask() {

		String siteName = propertyFactory
				.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME)
				.asString("").get();

		String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("").get();
		if (StringUtils.isBlank(siteName)) {
			siteName = domain;
		}

		boolean isGoogleNewsSitemapEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.GOOGLE_NEWS_SITEMAP_ENABLED).asBoolean(false).get();
		return new GoogleNewsSitemapTask(googleNewsSitemapService(), siteName, domain,  isGoogleNewsSitemapEnabled);
	}

	protected Map<String, ExternalComponentService> createMapOfExternalServices(List<ExternalComponentService> externalServices) {

		Map<String, ExternalComponentService> answer = Collections.unmodifiableMap(
				externalServices
						.stream()
						.collect(Collectors.toMap(ExternalComponentService::getName, Function.identity())));

		return answer;
	}

	@Bean
	public ExternalComponentTask externalComponentTask(List<ExternalComponentService> externalComponentServices) {
		return new ExternalComponentTask(createMapOfExternalServices(externalComponentServices), threadPoolExecutor());
	}

	@Bean
	public JsonLdSchemaTask jsonLdSchemaTask() {
		return new JsonLdSchemaTask(documentTask(), attributionTask(), isAttributionEnabled());
	}

	private boolean isAttributionEnabled() {
		return propertyFactory.getProperty(MantleExternalConfigKeys.ATTRIBUTION_ENABLED).asBoolean(false).get();
	}

	@Bean
	public SailthruTagsTask sailthruTagsTask() {
		final String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("").get();
		return new SailthruTagsTask(domain);
	}

	@Bean
	public ParselyTagsTask parselyTagsTask() {
		return new ParselyTagsTask();
	}

	/**
	 * Necessary for JMXMP support
	 * https://iacpublishing.atlassian.net/wiki/spaces/TECH/pages/183337149/How+to+Connect+to+JMX+over+JMXMP
	 * @return {@link org.springframework.jmx.support.ConnectorServerFactoryBean}
	 */
	@Bean
	public ConnectorServerFactoryBean connectorServerFactoryBean() {
		String[] serviceUrlParts = StringUtils.split(ConnectorServerFactoryBean.DEFAULT_SERVICE_URL, ":");
		int defaultPort = Integer.parseInt(serviceUrlParts[serviceUrlParts.length - 1]);
		int effectivePort = defaultPort + Integer.parseInt(System.getProperty("port.offset", "0"));
		serviceUrlParts[serviceUrlParts.length - 1] = Integer.toString(effectivePort);
		String effectiveServiceUrl = StringUtils.join(serviceUrlParts, ":");
		ConnectorServerFactoryBean answer = new ConnectorServerFactoryBean();
		answer.setServiceUrl(effectiveServiceUrl);
		return answer;
	}

    @Bean
	public CacheTemplate<String> externalResourceCache() {
    	return getPersistentCache(
				"CachedExternalComponentResource",
				objectMapper().getTypeFactory().constructType(String.class),
				1000, false, 3600 * 24);
	}

	@Override
	@Bean
	public ThreadPoolExecutor threadPoolExecutor() {
		Integer maximumPoolSize = propertyFactory.getProperty(GlobeExternalConfigKeys.TASK_EXECUTOR_POOL_MAX_SIZE).asInteger(null)
				.get();
		Integer corePoolSize = propertyFactory.getProperty(GlobeExternalConfigKeys.TASK_EXECUTOR_POOL_SIZE).asInteger(null).get();
		return new MantleThreadPoolExecutor(corePoolSize, maximumPoolSize, 60, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>(), GlobeThreadFactory.getNewInstance("MantleThreadPool"));
	}

	@Override
	protected <T> CacheTemplate<T> getPersistentCache(String name, JavaType persistType, int defaultLocalMaxElements,
			boolean localEternal, long defaultTtl, boolean isInMemoryWrapperEnabled, boolean isRedisServerEnabled,
		  	boolean isInMemoryCacheEnabled, boolean isCacheEnabled) {

		CacheTemplate<T> persistentCache = super.getPersistentCache(name, persistType, defaultLocalMaxElements,
				localEternal, defaultTtl, isInMemoryWrapperEnabled, isRedisServerEnabled, isInMemoryCacheEnabled,
				isCacheEnabled);
		return new CacheClearanceTemplateModifier<T>(persistentCache, name);
	}

	@Bean
	@Autowired
	public CacheClearanceCandidateRepo getCacheClearingCandidateRepository(List<CacheClearanceEventHandler> cacheClearanceEventHandlers) {
		boolean isHeaderEnabled = Boolean.TRUE.equals(propertyFactory
				.getProperty(MantleExternalConfigKeys.CACHE_CLEARANCE_HEADER_ENABLED).asBoolean(Boolean.FALSE).get());
		final int defaultSecondaryDuration = 60; // 60 minutes is the average document cache TTL
		int secondaryDuration = propertyFactory.getProperty(MantleExternalConfigKeys.CACHE_CLEARANCE_SECONDARY_DURATION)
				.asInteger(defaultSecondaryDuration).get();
		long replicationDelay = propertyFactory.getProperty(MantleExternalConfigKeys.CACHE_CLEARANCE_REPLICATION_DELAY)
				// 15 seconds as a default: https://dotdash.atlassian.net/browse/GLBE-9581?focusedCommentId=939452
				.asLong(15000L).get();

		Vertical seleneVertical = null;
		try {
			seleneVertical = getSeleneVertical();
		} catch (Exception e) {
			logger.warn("Cache clearance will be disabled since Selene vertical is not set, to enable set [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
		}

		KafkaConsumer<String, String> verticalCacheConsumer = null;
		if (seleneVertical != null) {
			verticalCacheConsumer = verticalCacheClearanceListener(getSeleneVertical().toString().toLowerCase());
		}

		boolean mutexEnabled = propertyFactory.getProperty(GlobeExternalConfigKeys.REDIS_SERVER_TRANSACTIONS_ENABLED).asBoolean(false).get();

		int defaultElements = mutexEnabled ? 0 : 200;

		// this cache is not tied to disabling of in memory cache config
		Ehcache ehCache = this.getEhcache("CacheClearanceCandidateRepo_cacheClearanceCandidatePaths", defaultElements, false, secondaryDuration * 60);

		return new CacheClearanceCandidateRepo(verticalCacheConsumer, ehCache, environmentConfig(),
				isHeaderEnabled, secondaryDuration, replicationDelay, cacheClearanceEventHandlers, urlDataFactory());
	}

	private KafkaConsumer<String, String> verticalCacheClearanceListener(String vertical) {
		EnvironmentConfig envConfig = environmentConfig();
		KafkaConsumer<String, String> answer;
		final String TOPIC_NAME = "realtime-" + vertical + "-resupply-cache";
		final String GROUP_ID = envConfig.getEnvironment().toLowerCase() + "-"
				+ vertical + "-" + UUID.randomUUID();

		String bootstrapServers = propertyFactory.getProperty(MantleExternalConfigKeys.KAFKA_BOOTSTRAP_SERVERS).asString(null)
				.get();

		if (StringUtils.isNotBlank(bootstrapServers)) {
			Properties properties = getCacheClearanceConsumerConfigurations(GROUP_ID, bootstrapServers);
			answer = new KafkaConsumer<String, String>(properties);
			answer.subscribe(Arrays.asList(TOPIC_NAME, CacheClearanceCandidateRepo.UNPUBLISH_TOPIC_NAME));
		} else {
			logger.warn("Couldn't find required config values for vertical cache clearing Kafka consumer, "
					+ "Cache clearing will be disabled.");
			answer = null;
		}

		return answer;
	}

	private Properties getCacheClearanceConsumerConfigurations(final String GROUP_ID, String bootstrapServers) {
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		// Following two props ensure that consumer doesn't read any previous data as
		// soon as it comes to life
		// and only reads data that became available after this consumer came to life.
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		// how often heart-beat thread should send heart-beat to kafka, setting to every
		// 15 seconds
		properties.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "15000");
		// if heart-beat is not received by kafka for following duration then it
		// declares consumer is dead, setting to 60 seconds
		properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");

		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		return properties;
	}

	@Bean
	public LegacyUrlMap buildLegacyUrlMap() {
		LegacyUrlMap answer = LegacyUrlMap.emptyLegacyUrlMap();
		if (isLegacyDocumentsSupported()) {

			if(propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URLS_SERVICE_BASED_MAP).asBoolean(false).get()) {
				answer = new DedicatedServiceBasedLegacyUrlMap(legacyUrlService(), getSeleneVertical(), minimumLegacyDocuments(),
						environmentConfig());
			}else{
				// If the list of legacy urls may change, search data should not be cached
				DeionSearchService deionSearchService = legacyUrlMapDeionSearchService();
				answer = new LegacyUrlMapImpl(deionSearchService, getSeleneVertical(), minimumLegacyDocuments(),
						environmentConfig());
			}

			if(isLegacyUrlMapPermissiveEnabled()){
				String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("").get();
				answer = new LegacyUrlMapPermissive(documentService(), seleneDocUrlsWithoutWww(), domain, answer);
				answer.addPathsToExclude(additionalElementPathsToExcludeFromLegacyUrlMapPermissive());
			}

			if(isLegacyUrlMapUpdatesEnabled()){
				scheduleLegacyMapRepopulate(answer);
			}
		}
		return answer;
	}

	@Bean
	public CacheClearanceLegacyUrlUpdater legacyUrlUpdater() {
		if (isLegacyUrlMapUpdatesEnabled()) {
			String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("").get();
			return new CacheClearanceLegacyUrlUpdater(domain, documentService(), buildLegacyUrlMap(), urlDataFactory(), seleneDocUrlsWithoutWww());
		}
		return null;
	}

	@Bean
	public boolean isLegacyUrlMapUpdatesEnabled(){
		return propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URL_UPDATER_ENABLED).asBoolean(false).get();
	}

	@Bean
	public boolean isLegacyUrlMapPermissiveEnabled(){
		return propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URLS_PREMISSIVE).asBoolean(false).get();
	}

	@Bean
	public List<String> additionalElementPathsToExcludeFromLegacyUrlMapPermissive(){
		List<String> paths = new ArrayList<>();
		paths.add("/user-proxy/**");
		paths.add("/account/**");
		paths.add("/element-api/content-proxy/**");
		paths.add("/dist/**");
		paths.add("/font/**");
		paths.add("/hermes/**");
		paths.add("/img/**");
		paths.add("/sites/**");
		paths.add("/sweepstakes/**");
		paths.add("/content/**");
		paths.add("/crt/**");
		paths.add("/kismet/**");
		paths.add("/**/null");
		paths.add("/**/undefined");
		return paths;
	}

	/**
	 * Verticals should set this consul-config if they want to support
	 * additional logging for legacy urls. Note: This value should not change much as
	 * legacy documents are imported as part of one time content-migration process
	 * during vertical re-platform. However, it is recommended to keep this
	 * value fresh with latest count from SOLR.
	 */
	public Long minimumLegacyDocuments() {
		return propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URLS_MIN_SIZE).asLong(0L).get();
	}

	@Bean
	public EmbedService embedService() {

		/* statically configured to these providers for the time being.
		 * in the future the providers can be automatically loaded.
		 * https://iacpublishing.atlassian.net/browse/GLBE-6578
		 */
		List<EmbedProvider> providers = new ArrayList<>();
		OEmbedEndpoint giphy = new OEmbedEndpoint("giphy", "https://giphy.com/services/oembed",
				ImmutableList.of("https://giphy.com/gifs/*", "http://gph.is/*", "https://media.giphy.com/media/*/giphy.gif"));

		OEmbedEndpoint reddit = new OEmbedEndpoint("reddit", "https://www.reddit.com/oembed",
				ImmutableList.of("https://reddit.com/r/*/comments/*/*/", "https://www.reddit.com/r/*/comments/*/*/"));

		OEmbedEndpoint tiktok = new OEmbedEndpoint("tiktok", "https://www.tiktok.com/oembed",
				ImmutableList.of("https://www.tiktok.com/*/video/*"));

		OEmbedEndpoint twitter = new OEmbedEndpoint("twitter", "https://publish.twitter.com/oembed",
				ImmutableList.of("https://twitter.com/*/status/*", "https://*.twitter.com/*/status/*"));

		OEmbedEndpoint spotify = new OEmbedEndpoint("spotify", "https://embed.spotify.com/oembed/",
				ImmutableList.of("https://*.spotify.com/**")); // ** intentional to match recursively

		OEmbedEndpoint vimeo = new OEmbedEndpoint("vimeo", "https://vimeo.com/api/oembed.json",
				ImmutableList.of("https://vimeo.com/*", "https://vimeo.com/album/*/video/*", "https://vimeo.com/channels/*/*",
				"https://vimeo.com/groups/*/videos/*", "https://vimeo.com/ondemand/*/*", "https://player.vimeo.com/video/*"));

		//not all verts using instagram embeds, if they do then they MUST provide valid access token
		String facebookAccessToken = getSecureProperty(MantleExternalConfigKeys.FACEBOOK_ACCESS_TOKEN, false);
		if(facebookAccessToken == null) {
			facebookAccessToken = "NOT_FOUND";
		}

		try {
			facebookAccessToken = URLEncoder.encode(facebookAccessToken, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		    throw new GlobeException(e.getMessage(), e);
		}

		final String facebookGraphApiVersion = "v14.0";

		OEmbedEndpoint facebookPage = new OEmbedEndpoint("facebook-page", "https://graph.facebook.com/"+ facebookGraphApiVersion +"/oembed_page?access_token=" + facebookAccessToken,
				ImmutableList.of("https://www.facebook.com/*"));
		OEmbedEndpoint facebookPost = new OEmbedEndpoint("facebook-post", "https://graph.facebook.com/" + facebookGraphApiVersion + "/oembed_post?access_token=" + facebookAccessToken,
				ImmutableList.of("https://www.facebook.com/*/posts/*",
								 "https://www.facebook.com/*/posts/*/*",
								 "https://www.facebook.com/*/activity/*",
								 "https://www.facebook.com/photo.php?fbid=*",
								 "https://www.facebook.com/photo?fbid=*",
								 "https://www.facebook.com/photos/*",
								 "https://www.facebook.com/*/photos/*",
								 "https://www.facebook.com/*/photos/*/*",
								 "https://www.facebook.com/*/photos/*/*/*",
								 "https://www.facebook.com/permalink.php?story_fbid=*",
								 "https://www.facebook.com/media/set?set=*",
								 "https://www.facebook.com/questions/*",
								 "https://www.facebook.com/notes/*/*/*"));
		OEmbedEndpoint facebookVideos = new OEmbedEndpoint("facebook-video", "https://graph.facebook.com/" + facebookGraphApiVersion + "/oembed_video?access_token=" + facebookAccessToken,
				ImmutableList.of("https://www.facebook.com/*/videos/*",
								 "https://www.facebook.com/*/videos/*/",
								 "https://www.facebook.com/*/videos/*/*",
								 "https://www.facebook.com/*/videos/*/*/*",
								 "https://www.facebook.com/video.php?id=*",
								 "https://www.facebook.com/video.php?v=*",
								 "https://www.facebook.com/watch/*"));

		OEmbedEndpoint instagram = new OEmbedEndpoint("instagram", "https://graph.facebook.com/" + facebookGraphApiVersion + "/instagram_oembed?access_token=" + facebookAccessToken, // ** intentional to match recursively
				ImmutableList.of("http://instagram.com/p/**",
								 "http://instagr.am/p/**",
								 "http://www.instagram.com/p/**",
								 "http://www.instagr.am/p/**",
								 "https://instagram.com/p/**",
								 "https://instagr.am/p/**",
								 "https://www.instagram.com/p/**",
								 "https://www.instagr.am/p/**"));

		OEmbedEndpoint youtube = new OEmbedEndpoint("youtube", "https://www.youtube.com/oembed",
				ImmutableList.of("https://*.youtube.com/watch*", "https://*.youtube.com/v/*", "https://youtu.be/*"));

		OEmbedProvider oEmbedProvider = new OEmbedProvider(ImmutableList.of(facebookPost, facebookVideos, facebookPage,
				giphy, instagram, reddit, spotify, tiktok, twitter, vimeo, youtube));
		providers.add(oEmbedProvider);

		EmbedService service;

		//Change as part of GLBE-8380 to be default enabled
		if (!propertyFactory.getProperty(MantleExternalConfigKeys.IFRAMELY_ENABLED).asBoolean(false).get()) {
			service = new EmbedServiceImpl(providers);
			if (propertyFactory.getProperty(MantleExternalConfigKeys.EMBED_CACHE_ENABLED).asBoolean(true).get()) {
				CacheTemplate<EmbedContent> cache = getPersistentCache("EmbedService_getContent",
						objectMapper().getTypeFactory().constructType(EmbedContent.class), 1000, false, 3600);
				service = new CachedEmbedService(service, cache);
			}
		} else {
			final String iframelyApiKey = getSecureProperty(MantleExternalConfigKeys.IFRAMELY_API_KEY);
			IframelyEmbedProvider iframelyEmbedProvider = new IframelyEmbedProvider ("https://iframe.ly/api/oembed", iframelyApiKey);
			service = new IframelyEmbedService(iframelyEmbedProvider);

			if (propertyFactory.getProperty(MantleExternalConfigKeys.IFRAMELY_CACHE_ENABLED).asBoolean(true).get()) {
				CacheTemplate<EmbedContent> cache = getPersistentCache("IframelyEmbedService_getContent",
						objectMapper().getTypeFactory().constructType(EmbedContent.class), 2000, false, 3600);
				service = new CachedIframelyEmbedService(service, cache);
			}
		}

		return service;
	}

	@Bean
	public EmbedTask embedTask() {
		return new EmbedTask(embedService());
	}

	/**
	 * Creates an object pool to recycle instances of Mac object used for generation of security hash in Thumbor URLs.
	 * @param thumborKey Key used for generating Thumbor url hashes
	 * @return Object pool of Mac objects configured for use in Thumbor library
	 */
	protected GenericObjectPool<Mac> buildPollexorHashPool(String thumborKey) {
		final int maxSize = propertyFactory.getProperty(MantleExternalConfigKeys.THUMBOR_MAC_POOL_MAX_SIZE).asInteger(100).get();
		final boolean enabled = propertyFactory.getProperty(MantleExternalConfigKeys.THUMBOR_MAC_POOL_ENABLED).asBoolean(true).get();

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(maxSize);

		GenericObjectPool<Mac> pool = null;

		if (enabled) {
			pool = new GenericObjectPool<Mac>(new PollexorImageResizer.HmacSHA1ObjectFactory(thumborKey), config) {
				AtomicLong maxActive = new AtomicLong(0);

				public Mac borrowObject() throws Exception {
					/* ** Temporary code for tuning of object pool **
					 * The best configuration for the object pool is not known at this time. As the maximum number of
					 * concurrently active objects increases, the new value is logged. This allows monitoring of the
					 * realistic maximum value needed in production to tune pool settings accordingly.
					 */
					long currentActive = getNumActive();
					if (currentActive > maxActive.get()) {
						maxActive.getAndAccumulate(currentActive, Math::max);
						thumborPoolLogger.info("HmacSHA1Pool concurrent borrowed count reached new max of [{}]", maxActive.get());
					}

					return super.borrowObject();
				}

				public void returnObject(Mac obj) {
					// Explicitly reset mac for next use. (In case state changed without doFinal call).
					obj.reset();
					super.returnObject(obj);
				}
			};
			// Threads should not default to generating their own Mac if pool is exhausted, not block
			pool.setBlockWhenExhausted(false);
		}
		return pool;
	}

	@Bean
	public ToolsTask toolsTask(){
		return new ToolsTask();
	}

	/**
	 * Vertical should have this = true if they support legacy urls -
	 * which do not conform to dotdash's slug-docId format.
	 */
	protected Boolean isLegacyDocumentsSupported() {
	    return propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_URLS_SUPPORT).asBoolean(false).get();
	}

	/**
	 * Check if it's legacy url, if yes then it's a normal legacy url. No need for sanitization, just set docId from the legacy url map.
	 *
	 * If it's not a legacy url then it must be normal url so don't need any sanitization or special handling.
	 *
	 */
	private PlatformUrlDataFactory getUrlDataFactory(String homeDocId, String appName) {
		return new DefaultingBaseUrlDocumentUrlDataFactory(
				new DefaultingUrlDataFactory(new ValidDomainUrlDataFactory(appName)), Long.valueOf(homeDocId)) {

			private final String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("about.com").get();
			@Override
			public PlatformUrlData create(String url) {
				URI uri = null;
				try {
					uri = new URI(url);
				} catch (URISyntaxException e) {
					//Error logging removed since it seems like it only applies to external urls
					//and thus prone to creating noise over warning us of a valid error
				}

				PlatformUrlData answer = super.create(url);

				if (uri != null) {

					if(answer instanceof VerticalUrlData) {

						//We want to avoid going to the legacy map in the case of the domain being an outside domain
						//So we need to make sure the host matches our domain, or it has no host which means it's one of ours
						//This check was added for LegacyURLMapPermissive
						boolean isAUrlFromTheVertical = uri.getHost() == null || uri.getHost().contains(domain);
						Long docId = isAUrlFromTheVertical ? buildLegacyUrlMap().getDocId((uri.getPath() != null) ? uri.getPath() : uri.toString()) : null;
						boolean isLegacyUrl = docId != null;

						com.about.hippodrome.url.VerticalUrlData.Builder builder = ((VerticalUrlData) answer).with().legacyDocument(isLegacyUrl);
						if (isLegacyUrl) {
							builder = builder.docId(docId);
						}
						answer = builder.build();
					}
				}
				return answer;
			}
		};
	}

	/**
	 * This is {@link BaseDocumentEx.Vertical} used to query
	 * Solr. e.g. {@link #buildLegacyUrlMap()}
	 * NOTE: Lithospheric services (Commerce and PRM) will not have this config set, so expect exception in that case.
	 */
	protected Vertical getSeleneVertical() {
		String vertical = propertyFactory.getProperty(MantleExternalConfigKeys.SELENE_VERTICAL).asString(null).get();
		Vertical answer = EnumUtils.getEnum(Vertical.class, vertical);
		if (answer == null) {
			throw new GlobeException("Selene vertical provided in config is invalid.");
		}
		return answer;
	}

	@Bean
	public RssFeedService rssFeedservice() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("rssFeedSearch");
		String mediaType = isUseSmile("rssFeedSearch") ? ClientMediaType.SMILE_V1.getMediaType()
				: ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		RssFeedService serviceImpl = new RssFeedServiceImpl(httpConfigBuilder.build());
		if (propertyFactory.getProperty(MantleExternalConfigKeys.RSSFEED_SEARCH_CACHE_ENABLED).asBoolean(false).get()) {
			CacheTemplate<RssFeedSearchResponse> cache = getPersistentCache("CachedRssFeedService_search",
					objectMapper().getTypeFactory().constructType(RssFeedSearchResponse.class), 100, false,
					(long) 60 * 15);
			RssFeedService cachedImpl = new CachedRssFeedService(serviceImpl, cache);
			return cachedImpl;
		}
		return serviceImpl;
	}

	/**
	 * Note: Extending vertical must override this bean config with a valid instance
	 * of {@link FeedBuilderFactory} in order for rss feed end point to work. Mantle
	 * does not support working rss feed out-of-the-box.
	 */
	@Bean
	@Deprecated
	public FeedBuilderFactory feedBuilderFactory() {
		return null;
	}

	@Bean
	@Autowired
	public FeedTask feedTask(Optional<FeedBuilderFactory> feedBuilderFactory, Map<String, FeedBuilder> feedBuilderMap) {
		if (!feedBuilderFactory.isPresent() && feedBuilderMap == null) {
			logger.warn("No FeedBuilderFactory and no feedBuilderMap specified: feeds will be disabled.");
		}
		return new FeedTask(feedBuilderFactory.orElse(null), feedBuilderMap);
	}

	@Bean(name = "smart-news")
	public FeedBuilder smartNewsFeedBuilder() {
		Vertical seleneVertical = null;

		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("Smart news feed disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		return new SmartNewsFeedBuilder(rssFeedservice(), renderUtils(), seleneVertical, authorService(), taxeneRelationTask(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_DESCRIPTION).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_LINK).asString(null).get());
	}

	@Bean(name = GOOGLE_NEWS_FEED_NAME)
	public FeedBuilder googleNewsFeedBuilder() {
		Vertical seleneVertical = null;

		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("Google news feed disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		return new GoogleNewsFeedBuilder(rssFeedservice(), renderUtils(), seleneVertical, authorService(),
				taxeneRelationTask(), propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_DESCRIPTION).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_LINK).asString(null).get());
	}

	@Bean(name = GOOGLE_NEWS_TAXONOMY_FEED_NAME)
	public FeedBuilder googleNewsTaxonomyFeedBuilder() {
		Vertical seleneVertical = null;

		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("Google news taxonomy feed disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		return new GoogleNewsTaxonomyFeedBuilder(rssFeedservice(), renderUtils(), seleneVertical, authorService(),
				taxeneRelationTask(), propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_DESCRIPTION).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_LINK).asString(null).get());
	}

	@Bean(name = FLIPBOARD_FEED_NAME)
	public FeedBuilder flipboardFeedBuilder() {
		Vertical seleneVertical = null;

		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("Flipboard feed disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		return new FlipboardFeedBuilder(rssFeedservice(), renderUtils(), seleneVertical, authorService(),
				taxeneRelationTask(), propertyFactory.getProperty(MantleExternalConfigKeys.FLIPBOARD_RSS_FEED_NAME).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.FLIPBOARD_RSS_FEED_DESCRIPTION).asString("").get());
	}

	@Bean(name = PINTEREST_TAXONOMY_FEED_NAME)
	public FeedBuilder pinterestTaxonomyFeedBuilder() {
		Vertical seleneVertical = null;

		try {
			seleneVertical = getSeleneVertical();
		}catch(GlobeException ex) {
			logger.warn("Pinterest feed disabled; to enable you need to add [{}] to your application-config",
					MantleExternalConfigKeys.SELENE_VERTICAL);
			return null;
		}

		return new PinterestTaxonomyFeedBuilder(rssFeedservice(), renderUtils(), getSeleneVertical(), authorService(),
				taxeneRelationTask(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_DESCRIPTION).asString("").get(),
				propertyFactory.getProperty(MantleExternalConfigKeys.RSS_NEWS_LINK).asString(null).get());
	}

	/**
	 * Maps our accepted nutrient names to their id's returned from nutritionx
	 */
    @Bean
	@Autowired
    public StructuredNutritionTask structuredNutritionTask(BusinessOwnedVerticalDataService bovd, JsonTask jsonTask) {
		boolean useLegacyMeredithAllowedNutrients = propertyFactory.getProperty(MantleExternalConfigKeys.LEGACY_MEREDITH_ALLOWED_NUTRIENTS).asBoolean(false).get();
		return new StructuredNutritionTask(allowedNutrients(useLegacyMeredithAllowedNutrients, bovd, jsonTask));
    }

	private Map<Integer, String> allowedNutrients(boolean useLegacyMeredithAllowedNutrients, BusinessOwnedVerticalDataService bovd, JsonTask jsonTask) {
		Map<Integer, String> allowedNutrients = new HashMap<>();
		Map<Integer, String> legacyDotdashAllowedNutrients = legacyDotdashAllowedNutrients();

		if (useLegacyMeredithAllowedNutrients) {
			final String bovdPath = "legacy-meredith/allowed_nutrients.json";

			byte[] byteArr = bovd.getResource(bovdPath);
			String jsonStr = new String(byteArr);
			Map<String, Object> map = jsonTask.json(jsonStr);
			List<Map<String, String>> legacyMeredithAllowedNutrients = (List<Map<String, String>>) map.get("allowedNutrients");

			if (legacyMeredithAllowedNutrients == null)
				throw new GlobeException("Bovd file {} is configured improperly. Expected key allowedNutrients");

			for (Map<String, String> model : legacyMeredithAllowedNutrients) {
				try {
					int currNutrientId = Integer.parseInt(model.get("id"));

					// Legacy Dotdash nutrition label takes precedence since the frontend is hard coded to use the label by name
					if (legacyDotdashAllowedNutrients.containsKey(currNutrientId)) {
						allowedNutrients.put(currNutrientId, legacyDotdashAllowedNutrients.get(currNutrientId));
					} else {
						String label = model.get("label");
						if (StringUtils.isBlank(label)) {
							logger.error("Missing allowed nutrition label for nutrition id {}", currNutrientId);
						} else {
							allowedNutrients.put(currNutrientId, label);
						}
					}
				} catch (NumberFormatException e) {
					logger.error("Invalid allowed nutrition id {} in bovd {}", model.get("id"), bovdPath);
				}
			}
		} else {
			allowedNutrients = legacyDotdashAllowedNutrients;
		}

		return allowedNutrients;
	}

	/*
	 * Careful consideration is required before modifiying this map since the frontend relies on accessing these map
	 * values as keys. Legacy meredith sites also rely on this map when creating their own mapping of allowed nutrients
	 */
	private Map<Integer, String> legacyDotdashAllowedNutrients() {
		Map<Integer, String> legacyDotdashAllowedNutrients = new HashMap<>();
		legacyDotdashAllowedNutrients.put(203, "protein");
		legacyDotdashAllowedNutrients.put(205, "carbohydrate");
		legacyDotdashAllowedNutrients.put(291, "dietaryFiber");
		legacyDotdashAllowedNutrients.put(269, "totalSugars");
		legacyDotdashAllowedNutrients.put(204, "totalFat");
		legacyDotdashAllowedNutrients.put(606, "saturatedFat");
		legacyDotdashAllowedNutrients.put(601, "cholesterol");
		legacyDotdashAllowedNutrients.put(301, "calcium");
		legacyDotdashAllowedNutrients.put(307, "sodium");
		legacyDotdashAllowedNutrients.put(303, "iron");
		legacyDotdashAllowedNutrients.put(306, "potassium");
		legacyDotdashAllowedNutrients.put(401, "vitaminC");
		legacyDotdashAllowedNutrients.put(208, "calories");

		return legacyDotdashAllowedNutrients;
	}

    @Bean
	public ComplianceTask complianceTask() {
		// TESU-6460
		String privacyReqSenderEmail = propertyFactory.getProperty(MantleExternalConfigKeys.PRIVACY_REQ_SENDER_EMAIL)
				.asString("sesadmin@dotdash.com").get();
		String privacyReqRecipientEmail = propertyFactory.getProperty(MantleExternalConfigKeys.PRIVACY_REQ_RECIPIENT_EMAIL)
				.asString("privacyRequests@dotdash.com").get();
		String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN)
				.asString("about.com").get();

		return new ComplianceTask(amazonSimpleEmailService(),
				privacyReqSenderEmail,
				privacyReqRecipientEmail,
				domain,
				consentBannerService());
	}

	@Bean
	public AmazonSimpleEmailService amazonSimpleEmailService() {
    	return AmazonSimpleEmailServiceClientBuilder.standard().build();
	}

	// TODO: look into moving all the prebid related beans to their own spring boot module
	@Bean
	public BidderCreator criteoCreator() {
		return new CriteoCreator();
	}

	@Bean
	public BidderCreator criteoPGCreator() {
		return new CriteoPGCreator();
	}

	@Bean
	public BidderCreator indexExchangeCreator() {
		return new IndexExchangeCreator();
	}

	@Bean
	public BidderCreator roundelCreator() {
		return new RoundelCreator();
	}

	@Bean
	public BidderCreator rubiconProjectCreator() {
		return new RubiconProjectCreator();
	}

	@Bean
	public BidderCreator pubmaticCreator() {
		return new PubmaticCreator();
	}

	@Bean
	public BidderCreator rubiconPGCreator() {
		return new RubiconPGCreator();
	}

	@Bean
	public BidderCreator trustXCreator() {
		return new TrustXCreator();
	}

	@Bean
	public BidderCreator appNexusCreator() {
		return new AppNexusCreator();
	}

	@Bean
	public BidderCreator oneMobileCreator() {
		return new OneMobileCreator();
	}

	@Bean
	public BidderCreator ttdCreator() {
		return new TtdCreator();
	}

	@Bean
	@Autowired
	public PrebidConfigurationService prebidConfigurationService(List<BidderCreator> bidderCreators, BusinessOwnedVerticalDataService bovdService) {
		Map<String, BidderCreator> bidderCreatorMap = bidderCreators.stream().collect(Collectors.toMap(BidderCreator::name, Function.identity()));
		String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString("about.com").get();
		DomainSpecificBeanVerifier beanVerifier = new DomainSpecificBeanVerifier(domain);
		PrebidConfigurationService service = new CsvBasedPrebidConfigurationService(bidderCreatorMap, bovdService, "mantle/prebid.csv", beanVerifier);
		if (propertyFactory.getProperty(MantleExternalConfigKeys.PREBID_CONFIG_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<PrebidConfiguration> cacheTemplate = getPersistentCache("CachedPrebidConfigurationService_getConfiguration",
					objectMapper().getTypeFactory().constructType(PrebidConfiguration.class), 1, false, 3600);
			service = new CachedPrebidConfigurationService(service, cacheTemplate);
		}
		return service;
	}

	@Bean
	@Autowired
	public PrebidTask prebidTask(PrebidConfigurationService prebidConfigurationService) {
		return new PrebidTask(prebidConfigurationService);
	}

	@Bean
	public MockPrebidTask mockPrebidTask() {
		return new MockPrebidTask(objectMapper());
	}

	@Bean
	public ConsentBannerService consentBannerService() {
		String domainId = propertyFactory.getProperty(MantleExternalConfigKeys.CMP_ONETRUST_DOMAIN_ID).asString(null).get();
		String lang = propertyFactory.getProperty(MantleExternalConfigKeys.CMP_ONETRUST_LANG).asString("en").get();
		boolean ssrEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.CMP_ONETRUST_SSR_ENABLED).asBoolean(false).get();
		String sdkVersion = propertyFactory.getProperty(MantleExternalConfigKeys.CMP_ONETRUST_VERSION).asString(null).get();
		Set<String> templates = Set.of(StringUtils.split(propertyFactory.getProperty(MantleExternalConfigKeys.CMP_ONETRUST_TEMPLATES).asString("GDPR").get(), ','));
		ConsentBannerService service = new OneTrustConsentBannerService(domainId, lang, templates, ssrEnabled, sdkVersion);
		if (propertyFactory.getProperty(MantleExternalConfigKeys.CMP_DOMAIN_DATA_CACHE_ENABLED).asBoolean(true).get()) {
			final long defaultTtl = 3600;
			CacheTemplate<CachedConsentBannerService.Template> templateCache = getPersistentCache("CachedConsentBannerService_getDomainData_templateCache",
				objectMapper().getTypeFactory().constructType(CachedConsentBannerService.Template.class),
				1000000, false, defaultTtl);
			CacheTemplate<Map<String, Object>> domainDataCache = getPersistentCache("CachedConsentBannerService_getDomainData_domainDataCache",
				objectMapper().getTypeFactory().constructMapType(Map.class, String.class, Object.class),
				ConsentBannerService.Template.values().length, false, defaultTtl);
			service = new CachedConsentBannerService(service, templateCache, domainDataCache);
		}
		return service;
	}

	@Bean
	public MantleCorsConfigs mantleCorsConfigs() {
		String primaryDomainName = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString(null).get();

		if(primaryDomainName == null) {
			throw new GlobeException(GlobeExternalConfigKeys.DOMAIN + " is not set");
		}

		return new MantleCorsConfigs(primaryDomainName);
	}

	@Bean
	public ConversationsTask conversationsTask() {
		return new ConversationsTask(bovdService());
	}

	//Will figure out what order should really be as use cases evolve.
	//https://dotdash.atlassian.net/browse/GLBE-8151
	@Order(0)
	@Bean
	public DocumentPreprocessor productRecordPreprocessor() {
		return new ProductRecordPreprocessor(productService());
	}

	//amazonIdPreprocessor should load after productRecordPreprocessor so that the newly hydrated PRBs also get the amazon fallback
	@Order(1)
	@Bean
	public DocumentPreprocessor amazonIdPreprocessor() {
		return new AmazonIdPreprocessor(verticalConfigService());
	}

	@Order(2)
	@Bean
	@Autowired
	public DocumentPreprocessor elementRewritingPreprocessor(Optional<List<ElementRewriter>> elementRewriters) {
    	return new ElementRewriterPreprocessor(elementRewriters.orElse(null));
	}

	@Order(3)
	@Bean
	@Autowired
	public DocumentPreprocessor primaryAndSecondaryTaxonomyPreprocessor() {
		if (propertyFactory.getProperty(MantleExternalConfigKeys.LINK_PRIMARY_AND_SECONDARY_TAXONOMY_TO_DOCS_ENABLED).asBoolean(false).get()) {
			return new PrimaryAndSecondaryTaxonomyPreprocessor(taxeneRelationService());
		}
		return null;
	}

	@Order(4)
	@Bean
	@Autowired
	public DocumentPreprocessor descriptiveTaxonomyTermPreprocessor() {
		if (isDescriptiveTaxonomyTermSupported()) {
			return new DescriptiveTaxonomyTermPreprocessor(descriptiveTaxonomyTermService());
		}
		return null;
	}

	@Order(5)
	@Bean
	@Autowired
	public DocumentPreprocessor NLPPreprocessor() {
		return new NLPPreprocessor();
	}

	@Order(6)
	@Bean
	public DocumentPreprocessor profilePreprocessor() {
		return new ProfilePreprocessor(productService());
	}

	@Bean
	public Mantle410Service mantle410Service(){

    	String domain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString(null).get();
    	Mantle410Service service = new Mantle410ServiceImpl(domain,"mantle/pagegone.csv", bovdService(),
				documentArchiveService(), seleneDocUrlsWithoutWww());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.MANTLE_410_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<Set<String>> cacheTemplate = getPersistentCache("CachedMantle410Service_is410",
					objectMapper().getTypeFactory().constructCollectionType(Set.class, String.class), 1, false, 3600);
			service = new CachedMantle410Service(service, domain, cacheTemplate, documentArchiveService(), seleneDocUrlsWithoutWww());
		}
    	return service;
	}

	@Bean
	public DisqusService disqusService(){
    	String apiKey = propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_API_KEY).asString("0hAxGCuBYBDV3JDoSloutHVPrFIQgJ3vPiqw2ceC5t7RHt4oQldW66Ba0C5Mt4e5").get();
		String forumName = propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_FORUM_NAME).asString(null).get();
		DisqusService disqusService = new DisqusServiceImpl(apiKey,forumName);

		//No forumName means no disqus so we should not allocate the cache for this
		if (forumName != null && propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_CACHE_ENABLED).asBoolean(true).get()) {

			//Template Size is a guess here, per ticket GLBE-8377 we wanted a 24 hour cache
			CacheTemplate<DisqusThreadDetails> cacheTemplateGetThreadDetails = getPersistentCache("CachedDisqusThreadDetails_getThreadDetails",
					objectMapper().getTypeFactory().constructType(DisqusThreadDetails.class), 5000, false, 86400);

			CacheTemplate<List<DisqusPost>> cacheTemplateGetPopularComments = getPersistentCache("CachedDisqusThreadDetails_getPopularComments",
					objectMapper().getTypeFactory().constructCollectionType(List.class, DisqusPost.class), 5000, false, 86400);

			CacheTemplate<List<DisqusPost>> cacheTemplateGetOriginalComments = getPersistentCache(
					"CachedDisqusThreadDetails_getOriginalComments",
					objectMapper().getTypeFactory().constructCollectionType(List.class, DisqusPost.class), 5000, false,
					86400);

			disqusService = new CachedDisqusService(disqusService, cacheTemplateGetThreadDetails, cacheTemplateGetPopularComments, cacheTemplateGetOriginalComments);
		}

		return disqusService;
	}

	@Bean
	public VerticalConfigService verticalConfigService() {
		HttpServiceClientConfig.Builder httpConfigBuilder = defaultSeleneClientConfig("verticalconfig");
		String mediaType =  isUseSmile("verticalconfig") ? ClientMediaType.SMILE_V1.getMediaType() : ClientMediaType.JSON_V1.getMediaType();
		httpConfigBuilder.setMediaType(mediaType);
		VerticalConfigServiceImpl service = new VerticalConfigServiceImpl(httpConfigBuilder.build());

		if (propertyFactory.getProperty(MantleExternalConfigKeys.VERTICAL_CONFIG_CACHE_ENABLED).asBoolean(true).get()) {
			CacheTemplate<Map<String, ?>> verticalConfigCache = getPersistentCache(
					"CachedVerticalConfigService_getVerticalConfig",
					objectMapper().getTypeFactory().constructType(Map.class),
					1000, false,
					3600);
			return new CachedVerticalConfigService(service, verticalConfigCache);
		}
		return service;
	}

	@Bean
	public DisqusTask disqusTask(){

		//Max limit is 100
		int defaultLimit = propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_DEFAULT_POPULAR_COMMENT_LIMIT).asInteger(3).get();

		//Choices: 30s, 1h, 6h, 12h, 1d, 3d, 7d, 30d, 60d, 90d
		String defaultMaxAge = propertyFactory.getProperty(MantleExternalConfigKeys.DISQUS_DEFAULT_POPULAR_MAX_AGE).asString("90d").get();

    	return new DisqusTask(disqusService(),defaultMaxAge, defaultLimit);
	}

	@Bean
	public MantleShutdownHook shutdownHook() {
		String threadDumpFilePath = propertyFactory.getProperty(MantleExternalConfigKeys.SHUTDOWN_THREAD_DUMP_PATH).asString(null).get();
		if (threadDumpFilePath != null) {
			return new MantleShutdownHook(threadDumpFilePath);
		}
		return null;
	}

	@Bean
	public EntityReferenceTask entityReferenceTask() {
		return new EntityReferenceTask(bovdService(), jsonTask());
	}

	@Bean
	public SafeListParamaterFailedLogger safeListParamaterFailedLogger(){
    	return new SafeListParamaterFailedLogger();
	}

	@Bean
	public RegSourcesService regSourcesService() {
		RegSourcesService regSourcesService = null;
		HttpServiceClientConfig httpServiceClientConfig = defaultSharedServicesClientConfig("regsources").build();
		String sharedServiceApiKey = getSecureProperty(MantleExternalConfigKeys.SHARED_SERVICES_API_KEY, false);

		if(areRequiredPropertiesConfiguredForSharedServices(httpServiceClientConfig, sharedServiceApiKey)) {
			regSourcesService = new RegSourcesServiceImpl(httpServiceClientConfig, ImmutableMap.of(SHARED_SERVICES_AUTH_HEADER_NAME, sharedServiceApiKey));

			if(propertyFactory.getProperty(MantleExternalConfigKeys.SHARED_SERVICE_REGSOURCES_CACHE_ENABLED).asBoolean(true).get()) {
				CacheTemplate<RegSource> regSourceCacheTemplate = getPersistentCache(
						"CachedRegSourcesServices_getRegSourceById",
						objectMapper().getTypeFactory().constructType(RegSource.class),
						100, false, ONE_DAY_IN_SECONDS);

				regSourcesService = new CachedRegSourcesServiceImpl(regSourceCacheTemplate, regSourcesService);
			}
		}

    	return regSourcesService;
	}

	private HttpServiceClientConfig.Builder defaultSharedServicesClientConfig(String clientName) {
        HttpServiceClientConfig.Builder httpServiceClientConfigBuilder = defaultBaseClientConfig(clientName);
        httpServiceClientConfigBuilder.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON);
        httpServiceClientConfigBuilder.setBaseUrl(determineSharedServicesUrl());

        return httpServiceClientConfigBuilder;
    }

    // currently determines legacy meredith's shared services url from app configs but can be discovered from service discovery if & when it's moved over to Squadron
	private String determineSharedServicesUrl() {
    	return propertyFactory.getProperty(MantleExternalConfigKeys.SHARED_SERVICES_BASE_URL).asString(null).get();
	}

	private boolean areRequiredPropertiesConfiguredForSharedServices(HttpServiceClientConfig httpServiceClientConfig, String sharedServiceApiKey) {
		if(httpServiceClientConfig.getBaseUrl() != null && sharedServiceApiKey == null) {
			throw new GlobeException("Shared service's api key config [" + MantleExternalConfigKeys.SHARED_SERVICES_API_KEY + "] required for registration service is missing");
		}else if (sharedServiceApiKey != null && httpServiceClientConfig.getBaseUrl() == null) {
			throw new GlobeException("Shared service's base url config [" + MantleExternalConfigKeys.SHARED_SERVICES_BASE_URL + "] required for registration service is missing");
		}

		return httpServiceClientConfig.getBaseUrl() != null && sharedServiceApiKey != null;
	}

	@Bean
	public RegSourcesTask regSourcesTask() {
    	return new RegSourcesTask(regSourcesService());
	}

	@Bean
	public RegistrationTask registrationTask() {
    	return new RegistrationTask(registrationService(), propertyFactory.getProperty(MantleExternalConfigKeys.SHARED_SERVICES_BRAND).asString(null).get());
	}

	@Bean
	public RegistrationService registrationService() {
		RegistrationService registrationService = null;
		HttpServiceClientConfig httpServiceClientConfig = defaultSharedServicesClientConfig("registration").build();
		String sharedServiceApiKey = getSecureProperty(MantleExternalConfigKeys.SHARED_SERVICES_API_KEY, false);

		if (areRequiredPropertiesConfiguredForSharedServices(httpServiceClientConfig, sharedServiceApiKey)) {
			registrationService = new RegistrationServiceImpl(httpServiceClientConfig, ImmutableMap.of(SHARED_SERVICES_AUTH_HEADER_NAME, sharedServiceApiKey));
		}

		return registrationService;
	}

	@Bean
	public DDMAccountAuthService ddmAccountAuthService(){
		String domain = "." + renderUtils().getDomain("/");
		return new DDMAccountAuthService(domain, auth0Verifier());
	}

	@Bean
	public Auth0Verifier auth0Verifier (){
		String authTenant = propertyFactory.getProperty(MantleExternalConfigKeys.AUTH0_DOMAIN).asString(null).get();
		return new Auth0Verifier(authTenant);
	}

	@Bean
	public BookmarksService bookmarksService() {
		BookmarksService answer = null;

		String baseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.BOOKMARKS_BASE_URL).asString(null).get();
		if (StringUtils.isNotBlank(baseUrl)) {
			String brand = propertyFactory.getProperty(MantleExternalConfigKeys.SHARED_SERVICES_BRAND).asString(null).get();
			HttpServiceClientConfig httpServiceClientConfig = defaultBaseClientConfig("bookmarks").setBaseUrl(baseUrl)
						.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON).build();
			answer = new BookmarksServiceImpl(httpServiceClientConfig, brand);
		}

		return answer;
	}

	@Bean
	public ContentGraphService contentGraphService() {
		ContentGraphService answer = null;

		String baseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.CONTENT_GRAPH_BASE_URL).asString(null).get();
		if (StringUtils.isNotBlank(baseUrl)) {
			String apiKey = getSecureProperty(MantleExternalConfigKeys.CONTENT_GRAPH_API_KEY);
			HttpServiceClientConfig httpServiceClientConfig = defaultBaseClientConfig("contentGraph").setBaseUrl(baseUrl)
						.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON).build();
			answer = new ContentGraphServiceImpl(httpServiceClientConfig, apiKey);
		}

		return answer;
	}

	@Bean
	public CampaignService campaignService() {
		String campaignsConfigPath = propertyFactory.getProperty(MantleExternalConfigKeys.CAMPAIGNS_CONFIG_PATH).asString(null).get();

		if (campaignsConfigPath != null) {
			return new CampaignServiceImpl(bovdService(), campaignsConfigPath);
		}

		return null;
	}

	@Bean
	public CampaignTask campaignTask() {
		return new CampaignTask(campaignService());
	}

	@Bean
	public DigitalIssueTask digitalIssueTask() {
		String appName = environmentConfig().getApplication();

		return new DigitalIssueTask(bovdTask(), jsonTask(), appName);
	}
}
