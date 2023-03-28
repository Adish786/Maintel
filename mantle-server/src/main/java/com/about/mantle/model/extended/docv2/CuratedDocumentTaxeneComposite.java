package com.about.mantle.model.extended.docv2;

import java.util.List;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Class acts like DocumentTaxeneComposite, but is for curated lists. This is to avoid deserialization issues that stems from 
 * DocumentTaxeneComposite<T extends BaseDocument> and curated lists having no identity as they have template types to other 
 * children of base document.
 */
public class CuratedDocumentTaxeneComposite{

	private CuratedDocumentEx document;
	private TaxeneNodeEx taxeneNode;
	@Deprecated
	private List<TaxonomyDocumentEx> taxonomyDocAllLevelList;
	private List<BaseDocumentEx> ancestors;

	/**
	 * For Jackson only.  Don't use directly.
	 */
	protected CuratedDocumentTaxeneComposite(){}

	protected CuratedDocumentTaxeneComposite(Builder builder) {
		this.document = builder.document;
		this.taxeneNode = builder.taxeneNode;
		this.taxonomyDocAllLevelList = builder.taxonomyDocAllLevelList;
		this.ancestors = builder.ancestors;
	}

	public CuratedDocumentEx getDocument() {
		return document;
	}

	public TaxeneNodeEx getTaxeneNode() {
		return taxeneNode;
	}

	/**
	 * @see DocumentTaxeneComposite#getTaxonomyDocAllLevelList()
	 */
	@Deprecated
	public List<TaxonomyDocumentEx> getTaxonomyDocAllLevelList() {
		return taxonomyDocAllLevelList;
	}

	/**
	 * @see DocumentTaxeneComposite#getAncestors()
	 */
	public List<BaseDocumentEx> getAncestors() {
		return ancestors;
	}

	public static Builder curatedDocumentBuilder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder{
		private CuratedDocumentEx document;
		private TaxeneNodeEx taxeneNode;
		@Deprecated
		private List<TaxonomyDocumentEx> taxonomyDocAllLevelList;
		private List<BaseDocumentEx> ancestors;

		public Builder() {

		}

		public Builder(CuratedDocumentTaxeneComposite curatedDocumentTaxeneComposite) {
			this.document = curatedDocumentTaxeneComposite.document;
			this.taxeneNode = curatedDocumentTaxeneComposite.taxeneNode;
			this.taxonomyDocAllLevelList = curatedDocumentTaxeneComposite.taxonomyDocAllLevelList;
			this.ancestors = curatedDocumentTaxeneComposite.ancestors;
		}

		public Builder  document(CuratedDocumentEx document) {
			this.document = document;
			return this;
		}

		public Builder taxeneNode(TaxeneNodeEx taxeneNode) {
			this.taxeneNode = taxeneNode;
			return this;
		}

		@Deprecated
		public Builder taxonomyDocAllLevelList(List<TaxonomyDocumentEx> taxonomyDocAllLevelList) {
			this.taxonomyDocAllLevelList = taxonomyDocAllLevelList;
			return this;
		}

		public Builder ancestors(List<BaseDocumentEx> ancestors) {
			this.ancestors = ancestors;
			return this;
		}

		public CuratedDocumentTaxeneComposite build() {
			return new CuratedDocumentTaxeneComposite(this);
		}

	}
}
