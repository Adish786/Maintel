package com.about.mantle.render.image;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.render.ImageResizer;
import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import com.squareup.pollexor.ThumborUrlBuilder.FitInStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public class PollexorImageResizer implements ImageResizer {
	private static final Logger logger = LoggerFactory.getLogger(PollexorImageResizer.class);

	private final GenericObjectPool<Mac> pool;
	private long lastPoolBorrowErrorMillis = 0;
	private final long poolBorrowErrorLogTimeout = 60 * 1000; // 1 minute
	private final Thumbor thumbor;
	private final String watermarkFilter;

	public PollexorImageResizer(String prefix, String key) {
		this(prefix, key, null, null);
	}

	public PollexorImageResizer(String prefix, String key, GenericObjectPool<Mac> pool, String watermarkFilter) {
		key = trimToNull(key);
		if (key == null) this.thumbor = Thumbor.create(prefix);
		else this.thumbor = Thumbor.create(prefix, key);
		this.pool = pool;
		this.watermarkFilter = watermarkFilter;
	}

	@Override
	public ImageEx resize(ImageEx image, ImageType type, String fitInStyle, RequestContext requestContext,
						  String cropSetting, String[] filters) {
		return resize(image, type, fitInStyle, requestContext, cropSetting, filters, false);
	}

	@Override
	public ImageEx resize(ImageEx image, ImageType type, String fitInStyle, RequestContext requestContext,
			String cropSetting, String[] filters, boolean addWatermark) {

		ImageEx resizedImage = new ImageEx(image);

		resizedImage.setWidth(type.getWidth());
		resizedImage.setHeight(type.getHeight());

		// Finalize url
		resizedImage.setUrl(resize(image.getObjectId(), type.getWidth(), type.getHeight(), fitInStyle,
				requestContext, cropSetting, filters, addWatermark));

		return resizedImage;
	}

	@Override
	public String resize(String objectId, int width, int height, String fitInStyle,
						 RequestContext requestContext, String cropSetting, String[] filters) {
		return resize(objectId, width, height, fitInStyle, requestContext, cropSetting, filters, false);
	}

	@Override
	public String resize(String objectId, int width, int height, String fitInStyle,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark) {

		if (StringUtils.isBlank(objectId)) {
		    throw new GlobeException("Cannot resize image - objectId was not provided");
		}

		ThumborUrlBuilder builder = thumbor.buildImage(objectId);

		builder.resize(width, height);

		applyFilters(filters, builder);

		if (cropSetting != null && cropSetting.equals("smart")) {
			builder.smart();
		}

		if (addWatermark && watermarkFilter != null) {
			applyFilters(new String[]{watermarkFilter}, builder);
		}

		if (isNotBlank(fitInStyle)) {
			builder.fitIn(FitInStyle.valueOf(fitInStyle));
		}

		/* Mac objects from pool will be passed into ThumborUrlBuilder's toUrl call to avoid creating a new Mac for
		 * each Thumbor url on the page.
		 * Workaround for JDK-7092821 (https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7092821)
		 */
		String resizedImageUrl = null;
		try {
			if (pool != null) {
				Mac mac = null;
				try {
					mac = pool.borrowObject();
					resizedImageUrl = builder.toUrl(mac);
				} catch (NoSuchElementException e) {
					/* Check for time of last pool borrow error is not thread safe as a worst case error is multiple
					 * log entries. Due to unlikelihood of pool exhaustion occurring, simplicity of error handling is
					 * being prioritized.
					 */
					long currentMillis = DateTime.now().getMillis();
					if (currentMillis - lastPoolBorrowErrorMillis > poolBorrowErrorLogTimeout) {
						logger.warn("Unable to retrieve pooled thumbor Mac. Defaulting to single-use Mac call.");
						lastPoolBorrowErrorMillis = currentMillis;
					}
					resizedImageUrl = builder.toUrl();
				} finally {
					if (mac != null) {
						pool.returnObject(mac);
					}
				}
			} else {
				resizedImageUrl = builder.toUrl();
			}
		} catch (Exception e) {
			throw new GlobeException("Error generating Thumbor URL", e);
		}

		return generateAbsoluteImageUrl(requestContext, resizedImageUrl);
	}

	/**
	 * Takes the resizedImageUrl from thumbor and generates an absolute url from the initial requests host
	 * or returns the resizedImageUrl if already absolute
	 * @param requestContext
	 * @param resizedImageUrl
	 * @return
	 */
	protected String generateAbsoluteImageUrl(RequestContext requestContext, String resizedImageUrl) {
		if (resizedImageUrl.startsWith("http")) {
			return resizedImageUrl;
		}

		StringBuilder host = new StringBuilder();
		host.append("http").append(requestContext.isSecure() ? "s" : "").append("://").append(requestContext.getServerName());
		if (requestContext.getServerPort() != 80 && requestContext.getServerPort() != 443) {
			host.append(':').append(requestContext.getServerPort());
		}
		return host.append(resizedImageUrl).toString();
	}

	private void applyFilters(String[] filters, ThumborUrlBuilder builder) {
		if (isNotEmpty(filters)) {
			// Ignore empty strings
			String[] validFilters = Arrays.stream(filters).filter(filter -> isNotBlank(filter)).toArray(String[]::new);

			if (isNotEmpty(validFilters)) builder.filter(validFilters);
		}
	}

	/**
	 * Factory for HmacSHA1 Mac objects used to create security hash in thumbor URLs.
	 */
	public static class HmacSHA1ObjectFactory extends BasePooledObjectFactory<Mac> {
		private String key;

		public HmacSHA1ObjectFactory(String key) {
			this.key = key;
		}

		@Override
		public Mac create() throws Exception {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(this.key.getBytes(), "HmacSHA1"));
			return mac;
		}

		@Override
		public PooledObject<Mac> wrap(Mac mac) {
			return new DefaultPooledObject<Mac>(mac);
		}
	}
}
