package com.about.mantle.model.services.document.preprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.infocat.model.InfoCatRecordPair;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.ReviewStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx.StructuredContentProductRecordDataEx;
import com.about.mantle.model.product.BaseProductEx.ProductType;

/**
 * Handles preprocessing for product records 
 */
public class ProductRecordPreprocessor extends AbstractStructuredContentBlockPreprocessor {
	
	protected final ProductService productService;
	private static Logger logger = LoggerFactory.getLogger(ProductRecordPreprocessor.class);
	
	public ProductRecordPreprocessor (ProductService productService) {
		this.productService = productService;
	}

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		if(!(document instanceof StructuredContentBaseDocumentEx)) {
			return document;
		}

		document.setInfoCatProductRecords(processStructuredContentDocumentForProduct((StructuredContentBaseDocumentEx)document));

		// Skip for Review SC docs as the product record block is an entity and not within the content blocks
		if (document instanceof ReviewStructuredContentDocumentEx) {
			return document;
		}

		// Only process document if product records found
		if (document.getInfoCatProductRecords().isEmpty()) {
			return document;
		}
		
		return super.preProcessDocument(document);
	}
	
	/**
	 * Returns a list of each PRODUCTRECORD block and its associated Product
	 * @param document Document to retrieve Products for
	 * @return
	 */
	private List<InfoCatRecordPair>  processStructuredContentDocumentForProduct (StructuredContentBaseDocumentEx document){
		if (productService == null) return new ArrayList<>();

		List<StructuredContentProductRecordDataEx> listOfProduct;
		
		if(document instanceof ReviewStructuredContentDocumentEx) {
				listOfProduct = Stream.of(((ReviewStructuredContentDocumentEx)document).getEntity().getData())
							.filter(entity -> entity.getType() == ProductType.PRODUCTRECORD)
							.map(entity-> {
								StructuredContentProductRecordDataEx data = new StructuredContentProductRecordDataEx();
								data.setProductId(entity.getId());
								return data;
							})
							.collect(Collectors.toCollection(ArrayList::new));
			
		} else {
			listOfProduct = document.getContentsStreamOfType("PRODUCTRECORD")
								.map(record-> ((StructuredContentProductRecordEx)record).getData())
								.collect(Collectors.toCollection(ArrayList::new)); 
			
		}

		List<String> productIds = listOfProduct.stream().map(product -> product.getProductId())
			 	.collect(Collectors.toCollection(ArrayList::new));
		
		Map<String,Product> products = productService.getProducts(productIds); 

		return listOfProduct.stream().map(productRecord -> { 

			Product product = products.get(productRecord.getProductId());

			InfoCatRecordPair pairOfRecordAndProduct = null;
			
			if(product != null){
				pairOfRecordAndProduct = new InfoCatRecordPair(productRecord, product);
			}else{
				logger.error("Error finding product "+productRecord.getProductId());
			}
			
			return pairOfRecordAndProduct;
			
		}).filter(productPair -> productPair != null).collect(Collectors.toCollection(ArrayList::new)); 
	}

	/**
	 * Filters out desired content blocks for further processing
	 * @param doc
	 * @param content
	 */
	@Override
	protected void processContentBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> content) {
		if ("PRODUCTRECORD".equals(content.getType())) {
			hydrateProductRecordBlock(doc, content);
		}
	}
	
	/**
	 * Hydrates a PRODUCTRECORD block from a list of SC blocks with product data
	 * @param doc
	 * @param currentBlock
	 */
	private static void hydrateProductRecordBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> currentBlock) {
		StructuredContentProductRecordEx productRecordBlock = (StructuredContentProductRecordEx)currentBlock;
		StructuredContentProductRecordDataEx blockData = productRecordBlock.getData();

		Optional<Product> optionalProduct = doc.getInfoCatProductRecords().stream()
				.filter(pair -> pair.getStructuredContentProductRecordDataEx().equals(blockData))
				.map(pair -> pair.getProduct())
				.findFirst();

		// If no product found, unable to process product record block
		if (!optionalProduct.isPresent()) {
			logger.error("Unable to process product " + blockData.getProductId() + " in document " + doc.getDocumentId() + ". No product data found on document.");
			return;
		}
		
		Product product = optionalProduct.get();
		
		blockData.setProduct(product);
	}
}
