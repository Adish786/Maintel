package com.about.mantle.render.image;

import java.util.EnumMap;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.render.image.ImageDimension;
import com.google.common.collect.ImmutableMap;

public enum ImageType implements ImageDimension {

	FULL_SIZE(ImageSize.ORIGINAL_SIZE),
	CIRC_3COL(ImageSize.D85x56),
	CIRC_4COL(ImageSize.D130x86),
	CIRC_GRID(ImageSize.D215x130),
	SOCIAL_SHARE_SIZE(ImageSize.D215xM),
	DOCUMENTBODY_XSM(ImageSize.D215xM),
	DOCUMENTBODY_SM(ImageSize.D300xM),
	DOCUMENTBODY_MED(ImageSize.D385xM),
	DOCUMENT_LARGE(ImageSize.D640xM),
	DOCUMENT_LISTING(ImageSize.D215x143),
	LANDING_FEATURE(ImageSize.D676x437),
	LANDING_SUB_FEATURE(ImageSize.D215x216),
	LANDING_FEATURE2(ImageSize.D640x437),
	LANDING_SUB_FEATURE2(ImageSize.D318x212),
	CHANNEL_FEATURE(ImageSize.D400x400),
	SQUARE_ZOOMED_SM(ImageSize.D150x150),
	HERO_LARGE(ImageSize.D750xM),
	TILE_SM(ImageSize.D160xM),
	CC_DESKTOP(ImageSize.D1400x757),
	CC_TABLET(ImageSize.D1024x326),
	CC_MOBILE(ImageSize.D1024x326),
	CC(CC_DESKTOP, CC_TABLET, CC_MOBILE),
	CC_SOCIAL(ImageSize.D1200x630),
	MAP_CARD(ImageSize.D380x253);

	private final ImageSize size;
	private final EnumMap<DeviceCategory, ImageType> deviceToType;

	private ImageType(ImageSize size) {
		this.size = size;
		this.deviceToType = null;
	}

	private ImageType(ImageType typeDesktop, ImageType typeTablet, ImageType typeMobile) {
		this.size = null;

		this.deviceToType = new EnumMap<>(ImmutableMap.of(DeviceCategory.PERSONAL_COMPUTER, typeDesktop,
				DeviceCategory.TABLET, typeTablet, DeviceCategory.SMARTPHONE, typeMobile));
	}

	public ImageSize getSize() {
		return size;
	}

	public ImageType resolve(RequestContext requestContext) {

		if (deviceToType == null) return this;

		return deviceToType.get(requestContext.getUserAgent().getDeviceCategory());
	}

	@Override
	public Integer getWidth() {
		return size.getWidth();
	}

	@Override
	public Integer getHeight() {
		return size.getHeight();
	}
	
	public enum ImageSize {
		ORIGINAL_SIZE('0', 0, 0), //1
		D75x75('1', 75, 75),
		D250x170('2', 250, 170),
		D170x170('3', 170, 170),
		D250x250('4', 250, 250), //5
		D400x400('5', 400, 400),
		D120x120('6', 120, 120),
		D500x500('7', 500, 500),
		D200x200('8', 200, 200),
		D150x150('9', 150, 150), //10
		D80x60('A', 80, 60),
		D320x240('B', 320, 240),
		D84x84('C', 84, 84),
		D124x93('D', 124, 93),
		D126x84('E', 126, 84), //15
		D120xM('F', 120, 0),
		D160xM('G', 160, 0),
		D300x300('H', 300, 300),
		D210x210('I', 210, 210),
		D130x127('K', 130, 127), //20
		D99xM('J', 99, 0),
		D300xM('L', 300, 0),
		D450xM('M', 450, 0),
		D750xM('N', 750, 0),
		D85x56('O', 85, 56), //25
		D130x86('P', 130, 86),
		D215x130('Q', 215, 130),
		D215xM('R', 215, 0),
		D640xM('S', 640, 0),
		D676x437('T', 676, 437), //30
		D215x216('U', 215, 216),
		D215x143('V', 215, 143),
		D385xM('W', 385, 0),
		D640x437('X', 640, 437),
		D318x212('Y', 318, 212),//35
		D1400x757('_',1400,757),
		D1024x326('a',1024,326),
		D1200x630('b',1200,630),
		D380x253('c',380,253);
		
		private char directory;
		private Integer width;
		private Integer height;
		
		private ImageSize(char directory, Integer width, Integer height) {
			this.directory = directory;
			this.width = width;
			this.height = height;
		}
		
		public char getDirectory() {
			return directory;
		}

		public Integer getWidth() {
			return width;
		}

		public Integer getHeight() {
			return height;
		}
	}

}
