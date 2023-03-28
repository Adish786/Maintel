package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermParsedData;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.DescriptiveTaxonomyTermService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Grabs term ids from selene data and then given those ids the preprocessor hydrates
 * the document with a traversed list of parents.
 */
public class DescriptiveTaxonomyTermPreprocessor implements DocumentPreprocessor {

	protected final DescriptiveTaxonomyTermService descriptiveTaxonomyTermService;

	public DescriptiveTaxonomyTermPreprocessor(DescriptiveTaxonomyTermService descriptiveTaxonomyTermService) {
		this.descriptiveTaxonomyTermService = descriptiveTaxonomyTermService;
	}

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		if (descriptiveTaxonomyTermService == null) return document;
		DescriptiveTaxonomyTermParsedData descriptiveTaxonomyTermData = descriptiveTaxonomyTermService.generateDescriptionTaxonomyTermParsedData();
		if (descriptiveTaxonomyTermData == null) return document;
		if (!mapCreatedSuccessfully(descriptiveTaxonomyTermData.getDescriptiveTaxonomyTermMap())) return document;
		MetaDataEx metaData = document.getMetaData();
		if (metaData == null) return document;

		document.setMetaData(hydrateMTaxonomyIds(metaData, descriptiveTaxonomyTermData));

		return document;
	}

	public MetaDataEx hydrateMTaxonomyIds(MetaDataEx metaData,
										  DescriptiveTaxonomyTermParsedData descriptiveTaxonomyTermParsedData) {
		if (metaData.getmTaxonomyIds() == null) return metaData;

		// This is a set to remove any duplicates from the list
		// ex: parent is listed as taxId as well as being a parent of another listed taxId
		Set<String> taxonomyTermsForDocument = new HashSet<>();
		Stack<String> termsToFindParentsFor = new Stack<>();
		List<String> initialTaxonomyIds = metaData.getmTaxonomyIds().getList();
		termsToFindParentsFor.addAll(initialTaxonomyIds);
		taxonomyTermsForDocument.addAll(initialTaxonomyIds);

		while (termsToFindParentsFor.size() > 0) {
			String term = termsToFindParentsFor.pop();
			List<String> parents = getTaxonomyParent(term, descriptiveTaxonomyTermParsedData.getDescriptiveTaxonomyTermMap());
			if (parents != null) {
				taxonomyTermsForDocument.addAll(parents);
				termsToFindParentsFor.addAll(parents);
			}
		}

		Set<String> adTaxTermsFiltered = taxonomyTermsForDocument.stream()
				.filter(term -> isPartOfAdTaxGroup(term, descriptiveTaxonomyTermParsedData.getAdTaxonomyIds()))
				.collect(Collectors.toSet());

		metaData.setmTaxonomyIds(SliceableListEx.of(new ArrayList<>(taxonomyTermsForDocument)));
		metaData.setAdTaxonomyIds(SliceableListEx.of(new ArrayList<>(adTaxTermsFiltered)));

		return metaData;
	}

	private Boolean mapCreatedSuccessfully(Map<String, List<String>> descriptiveTaxonomyTermMap) {
		return descriptiveTaxonomyTermMap != null &&
				!descriptiveTaxonomyTermMap.isEmpty();
	}

	private List<String> getTaxonomyParent(String taxId, Map<String, List<String>> descriptiveTaxonomyTermMap) {
		return descriptiveTaxonomyTermMap.get(taxId);
	}

	private Boolean isPartOfAdTaxGroup(String taxId, HashSet<String> adTaxonomyIds) {
		return adTaxonomyIds.contains(taxId);
	}
}
