package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.util.List;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Decorates a subclass of {@link BaseDocumentEx} with its relevant TaxeneNode and its parent nodes.  Used when the
 * information from the doc is not enough and it needs some taxonomy info
 * @param <T>
 */
@JsonDeserialize(builder=DocumentTaxeneComposite.Builder.class)
public class DocumentTaxeneComposite<T extends BaseDocumentEx> implements Serializable{

	private static final long serialVersionUID = 502120992982207258L;
	private T document;
	private TaxeneNodeEx taxeneNode;

	/**
	 * Deprecated: use ancestors instead.
	 *
	 * This field is a flattened list of ancestors filtered on class type (@link TaxonomyDocumentEx).
	 * With the introduction of {@link com.about.mantle.model.extended.docv2.sc.TaxonomyStructuredContentDocumentEx}
	 * it is no longer capable of including all the desired ancestors in the list.
	 */
	@Deprecated
	private List<TaxonomyDocumentEx> taxonomyDocAllLevelList;

	/**
	 * Intended to be a flattened subset of the ancestors nested within the recursive taxeneNode structure.
	 * Provided as an optimization because constantly flattening a large list of {@link DocumentTaxeneComposite}
	 * for every request could be costly. This field enables us to do the flattening once and then cache the result.
	 */
	private List<BaseDocumentEx> ancestors;

	/**
	 * For Jackson only.  Do not use.
	 */
	protected DocumentTaxeneComposite() {
	}

	protected DocumentTaxeneComposite(Builder<T> builder) {
		this.document = builder.document;
		this.taxeneNode = builder.taxeneNode;
		this.taxonomyDocAllLevelList = builder.taxonomyDocAllLevelList;
		this.ancestors = builder.ancestors;
	}

	public T getDocument() {
		return document;
	}

	public TaxeneNodeEx getTaxeneNode() {
		return taxeneNode;
	}

	/**
	 * Deprecated: use getAncestors instead.
	 *
	 * Returns taxonomy documents for all parents of the taxeneNode with index 0 being the highest most generic level
	 * and the last index being the lowest, most specific.
	 * @return
	 */
	@Deprecated
	public List<TaxonomyDocumentEx> getTaxonomyDocAllLevelList() {
		return taxonomyDocAllLevelList;
	}

	/**
	 * Returns ancestor documents for the taxeneNode with index 0 being the highest most generic level
	 * and the last index being the lowest, most specific.
	 * How the ancestors are chosen depends on the service providing the object but it is generally a subset of all the
	 * ancestors nested within the recursive taxeneNode structure.
	 * @return
	 */
	public List<BaseDocumentEx> getAncestors() {
		return ancestors;
	}

	public static <T extends BaseDocumentEx> Builder<T> builder() {
		return new Builder<>();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder<T extends BaseDocumentEx> {
		private T document;
		private TaxeneNodeEx taxeneNode;
		@Deprecated
		private List<TaxonomyDocumentEx> taxonomyDocAllLevelList;
		private List<BaseDocumentEx> ancestors;

		public Builder() {

		}

		public Builder(DocumentTaxeneComposite<T> DocumentTaxeneComposite) {
			this.document = DocumentTaxeneComposite.document;
			this.taxeneNode = DocumentTaxeneComposite.taxeneNode;
			this.taxonomyDocAllLevelList = DocumentTaxeneComposite.taxonomyDocAllLevelList;
			this.ancestors = DocumentTaxeneComposite.ancestors;
		}

		public Builder<T> document(T document) {
			this.document = document;
			return this;
		}

		public Builder<T> taxeneNode(TaxeneNodeEx taxeneNode) {
			this.taxeneNode = taxeneNode;
			return this;
		}

		@Deprecated
		public Builder<T> taxonomyDocAllLevelList(List<TaxonomyDocumentEx> taxonomyDocAllLevelList) {
			this.taxonomyDocAllLevelList = taxonomyDocAllLevelList;
			return this;
		}

		public Builder<T> ancestors(List<BaseDocumentEx> ancestors) {
			this.ancestors = ancestors;
			return this;
		}

		public DocumentTaxeneComposite<T> build() {
			return new DocumentTaxeneComposite<>(this);
		}

	}

}
