package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.Serializable;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;

public class ImageRenditionsEx extends ResolvableImageEx implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final ImageRenditionsEx EMPTY = new ImageRenditionsEx() {

		private static final long serialVersionUID = 1L;

		@Override
		public void setDesktop(ImageEx desktop) {
		}

		@Override
		public void setTablet(ImageEx tablet) {
		}

		@Override
		public void setMobile(ImageEx mobile) {
		}
	};

	private ImageEx desktop;
	private ImageEx tablet;
	private ImageEx mobile;

	public ImageEx getDesktop() {
		return desktop;
	}

	public void setDesktop(ImageEx desktop) {
		this.desktop = desktop;
	}

	public ImageEx getTablet() {
		return tablet;
	}

	public void setTablet(ImageEx tablet) {
		this.tablet = tablet;
	}

	public ImageEx getMobile() {
		return mobile;
	}

	public void setMobile(ImageEx mobile) {
		this.mobile = mobile;
	}

	public static ImageRenditionsEx emptyIfNull(ImageRenditionsEx image) {
		return image != null ? image : EMPTY;
	}
	
	@Override
	public ImageEx resolve(RequestContext requestContext) {

		DeviceCategory deviceCategory = requestContext.getUserAgent().getDeviceCategory();

		ImageEx image = null;

		switch (deviceCategory) {
		case SMARTPHONE:
			image = getMobile();
			if ((image != null) && isNotEmpty(image.getUrl())) return image;
			break;
		case TABLET:
			image = getTablet();
			if ((image != null) && isNotEmpty(image.getUrl())) return image;
			break;
		case PERSONAL_COMPUTER:
			image = getDesktop();
			if ((image != null) && isNotEmpty(image.getUrl())) return image;
			break;
		}

		return ImageEx.EMPTY;
	}

}
