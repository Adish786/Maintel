package com.about.mantle.model.services.commerce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.property.RetailerListProperty;
import com.about.mantle.infocat.property.RetailerListProperty.RetailerListPropertyValue;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.services.DeionSearchFullDocumentService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.commerce.util.ASINExtractor;

/**
 * Please make sure you run AmazonRssServiceTest if you make any changes in this service.
 * For knowing what this service does, look at {@link AmazonRssService}
 */
public class AmazonRssServiceImpl implements AmazonRssService {

	private static final int REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS = 3;
	private static final String SPACE = " ";
	private static final String AMAZON = "amazon";
	private Logger logger = LoggerFactory.getLogger(AmazonRssService.class);

	private DeionSearchFullDocumentService deionSearchFullDocumentService;
	private DocumentService documentService;
	private final int BATCH_SIZE = 500;
	private static final Pattern YEAR_IN_TITLE = Pattern.compile("\\s\\w+\\s(20\\d{2})$");

	public AmazonRssServiceImpl(DeionSearchFullDocumentService deionSearchFullDocumentService, DocumentService documentService) {
		this.deionSearchFullDocumentService = deionSearchFullDocumentService;
		this.documentService = documentService;
	}

	/**
	 * if showLastPublishedInNHours == -1 then give all back else give back docs published in last
	 * `showLastPublishedInNHours` hour
	 */
	@Override
	public List<BaseDocumentEx> getDocumentsForRssFeed(String domain, int showLastPublishedInNHours) {
		return getDocumentsFromDeionSearch(domain, showLastPublishedInNHours);
	}

	private List<BaseDocumentEx> getDocumentsFromDeionSearch(String domain, int showLastPublishedInNHours) {
		List<BaseDocumentEx> answer = new ArrayList<>();
		List<BaseDocumentEx> results = new ArrayList<>();

		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		String query = getDocumentsSearchQuery(domain, showLastPublishedInNHours);
		logger.info("deionSearchFullDocumentService query :" + query);
		builder.setNoCache(true).setDomain(domain).setQuery(query).setLimit(BATCH_SIZE).setIncludeDocumentSummaries(true);

		try {
			int count = 0;
			do {
				builder.setOffset(count * BATCH_SIZE);
				DeionSearchFullResultEx deionSearchFullResultEx = deionSearchFullDocumentService
						.searchFullResults(builder.build());
				if (deionSearchFullResultEx != null) {
					SliceableListEx<BaseDocumentEx> items = deionSearchFullResultEx.getItems();
					results = items != null && items.getList() != null ? items.getList() : new ArrayList<>();
					answer.addAll(results);
				} else {
					results = new ArrayList<>();
				}
				count++;
			} while (!results.isEmpty());
		} catch (Exception e) {
			logger.error("Error while getting back results from Deion search for Amazon RSS feed", e);
		}

		logger.info("Total AmazonOSP documents found from deionSearchFullDocumentService: " + answer.size());

		// Do processing of documents
		processDocuments(answer);

		// filter documents before returning final list
		filterDocuments(answer);

		return answer;
	}

	private void processDocuments(List<BaseDocumentEx> documents) {
		if (documentService == null) return;

		for (ListIterator<BaseDocumentEx> docIterator = documents.listIterator(); docIterator.hasNext();) {
			// Only processing SC documents
			BaseDocumentEx doc = docIterator.next();
			if (!(doc instanceof StructuredContentBaseDocumentEx)) {
				continue;
			}

			docIterator.set(documentService.processDocument(doc));
		}
	}

	// @formatter:off
	/**
	 * - If there is an Amazon url and non-Amazon urls in one product, strip out the
	 * non-Amazon url.
	 * - If a product doesn’t have an Amazon url, exclude it from RSS feed.
	 * - Removes documents not having at least REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS product items
	 * with Amazon product(s)
	 * - Adjusts the document title based on final number of product items. e.g. if
	 * document title was "The 10 Best Selfie Sticks to Buy in 2018" and had 10
	 * product items in it, and after filtering if it has only 5 items, then title will be
	 * adjusted to "The 5 Best Selfie Sticks to Buy in 2018"
	 * 
	 */
	// @formatter:on
	private void filterDocuments(List<BaseDocumentEx> documents) {
		int totalDocsFromDeionSearch = documents.size();
        for (Iterator<BaseDocumentEx> docIterator = documents.iterator(); docIterator.hasNext();) {
        	AmazonOSPDocumentEx amazonOSPDoc = (AmazonOSPDocumentEx) docIterator.next();
			int originalNumberOfProductItemsInDoc = amazonOSPDoc.getProductItems().getTotalSize();
			//get product items in a document
			List<AmazonOSPDocumentEx.ProductItem> docProductItems = new ArrayList<>(amazonOSPDoc.getProductItems().getList());
			for (Iterator<AmazonOSPDocumentEx.ProductItem> docProductItemsIterator = docProductItems.iterator(); docProductItemsIterator
					.hasNext();) {
				//get the product
				StructuredContentProductRecordEx structuredContentProductRecordBlock = docProductItemsIterator.next().getProduct();
				Product product = structuredContentProductRecordBlock.getData().getProduct();
						
				RetailerListPropertyValue retailerProperty = ((RetailerListPropertyValue) product.getPropertyValue("retailers"));
				
				// create new ArrayList coz existing list is immutable
				List<Retailer> retailers = new ArrayList<>(retailerProperty.getPrimaryValue());
				String asin = retailerProperty.getAsin();
				
				// remove non-amazon products and amazon products without a valid ASIN
				retailers.removeIf(retailer -> !AMAZON.equalsIgnoreCase(retailer.getType()) || ASINExtractor.get(retailer.getUrl()) == null);
				if (retailers.isEmpty()) {
					docProductItemsIterator.remove();
				} else {
					retailers.forEach(retailer -> retailer.setUrl(getCanonicalAmazonProductUrl(retailer.getUrl())));
					
					//Update product's retailer property with updated retailer url
					product.updateProperty(new RetailerListProperty(asin, retailers));
				}
			}

			// if doesn't have at least REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS product items then remove that document.
			if (docProductItems.size() < REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS) {
				docIterator.remove();
			} else {
				// modified list back to product items
				amazonOSPDoc.setProductItems(SliceableListEx.of(docProductItems));	

				// remove prepositions and year from the end of the title
				// use title by default, bestTitle if unavailable
				String newTitle = YEAR_IN_TITLE.matcher(StringUtils.defaultIfEmpty(amazonOSPDoc.getTitle(), amazonOSPDoc.getBestTitle())).replaceAll("");
				
				// Adjust title if document has at least REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS items having amazon products
				int finalNumberOfProductItemsInDoc = amazonOSPDoc.getProductItems().getTotalSize();
				if (originalNumberOfProductItemsInDoc != finalNumberOfProductItemsInDoc) {
					// get BestTitle first, modify that and set to the title. Title will be used in template to populate the feed. 
					newTitle = newTitle.replace(SPACE + originalNumberOfProductItemsInDoc + SPACE,
							SPACE + finalNumberOfProductItemsInDoc + SPACE);
				}

				amazonOSPDoc.setTitle(newTitle);
			}
		}

		logger.info(String.format("Documents removed for not having at least %d items with amazon products in them: %d",
				REQUIRED_NUMBER_OF_AMZN_PRODUCT_ITEMS,  totalDocsFromDeionSearch - documents.size()));
		logger.info("Total documents after filtering:" + documents.size());
	}

	private String getCanonicalAmazonProductUrl(String url) {
		String asin = ASINExtractor.get(url);
		return "https://www.amazon.com/dp/" + asin;
	}

	/**
	 * Selene marks all commerce documents with authorKey 200005.
	 */
	private String getDocumentsSearchQuery(String domain, int showLastPublishedInNHours) {
		StringBuilder query = new StringBuilder("templateType:AMAZONOSP AND -noIndex:true AND state:ACTIVE AND (authorKey:200005 OR revenueGroup:COMMERCE)");

		// if showLastPublishedInNHours == -1 then return all documents
		if (showLastPublishedInNHours != -1) {
			query.append(" AND lastPublished:[NOW-" + showLastPublishedInNHours + "HOUR TO NOW]");
		}
		return query.toString();
	}

}
