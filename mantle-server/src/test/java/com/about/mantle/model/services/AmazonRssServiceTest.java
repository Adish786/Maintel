package com.about.mantle.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.infocat.model.product.DefaultProduct;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.property.RetailerListProperty;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx.ProductItem;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx.StructuredContentProductRecordDataEx;
import com.about.mantle.model.services.commerce.AmazonRssService;
import com.about.mantle.model.services.commerce.AmazonRssServiceImpl;
import com.about.mantle.model.services.impl.DocumentServiceImpl;


public class AmazonRssServiceTest {

	private static final String COMMERCE_DOC_TITLE = "The 10 Best Selfie Sticks to Buy in 2021";
	private static final Integer TOTAL_PRODUCT_ITEMS_IN_DOCUMENT = 10;
	private AmazonRssService amazonRssService;

	@Before
	public void before() {
		DeionSearchFullDocumentService deionSearchFullDocumentService = Mockito
				.mock(DeionSearchFullDocumentService.class);
		
		when(deionSearchFullDocumentService.searchFullResults(any(DeionSearchService.DeionSearchRequestContext.class)))
				.thenReturn(getDeionSearchFullDocumentResults()).thenReturn(getEmptyDeionSearchFullDocumentResults());
		amazonRssService = new AmazonRssServiceImpl(deionSearchFullDocumentService, null);
	}

	@Test
	public void testAmazonRssService () {
		List<BaseDocumentEx> result = amazonRssService.getDocumentsForRssFeed("tech", -1);
		assertNotNull(result);
		// documents with less than required amazon product items should have been removed. That leaves us with only two document.
		assertEquals(2, result.size());
		
		//first document will be with some product items having amazon products and some with non-amazon products.
		testAmazonRssServiceForAmazonAndOtherProductItemsDocument((AmazonOSPDocumentEx)result.get(0));
		
		//second document will be with product items with only amazon products in them.
		testAmazonRssServiceForJustAmazonProductItemsDocument((AmazonOSPDocumentEx)result.get(1));
	}

	public void testAmazonRssServiceForAmazonAndOtherProductItemsDocument(AmazonOSPDocumentEx amazonOSPDoc) {
		//Because getProductItemsWithAmazonAndOthers method adds amazon product to every other product items, only TOTAL_PRODUCT_ITEMS_IN_DOCUMENT/2 product items should be there at the end.
		assertEquals(TOTAL_PRODUCT_ITEMS_IN_DOCUMENT/2 , amazonOSPDoc.getProductItems().getTotalSize().intValue());

		//Now that we have only TOTAL_PRODUCT_ITEMS_IN_DOCUMENT/2 product items in the document verify COMMERCE_DOC_TITLE should also reflect that number
		//Also if there's a Year at the end of the title, it would be stripped out along with the preposition before it. 
		assertEquals(amazonOSPDoc.getTitle(), COMMERCE_DOC_TITLE.replace(" " + TOTAL_PRODUCT_ITEMS_IN_DOCUMENT + " ", " " + TOTAL_PRODUCT_ITEMS_IN_DOCUMENT/2 + " ").replaceAll("\\s\\w+\\s(20\\d{2})$",""));
		
		verifyAmazonOSPDocProducts(amazonOSPDoc);
	}
	
	public void testAmazonRssServiceForJustAmazonProductItemsDocument(AmazonOSPDocumentEx amazonOSPDoc) {
		
		//Because getProductItemsWithJustAmazon method adds amazon product to every product items, all TOTAL_PRODUCT_ITEMS_IN_DOCUMENT product items should be there at the end.
		assertEquals(TOTAL_PRODUCT_ITEMS_IN_DOCUMENT.intValue(), amazonOSPDoc.getProductItems().getTotalSize().intValue());

		//Now that we have TOTAL_PRODUCT_ITEMS_IN_DOCUMENT product items in the document verify COMMERCE_DOC_TITLE should also reflect that number
		//Also if there's a Year at the end of the title, it would be stripped out along with the preposition before it. 
		assertEquals(amazonOSPDoc.getTitle(), COMMERCE_DOC_TITLE.replaceAll("\\s\\w+\\s(20\\d{2})$",""));
		
		verifyAmazonOSPDocProducts(amazonOSPDoc);
	}
	
	private void verifyAmazonOSPDocProducts(AmazonOSPDocumentEx amazonOSPDoc) {
		for (AmazonOSPDocumentEx.ProductItem productItem : amazonOSPDoc.getProductItems()) {
			StructuredContentProductRecordEx product = productItem.getProduct();
			if (product instanceof StructuredContentProductRecordEx) {
				 // verify only amazon links exist in product record block, all other should have been removed
				 List<Retailer> retailerList = ((StructuredContentProductRecordEx) product).getData().getProduct().getRetailerList();
				 assertEquals(1, retailerList.size());
				 assertTrue(retailerList.get(0).getType().equals("amazon"));
				 //verify that additional tagging query params are removed and URL is formatted as expected
				 assertEquals("https://www.amazon.com/dp/B00EI7DPPI", retailerList.get(0).getUrl());
			}
		}
	}

	private DeionSearchFullResultEx getEmptyDeionSearchFullDocumentResults() {
		DeionSearchFullResultEx answer = new DeionSearchFullResultEx();
		answer.setItems(SliceableListEx.emptyList());
		answer.setNumFound(0);
		return answer;
	}

	private DeionSearchFullResultEx getDeionSearchFullDocumentResults() {
		DeionSearchFullResultEx answer = new DeionSearchFullResultEx();
		answer.setItems(getMockDocumentList());
		answer.setNumFound(4);
		return answer;
	}

	private SliceableListEx<BaseDocumentEx> getMockDocumentList() {
		List<BaseDocumentEx> docs = new ArrayList<>();
		docs.add(getAmazonOSPDocumentWithAmazonAndOthers());
		docs.add(getAmazonOSPDocumentWithoutAmazon());
		docs.add(getAmazonOSPDocumentWithOneAmazon());
		docs.add(getAmazonOSPDocumentWithJustAmazon());

		return SliceableListEx.of(docs);
	}
	
	private AmazonOSPDocumentEx getAmazonOSPDocumentWithAmazonAndOthers() {
		AmazonOSPDocumentEx amazonOSPDoc = new AmazonOSPDocumentEx();
		amazonOSPDoc.setTitle(COMMERCE_DOC_TITLE);
		amazonOSPDoc.setProductItems(getProductItemsWithAmazonAndOthers());
		amazonOSPDoc.setIntro(SliceableListEx.emptyList());
		amazonOSPDoc.setOutro(SliceableListEx.emptyList());
		return amazonOSPDoc;
	}
	
	private AmazonOSPDocumentEx getAmazonOSPDocumentWithoutAmazon() {
		AmazonOSPDocumentEx amazonOSPDoc = new AmazonOSPDocumentEx();
		amazonOSPDoc.setTitle(COMMERCE_DOC_TITLE);
		amazonOSPDoc.setProductItems(getProductItemsWithoutAmazon());
		amazonOSPDoc.setIntro(SliceableListEx.emptyList());
		amazonOSPDoc.setOutro(SliceableListEx.emptyList());
		return amazonOSPDoc;
	}
	
	private AmazonOSPDocumentEx getAmazonOSPDocumentWithOneAmazon() {
		AmazonOSPDocumentEx amazonOSPDoc = new AmazonOSPDocumentEx();
		amazonOSPDoc.setTitle(COMMERCE_DOC_TITLE);
		amazonOSPDoc.setProductItems(getProductItemsWithOneAmazon());
		amazonOSPDoc.setIntro(SliceableListEx.emptyList());
		amazonOSPDoc.setOutro(SliceableListEx.emptyList());
		return amazonOSPDoc;
	}
	
	private AmazonOSPDocumentEx getAmazonOSPDocumentWithJustAmazon() {
		AmazonOSPDocumentEx amazonOSPDoc = new AmazonOSPDocumentEx();
		amazonOSPDoc.setTitle(COMMERCE_DOC_TITLE);
		amazonOSPDoc.setProductItems(getProductItemsWithJustAmazon());
		amazonOSPDoc.setIntro(SliceableListEx.emptyList());
		amazonOSPDoc.setOutro(SliceableListEx.emptyList());
		return amazonOSPDoc;
	}

	private SliceableListEx<ProductItem> getProductItemsWithOneAmazon() {
		List<ProductItem> products = new ArrayList<>();
		for (int i = 0; i < TOTAL_PRODUCT_ITEMS_IN_DOCUMENT - 1; i++) {
			StructuredContentProductRecordEx product = getProductRecordBlockWithoutAmazon(i);
			products.add(new ProductItem(product, null));
		}
		products.add(new ProductItem(getProductRecordBlockWithAmazonAndOthers(1), null));
		return SliceableListEx.of(products);
	}

	private SliceableListEx<ProductItem> getProductItemsWithoutAmazon() {
		List<ProductItem> productItems = new ArrayList<>();
		for (int i = 0; i < TOTAL_PRODUCT_ITEMS_IN_DOCUMENT; i++) {
			StructuredContentProductRecordEx product = getProductRecordBlockWithoutAmazon(i);
			productItems.add(new ProductItem(product, null));
		}
		return SliceableListEx.of(productItems);
	}

	private SliceableListEx<ProductItem> getProductItemsWithAmazonAndOthers() {
		List<ProductItem> productItems = new ArrayList<>();
		for (int i = 0; i < TOTAL_PRODUCT_ITEMS_IN_DOCUMENT; i++) {
			StructuredContentProductRecordEx product = null;
			
			//Add amazon product to every alternate product blocks
			if(i % 2 == 0) {
				product = getProductRecordBlockWithAmazonAndOthers(i);
			}else {
				product = getProductRecordBlockWithoutAmazon(i);
			}
			
			productItems.add(new ProductItem(product, null));
		}
		return SliceableListEx.of(productItems);
	}

	private SliceableListEx<ProductItem> getProductItemsWithJustAmazon() {
		List<ProductItem> products = new ArrayList<>();
		for (int i = 0; i < TOTAL_PRODUCT_ITEMS_IN_DOCUMENT; i++) {
			StructuredContentProductRecordEx product = getProductRecordBlockWithJustAmazon(i);
			products.add(new ProductItem(product, null));
		}
		return SliceableListEx.of(products);
	}

	private StructuredContentProductRecordEx getProductRecordBlockWithoutAmazon(int i) {
		StructuredContentProductRecordEx productRecordBlock = new StructuredContentProductRecordEx();
		StructuredContentProductRecordDataEx productRecordBlockData = new StructuredContentProductRecordDataEx();
		Product product = new DefaultProduct();
		product.updateProperty(new RetailerListProperty(null, retailerListWithoutAmazon(i)));
		productRecordBlockData.setProduct(product);
		productRecordBlock.setData(productRecordBlockData);
		productRecordBlock.setType("COMMERCE");
		return productRecordBlock;
	}

	private StructuredContentProductRecordEx getProductRecordBlockWithAmazonAndOthers(int i) {
		StructuredContentProductRecordEx productRecordBlock = new StructuredContentProductRecordEx();
			StructuredContentProductRecordDataEx productRecordBlockData = new StructuredContentProductRecordDataEx();
		Product product = new DefaultProduct();
		product.updateProperty(new RetailerListProperty("B00EI7DPPI", retailerListWithAmazonAndOthers(i)));
		productRecordBlockData.setProduct(product);
		productRecordBlock.setData(productRecordBlockData);
		productRecordBlock.setType("COMMERCE");
		return productRecordBlock;
	}

	private StructuredContentProductRecordEx getProductRecordBlockWithJustAmazon(int i) {
		StructuredContentProductRecordEx productRecordBlock = new StructuredContentProductRecordEx();
		StructuredContentProductRecordDataEx productRecordBlockData = new StructuredContentProductRecordDataEx();
		
		Product product = new DefaultProduct();
		product.updateProperty(new RetailerListProperty("B00EI7DPPI", retailerListWithJustAmazon(i)));
		productRecordBlockData.setProduct(product);
		productRecordBlock.setData(productRecordBlockData);
		productRecordBlock.setType("COMMERCE");
		return productRecordBlock;
	}
	
	private List<Retailer> retailerListWithoutAmazon(int i2) {
		String[] commerceTypes = new String[] { "walmart", "bestbuy", "target" };
		return getRetailerList(commerceTypes, i2);
	}

	private List<Retailer> retailerListWithAmazonAndOthers(int i2) {
		String[] commerceTypes = new String[] { "amazon", "walmart", "bestbuy", "target" };
		return getRetailerList(commerceTypes, i2);
	}

	private List<Retailer> retailerListWithJustAmazon(int i2) {
		String[] commerceTypes = new String[] { "amazon"};
		return getRetailerList(commerceTypes, i2);
	}
	
	private List<Retailer> getRetailerList(String[] commerceTypes, int i2) {
		List<Retailer> retailers = new ArrayList<>();
		for (int i = 0; i < commerceTypes.length; i++) {
			Retailer retailer = new Retailer();
			retailer.setType(commerceTypes[i]);
			if ("amazon".equals(commerceTypes[i])) {
				retailer.setUrl("https://www." + commerceTypes[i] + ".com/mock-id" + i2 + "/dp/B00EI7DPPI?tag1=foo&tag2=bar");
			} else {
				retailer.setUrl("https://www." + commerceTypes[i] + ".com/mock-id" + i2 + "?tag1=foo&tag2=bar");
			}
			retailers.add(retailer);
		}
		return retailers;
	}

}
