package com.about.mantle.model.transformers;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.ua.DeviceCategory;
import com.about.mantle.model.services.SocialLinkService.SocialLink;
import com.about.mantle.model.services.SocialLinkService.SocialNetwork;
import com.google.common.collect.ImmutableMap;

public class SocialLinkTransformer {

	private static final Logger logger = LoggerFactory.getLogger(SocialLinkTransformer.class);

	private static final String URL_ENCODING_SCHEME = "UTF-8";

	public static Map<String, String> transformSocialLinks(List<SocialLink> socialLinks, String title, String imgUrl,
			DeviceCategory device) {
		if (isEmpty(socialLinks)) return ImmutableMap.of();

		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

		for (SocialLink socialLink : socialLinks) {
			if (!sameDevice(device, socialLink.getDevice())) continue;

			String socialUrl = createSocialUrl(socialLink.getUrl(), socialLink.getNetwork(), title, imgUrl);
			if (socialUrl != null) {
				builder.put(socialLink.getNetwork().name(), socialUrl);
			}
		}

		// Default table to same as pc if not present
		if (device == DeviceCategory.TABLET && builder.build().isEmpty()) {
			return transformSocialLinks(socialLinks, title, imgUrl, DeviceCategory.PERSONAL_COMPUTER);
		}

		return builder.build();
	}

	private static boolean sameDevice(DeviceCategory device,
			com.about.mantle.model.services.SocialLinkService.DeviceCategory seleneDevice) {
		if (device == null || seleneDevice == null) return false;
		return device.name().equals(seleneDevice.name());
	}

	@SuppressWarnings("incomplete-switch")
	private static String getTitleParameter(SocialNetwork socialNetwork) {
		switch (socialNetwork) {
		case TWITTER:
			return "text";
		case PINTEREST:
			return "description";
		case REDDIT:
		case FLIPBOARD:
			return "title";
		default:
			return null;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static String getImageParameter(SocialNetwork socialNetwork) {
		switch (socialNetwork) {
		case PINTEREST:
			return "media";
		default:
			return null;
		}
	}

	private static String createSocialUrl(String socialUrl, SocialNetwork network, String title, String imgUrl) {

		if (isBlank(socialUrl) || network == null) return null;

		StringBuilder urlBuilder = new StringBuilder(socialUrl);

		try {
			// add title
			if (isNotBlank(title)) {
				String titleParameter = getTitleParameter(network);
				if (titleParameter != null) {
					urlBuilder.append('&').append(titleParameter).append('=')
							.append(URLEncoder.encode(StringEscapeUtils.unescapeHtml4(title), URL_ENCODING_SCHEME));
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Error encoding '" + title + "' using scheme " + URL_ENCODING_SCHEME, e);
			return null;
		}

		try {
			// add image
			if (isNotBlank(imgUrl)) {
				String imageParameter = getImageParameter(network);
				if (imageParameter != null) {
					urlBuilder.append('&').append(imageParameter).append('=')
							.append(URLEncoder.encode(imgUrl, URL_ENCODING_SCHEME));
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Error encoding '" + imgUrl + "' using scheme " + URL_ENCODING_SCHEME, e);
			return null;
		}
		
		if(SocialNetwork.FLIPBOARD == network) {
			urlBuilder.append("&t=").append(Calendar.getInstance().getTimeInMillis());
		}

		return urlBuilder.toString();
	}
}
