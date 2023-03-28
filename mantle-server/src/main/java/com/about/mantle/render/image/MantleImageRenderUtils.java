package com.about.mantle.render.image;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.render.image.CoreImageRenderUtils;
import com.about.mantle.model.extended.docv2.ImageEx;

public interface MantleImageRenderUtils extends CoreImageRenderUtils {
	public ImageEx resizeImage(ImageEx image, ImageType type);

	/**
	 * Returns an image object that corresponds to the requested parameters, if image is empty/null a default image is
	 * chosen based on the channel.
	 *
	 * @param image
	 *            the image model that is to be examined and manipulated or substituted with a default
	 * @param type
	 *            the image type that is needed
	 * @param forceSize
	 *            whether or not to forceImage to the requested size
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height
	 * @param requestContext
	 *            Context for current request
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied on the image
	 * @return
	 */
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters);

	/**
	 * Returns an image object that corresponds to the requested parameters, if image is empty/null a default image is
	 * chosen based on the channel.
	 *
	 * @param image
	 *            the image model that is to be examined and manipulated or substituted with a default
	 * @param type
	 *            the image type that is needed
	 * @param forceSize
	 *            whether or not to forceImage to the requested size
	 * @param fitInStyle
	 *            String representation of ThumborUrlBuilder FitInStyle Enum, if and how image should fit in to defined
	 *            width and height
	 * @param requestContext
	 *            Context for current request
	 * @param cropSetting
	 *            If string is equal to <strong>"smart"</strong> then smart cropping for determining the important
	 *            portion of an image will be used
	 * @param filters
	 *            Array of strings which contains the filters that need to be applied on the image
	 * @param addWatermark
	 * 			  Whether image should be watermarked
	 * @return
	 */
	public ImageEx getImage(ImageEx image, ImageType type, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark);

	public String getImage(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
		    RequestContext requestContext, String cropSetting, String[] filters);

	public String getImage(String imageUrl, int width, int height, String fitInStyle, Boolean forceSize,
			RequestContext requestContext, String cropSetting, String[] filters, boolean addWatermark);

	public String getPlaceholder(RequestContext requestContext);

	/**
	 * Gets a blurry place holder from a thumbnail image from selene data
	 * 
	 * @param blurryPlaceholder 
	 * 			Encoded Thumbnail to be displayed
	 */
	public String getBlurryPlaceholder(String blurryPlaceholder);
	
	/**
	 * Generates a blurry place holder from a thumbnail image 
	 * 
	 * @param blurryPlaceholder 
	 * 			Encoded Thumbnail to be displayed
	 * @param imageUrl 
	 * 			Image Url used to get the file extension for displaying the encoded thumbnail. 
	 */
	public String generateBlurryPlaceholder(RequestContext requestContext, String imageUrl);
}
