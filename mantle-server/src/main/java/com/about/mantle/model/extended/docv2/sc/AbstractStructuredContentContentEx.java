package com.about.mantle.model.extended.docv2.sc;

import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCheetahEmbedEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCodeEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentDigiohEmbedEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFeaturedQuoteEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProfileEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentQuestionAndAnswerEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentRecordImageGalleryEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentBioEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCalloutEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCommerceInfoEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentComparisonListEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentDefinitionEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentEmbedEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentEntityReferenceEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFeaturedLinkEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGalleryEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGallerySlideEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGroupCalloutEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentIframeEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentLocationEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductSummaryEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentReviewEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSponsoredEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSpotlightEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentStarRatingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentUnknownEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentVideoBlockEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentTableEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentToolEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGuideEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFaqEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentKeyTermsEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentJourneyNavEx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Represents each chunk of Structured Content data.  In the Selene schema these are defined by the
 * `____Content` definitions, eg HeadingContent, ImageContent, HtmlContent, etc.  It is the content
 * of the structured content.
 */
@JsonTypeInfo(use = Id.NAME, visible = true, include = As.EXISTING_PROPERTY, property = "type", defaultImpl = StructuredContentUnknownEx.class)
@JsonSubTypes({
        @Type(value = StructuredContentHeadingEx.class, name = "HEADING"),
        @Type(value = StructuredContentSubheadingEx.class, name = "SUBHEADING"),
        @Type(value = StructuredContentHtmlEx.class, name = "HTML"),
        @Type(value = StructuredContentImageEx.class, name = "IMAGE"),
        @Type(value = StructuredContentTableEx.class, name = "TABLE"),
        @Type(value = StructuredContentFeaturedLinkEx.class, name = "FEATUREDLINK"),
        @Type(value = StructuredContentGalleryEx.class, name = "GALLERY"),
        @Type(value = StructuredContentGallerySlideEx.class, name = "GALLERYSLIDE"),
        @Type(value = StructuredContentCommerceInfoEx.class, name = "COMMERCE"),
        @Type(value = StructuredContentStarRatingEx.class, name = "STARRATING"),
        @Type(value = StructuredContentComparisonListEx.class, name = "COMPARISONLIST"),
        @Type(value = StructuredContentVideoBlockEx.class, name = "INLINEVIDEO"),
        @Type(value = StructuredContentIframeEx.class, name = "IFRAME"),
        @Type(value = StructuredContentLocationEx.class, name = "LOCATION"),
        @Type(value = StructuredContentCalloutEx.class, name = "CALLOUT"),
        @Type(value = StructuredContentGuideEx.class, name = "GUIDE"),   
        @Type(value = StructuredContentReviewEx.class, name = "REVIEW"),
        @Type(value = StructuredContentToolEx.class, name = "TOOL"),
        @Type(value = StructuredContentProductEx.class, name = "PRODUCT"),
        @Type(value = StructuredContentProductSummaryEx.class, name = "PRODUCTSUMMARY"),
        @Type(value = StructuredContentProfileEx.class, name = "PROFILE"),
        @Type(value = StructuredContentGroupCalloutEx.class, name = "GROUPCALLOUT"),
        @Type(value = StructuredContentEmbedEx.class, name = "EMBED"),
        @Type(value = StructuredContentCheetahEmbedEx.class, name = "CHEETAHEMBED"),
        @Type(value = StructuredContentBioEx.class, name = "BIO"),
        @Type(value = StructuredContentProductRecordEx.class, name = "PRODUCTRECORD"),
        @Type(value = StructuredContentDefinitionEx.class, name = "DEFINITION"),
        @Type(value = StructuredContentFaqEx.class, name = "FAQ"),
        @Type(value = StructuredContentQuestionAndAnswerEx.class, name = "QUESTIONANDANSWER"),
        @Type(value = StructuredContentKeyTermsEx.class, name = "KEYTERMS"),
        @Type(value = StructuredContentJourneyNavEx.class, name = "JOURNEYNAV"),
        @Type(value = StructuredContentCodeEx.class, name = "CODE"),
        @Type(value = StructuredContentSpotlightEx.class, name = "SPOTLIGHT"),
        @Type(value = StructuredContentEntityReferenceEx.class, name = "ENTITYREFERENCE"),
        @Type(value = StructuredContentFeaturedQuoteEx.class, name = "FEATUREDQUOTE"),
        @Type(value = StructuredContentRecordImageGalleryEx.class, name = "RECORDIMAGEGALLERY"),
        @Type(value = StructuredContentDigiohEmbedEx.class, name = "DIGIOHEMBED"),
        @Type(value = StructuredContentSponsoredEx.class, name = "SPONSORED")
})
public abstract class AbstractStructuredContentContentEx<T extends AbstractStructuredContentDataEx> {
	private static Logger logger = LoggerFactory.getLogger(AbstractStructuredContentContentEx.class);
    protected String type;
    private String parentType; // only populated for nested blocks in `StructuredContentDocumentProcessor`
    private String uuid;
    
    protected T data;
    @JsonProperty(value = "wasSliced")
    protected boolean wasSliced;

    /**
     * Returns the type of this data.  EG IMAGE, HTML, HEADING, etc.
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the type, e.g. GALLERYSLIDE, of this block's immediate parent or null if the block is not nested.
     *
     * @return
     */
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Does not come from Selene.  Is set to true if this SC block was actually generated by Mantle as part of a slice
     * @return
     */
    @JsonProperty(value = "wasSliced")
    public boolean getWasSliced() {
        return wasSliced;
    }

    @JsonProperty(value = "wasSliced")
    public void setWasSliced(boolean wasSliced) {
        this.wasSliced = wasSliced;
    }
    
    //currently a temporary solution for the issue of nested list items. Will likely be replaced by a unique grouping block at a later date. See GLBE-5893
    //Used to contain the determination if a html block is a step logic here for ease of future replacement and use. 
    @JsonIgnore
    public boolean isStep(){
    	return getData().getStepType() != null;
    }
   
    //currently a temporary solution for the issue of nested list items. Will likely be replaced by a unique grouping block at a later date. See GLBE-5893
    //Used to contain the determination if a html block is a step logic here for ease of future replacement and use. 
    @JsonIgnore
    public boolean isLastStep() {
    	return getData().getIsLastStep() != null ? getData().getIsLastStep() : false;
    }
    
    //currently a temporary solution for the issue of nested list items. Will likely be replaced by a unique grouping block at a later date. See GLBE-5893
    //Used to contain the determination if a html block is a step logic here for ease of future replacement and use. 
    @JsonIgnore
    public boolean isLastBlockOfLastStep() {
    	return getData().getIsLastBlockOfLastStep()  != null ? getData().getIsLastBlockOfLastStep() : false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", uuid=" + uuid +
                ", data=" + data +
                '}';
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
