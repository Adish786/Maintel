package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.about.globe.core.task.annotation.TaskParameters;
import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import com.about.mantle.render.ImageResizer;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.render.image.ImageSettings;

/**
 * It can be used to perform different operations (resizing etc) on images. Note: This task is handy when used with
 * {@link MantleRequestHandlerMethods#serveModel(RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * where it can perform operations on images and returns corresponding response.
 *
 */
@Tasks
public class ImageTask {

	private ImageResizer imageResizer;
	private MantleRenderUtils mantleRenderUtils;


	public ImageTask(ImageResizer imageResizer, MantleRenderUtils mantleRenderUtils) {
		this.imageResizer = imageResizer;
		this.mantleRenderUtils = mantleRenderUtils;
	}

	@Task(name="resizedImageUrl")
	@TimedComponent(category = "task")
	public String getResizedImageUrl(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "objectId") String objectId,
			@TaskParameter(name = "width") int width, @TaskParameter(name = "height") int height,
			@TaskParameter(name = "fitInStyle") String fitInStyle, @TaskParameter(name = "cropSetting") String cropSetting,
			@TaskParameter(name = "filters") String[] filters) {
		return imageResizer.resize(objectId, width, height, fitInStyle, requestContext, cropSetting, filters);
	}

	@Task(name="resizedImageUrl")
	@TimedComponent(category = "task")
	public String getResizedImageUrl(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "objectId") String objectId,
			@TaskParameter(name = "width") int width, @TaskParameter(name = "height") int height,
			@TaskParameter(name = "filters") String[] filters) {
		return imageResizer.resize(objectId, width, height, null, requestContext, null, filters);
	}

	@Task(name="resizedImageUrl")
	@TimedComponent(category = "task")
	public String getResizedImageUrl(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "objectId") String objectId,
			@TaskParameter(name = "width") int width, @TaskParameter(name = "height") int height) {
		return imageResizer.resize(objectId, width, height, null, requestContext, null, null);
	}

	@Task(name="resizedImageUrl")
	@TimedComponent(category = "task")
	public String getResizedImageUrl(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "objectId") String objectId,
			@TaskParameter(name = "width") int width) {
		return imageResizer.resize(objectId, width, 0, null, requestContext, null, null);
	}

	@Task(name="imageSettings")
	public ImageSettings imageSettings(@TaskParameter(name = "width") Integer width) {
		return new ImageSettings.Builder().width(width).build();
	}

	@Task(name="imageSettings")
	public ImageSettings imageSettings(@TaskParameter(name = "width") Integer width, @TaskParameter(name = "height") Integer height) {
		return new ImageSettings.Builder().width(width).height(height).build();
	}

	@Task(name="imageSettings")
	public ImageSettings imageSettings(@TaskParameter(name = "width") Integer width, @TaskParameter(name = "height") Integer height,
			@TaskParameter(name = "filters") String[] filters) {
		return new ImageSettings.Builder().width(width).height(height).filters(filters).build();
	}

	@Task(name="imageSettings")
	public ImageSettings imageSettings(@TaskParameter(name = "width") Integer width, @TaskParameter(name = "height") Integer height,
			@TaskParameter(name = "fitInStyle") String fitInStyle, @TaskParameter(name = "cropSetting") String cropSetting,
			@TaskParameter(name = "filters") String[] filters) {
		return new ImageSettings.Builder().width(width).height(height).fitInStyle(fitInStyle).cropSetting(cropSetting).filters(filters).build();
	}

	/**
	 * Not all verticals use srcset. Some verticals, e.g. lifestyle,
	 * specify distinct sizes for each device type especially when the image
	 * is constrained to a width that does not span the full viewport.
	 *
	 * If ignoreMaxBytesOutsideSrcset is true, this method will not use the max_bytes filter except in the srcset.
	 * With sufficiently large image dimensions, Thumbor will either hyper-pixelate or ignore the max_bytes
	 * constraint. The former is actively bad for perceived quality; the latter makes it irrelevant.
	 */
	@Task(name="primaryImageLinkHeader")
	@TimedComponent(category = "task")
	public Map<String, String> getPrimaryImageForLinkHeader(@RequestContextTaskParameter RequestContext requestContext,
															@TaskParameters PrimaryImageLinkHeaderModel model) {

		Map<String, String> answer = new LinkedHashMap<>();
		List<String> finalFilters = new ArrayList<>();
		boolean useGifv = Objects.requireNonNullElse(model.getUseGifv(), false);
		boolean ignoreMaxBytesOutsideSrcset = Objects.requireNonNullElse(model.getIgnoreMaxBytesOutsideSrcset(), false);
		
		if (useGifv && model.getImage().getObjectId().contains(".gif")) {
			String videoFilter = mantleRenderUtils.useWebm(requestContext) ? "gifv(webm)" : "gifv(mp4)";
			finalFilters.add(videoFilter);
		} else {
			finalFilters.addAll(model.getFilters());
			if (StringUtils.stripToEmpty(requestContext.getHeaders().getAccept()).contains("webp")) {
				finalFilters.add("format(webp)");
			}
		}

		String[] filtersArray = finalFilters.toArray(new String[0]);

		String thumborSrcset = model.getSrcset() == null ? null : mantleRenderUtils.getSrcsetThumbor(model.getImage(), model.getSrcset().get("minWidth"),
				model.getSrcset().get("maxWidth"), model.getSrcset().get("maxHeight"), model.getSrcset().get("stepCount"), model.getFitInStyle(), model.getForceSize(),
				requestContext, model.getCropSetting(), filtersArray);

		if (StringUtils.isNotBlank(thumborSrcset) && StringUtils.isNotBlank(model.getSizes())) {
			answer.put("srcset", "\"" + thumborSrcset + "\"");
			answer.put("sizes", "\"" + model.getSizes() + "\"");
		}

		String src = mantleRenderUtils.getImageSrc(model.getImage(), model.getWidth(), model.getHeight(), model.getFitInStyle(), model.getForceSize(),
				requestContext, model.getCropSetting(), filtersArray, ignoreMaxBytesOutsideSrcset);

		if (StringUtils.isNotBlank(src)) {
			answer.put("src", "\"" + src + "\"");
		}
		
		if (StringUtils.isNotBlank(model.getImage().getEncodedThumbnail())){
			answer.put("blurryPlaceholder", model.getImage().getEncodedThumbnail());
		}
		
		if (StringUtils.isNotBlank(model.getImage().getObjectId())){
			answer.put("objectId", model.getImage().getObjectId());
		} else if (StringUtils.isNotBlank(model.getImage().getUrl())){
			answer.put("objectId", model.getImage().getUrl());
		}

		return answer;
	}

	public static class PrimaryImageLinkHeaderModel {
		private ImageEx image;
		private Integer width;
		private String fitInStyle;
		private List<String> filters;
		private String cropSetting;
		private Map<String, Integer> srcset;
		private String sizes;
		private Boolean webpAuto;
		private Integer height;
		private Boolean forceSize;
		private Boolean useGifv;
		private Boolean ignoreMaxBytesOutsideSrcset;

		public ImageEx getImage() {
			return image;
		}

		public void setImage(ImageEx image) {
			this.image = image;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public String getFitInStyle() {
			return fitInStyle;
		}

		public void setFitInStyle(String fitInStyle) {
			this.fitInStyle = fitInStyle;
		}

		public List<String> getFilters() {
			return filters;
		}

		public void setFilters(List<String> filters) {
			this.filters = filters;
		}

		public String getCropSetting() {
			return cropSetting;
		}

		public void setCropSetting(String cropSetting) {
			this.cropSetting = cropSetting;
		}

		public Map<String, Integer> getSrcset() {
			return srcset;
		}

		public void setSrcset(Map<String, Integer> srcset) {
			this.srcset = srcset;
		}

		public String getSizes() {
			return sizes;
		}

		public void setSizes(String sizes) {
			this.sizes = sizes;
		}

		public Boolean getWebpAuto() {
			return webpAuto;
		}

		public void setWebpAuto(Boolean webpAuto) {
			this.webpAuto = webpAuto;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}

		public Boolean getForceSize() {
			return forceSize;
		}

		public void setForceSize(Boolean forceSize) {
			this.forceSize = forceSize;
		}

		public Boolean getUseGifv() {
			return useGifv;
		}

		public void setUseGifv(Boolean useGifv) {
			this.useGifv = useGifv;
		}

		public Boolean getIgnoreMaxBytesOutsideSrcset() {
			return ignoreMaxBytesOutsideSrcset;
		}

		public void setIgnoreMaxBytesOutsideSrcset(Boolean ignoreMaxBytesOutsideSrcset) {
			this.ignoreMaxBytesOutsideSrcset = ignoreMaxBytesOutsideSrcset;
		}
	}

}
