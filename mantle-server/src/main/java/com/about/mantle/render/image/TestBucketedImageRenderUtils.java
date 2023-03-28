package com.about.mantle.render.image;

import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.testing.GlobeBucket;
import com.about.mantle.model.extended.docv2.ImageEx;

public class TestBucketedImageRenderUtils extends MantleImageRenderUtilsImpl {
	protected final Map<String, MantleImageRenderUtils> testToDelegateMapping;
	protected final MantleImageRenderUtils defaultDelegate;

	public TestBucketedImageRenderUtils(String imageServerDomain, double sizeDifferentialThreshold, Map<String, MantleImageRenderUtils> testToDelegateMapping, MantleImageRenderUtils defaultDelegate) {
		super(imageServerDomain, sizeDifferentialThreshold);
		this.testToDelegateMapping = testToDelegateMapping;
		this.defaultDelegate = defaultDelegate;
	}

	@Override
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters) {
		GlobeBucket globeBucket = requestContext.getTests().get("imageServer");
		if ((globeBucket != null) && (globeBucket.getName() != null) && (testToDelegateMapping.containsKey(globeBucket.getName()))) {
			testToDelegateMapping.get(globeBucket.getName()).getImage(image, type, fitInStyle, forceSize, requestContext, cropSetting, filters);
		}
		return defaultDelegate.getImage(image, type, fitInStyle, forceSize, requestContext, cropSetting, filters);
	}
}
