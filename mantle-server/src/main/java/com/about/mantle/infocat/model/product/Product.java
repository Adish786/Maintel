package com.about.mantle.infocat.model.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.about.mantle.infocat.model.Audit;
import com.about.mantle.infocat.model.Category;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.model.ProductAttributeRating;
import com.about.mantle.infocat.model.PromotionalEvent;
import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.infocat.model.TaggedImage;
import com.about.mantle.infocat.property.MeasurementRangeProperty;
import com.about.mantle.infocat.property.MultiselectProperty;
import com.about.mantle.infocat.property.MultiselectValueDefinition;
import com.about.mantle.infocat.property.Property;
import com.about.mantle.infocat.property.PropertyValue;
import com.about.mantle.infocat.property.RetailerListProperty.RetailerListPropertyValue;
import com.about.mantle.infocat.property.StringListProperty;
import com.about.mantle.infocat.property.StringListProperty.StringListPropertyValue;
import com.about.mantle.infocat.property.URLProperty.UrlPropertyValue;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.google.common.collect.ImmutableList;

@JsonTypeInfo(use = Id.NONE, defaultImpl = DefaultProduct.class)
@JsonTypeResolver(ProductTypeResolver.class)
public abstract class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String categoryId;
	private Category category;
	private String legacyId;
	private BigDecimal rating;
	private String recordName;
	private Audit audit;
	private List<TaggedImage> images = Collections.emptyList();
	private Map<String, List<ImageEx>> taggedImageMap = Collections.emptyMap();
	private List<PromotionalEvent> promotionalEvents = Collections.emptyList();
	private List<ProductAttributeRating> ratings = Collections.emptyList();
	private List<Property<?>> properties = Collections.emptyList();
	private Map<String, PropertyValue<?>> propertiesMap;
	private Map<String, Property<?>> propertiesDefMap;
	/*
	 * Property mapping for accessing property maps by group
	 */
	private Map<String, List<Property<?>>> groupingMap;

	/*
	 * Ratings mapping for accessing rating value by name
	 */
	private Map<String, BigDecimal> ratingsMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public Audit getAudit() {
		return audit;
	}

	public void setAudit(Audit audit) {
		this.audit = audit;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	@JsonIgnore
	public ImageEx getImageForUsage(String imageTag) {
		ImageEx answer = null;
		List<ImageEx> images = getImagesForUsage(imageTag);
		if (CollectionUtils.isNotEmpty(images)) {
			answer = images.get(0);
		}
		return answer;
	}

	@JsonIgnore
	public List<ImageEx> getImagesForUsage(String imageTag) {
		List<ImageEx> answer = null;
		if (taggedImageMap != null) {
			answer = this.taggedImageMap.get(imageTag);
		}
		return answer;
	}

	@JsonIgnore
	public List<Property<?>> getPropertiesByGroup(String grouping) {
		List<Property<?>> answer = ImmutableList.of();
		if (groupingMap != null && grouping != null) {
			answer = this.groupingMap.get(grouping);
		}
		return answer;
	}

	@JsonIgnore
	public PropertyValue<?> getPropertyValue(String propertyName) {
		PropertyValue<?> answer = PropertyValue.EMPTY;
		if (propertiesMap != null && propertyName != null) {
			answer = ObjectUtils.defaultIfNull(this.propertiesMap.get(propertyName), PropertyValue.EMPTY);
		}
		return answer;
	}

	@JsonIgnore
	public PropertyValue<?> getPropertyValue(PropertyType key) {
		return getPropertyValue(key != null ? key.getKey() : null);
	}

	public List<TaggedImage> getImages() {
		return images;
	}

	public void setImages(List<TaggedImage> images) {
		this.images = images;

		if (images != null && !images.isEmpty()) {
			this.taggedImageMap = new HashMap<>();
			for (TaggedImage taggedImage : this.images) {
				for (String imageTag : taggedImage.getTags()) {
					this.taggedImageMap.putIfAbsent(imageTag, new ArrayList<>());
					this.taggedImageMap.get(imageTag).add(taggedImage.getImage());
				}
			}
		} else {
			this.taggedImageMap = null;
		}
	}

	public List<PromotionalEvent> getPromotionalEvents() {
		return promotionalEvents;
	}

	public void setPromotionalEvents(List<PromotionalEvent> promotionalEvents) {
		this.promotionalEvents = promotionalEvents;
	}

	public List<ProductAttributeRating> getRatings() {
		return ratings;
	}

	@JsonIgnore
	public BigDecimal getAttributeRating(String attributeName) {
		BigDecimal answer = null;
		if (ratingsMap != null && ratingsMap != null) {
			answer = ObjectUtils.defaultIfNull(this.ratingsMap.get(attributeName), null);
		}
		return answer;
	}

	public void setRatings(List<ProductAttributeRating> ratings) {
		this.ratings = ratings;

		if (ratings != null && !ratings.isEmpty()) {
			this.ratingsMap = this.ratings.stream()
					.filter(rating -> rating.getAttribute().getName() != null)
					.collect(Collectors.toMap(rating -> rating.getAttribute().getName(), rating -> rating.getValue()));
		}
	}

	public List<Property<?>> getProperties() {
		return properties;
	}

	@JsonIgnore
	public Property<?> getProperty(String propertyName) {
		Property<?> answer = null;
		if (propertiesDefMap != null && propertiesDefMap != null) {
			answer = ObjectUtils.defaultIfNull(this.propertiesDefMap.get(propertyName), null);
		}
		return answer;
	}

	@JsonIgnore
	public Property<?> getProperty(PropertyType key) {
		return getProperty(key != null ? key.getKey() : null);
	}

	@JsonIgnore
	public String getParsedPropertyValue(String propertyName) {
		Property<?> property = null;

		if (propertiesDefMap != null) {
			property = ObjectUtils.defaultIfNull(this.propertiesDefMap.get(propertyName), null);
		}

        if (property == null){
            return null;
        }

        switch(property.getPropertyDefinitionMetadata().getDefinition().getType()){
            case MULTI_SELECT:
                MultiselectProperty selections = (MultiselectProperty)property;
                List<String> selectedValues = new ArrayList<String>();

                for(MultiselectValueDefinition sV : selections.getValue().getPrimaryValue()){
                    if(sV.getIsSelected()){
                        selectedValues.add(sV.getValue());
                    }
                }

                if (selectedValues.size() <= 0){
                    return null;
                }

                return String.join(", ", selectedValues);
            case MEASUREMENT_RANGE:
                MeasurementRangeProperty measurementRange =(MeasurementRangeProperty)property;
				String measurementMinValue = Objects.toString(measurementRange.getValue().getMeasurementMinValue());
				String measurementMaxValue = Objects.toString(measurementRange.getValue().getMeasurementMaxValue());
				String measurementUnit = measurementRange.getValue().getUOM();

                return measurementMinValue + " to " + measurementMaxValue + " " + measurementUnit;
			case STRING_LIST:
				StringListProperty stringList = (StringListProperty)property;

				return String.join(", ", stringList.getValue().getStringValues());
            default:
                return Objects.toString(property.getValue().getPrimaryValue());
		}
	}

	public void setProperties(List<Property<?>> properties) {
		this.properties = properties;
		if (properties != null && !properties.isEmpty()) {
			this.propertiesMap = new HashMap<>();
			this.propertiesDefMap = new HashMap<>();

			for (Property<?> property : this.properties) {
				this.propertiesMap.putIfAbsent(property.getPropertyDefinitionMetadata().getDefinition().getName(),
						property.getValue());
				this.propertiesDefMap.putIfAbsent(property.getPropertyDefinitionMetadata().getDefinition().getName(),
						property);
			}

			this.groupingMap = this.properties.stream()
					.filter(property -> property.getPropertyDefinitionMetadata().getGrouping() != null)
					.collect(Collectors.groupingBy(property -> property.getPropertyDefinitionMetadata().getGrouping(),
							Collectors.toList()));
		} else {
			this.propertiesMap = null;
		}

	}

	/*
	 * Helper methods for FE
	 */
	@JsonIgnore
	public abstract String getProductName();

	@JsonIgnore
	public abstract String getShortTitle();

	@JsonIgnore
	public abstract String getLongTitle();

	@JsonIgnore
	public List<String> getPrimaryGtin() {
		@SuppressWarnings("rawtypes")
		PropertyValue amazonGtin = getPropertyValue(PropertyType.AMAZONGTIN);
		@SuppressWarnings("rawtypes")
		PropertyValue gtin = getPropertyValue(PropertyType.GTIN);

		if (!PropertyValue.isEmpty(amazonGtin)) {
			return ((StringListPropertyValue) amazonGtin).getPrimaryValue();
		}

		if (!PropertyValue.isEmpty(gtin)) {
			return ((StringListPropertyValue) gtin).getPrimaryValue();
		}

		return null;
	}

	@JsonIgnore
	public List<String> getPrimaryMpn() {
		return PropertyValue.isEmpty(getPropertyValue(PropertyType.MPN))
				? null
				: ((StringListPropertyValue) getPropertyValue(
						PropertyType.MPN)).getPrimaryValue();
	}

	@JsonIgnore
	public List<Retailer> getRetailerList() {
		return PropertyValue.isEmpty(getPropertyValue(PropertyType.RETAILERS)) ? null
				: ((RetailerListPropertyValue) getPropertyValue(PropertyType.RETAILERS)).getPrimaryValue();
	}

	@JsonIgnore
	public Pair<String, String> getCampaignUrlValue() {
		return PropertyValue.isEmpty(getPropertyValue(PropertyType.CAMPAIGNURL)) ? null
				: ((UrlPropertyValue) getPropertyValue(PropertyType.CAMPAIGNURL)).getPrimaryValue();
	}

	public void updateProperty(Property<?> property) {

		setProperties(
				Stream.concat(
						Stream.of(property),
						getProperties().stream()
								.filter(p -> p.getPropertyDefinitionMetadata().getDefinition().getName() != property
										.getPropertyDefinitionMetadata().getDefinition().getName()))
						.collect(Collectors.toList()));

	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Product{");
		sb.append("id='").append(id);
		sb.append("', recordName='").append(recordName);
		sb.append("', categoryId='").append(categoryId);
		sb.append("', rating='").append(rating);
		sb.append("'}");
		return sb.toString();
	}

	/*
	 * This is not an exhaustive list of property types. Products can have property
	 * values not being actively used by the backend.
	 * However, if products do have added extra properties added they can still be
	 * accessed directly by the frontend by passing in
	 * the String key.
	 * 
	 */
	public enum PropertyType {
		RETAILERS("retailers"),
		DESTINATIONURLS("destinationUrls"),
		GTIN("gtin"),
		MPN("mpn"),
		AMAZONGTIN("amazonGtin"),
		CAMPAIGNURL("campaignUrl");

		private String key;

		PropertyType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}
}
