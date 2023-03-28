package com.about.mantle.render;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.render.image.ImageType;

public interface ImageResizer {

	/**
	 * Resize {@link ImageEx} to size specified by {@link ImageType}. The supplied url and {@link RequestContext} can be
	 * used as the context for resizing. They can be used to drive cropping, filtering and other settings for the
	 * resize.
	 *
	 * @param image
	 *            {@link ImageEx} to resize.
	 * @param type
	 *            {@link ImageType} indicating desired size of new {@link ImageEx}.
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height.
	 * @param requestContext
	 *            {@link RequestContext} which can be used to drive resize settings.
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used.
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied to the image.
	 *
	 * @return New {@link ImageEx} object with resizing applied.
	 * @throws NullPointerException
	 *             if image is null.
	 */
	ImageEx resize(ImageEx image, ImageType type, String fitInStyle, RequestContext requestContext,
			String cropSetting, String[] filters);

	/**
	 * Resize {@link ImageEx} to size specified by {@link ImageType}. The supplied url and {@link RequestContext} can be
	 * used as the context for resizing. They can be used to drive cropping, filtering and other settings for the
	 * resize.
	 *
	 * @param image
	 *            {@link ImageEx} to resize.
	 * @param type
	 *            {@link ImageType} indicating desired size of new {@link ImageEx}.
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height.
	 * @param requestContext
	 *            {@link RequestContext} which can be used to drive resize settings.
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used.
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied to the image.
	 * @param addWatermark
	 * 			  Whether image should be watermarked
	 *
	 * @return New {@link ImageEx} object with resizing applied.
	 * @throws NullPointerException
	 *             if image is null.
	 */
	ImageEx resize(ImageEx image, ImageType type, String fitInStyle, RequestContext requestContext,
				   String cropSetting, String[] filters, boolean addWatermark);

	/**
	 * Return resized image url to size specified by width and height. The supplied url and {@link RequestContext} can
	 * be used as the context for resizing. They can be used to drive cropping, filtering and other settings for the
	 * resize.
	 *
	 * @param objectId
	 *            Id of original image.
	 * @param width
	 *            Desired width of resized image.
	 * @param height
	 *            Desired height of resized image.
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height.
	 * @param requestContext
	 *            {@link RequestContext} which can be used to drive resize settings.
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used.
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied to the image.
	 *
	 * @return String of resized image url.
	 */
	String resize(String objectId, int width, int height, String fitInStyle, RequestContext requestContext,
			String cropSetting, String[] filters);

	/**
	 * Return resized image url to size specified by width and height. The supplied url and {@link RequestContext} can
	 * be used as the context for resizing. They can be used to drive cropping, filtering and other settings for the
	 * resize.
	 *
	 * @param objectId
	 *            Id of original image.
	 * @param width
	 *            Desired width of resized image.
	 * @param height
	 *            Desired height of resized image.
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height.
	 * @param requestContext
	 *            {@link RequestContext} which can be used to drive resize settings.
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used.
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied to the image.
	 * @param addWatermark
	 * 			  Whether image should be watermarked
	 *
	 * @return String of resized image url.
	 */
	String resize(String objectId, int width, int height, String fitInStyle, RequestContext requestContext,
				  String cropSetting, String[] filters, boolean addWatermark);
}
