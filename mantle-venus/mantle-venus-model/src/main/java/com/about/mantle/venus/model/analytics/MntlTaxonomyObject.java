package com.about.mantle.venus.model.analytics;

import java.util.List;

public class MntlTaxonomyObject {

	private List<List<MntlTaxonomyNode>> taxonomyNodes;

	public List<List<MntlTaxonomyNode>> getTaxonomyNodes() {
		return taxonomyNodes;
	}

	public void setTaxonomyNodes(List<List<MntlTaxonomyNode>> taxonomyNodes) {
		this.taxonomyNodes = taxonomyNodes;
	}

}