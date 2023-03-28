package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.Serializable;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.render.image.ImageDimension;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ImageEx extends ResolvableImageEx implements Serializable, ImageDimension {

	/**
	 * Immutable null constant
	 */
	public static final ImageEx EMPTY = new ImageEx() {

		private static final long serialVersionUID = 1L;

		@Override
		public void setUrl(String url) {
		}

		@Override
		public void setCaption(String caption) {
		}

		@Override
		public void setOwner(String owner) {
		}

		@Override
		public void setAlt(String alt) {
		}

		@Override
		public void setWidth(Integer width) {
		}

		@Override
		public void setHeight(Integer height) {
		}

		@Override
		public void setId(String id) {
		}

		@Override
		public void setFileName(String fileName) {
		}

		@Override
		public void setObjectId(String objectId) {
		}

		@Override
		public void setObjectPath(String objectPath) {
		}
		
		@Override
		public void setSensitive(Boolean sensitive) {
		}

		@Override
		public void setOriginal(Boolean original) {
		}
		
		@Override
		public void setEncodedThumbnail(String encodedThumbnail) {		
		}
	};

	private static final long serialVersionUID = 1L;

	private String url;
	private String caption;
	private String owner;
	private String alt;
	private Integer width;
	private Integer height;
	private String id;
	private String fileName;
	private String objectPath;
	private String objectId;
	private Boolean sensitive;
	// This field is defaulted to false as selene is not currently providing a value for this property and some default should exist
	private Boolean original = Boolean.FALSE;
	private String encodedThumbnail;
	private FocalPoint focalPoint;
	private String rightsUsage;

	public ImageEx() {
	}

	public ImageEx(ImageEx src) {
		this.url = src.url;
		this.caption = src.caption;
		this.owner = src.owner;
		this.alt = src.alt;
		this.width = src.width;
		this.height = src.height;
		this.id = src.id;
		this.fileName = src.fileName;
		this.objectPath = src.objectPath;
		this.objectId = src.objectId;
		this.sensitive = src.sensitive;
		this.original = src.original;
		this.encodedThumbnail = src.encodedThumbnail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	public Integer getWidth(Integer defaultWidth) {
		return defaultIfNull(width, defaultWidth);
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public Integer getHeight() {
		return height;
	}

	public Integer getHeight(Integer defaultHeight) {
		return defaultIfNull(height, defaultHeight);
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectPath() {
		return objectPath;
	}
	
	public void setObjectPath(String objectPath) {
		this.objectPath = objectPath;
	}
	
	public Boolean getSensitive() {
		return sensitive;
	}
	
	public void setSensitive(Boolean sensitive) {
		this.sensitive = sensitive;
	}

	public Boolean getOriginal() {
		return original;
	}

	public void setOriginal(Boolean original) {
		this.original = original;
	}
	
	public String getEncodedThumbnail() {
		return encodedThumbnail;
	}

	public void setEncodedThumbnail(String encodedThumbnail) {
		this.encodedThumbnail = encodedThumbnail;
	}

	public FocalPoint getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(FocalPoint focalPoint) {
		this.focalPoint = focalPoint;
	}

	public String getRightsUsage() {
		return rightsUsage;
	}

	public void setRightsUsage(String rightsUsage) {
		this.rightsUsage = rightsUsage;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return this.equals(EMPTY) || isBlank(getObjectId());
	}
	
	@JsonIgnore
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	/**
	 * Owner field uses quilljs to populate the field with rich text, and as such 
	 * is padded with <p> tags by the CMS, figuring out if the field is empty requires sanitization
	 */
	@JsonIgnore
	public boolean hasOwner() {
		if (owner == null) {
			return false;
		}

		String sanitizedOwner = Jsoup.clean(this.owner, Whitelist.none());
		return !sanitizedOwner.isBlank();
	}

	/**
	 * Use {@link #getImageOrientation(float, float)} instead
	 * @return
	 */
	@JsonIgnore
	public ImageOrientation getImageOrientation() {
		return getImageOrientation((float) 1.25, (float) 1.0);
	}

	@JsonIgnore
	public ImageOrientation getImageOrientation(float landscapeThreshold, float portraitThreshold) {

		float imageRatio = getImageRatio();

		if (imageRatio > landscapeThreshold) return ImageOrientation.LANDSCAPE;
	    if (imageRatio < portraitThreshold) return ImageOrientation.PORTRAIT;

	    return ImageOrientation.SQUARE;
    }

	/**
	 * Returns width / height.  < 1 means that it is a portrait, > 1 landscape
	 * @return
	 */
	@JsonIgnore
    public float getImageRatio() {

		float width = getWidth(0);
		float height = getHeight(1);

		if (height == 0) {
			throw new GlobeException("Image height of 0 not supported");
		}

		return width / height;
	}
	
	@JsonIgnore
	public ImageResolution getImageResolution() {
        if(getWidth(0) < 768) return ImageResolution.LOW_RES;
        return ImageResolution.HIGH_RES;
    }

	@Override
	@JsonIgnore
	public ImageEx resolve(RequestContext requestContext) {
		return this;
	}

	public static ImageEx emptyIfNull(ImageEx image) {
		return image != null ? image : EMPTY;
	}

    @Override
    public String toString() {
        return "ImageEx{" +
                "url='" + url + '\'' +
                ", caption='" + caption + '\'' +
                ", alt='" + alt + '\'' +
                ", id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", objectId='" + objectId + '\'' +
                ", encodedThumbnail='" + (encodedThumbnail!=null) + '\'' +
                '}';
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof ImageEx)) return false;

		ImageEx imageEx = (ImageEx) o;

		return getObjectId() != null ? getObjectId().equals(imageEx.getObjectId()) : imageEx.getObjectId() == null;
	}

	@Override
	public int hashCode() {
		return getObjectId() != null ? getObjectId().hashCode() : 0;
	}
}
