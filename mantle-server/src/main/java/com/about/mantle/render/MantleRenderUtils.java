package com.about.mantle.render;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.UserAgent;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.task.processor.ModelResult;
import com.about.hippodrome.url.PlatformUrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.info.UrlTypeV2;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.htmlslicing.HtmlSlicer;
import com.about.mantle.htmlslicing.HtmlSlicerConfig;
import com.about.mantle.http.util.MantleIpDetectorUtils;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.ItemEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentVideoBlockEx;
import com.about.mantle.model.services.CuratedDomainService;
import com.about.mantle.model.services.commerce.VendorLookupService;
import com.about.mantle.model.services.document.ElementRewriter;
import com.about.mantle.model.services.document.preprocessor.Element;
import com.about.mantle.render.image.ImageType;
import com.about.mantle.render.image.MantleImageRenderUtils;
import com.google.common.math.IntMath;
import com.netflix.archaius.api.PropertyFactory;
import freemarker.ext.beans.BooleanModel;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.joda.time.DateTime;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * This class provides convenience methods to the freemarker context that are not tied to a specific model
 */

public class MantleRenderUtils extends CoreRenderUtils {
	private static final Pattern dashPattern = Pattern.compile("[\\-\\s]+");
	private static final Pattern alphaNumericPattern = Pattern.compile("[^\\-a-z0-9]");
    private static final DateTimeFormatter longFormDateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter shortFormDateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");

	private Map<String, List<ElementRewriter>> elementRewriters;
	private final CuratedDomainService curatedDomainService;
	private final MantleImageRenderUtils mantleImageRenderUtils;
	private final ProjectInfo projectInfo;
	private final MantleIpDetectorUtils ipDetectorUtils;
	private final HtmlSlicer htmlSlicer;
	private final VendorLookupService vendorLookupService;
	private final boolean suppressEndComments;
	private final Boolean isCaesEnabled;
	private final boolean uncappedImageWidthsEnabled;
	private final String untrustedTrackingCodesBaseUrlStr;
	private final boolean useBlurryPlaceholder;
	private final boolean generateBlurryPlaceholderIfMissingFromSelene;
	private final Set<String> safeListDomains;
	private final EnvironmentConfig envConfig;

	// Ex:
	// https://cf-images.us-east-1.prod.boltdns.net/v1/static/1125866826/7cb301c2-7cf4-4b8d-a82f-dabbbbe512b5/23b2fa53-2fdb-47a8-bf91-b350ea9e7b73/864x1080/match/image.jpg
	// equals https://${letters, numbers, ., /, -}/WIDTHxHEIGHT/${letters, numbers,
	// ., /, -}
	private static final Pattern THUMBNAIL_URL_HEIGHT_PATTERN = Pattern
			.compile("https:\\/\\/[\\d\\w\\-\\.\\/]+\\/\\d+x(\\d+)\\/[\\d\\w\\-\\.\\/]+");
	private static final Pattern THUMBNAIL_URL_WIDTH_PATTERN = Pattern
			.compile("https:\\/\\/[\\d\\w\\-\\.\\/]+\\/(\\d+)x\\d+\\/[\\d\\w\\-\\.\\/]+");
	private static final String SIXTEEN_NINE_RATIO = "56.25%";

	public MantleRenderUtils(String domain, MantleImageRenderUtils imageRenderUtils, UrlDataFactory urlDataFactory,
							 ProjectInfo projectInfo, MantleIpDetectorUtils ipDetectorUtils,
							 HtmlSlicer htmlSlicer, VendorLookupService vendorLookupService, PropertyFactory propertyFactory,
							 EnvironmentConfig envConfig, CuratedDomainService curatedDomainService, boolean useBlurryPlaceholder,
							 boolean generateBlurryPlaceholderIfMissingFromSelene, List<ElementRewriter> elementRewriters) {
		super(domain, imageRenderUtils, urlDataFactory, propertyFactory);
		this.mantleImageRenderUtils = imageRenderUtils;
		this.curatedDomainService = curatedDomainService;
		this.projectInfo = projectInfo;
		this.ipDetectorUtils = ipDetectorUtils;
		this.htmlSlicer = htmlSlicer;
		this.vendorLookupService = vendorLookupService;
		this.suppressEndComments = propertyFactory.getProperty(MantleExternalConfigKeys.SUPPRESS_END_COMMENTS).asBoolean(Boolean.FALSE).get();
		this.isCaesEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.CAES_ENABLED).asBoolean(Boolean.FALSE).get();
		this.uncappedImageWidthsEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.UNCAPPED_IMAGE_WIDTHS_ENABLED).asBoolean(Boolean.FALSE).get();
		this.useBlurryPlaceholder = useBlurryPlaceholder;
		this.generateBlurryPlaceholderIfMissingFromSelene = generateBlurryPlaceholderIfMissingFromSelene;

		String safeListDomainsConfigValue = propertyFactory.getProperty(MantleExternalConfigKeys.SAFELIST_DOMAINS).asString("").get();
		this.safeListDomains = Arrays.stream(StringUtils.split(safeListDomainsConfigValue)).filter(d -> !d.isEmpty())
				.collect(Collectors.toSet());

		// Mapping rewriters according to targeted elements
		if (elementRewriters != null) {
			this.elementRewriters = elementRewriters.stream()
					.flatMap(x -> x.getTargetElementNames().stream().distinct().map(y -> new AbstractMap.SimpleEntry<>(x, y)))
					.collect(Collectors.groupingBy(
							Map.Entry::getValue,
							Collectors.mapping(Map.Entry::getKey, Collectors.toList())
					));
		}

		// Derive default sandboxed content url from domain / environment
		String defaultSandboxedContentUrl = "";

		this.envConfig = envConfig;
		if (envConfig != null && domain.indexOf('.') > 0) {
			boolean isProd = "prod".equalsIgnoreCase(envConfig.getAccountName());
			StringBuilder builder = new StringBuilder();
			builder.append("https://").append(domain, 0, domain.indexOf('.')).append(isProd ? "" : "-qa").append(".forwardco.com");
			defaultSandboxedContentUrl = builder.toString();
		}
		this.untrustedTrackingCodesBaseUrlStr = propertyFactory.getProperty(MantleExternalConfigKeys.SANDBOXED_CONTENT_BASE_URL).asString(defaultSandboxedContentUrl).get();
	}

	public String getRetailerName(String url) {
		return vendorLookupService.getRetailerName(url);
	}

	/**
	 * Allows modification of elements being created via components through registered ElementRewriter beans.
	 * Should probably be renamed to something anchor specific, as the href attribute is passed in separately and not
	 * part of a map.
	 * @param tagName
	 * @param href
	 * @param attributes
	 * @param requestContext
	 * @return
	 */
	public RenderUtilsElementWrapper processElementRewriters(String tagName, String href, Map<String, String> attributes, RequestContext requestContext) {
		RenderUtilsElementWrapper wrapper = new RenderUtilsElementWrapper(tagName, href, attributes);

		if (elementRewriters == null || !elementRewriters.containsKey(tagName)) return wrapper;

		for (ElementRewriter elementRewriter : elementRewriters.get(tagName)) {
			elementRewriter.processElement(getDocument(requestContext), wrapper);
		}
		return wrapper;
	}

	@Override
	public String rewriteContentUrl(String url, RequestContext requestContext) {
		@SuppressWarnings("rawtypes")
		PlatformUrlData.Builder rewrittenUrl = (PlatformUrlData.Builder)shouldRewriteContentUrl(url, requestContext);
		if (rewrittenUrl == null) return url;

		PlatformUrlData requestUrlData = (PlatformUrlData)requestContext.getUrlData();

		Map<String, List<String>> queryParams = requestUrlData.getQueryParams();
		if (queryParams != null) {

			boolean isAboutIpAddress = isInternalIp(requestContext.getHeaders().getRemoteIp());

			// @formatter:off
			queryParams.keySet().stream().filter(StringUtils::isNotBlank)
					.filter((query) -> isAboutIpAddress ? isPresevedQueryParam(query) : false).forEach(queryParam -> {
				List<String> values = queryParams.get(queryParam);
				String value = isNotEmpty(values) ? values.get(0) : "";
				rewrittenUrl.queryParam(queryParam, value);
			});
			// @formatter:on
		}

		try {
			rewrittenUrl.scheme(requestUrlData.getScheme());
			rewrittenUrl.environment(requestUrlData.getEnvironment());
			rewrittenUrl.hypenatedSubdomain(requestUrlData.isHypenatedSubdomain());
			rewrittenUrl.port(requestUrlData.getPort());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unsupported encoding", e);
		}

		return rewrittenUrl.build().getUrl();
	}

	public boolean isWatermarkedImage(ImageEx image) {
		return image != null && image.getOriginal();
	}

	public boolean isWatermarkedImage(SimpleHash imageHash) {
		if (imageHash == null) return false;

		try {
			if (!imageHash.containsKey("original")) {
				return false;
			}
			TemplateModel templateModel = imageHash.get("original");
			if (!(templateModel instanceof BooleanModel)) return false;
			BooleanModel model = (BooleanModel) templateModel;
			return model.getAsBoolean();
		} catch (TemplateModelException e) {
			throw new GlobeException("failed to parse image hash", e);
		}
	}

	@Override
	public String rewriteImageUrl(String url, RequestContext requestContext) {
		return mantleImageRenderUtils.rewriteImageUrl(url, requestContext);
	}

	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
							RequestContext requestContext, String cropSetting, String[] filters) {
		return mantleImageRenderUtils.getImage(image, type, fitInStyle, forceSize, requestContext, cropSetting, filters, isWatermarkedImage(image));
	}

	public String getImageSrc(ImageEx image, int width, int height, String fitInStyle, Boolean forceSize,
							  RequestContext requestContext, String cropSetting, String[] filters,
							  boolean ignoreMaxBytesOutsideSrcset) {
		String imgUrl = image.getObjectId() != null ? image.getObjectId() : image.getUrl();
		return getImageSrc(imgUrl, width, height, fitInStyle, forceSize, requestContext, cropSetting, filters,
				isWatermarkedImage(image), ignoreMaxBytesOutsideSrcset);
	}

	public String getImageSrc(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
							  RequestContext requestContext, String cropSetting, String[] filters, Boolean addWatermark) {
		return getImageSrc(imageUrl, width, height, fitInStyle, forceSize, requestContext,
				cropSetting, filters, addWatermark, false);
	}

	public String getImageSrc(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
							  RequestContext requestContext, String cropSetting, String[] filters, Boolean addWatermark,
							  boolean ignoreMaxBytesOutsideSrcset) {
		if (ignoreMaxBytesOutsideSrcset) {
			filters = filterOutMaxBytes(filters);
		}
		return mantleImageRenderUtils.getImage(imageUrl, width, height, fitInStyle, forceSize, requestContext,
				cropSetting, filters, addWatermark);
	}

	public String getPlaceholderImage(RequestContext requestContext) {
		return mantleImageRenderUtils.getPlaceholder(requestContext);
	}

	public boolean useBlurryPlaceholder(){
		return useBlurryPlaceholder;
	}

	public boolean generateBlurryPlaceholderIfMissingFromSelene(){
		return generateBlurryPlaceholderIfMissingFromSelene;
	}

	public String getBlurryPlaceholder(String blurryPlaceholder){
		return mantleImageRenderUtils.getBlurryPlaceholder(blurryPlaceholder);
	}

	public String generateBlurryPlaceholder(RequestContext requestContext, String imageUrl){
		return mantleImageRenderUtils.generateBlurryPlaceholder(requestContext, imageUrl);
	}

	public String getSrcsetThumbor(ImageEx image, int minWidth, int maxWidth, int maxHeight,
								   int stepCount, String fitInStyle, Boolean forceSize, RequestContext requestContext, String cropSetting,
								   String[] filters) {
		String imgUrl = image.getObjectId() != null ? image.getObjectId() : image.getUrl();
		return getSrcsetThumbor(imgUrl, minWidth, maxWidth, maxHeight, stepCount, fitInStyle, forceSize,
				requestContext, cropSetting, filters, isWatermarkedImage(image));
	}

	public String getSrcsetThumbor(String imageUrl, int minWidth, int maxWidth, int maxHeight,
								   int stepCount, String fitInStyle, Boolean forceSize, RequestContext requestContext, String cropSetting,
								   String[] filters) {
		return getSrcsetThumbor(imageUrl, minWidth, maxWidth, maxHeight, stepCount, fitInStyle, forceSize,
				requestContext, cropSetting, filters, false);
	}

	/*
	 * Returns a String comprised of other Strings, each representing an imageUrl
	 * with its respective width. The number of Strings depends on the stepCount,
	 * ultimately returning a number of images equal to stepCount + 2 to include the minWidth and maxWidth
	 * Ex. minWidth = 400, maxWidth = 1800, stepCount = 5 returns 7 images with
	 * 400w, 633w, 866w, 1099w, 1332w, 1565w, 1800w in the srcset
	*/
	public String getSrcsetThumbor(String imageUrl, int minWidth, int maxWidth, int maxHeight,
								   int stepCount, String fitInStyle, Boolean forceSize, RequestContext requestContext, String cropSetting,
								   String[] filters, Boolean addWatermark) {
		StringBuilder srcset = new StringBuilder(imageUrl.length() * 2 * stepCount);

		// Calculate the distance between each sequential step
		int stepSize = (int) Math.floor(((maxWidth - minWidth) / (stepCount + 1)));
		// If stepSize is zero, return image with minWidth and avoid looping # of stepCount times
		if (stepSize == 0) {
			srcset.append(mantleImageRenderUtils.getImage(imageUrl, minWidth, maxHeight, fitInStyle,
			forceSize, requestContext, cropSetting, filters, addWatermark)).append(' ').append(minWidth).append('w');
			return srcset.toString();
		}

		// Update the loop to have 0 <= i <= stepCount
		// i = 0 appends the first image with minWidth
		// i = stepCount appends image with final w calculation before maxWidth
		for (int i = 0; i <= stepCount; i++) {
			// Keep index as positive int to calculate next stepWidth for proceeding image
			int stepWidth = minWidth + (i * stepSize);
			// Append newly generated image + stepWidth to srcset
			srcset.append(mantleImageRenderUtils.getImage(imageUrl, stepWidth, maxHeight, fitInStyle,
					forceSize, requestContext, cropSetting, filters, addWatermark)).append(' ').append(stepWidth).append("w, ");
		}
		// Append final image + maxWidth to srcset
		srcset.append(getThumborUrl(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize,
				requestContext, cropSetting, filters, addWatermark)).append(' ').append(maxWidth).append('w');

		return srcset.toString();
	}

	public String getThumborUrl(ImageEx image, int maxWidth, int maxHeight, String fitInStyle,
								Boolean forceSize, RequestContext requestContext, String cropSetting, String[] filters) {
		String imgUrl = image.getObjectId() != null ? image.getObjectId() : image.getUrl();
		return getThumborUrl(imgUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext,
				cropSetting, filters, isWatermarkedImage(image));
	}

	public String getThumborUrl(String imageUrl, int maxWidth, int maxHeight, String fitInStyle,
								Boolean forceSize, RequestContext requestContext, String cropSetting, String[] filters) {
		return getThumborUrl(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext,
				cropSetting, filters, false);
	}

	public String getThumborUrl(String imageUrl, int maxWidth, int maxHeight, String fitInStyle,
								Boolean forceSize, RequestContext requestContext, String cropSetting, String[] filters, Boolean addWatermark) {
		return getThumborUrl(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext,
				cropSetting, filters, false, false);
	}

	public String getThumborUrl(String imageUrl, int maxWidth, int maxHeight, String fitInStyle,
								Boolean forceSize, RequestContext requestContext, String cropSetting, String[] filters,
								Boolean addWatermark, boolean ignoreMaxBytes) {
		if (ignoreMaxBytes) {
			filters = filterOutMaxBytes(filters);
		}
		return mantleImageRenderUtils.getImage(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext,
				cropSetting, filters, addWatermark);
	}

	/*
	 * See https://docs-imagesvc.meredithcorp.io/ for documentation and valid optional query params
	 * 'height' and 'width' instead of their abbreviation is also valid as a key in the map for convenience
	 */
	public String getImageSvcUrl(String imageUrl, Map<String, Object> optionalQueryParams) {
		if (StringUtils.isEmpty(imageUrl)) {
			throw new GlobeException("Can not generate an image service url with an empty/null string");
		}

		String baseUrl = "https://imagesvc.meredithcorp.io/v3/";
		// Same tag used by legacy meredith sites that haven't
		// migrated over for caching purposes
		String tag = "mm";
		String path = imageUrl.endsWith(".gif") ? "gif" : "image";

		StringBuilder imageSvcUrl = new StringBuilder()
											.append(baseUrl)
											.append(tag)
											.append("/")
											.append(path)
											.append("?url=")
											.append(URLEncoder.encode(imageUrl, StandardCharsets.UTF_8));

		for (Map.Entry<String, Object> entry : optionalQueryParams.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null)
				continue;

			String queryParamName = entry.getKey().toLowerCase();

			// Allows convenience for the frontend to pass in the full name
			if ("height".equals(queryParamName))
				queryParamName = "h";
			else if ("width".equals(queryParamName))
				queryParamName = "w";

			imageSvcUrl.append("&")
						.append(queryParamName)
						.append("=")
						.append(entry.getValue());
		}

		return imageSvcUrl.toString();
	}

	public boolean useWebm(RequestContext requestContext) {
		UserAgent userAgent = requestContext.getUserAgent();
		if (userAgent != null) {
			String browserFamilyName = userAgent.getFamilyName();
			String osName = userAgent.getOsName();
			if ((StringUtils.isNotEmpty(browserFamilyName) && browserFamilyName.equals("Safari")) || (StringUtils.isNotEmpty(osName) && osName.contains("iOS"))) {
				return false;
			}
		}
		// by default return true since webm is widely supported and google crawlers can have a null userAgent
		return true;
	}

	public boolean useWebp(RequestContext requestContext) {
		// check the accept headers first, this will work for most browsers
		if (StringUtils.stripToEmpty(requestContext.getHeaders().getAccept()).contains("webp")) {
			return true;
		}

		UserAgent userAgent = requestContext.getUserAgent();
		if (userAgent != null) {
			String browserVersion = userAgent.getVersionMajor();
			String osName = userAgent.getOsName();
			// if device is iOS, then any browser with a version of 14 or higher (Safari, Firefox, Chromium) will be able to load a webp image
			if ((StringUtils.isNotEmpty(osName) && osName.contains("iOS")) && (StringUtils.isNotEmpty(browserVersion) && Integer.parseInt(browserVersion) >= 14)) {
				return true;
			}
		}
		// by default return false to prevent images breaking
		return false;
	}

	public boolean isNoFollow(RequestContext requestContext, String uri) {
		if (isNotBlank(uri)) {
			Set<String> safelist = Sets.newHashSetWithExpectedSize(1);
			safelist = curatedDomainService.getDomainsBySource(CuratedDomainService.SEO_SAFELIST, null);
			BaseDocumentEx doc = getDocument(requestContext);
			if (doc != null) {
				String subType = "NONE";
				String revenueGroup = doc.getRevenueGroup();
				if (StringUtils.isNotEmpty(revenueGroup)) {
					subType = revenueGroup;
				}
				safelist = Sets.union(safelist, curatedDomainService.getDomainsBySource(CuratedDomainService.REVGROUP_SAFELIST, subType));
			}
			if (safeListDomains != null) safelist = Sets.union(safelist, safeListDomains);

			return !isDomainInList(getDomain(uri), safelist);
		}
		return true;
	}

	/**
	 * Determines if a link is sponsored by checking the REVGROUP_SPONSOREDLIST and optionally the
	 * CAES_SPONSOREDLIST list if CAES is enabled
	 * @param requestContext The request context for the document
	 * @param uri The URI to get the domain from
	 * @return a boolean that indicates whether a link is sponsored
	 */
	public boolean isSponsored(RequestContext requestContext, String uri) {
		List<String> domainSources = new ArrayList<>(List.of(CuratedDomainService.REVGROUP_SPONSOREDLIST));
		if (isCaesEnabled(requestContext)) {
			domainSources.add(CuratedDomainService.CAES_SPONSOREDLIST);
		}
		return isRevenueDocAndUriInDomainSources(requestContext, uri, domainSources);
	}

	/**
	 * Determines if a link is in the CAES curated domain list if CAES is enabled
	 * @param requestContext The request context for the document
	 * @param uri The URI to get the domain from
	 * @return a boolean that indicates whether a link is in the CAES curated domain list
	 */
	public boolean isCaes(RequestContext requestContext, String uri) {
		return isCaesEnabled(requestContext) && isRevenueDocAndUriInDomainSources(requestContext, uri, List.of(CuratedDomainService.CAES_SPONSOREDLIST));
	}

	/**
	 * Returns whether Centralized Affiliate Ecommerce Service functionality is enabled. Includes requestContext
	 * so that it can be overridden (ex: by commerce) with custom logic since some verticals may have CAES
	 * enabled and others might not
	 */
	protected boolean isCaesEnabled(RequestContext requestContext) {
		return this.isCaesEnabled;
	}

	public boolean uncappedImageWidthsEnabled() {
		return uncappedImageWidthsEnabled;
	}

	private boolean isRevenueDocAndUriInDomainSources(RequestContext requestContext, String uri, List<String> domainSources) {
		if (isNotBlank(uri)) {
			if (curatedDomainService != null) {
				BaseDocumentEx doc = getDocument(requestContext);
				// Read provided domainSources lists for all type of revenue group documents
				if (doc != null && StringUtils.isNotEmpty(doc.getRevenueGroup())) {
					for (String domainSource : domainSources) {
						Set<String> sponsoredDomainlist = curatedDomainService.getDomainsBySource(domainSource, null);
						if (isDomainInList(getDomain(uri), sponsoredDomainlist)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean isDomainInList(String domain, Set<String> safelist) {
		if (domain == null) return false;
		if (safelist.contains(domain)) return true;
		return isDomainInList(domainTail(domain), safelist);
	}

	private String domainTail(String domain) {
		int dotIndex = domain.indexOf('.');
		if (dotIndex == -1) return null;
		return domain.substring(dotIndex + 1);
	}

	public String getContentGroup(int templateCode) {
		try {
			return UrlTypeV2.fromCode(templateCode).getContentGroup();
		} catch (Exception e) {
			return "Other";
		}
	}

	public Boolean itemLinkContains(List<ItemEx> list, String text) {
		if (isEmpty(text) || CollectionUtils.isEmpty(list)) return false;

		return list.stream().anyMatch(item -> item.getLink() != null && item.getLink().getUri() != null
				&& item.getLink().getUri().contains(text));
	}

	public boolean isInternalIp(String ip) {
		return ipDetectorUtils != null ? ipDetectorUtils.ipAddressExists(ip) : false;
	}

	public String getStaticPath() {
		return "/static/" + projectInfo.getVersion();
	}

	public String toJSONString(Map<String, Object> map) {
		return JSONObject.toJSONString(map);
	}

	public JSONObject fromJSONString(String string) {
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(string);
		} catch (ParseException e) {
			throw new GlobeException("failed to parse json string", e);
		}
	}

	public String base64encode(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
	}

	public String base64decode(String string) {
		byte[] bytes = Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Round a number to the nearest half, e.g. 4.28 -> 4.5, 2.55 -> 2.5, etc.
	 * @param n
	 * @return
	 */
	public Double nearestHalf(Double n) {
		return n == null ? null : Math.round(n * 2.0) / 2.0;
	}

	/**
	 * Round a number to the nearest tenth, e.g. 4.28 -> 4.3, 2.55 -> 2.6, etc.
	 * @param n
	 * @return
	 */
	public Double nearestTenth(Double n) {
		return n == null ? null : Math.round(n * 10.0) / 10.0;
	}

	/**
	 * Format a number according to the specified format.
	 * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DecimalFormat.html
	 * for how to specify the format.
	 * Examples:
	 *    fmt: "#.#", n: 2.3 -> 2.3
	 *    fmt: "#.#", n: 2 -> 2
	 *    fmt: "#.0", n: 2.3 -> 2.3
	 *    fmt: "#.0", n: 2 -> 2.0
	 * @param fmt
	 * @param n
	 * @return
	 */
	public String formatNumber(String fmt, Double n) {
		if (StringUtils.isBlank(fmt) || n == null) return null;
		return new DecimalFormat(fmt).format(n);
	}

    /**
     * Returns name of root element wrapping html
     * @param html
     * @return
     */
    public String getTag(String html) {
        if (html.startsWith("<")) {
        	int tagEnd = html.indexOf('>');
        	if (tagEnd > 0 && html.charAt(tagEnd-1) == '/') {
        		return null;
			}
            return html.substring(1, tagEnd);
        }
        return null;
    }

    /**
     * Removes wrapping element from html
     * @param html
     * @return
     */
    public String stripOuterTag(String html) {
        if (html.startsWith("<")) {

        	//get the end of the starting tag
            int startTagEnd = html.indexOf('>');
			if (startTagEnd > 0 && html.charAt(startTagEnd-1) == '/') {
				return html;
			}

			//get the start of the ending tag for substring's end index
			int endTagStart = html.lastIndexOf('<');
            return html.substring(startTagEnd + 1, endTagStart);
        }
        return html;
    }

	/**
	 * Chops the content as per given {@code htmlSliceConfig}. If {@code htmlSliceConfig} is null or empty then
	 * {@link com.about.mantle.htmlslicing.HtmlSlicerConfig#getDefaultConfig()} is used.
	 *
	 * @param content
	 * @param htmlSliceConfig
	 *
	 */
	public List<HtmlSlice> chopContent(String content, String htmlSliceConfig) {
		return chopContent(content, htmlSliceConfig, false);
	}

	/**
	 * Chops the content as per given {@code htmlSliceConfig}. If {@code htmlSliceConfig} is null or empty then
	 * {@link com.about.mantle.htmlslicing.HtmlSlicerConfig#getDefaultConfig()} is used. Further, it chops only first
	 * html slice into two parts as per {@link #chopBeforeLastTag(String)}
	 *
	 * @param content
	 * @param htmlSliceConfig
	 *
	 */
	public List<HtmlSlice> chopContentInline(String content, String htmlSliceConfig) {
		return chopContent(content, htmlSliceConfig, true);
	}

	private List<HtmlSlice> chopContent(String content, String htmlSliceConfig, boolean isInlineChop) {

		HtmlSlicerConfig htmlSlicerConfig = isEmpty(htmlSliceConfig) ? HtmlSlicerConfig.getDefaultConfig()
				: new HtmlSlicerConfig(htmlSliceConfig);
		// We have following cases here.
		// 1. For valid HTML content - We get two chopped blocks from htmlSlicer
		// 2. For valid HTML content - We get only one block back from htmlSlicer (e.g. no chopping's done if content is
		// of 200 chars and we asked htmlSlicer to chop on 500 chars)
		// 3. For plain text - We get two chopped blocks from htmlSlicer
		// 4. For plain text - We get only one block from htmlSlicer (no chopping just like case #2)
		List<HtmlSlice> choppedBlocks = htmlSlicer.applyFormatting(content, htmlSlicerConfig);
		// Next condition will yield false for case #2 and case #4. No chopping done from HtmlSlicer so don't bother
		// about further inline chopping.
		if (isInlineChop && choppedBlocks.size() > 1) {
			// For case #1, need to insert chop button just before first block's ending tag, chopBeforeLastTag
			// does that; however for case #3 chopBeforeLastTag function shouldn't chop the first block, because it
			// won't find html tags in plain text and whatever slices we got back from htmlSlicer should be returned as
			// it is.
			List<String> choppedContent = chopBeforeLastTag(choppedBlocks.get(0).getContent());
			if (choppedContent.size() == 2) {
				choppedBlocks.get(0).setContent(choppedContent.get(0));
				HtmlSlice lastTagsSlice = new HtmlSlice();
				String lastTags = choppedContent.get(1);
				lastTagsSlice.setContent(lastTags);
				lastTagsSlice.setCharacterCount(lastTags.length());
				lastTagsSlice.setWordCount(0);
				choppedBlocks.add(1, lastTagsSlice);
			}
		}
		return choppedBlocks;
	}

	//@formatter:off
	/**
	 * Looks for the last tag in the HTML content and splits content in to two parts.
	 * e.g. For string "<h1><ol><li>sometext</li></ol></h1>"
	 * will return list =>["<h1><ol><li>sometext", "</li></ol></h1>"]
	 * @param content
	 * @return List of strings containing two elements.
	 */
	//@formatter:on
	private List<String> chopBeforeLastTag(String content) {
		int index = content.lastIndexOf("</") - 1;
		while (index > 0) {
			char currentChar = content.charAt(index);
			if (currentChar == '>') {
				index = content.lastIndexOf("</", index) - 1;
			} else if (currentChar == ' ') {
				index--;
			} else {
				break;
			}
		}
		List<String> choppedStrings = new ArrayList<>();
		if (index > 0) {
			choppedStrings.add(content.substring(0, index + 1));
			choppedStrings.add(content.substring(index + 1));
		}

		return choppedStrings;
	}

	/**
	 * Gives back human readable time in hours and minutes from duration in milliseconds. It
	 * does not return 'Seconds' part. It will pluralize hours and minutes if
	 * applicable e.g. 1 hour, 2 hours etc. Any of minute(s)/hour(s) parts are 0
	 * then they will be skipped from final result. e.g. for '0 hours 5 minutes'
	 * final result will be '5 minutes'. Negative input will be ignored and empty
	 * string will be returned. NOTE: Please make sure you run Junit for this method
	 * in {@link MantleRenderUtilsTest} if you make changes.
	 *
	 * @param milliseconds
	 * @return human readable time
	 */
	public String convertToReadableTime(long milliseconds) {
		int seconds = (int) (milliseconds / 1000);
		int hours = (int) (seconds / 3600);
		int minutes = (int) ((seconds % 3600) / 60);
		StringBuilder answer = new StringBuilder();

		if (hours > 0) {
			answer.append(hours).append(" hour");
			if (hours > 1) {
				answer.append("s");
			}
			if (minutes > 0) {
				answer.append(" ");
			}
		}

		if (minutes > 0) {
			answer.append(minutes).append(" minute");
			if (minutes > 1) {
				answer.append("s");
			}
		}

		return answer.toString();
	}

	public String getCallToAction(String callToAction,String url) {

		String buttonAction;
		if(callToAction == null){
			callToAction = "Buy on";
		}

		if(callToAction.equalsIgnoreCase("Buy on") || callToAction.equalsIgnoreCase("See Rates On") || callToAction.equalsIgnoreCase("View On")){
			buttonAction = callToAction +" "+ StringUtils.capitalize(getRetailerName(url));
		}else{
			buttonAction = callToAction;
		}
		return buttonAction;
	}


	public boolean suppressEndComments(RequestContext requestContext) {
		String forceParam = requestContext.getParameterSingle("forceEndComments");
		return suppressEndComments && !"true".equals(forceParam);
	}

	private String resolveBaseScBlockName(AbstractStructuredContentContentEx<?> block) {
		// Component name defaults to block type
		String blockType = block.getType().toLowerCase();
		String componentName = "mntl-sc-block-" + blockType;

		// Handle component variations based on parent component
		if (block.getParentType() != null) {
			/* Instead of forcing a separate XML component for all nested blocks
			 * we limit it to just the ones that absolutely need it.
			 */
			if ("GALLERYSLIDE".equals(block.getParentType()) && "IMAGE".equals(block.getType())) {
				componentName = "mntl-sc-block-galleryslide-image";
			}
		}

		switch (blockType) {
			case "inlinevideo":
				String templateType = ((StructuredContentVideoBlockEx) block).getData().getDocument().getTemplateType().getDisplayName();
				if ("BrightcoveVideo".equals(templateType)) {
					componentName = "mntl-sc-block-inlinevideo--bc";
				}
                break;
		}

		return componentName;
	}

	/**
	 * Takes a structured content block and returns the ID of the component to
	 * render that block with. This function is final to ensure mantle can
	 * always force specific logic for new components. Verticals can override
	 * the resolveScBlockName method that takes in a componentName String
	 * parameter
	 * @param block
	 * @param context
	 * @return ID of component to render
	 */
	public final String resolveScBlockName(AbstractStructuredContentContentEx<?> block, RequestContext context) {
		String componentName = resolveBaseScBlockName(block);
		return resolveScBlockName(block, context, componentName);
	}

	// AMP is no longer supported
	@Deprecated
	public boolean isAmp(RequestContext context) {
		return false;
	}

	/**
	 * Allows a custom component to be substituted for the component selected by mantle
	 * to be rendered for the given block. Verticals overriding this function should
	 * use the given componentName, not block type, to base their decisions.
	 * @param block Block to select a component ID for
	 * @param context
	 * @param componentName ID of component selected by mantle to render
	 * @return ID of component to render
	 */
	protected String resolveScBlockName(AbstractStructuredContentContentEx block, RequestContext context, String componentName) {
		return componentName;
	}

	public String untrustedTrackingCodesBaseUrl() {
		return untrustedTrackingCodesBaseUrlStr;
	}

	/**
	 * Takes the manifest instance id and modifiers to the string to convert it from lower hyphen syntax to lower camel case
	 *
	 * @param input
	 * @return
	 */
	public String camelCaseInstanceId(String input) {
		return LOWER_HYPHEN.converterTo(LOWER_CAMEL).convert(input);
	}

	/**
	 * Takes the manifest instance id and splits out the instance number
	 *
	 * @param manifestId
	 * @return
	 */
	public String parseInstanceId(String manifestId) {
		return manifestId.split("_")[1].split("-")[0];
	}

	/**
	 * videoAspectRatio
	 *
	 * Takes in a height and width and determines the aspect ratio for the video
	 * If either the height or width are not parseable into an int, returns standard 16:9 aspect ratio
	 *
	 * @param height
	 * @param width
	 * @return
	 */
	public String videoAspectRatio(String height, String width){
		String aspectRatio;

		try{
			int heightNumeric = Integer.parseInt(height);
			int widthNumeric = Integer.parseInt(width);

			int factor =  IntMath.gcd(heightNumeric, widthNumeric);

			int widthRatio = widthNumeric / factor;
			int heightRatio = heightNumeric / factor;
			aspectRatio = widthRatio+":"+heightRatio;
		}catch (NumberFormatException E){
			aspectRatio = "16:9";
		}

		return aspectRatio;
    }

	/**
	 *
	 * Formats a String so that it can be used as a hash in a url
	 *
	 * @param text the text to format
	 * @return the hash-acceptable text
	 */
	public static String formatForHash(String text) {
		if (text == null) return "";

		String formattedText =  text.trim().toLowerCase();
		formattedText = dashPattern.matcher(formattedText).replaceAll("-");
		return alphaNumericPattern.matcher(formattedText).replaceAll("");
	}

	/**
	 *
	 * Formats a String from camel case to sentence case
	 *
	 * @param text the text to format
	 * @return the sentence case text
	 */
	public String formatCamelCaseToSentenceCase(String text) {
		return StringUtils.capitalize(String.join(" ", text.split("(?=\\p{Upper})")));
	}

	/**
	 *
	 * Formats a String from upper underscore case to sentence case
	 *
	 * @param text the text to format
	 * @return the sentence case text
	 */
	public String formatUpperUnderscoreCaseToSentenceCase(String text) {
		return formatCamelCaseToSentenceCase(UPPER_UNDERSCORE.converterTo(UPPER_CAMEL).convert(text));
	}

	/**
	 * Finds document in requestContext. This is exposed to allow mantle-ref to override and
	 * retrieve the mock document instead.
	 * @param requestContext
	 * @return
	 */
	protected BaseDocumentEx getDocument(RequestContext requestContext) {
		if (requestContext != null && requestContext.getModels().containsKey("DOCUMENT")) {
			return getModelValue(requestContext.getModel("DOCUMENT"), BaseDocumentEx.class);
		}
		return null;
	}

	protected <T> T getModelValue(ModelResult result, Class<T> bindToTarget) {
		if ((result != null) && (result.getValue() != null) && (bindToTarget.isInstance(result.getValue()))) {
			return bindToTarget.cast(result.getValue());
		}
		return null;
	}

	/**
	 * Pattern Library function
	 * Converts path of css resource to scss resource to allow pattern library to display original scss for
	 * component
	 * @param cssPath
	 * @return
	 */
	public String getScssPath(String cssPath) {
		return cssPath.replace("/static", "").replace(".css", ".scss");
	}

	// Intended for use from bio pages only. We are allowed to assume that bio pages will only have
	// one author listed in bylines, therefore getting the first item in the bylines list is safe
	public String fetchFirstBylineAuthorIdForDocument(BaseDocumentEx document) {
		if (document != null && !document.getBylines().getList().isEmpty()) {
			return document.getBylines().getList().get(0).getAuthorId();
		}
		return null;
	}

	/**
	 * Element wrapper used to expose elements from freemarker to ElementRewriter beans.
	 * The href property makes this specific to anchor tags but this could be made more generic.
	 * The rationale for storing href separately in the wrapper is to mirror the storage of variables
	 * in the anchor macro and avoid having to remove href from the map in freemarker.
	 */
	public static class RenderUtilsElementWrapper implements Element {
		private String tagName;
		private String href;
		private Map<String, String> attributes;

		public RenderUtilsElementWrapper(String tagName, String href, Map<String, String> attributes) {
			this.tagName = tagName;
			this.href = href;
			this.attributes = new HashMap<>(attributes);
		}

		public String getAttribute(String name) {
			if ("href".equals(name)) {
				return href;
			} else {
				return attributes.getOrDefault(name, null);
			}
		}

		public void setAttribute(String name, String value) {
			if ("href".equals(name)) {
				href = value;
			} else {
				attributes.put(name, value);
			}
		}

		public String getTagName() {
			return tagName;
		}

		// Functions to expose fields to freemarker
		public Map<String, String> getAttributes() {
			return attributes;
		}

		public String getHref() {
			return href;
		}
	}

    /**
    * A utility method to assist front-end in determining temporal strings to show
    * on recirc cards based on document last-published/updated date passed by
    * caller code
    */
    public String computeUpdatedDate(DateTime updatedDateEpochMilli, boolean useLongForm) {
        return computeUpdatedDate(updatedDateEpochMilli, useLongForm, "America/New_York");
    }

    public String computeUpdatedDate(DateTime updatedDateEpochMilli, boolean useLongForm, String timeZone) {
        Instant currentDate = Instant.now();
        Instant updatedDate = Instant.ofEpochMilli(updatedDateEpochMilli.getMillis());
        Duration duration = Duration.between(updatedDate, currentDate);
        long elapsedDays = duration.toDays();
        String answer = "";

        // Conservative check if upstream systems allow future instant or to show
        // Singular "day" literal when elapsed day is 1
        if (elapsedDays < 0 || elapsedDays == 1) {
            answer = "1 day ago";
        } else if (elapsedDays == 0) {
            answer = computeDateWhenElapsedDayIsZero(duration);
        } else if (elapsedDays > 7) {
            answer = formatDateToStringWithTimeZone(updatedDate, useLongForm, timeZone);
        } else {
            answer = elapsedDays + " days ago";
        }

        return answer;
    }

    private String computeDateWhenElapsedDayIsZero(Duration duration) {
        long elapsedHours = duration.toHours();
        long elapsedMinutes = duration.toMinutes();
        String answer = "";

        // Filter for illegal duration i.e. future instant in hours.
        if (elapsedHours < 0) {
            answer = "1 day ago";
            // for singular "hour" literal or when duration elapsed less than an hour e.g.
            // minutes
        } else if (elapsedHours < 1 && elapsedMinutes < 1) {
            answer = "a few seconds ago";
        } else if (elapsedHours < 1 && elapsedMinutes < 2) {
            answer = "1 minute ago";
        } else if (elapsedHours < 1) {
            answer = elapsedMinutes + " minutes ago";
        } else if (elapsedHours == 1) {
            answer = "1 hour ago";
        } else {
            answer = elapsedHours + " hours ago";
        }

        return answer;
    }

    private String formatDateToStringWithTimeZone(Instant date, Boolean useLongForm, String timeZone) {
        if (useLongForm) {
            return longFormDateTimeFormatter.withZone(ZoneId.of(timeZone)).format(date);
        }

        return shortFormDateTimeFormatter.withZone(ZoneId.of(timeZone)).format(date);
    }

	/**
	 * Returns the correct padding bottom to use for a video container el given a
	 * thumbnail url in the format below.
	 * This assumes that the thumbnail is the same ratio as the video.
	 * Required as long as "templateType": "BRIGHTCOVEVIDEO" has no accessible width
	 * + height information (https://dotdash.atlassian.net/browse/AXIS-3637).
	 * https://cf-images.us-east-1.prod.boltdns.net/v1/static/1125866826/7cb301c2-7cf4-4b8d-a82f-dabbbbe512b5/23b2fa53-2fdb-47a8-bf91-b350ea9e7b73/864x1080/match/image.jpg
	 *
	 * @param height the height of the video
	 * @param width  the width of the video
	 * @return padding bottom as a string
	 */
	public String getVideoContainerPaddingBottomFromUrl(String url) {
		Matcher heightMatcher = THUMBNAIL_URL_HEIGHT_PATTERN.matcher(url);
		Matcher widthMatcher = THUMBNAIL_URL_WIDTH_PATTERN.matcher(url);

		if (heightMatcher.matches() && widthMatcher.matches()) {
			try {
				double parsedHeight = Double.parseDouble(heightMatcher.group(1));
				double parsedWidth = Double.parseDouble(widthMatcher.group(1));
				double ratio = (parsedHeight / parsedWidth) * 100;

				return Double.valueOf(ratio).toString() + '%';
			} catch (NullPointerException | NumberFormatException | IllegalStateException e) {
				return SIXTEEN_NINE_RATIO;
			}
		} else {
			return SIXTEEN_NINE_RATIO;
		}
	}

	// This method deliberately leaves in nulls as we only want this to filter out max_bytes and
	// not to unexpectedly change the size of the array or have other consequences
	private static String[] filterOutMaxBytes(String[] filters) {
		return Arrays.stream(filters).filter(x -> x == null || !x.toLowerCase().startsWith("max_bytes(")).toArray(String[]::new);
	}
}
