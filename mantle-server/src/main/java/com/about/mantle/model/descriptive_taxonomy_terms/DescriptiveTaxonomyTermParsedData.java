package com.about.mantle.model.descriptive_taxonomy_terms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DescriptiveTaxonomyTermParsedData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, List<String>> descriptiveTaxonomyTermMap = new HashMap<>();
    private HashSet<String> adTaxonomyIds = new HashSet<>();

    public DescriptiveTaxonomyTermParsedData() {}

    public DescriptiveTaxonomyTermParsedData(Map<String, List<String>> descriptiveTaxonomyTermMap,
                                             HashSet<String> adTaxonomyIds) {
        this.descriptiveTaxonomyTermMap = descriptiveTaxonomyTermMap;
        this.adTaxonomyIds = adTaxonomyIds;
    }

    public Map<String, List<String>> getDescriptiveTaxonomyTermMap() {
        return descriptiveTaxonomyTermMap;
    }

    public void setDescriptiveTaxonomyTermMap(Map<String, List<String>> descriptiveTaxonomyTermMap) {
        this.descriptiveTaxonomyTermMap = descriptiveTaxonomyTermMap;
    }

    public HashSet<String> getAdTaxonomyIds() {
        return adTaxonomyIds;
    }

    public void setAdTaxonomyIds(HashSet<String> adTaxonomyIds) {
        this.adTaxonomyIds = adTaxonomyIds;
    }
}
