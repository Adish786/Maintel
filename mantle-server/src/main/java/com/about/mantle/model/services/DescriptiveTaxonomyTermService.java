package com.about.mantle.model.services;

import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermParsedData;

import java.util.List;
import java.util.Map;

/**
 * Service that allows us to read Meredith's legacy Taxonomy API.
 * Api url: https://(ci.)taxonomy.meredith.services/v1/taxonomy/light/groups/udt
 */
public interface DescriptiveTaxonomyTermService {

	/**
	 * Generates the map of taxonomy terms to their parents.
	 */
	public DescriptiveTaxonomyTermParsedData generateDescriptionTaxonomyTermParsedData();
}
