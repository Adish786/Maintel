package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NONE, visible = true)
public class CuratedDocumentEx extends BaseDocumentEx implements Comparable<CuratedDocumentEx> {

	private static final long serialVersionUID = 1L;

	private String programmingTitle;
	private String programmingDescription;
	private ImageEx programmingImage = ImageEx.EMPTY;
	private DateTime addedDate;
    private ImageEx thumbImage;
    private String prepTime;
	private String cookTime;
	private SliceableListEx<TypedTimeRange> typedTimeRanges;

	public CuratedDocumentEx() {
	}

	public CuratedDocumentEx(BaseDocumentEx document) {
		super(document);
	}

	@Override
	@JsonIgnore
	public String getBestTitle() {
		if (isNotBlank(getProgrammingTitle())) return getProgrammingTitle();
		return super.getBestTitle();
	}

	public String getProgrammingTitle() {
		return programmingTitle;
	}

	public void setProgrammingTitle(String programmingTitle) {
		this.programmingTitle = programmingTitle;
	}
	
	@Override
	public String getDescription(){
		if(isNotBlank(getProgrammingDescription())) return getProgrammingDescription();
		if(isNotBlank(getSummary().getDescription())) return getSummary().getDescription();
		return super.getDescription();
	}

	public String getProgrammingDescription() {
		return programmingDescription;
	}

	public void setProgrammingDescription(String programmingDescription) {
		this.programmingDescription = programmingDescription;
	}

	public ImageEx getProgrammingImage() {
		return programmingImage;
	}

	public void setProgrammingImage(ImageEx programmingImage) {
		this.programmingImage = ImageEx.emptyIfNull(programmingImage);
	}

    public ImageEx getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(ImageEx thumbImage) {
        this.thumbImage = thumbImage;
    }

	public DateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(DateTime addedDate) {
		this.addedDate = addedDate;
	}
	
	public String getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(String prepTime) {
		this.prepTime = prepTime;
	}

	public String getCookTime() {
		return cookTime;
	}

	public void setCookTime(String cookTime) {
		this.cookTime = cookTime;
	}

	public SliceableListEx<TypedTimeRange> getTypedTimeRanges() {
		return typedTimeRanges;
	}

	public void setTypedTimeRanges(SliceableListEx<TypedTimeRange> typedTimeRanges) {
		this.typedTimeRanges = typedTimeRanges;
	}

	public int compareTo(CuratedDocumentEx o) {
		if (this.getBestTitle() == null ^ o.getBestTitle() == null) {
			return (this.getBestTitle() == null) ? -1 : 1;
		}
		if (this.getBestTitle() == null && o.getBestTitle() == null) {
			return 0;
		}
		return this.getBestTitle().compareToIgnoreCase(o.getBestTitle());
	}
}
