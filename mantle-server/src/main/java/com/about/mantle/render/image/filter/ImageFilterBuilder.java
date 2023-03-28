package com.about.mantle.render.image.filter;

import org.apache.commons.lang3.StringUtils;

import com.squareup.pollexor.ThumborUrlBuilder;

/**
 * This class is responsible for creating valid thumbor filters. Most of the used filters are provided by Pollexor's
 * {@link ThumborUrlBuilder} builder class.
 */
public final class ImageFilterBuilder {

	private ImageFilterBuilder() {
	}

	/**
	 * This filter changes the image to grayscale.
	 * 
	 * @return a new string object with the specified filter value.
	 */
	public static String grayscale() {
		return ThumborUrlBuilder.grayscale();
	}

	/**
	 * This filter increases or decreases the image brightness.
	 *
	 * @param amount
	 *            -100 to 100 - The amount (in %) to change the image brightness. Positive numbers make the image
	 *            brighter and negative numbers make the image darker.
	 * 
	 * @return a new string object with the specified filter value.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code amount} outside bounds.
	 */
	public static String brightness(int amount) {
		return ThumborUrlBuilder.brightness(amount);
	}

	/**
	 * The filter increases or decreases the image contrast.
	 *
	 * @param amount
	 *            -100 to 100 - The amount (in %) to change the image contrast. Positive numbers increase contrast and
	 *            negative numbers decrease contrast.
	 * 
	 * @return a new string object with the specified filter value.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code amount} outside bounds.
	 * 
	 */
	public static String contrast(int amount) {
		return ThumborUrlBuilder.contrast(amount);
	}

	/**
	 * The filter defines a focal point on the image.
	 *
	 * @param x
	 * @param y
	 *
	 * @return a new string object with the specified filter value.
	 *
	 */
	public static String focalPoint(int x, int y) {
		StringBuilder sb = new StringBuilder(40);
		//@formatter:off
		sb.append("focal(").append(x-1).append("x").append(y-1)
				.append(":")
				.append(x+1).append("x").append(y+1)
				.append(")");
		//@formatter:on
		return sb.toString();
	}

	/**
	 * This filter changes the amount of color in each of the three channels.
	 *
	 * @param red
	 *            The amount of redness in the picture. Can range from -100 to 100 in percentage.
	 * @param green
	 *            The amount of greenness in the picture. Can range from -100 to 100 in percentage.
	 * @param blue
	 *            The amount of blueness in the picture. Can range from -100 to 100 in percentage.
	 * 
	 * @return a new string object with the specified filter value.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code red}, {@code green}, or {@code blue} are outside of bounds.
	 */
	public static String rgb(int red, int green, int blue) {
		return ThumborUrlBuilder.rgb(red, green, blue);
	}

	/**
	 * This filter tells thumbor not to upscale the images.
	 * 
	 * @return a new string object with the specified filter value.
	 */
	public static String noUpscale() {
		return ThumborUrlBuilder.noUpscale();
	}

	/**
	 * This filter tells thumbor to strip the ICC profile from the image.
	 *
	 * @return a new string object with the specified filter value.
	 */
	public static String stripIcc() {
		return ThumborUrlBuilder.stripicc();
	}

	/**
	 * This filter scales the color saturation of an image based on the given percentage.
	 * 
	 * @param percentage
	 *            Expects a decimal percentage, probably between 0.0 and 2.0. A value of 0.0 yields a grayscale image,
	 *            1.0 the original image, and 2.0 an image twice as saturated.
	 * 
	 * @return a new string object with the specified filter value.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code percentage} is outside of bounds.
	 */
	public static String saturation(double percentage) {
		if (percentage < 0.0 || percentage > 2.0) {
			throw new IllegalArgumentException("Percentage value must be between 0.0 and 2.0, inclusive.");
		}
		return "saturation(" + percentage + ")";
	}

	/**
	 * This filter blends the fill color with the image pixels.
	 *
	 * @param red
	 *            The amount of redness in the picture. Can range from -100 to 100 in percentage.
	 * @param green
	 *            The amount of greenness in the picture. Can range from -100 to 100 in percentage.
	 * @param blue
	 *            The amount of blueness in the picture. Can range from -100 to 100 in percentage.
	 * @param color
	 *            A 6 digit HEX value for the fill color.
	 * 
	 * @return a new string object with the specified filter value.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code red}, {@code green}, or {@code blue} are outside of bounds or {@code color} is not a valid
	 *             HEX value.
	 */
	public static String colorize(int red, int green, int blue, String color) {
		StringBuilder sb = new StringBuilder(40);
		try {
			Integer.parseInt(color, 16);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Color mush be a valid Hex value");
		}
		if (!isValidAmount(red)) {
			throw new IllegalArgumentException("Red value must be between -100 and 100, inclusive.");
		}
		if (!isValidAmount(green)) {
			throw new IllegalArgumentException("Green value must be between -100 and 100, inclusive.");
		}
		if (!isValidAmount(blue)) {
			throw new IllegalArgumentException("Blue value must be between -100 and 100, inclusive.");
		}
		//@formatter:off
		sb.append("colorize(").append(red).append(",")
							  .append(green).append(",")
							  .append(blue).append(",")
							  .append(color).append(")");
		//@formatter:on
		return sb.toString();
	}

	/**
	 * This method just return an empty string
	 * 
	 * @return an empty string
	 */
	public static String empty() {
		return StringUtils.EMPTY;
	}

	private static boolean isValidAmount(int value) {
		return value >= -100 && value <= 100;
	}
}
