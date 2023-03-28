package com.about.mantle.render.image;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.render.image.CoreImageRenderUtilsImpl;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.render.image.ImageType.ImageSize;
import com.about.mantle.render.image.filter.ImageFilterBuilder;

public class MantleImageRenderUtilsImpl extends CoreImageRenderUtilsImpl implements MantleImageRenderUtils {
	private static final Pattern IMAGE_URL_MATCH_PATTERN = Pattern
			.compile("^(([^:]+)://(([^.]+)\\.tqn\\.com))((/[^/]/){1})(.*)?$");
	private static final Pattern IMAGE_URL_REWRITE_PATTERN = Pattern
			.compile("^http://[A-Za-z0-9]+\\.tqn\\.com/[a-z]/[^/]+/1/([0-9a-zA-Z_])/([0-9a-zA-Z_-]/){2,3}.+\\..{3,4}$");
	private static final Pattern DEFAULT_IMAGE_URL_PATTERN = Pattern
			.compile("^http://[A-Za-z0-9]+\\.tqn\\.com/[a-z]/default/[0-9a-zA-Z-]+/([0-9a-zA-Z])/.+\\..{3}$");

	protected static final String MINIMAL_IMG_URL = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mM88x8AAp0BzdNtlUkAAAAASUVORK5CYII=";
	protected static final String ENCODED_IMG_PREFIX= "data:image/gif;charset=utf-8;base64,";
	
	//This is meant to be a close enough guess for dimensioning a mantle generated blurry image See GLBE-7617
	protected static final int ARBITRARY_DIMENSION_FOR_MANTLE_GENERATED_ENCODED_IMAGE = 10; 
	private static final String[] blurryFilters = new String[] { ImageFilterBuilder.noUpscale(), ImageFilterBuilder.stripIcc() };

	public MantleImageRenderUtilsImpl(String imageServerDomain, double sizeDifferentialThreshold) {
		super(imageServerDomain, sizeDifferentialThreshold);
	}

	@Override
	public String rewriteImageUrl(String url, RequestContext requestContext) {
		Matcher imageUrlMatcher = IMAGE_URL_MATCH_PATTERN.matcher(url);

		if (!imageUrlMatcher.matches()) return url;

		StringBuilder sb = new StringBuilder(url.length() + 5);
		sb.append(imageUrlMatcher.group(2)).append(requestContext.getUrlData().isHttpSecure() ? "s://" : "://"); // ex.
																													// http://

		sb.append(getImageServerDomain(requestContext) + "/y/");
		if (imageUrlMatcher.group(7) != null) sb.append(imageUrlMatcher.group(7));
		return sb.toString();
	}

	@Override
	public ImageEx resizeImage(ImageEx image, ImageType type) {
		ImageSize size = type.getSize();

		ImageEx resizedImage = new ImageEx(image);
		resizedImage.setUrl(changeImageSizeinImageUrl(resizedImage.getUrl(), size));

		if (isImageLargeEnough(resizedImage, type)) {
			// TODO: Why do we have this logic, cant we just use the size that the image was resized to?
			double width = (double) resizedImage.getWidth();
			double height = (double) resizedImage.getHeight();

			double sizeWidth = (double) size.getWidth();
			double sizeHeight = (double) size.getHeight();

			if (size.getWidth() > 0 && size.getHeight() > 0) {
				resizedImage.setWidth(size.getWidth());
				resizedImage.setHeight(size.getHeight());
			} else if (sizeWidth > 0.0) {
				double delta = width / sizeWidth;
				resizedImage.setWidth((int) (width / delta));
				resizedImage.setHeight((int) (height / delta));
			} else if (sizeHeight > 0.0) {
				double delta = height / sizeHeight;
				resizedImage.setWidth((int) (width / delta));
				resizedImage.setHeight((int) (height / delta));
			}
		}

		return resizedImage;
	}

	@Override
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
							RequestContext requestContext, String cropSetting, String[] filters) {
		return getImage(image, type, fitInStyle, forceSize, requestContext, cropSetting, filters, false);
	}

	@Override
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark) {
		// If no image after defaulting just return as is
		if (image == null || isBlank(image.getUrl())) return image;

		if (forceSize) {
			return resizeImage(image, type);
		}

		// Image is large enough resize down to what's needed
		if (isImageLargeEnough(image, type)) {
			return resizeImage(image, type);
		}

		// Fall back to original or default image unsized
		return image;
	}

	@Override
	public String getImage(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
						   RequestContext requestContext, String cropSetting, String[] filters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getImage(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark) {
		throw new UnsupportedOperationException();
	}

	private String changeImageSizeinImageUrl(String url, ImageSize imageSize) {
		String newUrl = url;
		Matcher matcher = IMAGE_URL_REWRITE_PATTERN.matcher(url);
		Matcher defaultMatcher = DEFAULT_IMAGE_URL_PATTERN.matcher(url);
		if (matcher.matches() && (matcher.group(1) != null)) {
			MatchResult result = matcher.toMatchResult();
			StringBuilder builder = new StringBuilder();
			builder.append(url.substring(0, result.start(1))).append(imageSize.getDirectory())
					.append(url.substring(result.end(1)));
			newUrl = builder.toString();
		} else if (defaultMatcher.matches() && (defaultMatcher.group(1) != null)) {
			MatchResult result = defaultMatcher.toMatchResult();
			StringBuilder builder = new StringBuilder();
			builder.append(url.substring(0, result.start(1))).append(imageSize.getDirectory())
					.append(url.substring(result.end(1)));
			newUrl = builder.toString();
		}
		return newUrl;
	}

	@Override
	public String getPlaceholder(RequestContext requestContext) {
		return MINIMAL_IMG_URL;
	}

	@Override
	public String getBlurryPlaceholder(String blurryPlaceholder) {
		
		return ENCODED_IMG_PREFIX + blurryPlaceholder;
	}

	@Override
	public String generateBlurryPlaceholder(RequestContext requestContext, String imageUrl) {
		return getImage(imageUrl, ARBITRARY_DIMENSION_FOR_MANTLE_GENERATED_ENCODED_IMAGE, ARBITRARY_DIMENSION_FOR_MANTLE_GENERATED_ENCODED_IMAGE, "", true, requestContext, "", blurryFilters, false);
	}
}
