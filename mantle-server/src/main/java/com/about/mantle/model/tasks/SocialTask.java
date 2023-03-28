package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static com.about.mantle.model.services.SocialLinkService.SocialNetwork.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.about.mantle.model.services.VerticalConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.model.extended.Configs;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.services.SocialLinkService;
import com.about.mantle.model.services.SocialLinkService.SocialLinkResponse;
import com.about.mantle.model.services.SocialLinkService.SocialNetwork;
import com.about.mantle.model.social.NetworkShare;
import com.about.mantle.model.social.NetworkShare.Network;
import com.about.mantle.model.transformers.SocialLinkTransformer;
import com.about.mantle.render.MantleRenderUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@Tasks
public class SocialTask {

	private final String FACEBOOK_APP_ID_KEY = "facebookAppId";
	private final String SHARE_ORDER_PC_TABLET = "shareOrder_pc_tablet";
	private final String SHARE_ORDER_MOBILE = "shareOrder_mobile";

	private static final Logger logger = LoggerFactory.getLogger(SocialTask.class);

	private final SocialLinkService socialLinkService;
	private final MantleRenderUtils renderUtils;
	private final VerticalConfigService verticalConfigService;
	private final String vertical;
	private final String facebookAppIdValue;

	@Deprecated
	public SocialTask(SocialLinkService socialLinkService, MantleRenderUtils renderUtils) {
		this(socialLinkService, renderUtils, null, null);
	}

	public SocialTask(SocialLinkService socialLinkService, MantleRenderUtils renderUtils,
					  VerticalConfigService verticalConfigService, String vertical) {
		this.socialLinkService = socialLinkService;
		this.renderUtils = renderUtils;
		this.verticalConfigService = verticalConfigService;
		this.vertical = vertical;
		this.facebookAppIdValue = initializeFacebookAppIdForMetaTags();
	}

	@Task(name = "facebookAppIdForMetaTags")
	public String getFacebookAppIdForMetaTags(@RequestContextTaskParameter RequestContext requestContext) {
		return facebookAppIdValue;
	}

	@Task(name = "documentShareSocialLinks")
	public Map<String, String> getDocumentShareSocialLinks(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		// TODO: must use new selene social service
		// return document.getSocialLinksMap(requestContext, renderUtils);
		return getEmptySocialLinks(requestContext);
	}

	@Task(name = "aboutFollowSocialLinks")
	public Map<String, String> getAboutFollowSocialLinks(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "forceDefaultAccounts") Boolean forceDefaultAccounts) {

		Configs configs = requestContext.getConfigs();
		Map<String, String> socialLinks = new HashMap<>();

		String facebook = defaultIfBlank(configs.getValue("followAboutFacebook", String.class), "aboutdotcom");
		String twitter = defaultIfBlank(configs.getValue("followAboutTwitter", String.class), "aboutdotcom");
		String pinterest = defaultIfBlank(configs.getValue("followAboutPinterest", String.class), "aboutdotcom");

		if (forceDefaultAccounts) {
			facebook = "aboutdotcom";
			twitter = "aboutdotcom";
			pinterest = "aboutdotcom";
		}

		socialLinks.put("FACEBOOK", "https://www.facebook.com/" + facebook);
		socialLinks.put("TWITTER", "https://twitter.com/" + twitter);
		socialLinks.put("PINTEREST", "https://www.pinterest.com/" + pinterest + "/");

		return socialLinks;
	}

	@Task(name = "authorFollowSocialLinks")
	public Map<String, String> getAboutFollowSocialLinks(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "rss") String rss, @TaskParameter(name = "author") AuthorEx author) {

		ImmutableMap.Builder<String, String> socialLinks = ImmutableMap.builder();
		socialLinks.putAll(author.getSocialPresenceMap());

		if (isNotBlank(rss)) {
			socialLinks.put("RSS", rss);
		}

		return socialLinks.build();
	}

	@Task(name = "emptySocialLinks")
	public Map<String, String> getEmptySocialLinks(@RequestContextTaskParameter RequestContext requestContext) {
		return ImmutableMap.of();
	}
	
	/**
	 * if social network needs an image then this task will get it from Proteus images accordingly   
	 */
	@Task(name = "socialLink")
	public Map<String, String> getSocialLink(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "network") String networks, @TaskParameter(name = "title") String title, 
			@TaskParameter(name = "document") BaseDocumentEx document) {
		
		String resolvedDefaultImageUrl = getResolvedImgUrl(requestContext, document.getImageForUsage("PRIMARY"));
		
		Set<String> socialNetworks = Sets
				.newLinkedHashSet(Splitter.on(',').trimResults().omitEmptyStrings().split(networks));
		socialNetworks.removeIf(s -> !EnumUtils.isValidEnum(SocialNetwork.class, s));
		
		Map<String, String> socialMap = new LinkedHashMap<String, String>();
		socialNetworks.forEach(s ->  {
			String resolvedSocialImgUrl = getResolvedImgUrl(requestContext, document.getImageForUsage(s));
			resolvedSocialImgUrl = StringUtils.isNotBlank(resolvedSocialImgUrl) ? resolvedSocialImgUrl : resolvedDefaultImageUrl;
			socialMap.put(s, getSocialLink(requestContext, SocialNetwork.valueOf(s), document.getUrl(), title, resolvedSocialImgUrl));
			});
		
		return socialMap;
	}

	/**
	 * This method gives the resolved thumbor url for Hero Large image dimensions (750x0).
	 * We use that for social images. 
	 */
	private String getResolvedImgUrl(RequestContext requestContext, ImageEx image) {
		String answer = "";
		if( image != null ) {
			String imgUrl = image.getObjectId() != null ? image.getObjectId() : image.getUrl();
			if ( StringUtils.isNotBlank(imgUrl) ){
				answer = renderUtils.getThumborUrl(image, 750, 0, null, false, requestContext, null, null);
			}
		}
		return answer;
	}

	@Task(name = "socialNetworkShare")
	public NetworkShare getNetworkShare(@RequestContextTaskParameter RequestContext requestContext) {
		return getNetworkShare(requestContext, requestContext.getUserAgent().getDeviceCategory());
	}

	// For device override
	@Task(name = "socialNetworkShare")
	public NetworkShare getNetworkShare(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "deviceCategory") DeviceCategory deviceCategory) {

		NetworkShare networkShare = new NetworkShare();

		// Determine device to pull correct taxCon key
		String taxconKey = "";
		switch (deviceCategory) {
		case PERSONAL_COMPUTER:
		case TABLET:
			taxconKey = SHARE_ORDER_PC_TABLET;
			break;
		case SMARTPHONE:
			taxconKey = SHARE_ORDER_MOBILE;
			break;
		default:
			taxconKey = SHARE_ORDER_PC_TABLET;
		}
		@SuppressWarnings("unchecked")
		List<List<String>> shareOrder = requestContext.getConfigs().getValue(taxconKey, List.class);

		// Process networks
		Map<String, String> messages = requestContext.getMessages();
		networkShare.setVisible(processNetworks(shareOrder.get(0), messages));
		if (shareOrder.size() > 1) {
			networkShare.setExtended(processNetworks(shareOrder.get(1), messages));
		}

		return networkShare;
	}

	// For setting networks explicitly (visible and extended)
	@Task(name = "socialNetworkShare")
	public NetworkShare getNetworkShare(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "visible") String visible, @TaskParameter(name = "extended") String extended) {
		NetworkShare networkShare = new NetworkShare();
		Map<String, String> messages = requestContext.getMessages();

		networkShare.setVisible(processNetworks(Arrays.asList(split(visible, ',')), messages));
		networkShare.setExtended(processNetworks(Arrays.asList(split(extended, ',')), messages));

		return networkShare;
	}

	// For setting networks explicitly (visible only)
	@Task(name = "socialNetworkShare")
	public NetworkShare getNetworkShareVisibleOnly(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "visible") String visible) {
		NetworkShare networkShare = new NetworkShare();
		Map<String, String> messages = requestContext.getMessages();

		networkShare.setVisible(processNetworks(Arrays.asList(split(visible, ',')), messages));

		return networkShare;
	}

	private List<Network> processNetworks(List<String> t, Map<String, String> messages) {
		List<Network> result = new ArrayList<>();

		t.forEach((s) -> {
			String longShare = messages.getOrDefault("longShare_" + s, "");
			String shortShare = messages.getOrDefault("shortShare_" + s, "");
			try {
				Network n = new Network(SocialNetwork.valueOf(s), longShare, shortShare);
				result.add(n);
			} catch (IllegalArgumentException e) {

				logger.error("Unsupported social network: " + s);
			}
		});

		return result;
	}

	protected String getSocialLink(RequestContext requestContext, SocialNetwork network, String url, String title,
			String imgUrl) {
		SocialLinkResponse slr = socialLinkService.getSocialLinks(url, network,
					com.about.mantle.model.services.SocialLinkService.DeviceCategory
							.valueOf(requestContext.getUserAgent().getDeviceCategory().name()));
		if (slr != null && CollectionUtils.isNotEmpty(slr.getData())) {

			Map<String, String> transformedUrl = SocialLinkTransformer.transformSocialLinks(slr.getData(), title,
					imgUrl, requestContext.getUserAgent().getDeviceCategory());
			return transformedUrl.get(network.name());
		}
		return null;

	}
	

	public static String getSocialNetwork(String socialPresence) {
		switch (socialPresence) {
		case "facebook":
			return FACEBOOK.name();
		case "twitter":
			return TWITTER.name();
		case "linkedin":
			return LINKEDIN.name();
		case "pinterest":
			return PINTEREST.name();
		case "instagram":
			return INSTAGRAM.name();
		case "website":
			return WEBSITE.name();
		default:
			return null;
		}
	}

	private String initializeFacebookAppIdForMetaTags() {
		if (verticalConfigService != null && vertical != null) {
			Map<String, ?> verticalConfig = verticalConfigService.getVerticalConfig(vertical);
			if (verticalConfig != null
					&& verticalConfig.get(FACEBOOK_APP_ID_KEY) != null
					&& StringUtils.isNotBlank((String) verticalConfig.get(FACEBOOK_APP_ID_KEY))) {
				return (String) verticalConfig.get(FACEBOOK_APP_ID_KEY);
			}
		}
		// This is an old app ID that was configured to be used by about.com only. No one has access
		// to this account anymore. We return it here because an empty/null app ID value in the meta tags is
		// not valid according to facebook. When it comes to meta tags specifically, Facebook at this
		// point in time doesn't care about the actual value of the app ID.
		// See AXIS-3203 for more details.
		// Do not use this for purposes other than meta tags without testing it first!
		return "121030274606741";
	}

}
