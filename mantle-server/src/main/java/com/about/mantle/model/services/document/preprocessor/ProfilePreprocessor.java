package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.model.product.Profile;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProfileEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProfileEx.StructuredContentProfileDataEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles preprocessing for profiles
 */
public class ProfilePreprocessor extends AbstractStructuredContentBlockPreprocessor {

	protected final ProductService productService;
	private static Logger logger = LoggerFactory.getLogger(ProfilePreprocessor.class);

	public ProfilePreprocessor(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		if(!(document instanceof StructuredContentBaseDocumentEx)) {
			return document;
		}

		return super.preProcessDocument(document);
	}

	/**
	 * Filters out desired content blocks for further processing
	 * @param doc
	 * @param content
	 */
	@Override
	protected void processContentBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> content) {
		if ("PROFILE".equals(content.getType())) {
			hydrateProfileBlock(doc, content);
		}
	}
	
	/**
	 * Hydrates product on PROFILE blocks
	 * @param doc
	 * @param currentBlock
	 */
	private void hydrateProfileBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> currentBlock) {
		StructuredContentProfileEx profileBlock = (StructuredContentProfileEx)currentBlock;
		StructuredContentProfileDataEx blockData = profileBlock.getData();

		Product product = productService.getProduct(blockData.getProfileId());

		// If no profile found, unable to process profile block
		if (product == null) {
			logger.error("Unable to process profile " + blockData.getProfileId() + " in document " + doc.getDocumentId() + ". No profile found for ID.");
			return;
		}

		blockData.setProfile(product);

		// This check is commented out as part of AXIS-3376 as the profile block is being changed to support
		// any product record until a larger conversation about the requirements of the block and of product
		// ancestor category handling can take place.
//		if (product instanceof Profile) {
//			Profile profile = (Profile) product;
//			blockData.setProfile(profile);
//		} else {
//			logger.error("Unable to process profile " + blockData.getProfileId() + " in document " + doc.getDocumentId() + ". ID describes non-profile record.");
//		}


	}
}
