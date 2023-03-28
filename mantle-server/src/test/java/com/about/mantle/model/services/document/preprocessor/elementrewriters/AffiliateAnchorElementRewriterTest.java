package com.about.mantle.model.services.document.preprocessor.elementrewriters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.BeforeClass;
import org.junit.Test;

import com.about.globe.core.cache.DisabledCacheTemplate;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.document.preprocessor.JSoupElementWrapper;
import com.about.mantle.model.services.document.preprocessor.elementrewriters.AffiliateAnchorElementRewriter.AffiliateAnchorElementRewriterConfig;

public class AffiliateAnchorElementRewriterTest {

	static AffiliateAnchorElementRewriter affiliateAnchorElementRewriter;
	
	private static final BusinessOwnedVerticalDataService testBovdService = new BusinessOwnedVerticalDataService() {
		@Override
		public byte[] getResource(String path) {
			String filepath = "/ServicesTest/AffiliateAnchorElementRewriterTest/" + path;
			try (InputStream inputStream = getClass().getResourceAsStream(filepath)) {
				if (inputStream == null) {
					throw new RuntimeException("failed to load file: " + filepath);
				}
				return IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void addPropertyChangeLister(PropertyChangeListener propertyChangeListener) {
		}
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		affiliateAnchorElementRewriter = new AffiliateAnchorElementRewriter(testBovdService);
	}

	@Test
	public void validJsonToConfigObjectTest() {
		Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs = affiliateAnchorElementRewriter
				.getAffiliateAnchorElementRewriterConfigsFromBovd("validConfig.json");
		
		assertFalse(affiliateAnchorElementRewriterConfigs.isEmpty());
		assertThat(getDataAttributeValue("http://hasofferstracking.betterhelp.com/aff_c?abc=123",
				affiliateAnchorElementRewriterConfigs), is(not(isEmptyString())));
	}

	private String getDataAttributeValue(String href,
			Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs) {
		JSoupElementWrapper elementWrapper = getJsoupElementWrapper(href);
		
		affiliateAnchorElementRewriter.processElement(elementWrapper, affiliateAnchorElementRewriterConfigs);
		String dataAtrributeValue = elementWrapper.getAttribute(AffiliateAnchorElementRewriter.DATA_ATTRIBUTE_NAME);
		return dataAtrributeValue;
	}
	
	private JSoupElementWrapper getJsoupElementWrapper(String linkHref) {
		Element anchorElement = new Element(Tag.valueOf("a"), "");
		JSoupElementWrapper elementWrapper = new JSoupElementWrapper(anchorElement);
		elementWrapper.setAttribute("href", linkHref);
		return elementWrapper;
	}
	
	@Test
	public void invalidJsonToConfigObjectTest() {
		Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs = affiliateAnchorElementRewriter
				.getAffiliateAnchorElementRewriterConfigsFromBovd("invalidConfig.json");
		
		assertTrue(affiliateAnchorElementRewriterConfigs.isEmpty());
	}
	
	@Test
	public void catchAllConfigTest() {
		Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs = affiliateAnchorElementRewriter
				.getAffiliateAnchorElementRewriterConfigsFromBovd("catchAllConfig.json");
		
		assertTrue(getDataAttributeValue("http://www.impact.com/?partner=1234", affiliateAnchorElementRewriterConfigs)
				.contains("partner_id"));
		assertFalse(getDataAttributeValue("http://www.impact.com/", affiliateAnchorElementRewriterConfigs)
				.contains("partner_id"));
	}
	
	@Test
	public void substringOfSameRuleConfigTest() {
		Map<String, AffiliateAnchorElementRewriterConfig> affiliateAnchorElementRewriterConfigs = affiliateAnchorElementRewriter
				.getAffiliateAnchorElementRewriterConfigsFromBovd("substringOfSameRuleConfig.json");
		
		assertFalse(getDataAttributeValue("http://www.impact.com/?partner=1234", affiliateAnchorElementRewriterConfigs)
				.contains("partner_id"));
	}

}
