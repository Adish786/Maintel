package com.about.mantle.model.creditcard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.about.mantle.model.attributedef.AttributeDefEx;
import com.about.mantle.model.attributedef.value.AttributeUnknownValueEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.product.BaseProductEx;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class CreditCardEx extends BaseProductEx {
	
	private static final long serialVersionUID = 1L;
	
	private String issuer;
	private String network;
	private String primaryType;
	private SliceableListEx<String> types;
	private SliceableListEx<CreditScoreEx> creditScores;
	
	private Float overallScore;
	private Float customScore;
	
	/* 
	 * With below annotation categoryScores list will not be serialized to redis and not be cached
	 * and thus flattening of list into map will only occur once while deserializing Selene response 
	 * Flattened map is cached and used for future retrievals
	*/
	@JsonProperty(access = Access.WRITE_ONLY)
	private SliceableListEx<CategoryScoreEx> categoryScores;
	
	/* 
	 * With below annotation attributes list will not be serialized to redis and not be cached
	 * and thus flattening of list into map will only occur once while deserializing Selene response
	 * Flattened map is cached and used for future retrievals
	*/
	@JsonProperty(access = Access.WRITE_ONLY) 
	private SliceableListEx<CreditCardAttributeEx> attributes;

	//Map that contains view of flatten attribute list
	private Map<String, CreditCardAttributeView> attributesMap;
	
	//Map that contains view of flatten categoryScores list
	private Map<String, CreditCardCategoryView> categoryScoresMap;

	public CreditCardEx() {
		setType(ProductType.CREDITCARD);
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getPrimaryType() {
		return primaryType;
	}

	public void setPrimaryType(String primaryType) {
		this.primaryType = primaryType;
	}

	public SliceableListEx<String> getTypes() {
		return types;
	}

	public void setTypes(SliceableListEx<String> types) {
		this.types = types;
	}

	public SliceableListEx<CreditScoreEx> getCreditScores() {
		return creditScores;
	}

	public void setCreditScores(SliceableListEx<CreditScoreEx> creditScores) {
		this.creditScores = creditScores;
	}

	public void setAttributes(SliceableListEx<CreditCardAttributeEx> attributes) {
		this.attributes = attributes;
		
		if(this.attributes != null && this.attributesMap == null) {
			this.attributesMap = new HashMap<>(this.attributes.getTotalSize());
			for(CreditCardAttributeEx attribute: this.attributes.getList()) {
				if(!(attribute.getValue() instanceof AttributeUnknownValueEx)) {
					AttributeDefEx attributeDef = attribute.getAttributeDef();
					if(attributeDef != null) {
						CreditCardAttributeView creditCardAttributeView = new CreditCardAttributeView();
						creditCardAttributeView.setName(attributeDef.getName());
						creditCardAttributeView.setDisplayName(attributeDef.getDisplayName());
						creditCardAttributeView.setScore(attribute.getScore());
						if(attribute.getValue() != null) {
							creditCardAttributeView.setValue(attribute.getValue().getValue());
							creditCardAttributeView.setValueType(attribute.getValue().getType());
						}
						creditCardAttributeView.setWeightsMap(attributeDef.getWeightsMap());
						
						attributesMap.putIfAbsent(attributeDef.getName(), creditCardAttributeView);
					}
				}
			}
		}
	}
	
	public Map<String, CreditCardAttributeView> getAttributesMap() {
		return attributesMap;
	}
	
	public CreditCardAttributeView getAttribute(String attributeName) {
		return Objects.nonNull(this.attributesMap) ? attributesMap.get(attributeName) : null; 
	}

	public void setAttributesMap(Map<String, CreditCardAttributeView> attributesMap) {
		this.attributesMap = attributesMap;
	}

	public Float getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(Float overallScore) {
		this.overallScore = overallScore;
	}

	public Float getCustomScore() {
		return customScore;
	}

	public void setCustomScore(Float customScore) {
		this.customScore = customScore;
	}

	public void setCategoryScores(SliceableListEx<CategoryScoreEx> categoryScores) {
		this.categoryScores = categoryScores;
		
		if(this.categoryScores != null && this.categoryScoresMap == null) {
			this.categoryScoresMap = new HashMap<>(this.categoryScores.getTotalSize());
			for(CategoryScoreEx categoryScore: this.categoryScores.getList()) {
				CreditCardCategoryView creditCardView = new CreditCardCategoryView();
				creditCardView.setDisplayCategory(categoryScore.getDisplayCategory());
				creditCardView.setScore(categoryScore.getScore());
				categoryScoresMap.putIfAbsent(categoryScore.getCategory(), creditCardView);
			}
		}
	}
	
	public Map<String, CreditCardCategoryView> getCategoryScoresMap() {
		return categoryScoresMap;
	}
	
	public CreditCardCategoryView getCategoryScore(String category) {
		return Objects.nonNull(categoryScoresMap) ? categoryScoresMap.get(category) : null; 
	}

	public enum CreditScoreEx {
		NO_CREDIT_CHECK,
		POOR(350, 579),
		FAIR(580, 669),
		GOOD(670, 739),
		VERY_GOOD(740, 799),
		EXCELLENT(800, 850),
		NOT_SPECIFIED;

		private Integer from;
		private Integer to;

		private CreditScoreEx() {}

		private CreditScoreEx(Integer from, Integer to) {
			this.from = from;
			this.to = to;
		}

		public Integer getFrom() {
			return from;
		}

		public Integer getTo() {
			return to;
		}
	}
}
