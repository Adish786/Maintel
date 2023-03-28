package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TaxeneRelationshipEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Does an expensive secondary taxonomy call to hydrate document with a list of its primary and secondary ancestor docId's
 */
public class PrimaryAndSecondaryTaxonomyPreprocessor implements DocumentPreprocessor {

	protected final TaxeneRelationService taxeneRelationService;
	private static final List<TemplateTypeEx> EXCLUDED_TEMPLATES = List.of(TemplateTypeEx.TAXONOMY, TemplateTypeEx.BIO);

	public PrimaryAndSecondaryTaxonomyPreprocessor(TaxeneRelationService taxeneRelationService) {
		this.taxeneRelationService = taxeneRelationService;
	}

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {

		// do not need to check taxonomy on certain template types
		if (isExcluded(document)) return document;

		TaxeneTraverseRequestContext reqCtx = new TaxeneTraverseRequestContext.Builder()
				.setDocId(document.getDocumentId())
				.setActiveOnly(true)
				.setDirection(Direction.OUT)
				.setTraverseStrategy(TraverseStrategy.BREADTH_FIRST)
				.build();

		TaxeneNodeEx taxeneNodeEx = taxeneRelationService.traverse(reqCtx);

		if (taxeneNodeEx == null) return document;

		List<TaxeneRelationshipEx> taxeneRelationshipExList = taxeneNodeEx.getRelationships().getList();

		List<Long> primaryAndSecondaryAncestors = taxeneRelationshipExList.stream()
				.map(TaxeneRelationshipEx::getTargetNode)
				.map(TaxeneNodeEx::getDocId)
				.collect(Collectors.toList());

		document.setPrimaryAndSecondaryAncestors(SliceableListEx.of(primaryAndSecondaryAncestors));
		return document;
	}

	/**
	 * Provides boolean based on excluded template types. Meant to be overridden.
	 * Defaults to excluding documents with templates in the list in EXCLUDED_TEMPLATES.
	 *
	 * @param document
	 *            the document we are deciding to exclude or include
	 *
	 */
	protected boolean isExcluded(BaseDocumentEx document) {
		return EXCLUDED_TEMPLATES.contains(document.getTemplateType());
	}
}
