package com.about.mantle.model.services.document.preprocessor.elementrewriters;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.document.ElementRewriter;
import com.about.mantle.model.services.document.preprocessor.Element;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;


/**
 * AffiliateAnchorElementRewriter adds value for <b>data-affiliate-anchor-element-rewriter</b> data attribute to all
 * matching affiliate links (both inline and non inline) using the configs provided at BOVD location:
 * <i>mantle/affiliateAnchorElementRewriterConfigs.json</i>. <p> This data attribute will then be used in front-end to
 * find-replace TOKENs like ${DOC_ID}, ${REQUEST_ID}, ${SITE} etc.
 * 
 * Example data attribute value:
 * <pre> 
 * data-affiliate-link-rewriter="aff_sub2=${DOC_ID}&aff_sub3=${REQUEST_ID}"
 * </pre>
 * </p>
 *
 */
public class AffiliateAnchorElementRewriter implements ElementRewriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AffiliateAnchorElementRewriter.class);
	
	public static final String DATA_ATTRIBUTE_NAME = "data-affiliate-link-rewriter";
	private static final String BOVD_CONFIG_FILE_PATH = "mantle/affiliateAnchorElementRewriterConfigs.json";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Set<String> TARGET_ELEMENT_NAMES = ImmutableSet.of("a");
	private final BusinessOwnedVerticalDataService bovdService;
	private volatile Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs;

	public AffiliateAnchorElementRewriter(BusinessOwnedVerticalDataService bovdService) {
		this.bovdService = bovdService;
		this.affiliateAnchorElementRewriterConfigs = getAffiliateAnchorElementRewriterConfigsFromBovd(BOVD_CONFIG_FILE_PATH);

		bovdService.addPropertyChangeLister(evt -> {
			if (bovdService.GIT_PROPERTY.equals(evt.getPropertyName())) {
				this.affiliateAnchorElementRewriterConfigs = getAffiliateAnchorElementRewriterConfigsFromBovd(BOVD_CONFIG_FILE_PATH);
			}
		});
	}

	@Deprecated
	public AffiliateAnchorElementRewriter(BusinessOwnedVerticalDataService bovdService,
										  CacheTemplate<Map<String, AffiliateAnchorElementRewriterConfig>> affiliateAnchorElementRewriterConfigsCache) {
		this(bovdService);
	}

	@Override
	public void processElement(BaseDocumentEx document, Element element) {
			processElement(element, affiliateAnchorElementRewriterConfigs);
	}

	void processElement(Element element,
			Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs) {
		String affiliateAnchorElementHref = element.getAttribute("href");

		for(Map.Entry<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfig: affiliateAnchorElementRewriterConfigs.entrySet()) {
			if(affiliateAnchorElementHref.contains(affiliateAnchorElementRewriterConfig.getKey())) {
				StringBuilder dataAttributeValue = new StringBuilder();
				affiliateAnchorElementRewriterConfig.getValue().getQueryParams().entrySet().forEach(queryParamMapEntry -> dataAttributeValue
						.append(queryParamMapEntry.getKey()).append("=").append(queryParamMapEntry.getValue()).append("&"));
				element.setAttribute(DATA_ATTRIBUTE_NAME, dataAttributeValue.toString().substring(0, dataAttributeValue.length()-1));
				break;
			}
		}
	}

	Map<String, AffiliateAnchorElementRewriterConfig> getAffiliateAnchorElementRewriterConfigsFromBovd(String bovdConfigFilePath) {
		Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorRewriterConfigs = Collections.EMPTY_MAP;
		
		try {
			LOGGER.info("Fetching Affiliate Anchor Element Rewriter Configs from BOVD path {}", bovdConfigFilePath);
			byte[] bovdResource = bovdService.getResource(bovdConfigFilePath);
			affiliateAnchorRewriterConfigs = Collections.unmodifiableMap(objectMapper.readValue(bovdResource, new TypeReference<Map<String, AffiliateAnchorElementRewriterConfig>>() {}));
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch Affiliate Anchor Element Rewriter Configs from BOVD path {}", bovdConfigFilePath, ex);
		}
		
		return affiliateAnchorRewriterConfigs;
	}

	@Override
	public Set<String> getTargetElementNames() {
		return TARGET_ELEMENT_NAMES;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AffiliateAnchorElementRewriterConfig {
		private Map<String, Object> queryParams;
		
		public Map<String, Object> getQueryParams() {
			return queryParams;
		}
		
		public void setQueryParams(Map<String, Object> queryParams) {
			this.queryParams = queryParams;
		}
	}
	
}
