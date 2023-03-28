package com.about.mantle.model.services.document.preprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.infocat.model.InfoCatRecordPair;
import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.model.product.Product.PropertyType;
import com.about.mantle.infocat.property.Property;
import com.about.mantle.infocat.property.RetailerListProperty;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCommerceInfoEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCommerceInfoEx.StructuredContentCommerceInfoDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.services.VerticalConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AmazonIdPreprocessor extends AbstractStructuredContentBlockPreprocessor {
	
	protected final VerticalConfigService verticalConfigService;
	private static Logger logger = LoggerFactory.getLogger(AmazonIdPreprocessor.class);
	
	public AmazonIdPreprocessor (VerticalConfigService verticalConfigService) {
		this.verticalConfigService = verticalConfigService;
	}
	
	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		if(!(document instanceof StructuredContentBaseDocumentEx)) {
			return document;
		}
		
		if (!document.getInfoCatProductRecords().isEmpty()) {
			document.setInfoCatProductRecords(processInfoCatProductRecords((StructuredContentBaseDocumentEx)document));
		}
		
		return super.preProcessDocument(document);
	}

	@Override
	protected void processContentBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> content) {
		
		if (content instanceof StructuredContentProductRecordEx) {
			StructuredContentProductRecordEx productBlock = (StructuredContentProductRecordEx) content;
			
			processProductRecord(doc, productBlock.getData().getProduct());
			
		} else if(content instanceof StructuredContentCommerceInfoEx) {
			StructuredContentCommerceInfoEx commerceBlock = (StructuredContentCommerceInfoEx) content;
			StructuredContentCommerceInfoDataEx commerceBlockData = commerceBlock.getData();
			SliceableListEx<CommerceInfoEx> commerceInfo = commerceBlockData.getCommerceInfo();
			
			if(commerceInfo.stream().anyMatch(retailer -> "amazon".equalsIgnoreCase(retailer.getType()))) {
				
				String fallbackId = fetchAmazonFallbackId(doc);
				
				if (fallbackId != null) {
					commerceBlockData.setCommerceInfo(processCommerceInfo(commerceInfo, fallbackId));
				}
			}
		}
	}
	
	/**
	 * Replace a document's InfoCatRecordPairs with pairs that have processed retailer urls with amazon fallbacks 
	 * @param document
	 * @return
	 */
	private List<InfoCatRecordPair> processInfoCatProductRecords(StructuredContentBaseDocumentEx document){
		
		List<InfoCatRecordPair> productPairs = document.getInfoCatProductRecords();

		List<InfoCatRecordPair> newProductPairs =  productPairs.stream().map(pair -> { 

			Product product = pair.getProduct();
			
			processProductRecord(document, product);
			
			InfoCatRecordPair pairOfRecordAndProduct = new InfoCatRecordPair(pair.getStructuredContentProductRecordDataEx(), product);
			
			return pairOfRecordAndProduct;
			
		}).filter(productPair -> productPair != null).collect(Collectors.toCollection(ArrayList::new)); 
		
		return newProductPairs;
	}

	/**
	 * Process each retailer in a productrecord retailer list
	 * @param retailerList
	 * @param amazonId
	 * @return
	 */
	private void processProductRecord(BaseDocumentEx doc, Product product) {
		
		Property<?> retailerList = product != null ? product.getProperty(PropertyType.RETAILERS) : null;
		
		if(retailerList != null) {
			RetailerListProperty retailerProperty = (RetailerListProperty) retailerList;
			
			if(retailerProperty.getValue().getRetailers().stream().anyMatch(retailer -> "amazon".equalsIgnoreCase(retailer.getType()))) {
				
				String fallbackId = fetchAmazonFallbackId(doc);
				
				if (fallbackId != null) {
					List<Retailer> processedRetailers = processRetailerList(retailerProperty.getValue().getRetailers(), fallbackId);
					RetailerListProperty newRetailerProperty = new RetailerListProperty(retailerProperty);
					newRetailerProperty.getValue().setRetailers(processedRetailers);
					product.updateProperty(newRetailerProperty);
				}
			}
		}
	}

	/**
	 * Fetch amazon fallback id from VerticalConfigService
	 * @param doc
	 * @return
	 */
	private String fetchAmazonFallbackId(BaseDocumentEx doc) {
		
		String fallbackId = null;
		
		if (doc != null && doc.getVertical() != null) {
			Map<String, ?> verticalConfig = verticalConfigService.getVerticalConfig(doc.getVertical().toString());
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, String> amazonConfig = objectMapper.convertValue(verticalConfig.get("amazonConfig"), Map.class);
			fallbackId = amazonConfig != null ? amazonConfig.get("fallbackTrackingId") : null;
		}
		
		return fallbackId;
	}
	
	/**
	 * Process each retailer in a product record retailer list
	 * @param retailerList
	 * @param amazonId
	 * @return
	 */
	private List<Retailer> processRetailerList(List<Retailer> retailerList, String amazonId) {
		
		return retailerList.stream().map(retailer -> {
			if("amazon".equalsIgnoreCase(retailer.getType())) {
				Retailer newRetailer = new Retailer(retailer);
				newRetailer.setUrl(processUrl(retailer.getUrl(), amazonId));
				return newRetailer;
			} else {
				return retailer;
			}
		}).collect(Collectors.toList());
	}
	
	/**
	 * Process each retailer in a commerce info list
	 * @param commerceInfoList
	 * @param amazonId
	 * @return
	 */
	private SliceableListEx<CommerceInfoEx> processCommerceInfo(SliceableListEx<CommerceInfoEx> commerceInfoList, String amazonId) {
		
		List<CommerceInfoEx> newCommerceInfoList = commerceInfoList.stream().map(commerceInfo -> {
			if("amazon".equalsIgnoreCase(commerceInfo.getType())) {
				CommerceInfoEx newCommerceInfo = new CommerceInfoEx(commerceInfo);
				newCommerceInfo.setId(processUrl(commerceInfo.getId(), amazonId));
				return newCommerceInfo;
			} else {
				return commerceInfo;
			}
		}).collect(Collectors.toList());


		return SliceableListEx.of(newCommerceInfoList);
	}
	
	/**
	 * Parse a retailer url and update amazon tag with fallback id
	 * @param url
	 * @param amazonId
	 * @return
	 */
	private String processUrl(String url, String amazonId) {
		
		if(amazonId != null) {
			try {
				UriBuilder uriBuilder = UriBuilder.fromUri(url);
				//replaceQueryParam removes the existing tag and then adds the provided id meaning that
				//the new url will always have the fallback id even if the original url did not have a tag set.
				//This is the desired functionality
				uriBuilder.replaceQueryParam("tag", amazonId);
				return uriBuilder.toString();
			} catch (IllegalArgumentException e) {
				logger.warn("Error parsing product url {}", url);
			}
		}
		return url;
	}
}