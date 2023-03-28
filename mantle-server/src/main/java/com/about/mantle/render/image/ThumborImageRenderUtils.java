package com.about.mantle.render.image;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.render.ImageResizer;

public class ThumborImageRenderUtils extends MantleImageRenderUtilsImpl {
	private final ImageResizer imageResizer;

	public ThumborImageRenderUtils(String imageServerDomain, double sizeDifferentialThreshold,
			ImageResizer imageResizer) {
		super(imageServerDomain, sizeDifferentialThreshold);
		this.imageResizer = imageResizer;
	}

	@Override
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark) {
		// If image has object id then use new image resizer
		if (isNotBlank(image.getObjectId())) {
			return imageResizer.resize(image, type, fitInStyle, requestContext, cropSetting, filters, addWatermark);
		}
		return super.getImage(image, type, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark);
	}

	@Override
	public String getImage(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark) {
		return imageResizer.resize(imageUrl, width, height, fitInStyle, requestContext, cropSetting, filters, addWatermark);
	}
}
