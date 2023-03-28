package com.about.mantle.model.services.impl;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.TaxonomyDocumentEx;
import com.about.mantle.model.services.DocumentTaxeneService;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.NodeType;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.about.globe.core.util.CollectionsUtil.splitToSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * @see DocumentTaxeneService
 */
public class DocumentTaxeneServiceImpl implements DocumentTaxeneService {

	private final TaxeneRelationService taxeneService;
	private final ExecutorService executor;

	public DocumentTaxeneServiceImpl(TaxeneRelationService taxeneService, ExecutorService executor) {
		this.taxeneService = taxeneService;
		this.executor = executor;
	}

	/**
     * @see DocumentTaxeneService#getDocumentTaxeneComposite(BaseDocumentEx, String)
	 */
	@Override
	public <T extends BaseDocumentEx> DocumentTaxeneComposite<T> getDocumentTaxeneComposite(T document,
			String projection) {
		if (document == null) return null;

		DocumentTaxeneComposite.Builder<T> documentTaxeneCompositeBuilder = DocumentTaxeneComposite.builder();
		documentTaxeneCompositeBuilder.document(document);

		//@formatter:off
		TaxeneTraverseRequestContext reqCtx = new TaxeneTraverseRequestContext.Builder()
				.setDocId(document.getDocumentId())
				.setActiveOnly(true)
				.setDirection(Direction.OUT)
				.setIncludeDocumentSummaries(true)
				.setTraverseStrategy(TraverseStrategy.BREADTH_FIRST)
				.setRelationships(splitToSet("primaryParent"))
				.setNodeType(NodeType.INTERNAL_NODE)
				.setProjection(projection)
				.build();
		//@formatter:on

		TaxeneNodeEx taxeneNode = taxeneService.traverse(reqCtx);
		documentTaxeneCompositeBuilder.taxeneNode(taxeneNode);

		List<TaxonomyDocumentEx> taxonomyDocAllLevelList = new ArrayList<>();
		List<BaseDocumentEx> ancestors = new ArrayList<>();
		accumulateAncestors(taxeneNode, taxonomyDocAllLevelList, ancestors);
		documentTaxeneCompositeBuilder.taxonomyDocAllLevelList(taxonomyDocAllLevelList).ancestors(ancestors);

		return documentTaxeneCompositeBuilder.build();
	}

	/**
	 * @see DocumentTaxeneService#getDocumentTaxeneCompositeList(List, String)
	 */
	@Override
	public <T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> getDocumentTaxeneCompositeList(
			List<T> documentList, String projection) {
		if (isEmpty(documentList)) return null;

		List<CompletableFuture<DocumentTaxeneComposite<T>>> docs = documentList.stream()
				.map(d -> CompletableFuture.supplyAsync(() -> {
					return getDocumentTaxeneComposite(d, projection);
				}, executor)).collect(Collectors.toList());

		return docs.stream().map(f -> f.join()).filter(v -> v != null).collect(Collectors.toList());
	}

	/**
	 * @see DocumentTaxeneService#getCuratedDocumentTaxeneCompositeList(List, String)
     * @see CuratedDocumentTaxeneComposite
	 */
	@Override
	public List<CuratedDocumentTaxeneComposite> getCuratedDocumentTaxeneCompositeList(
			List<CuratedDocumentEx> documentList, String projection) {
		if (isEmpty(documentList)) return null;

		List<CompletableFuture<CuratedDocumentTaxeneComposite>> docs = documentList.stream().map(d -> {
			return CompletableFuture.supplyAsync(() -> getCuratedDocumentTaxeneComposite(d, projection), executor);
		}).collect(Collectors.toList());

		return docs.stream().map(f -> f.join()).collect(Collectors.toList());
	}

	/**
	 * Accumulates ancestor documents for all primary parents of the taxeneNode with index 0 being the highest most generic
	 * level and the last index being the lowest, most specific.
	 * @param taxeneNode
	 * @param taxonomyDocListAllLevel accumulates results
	 * @param ancestors accumulates results
	 */
	private void accumulateAncestors(TaxeneNodeEx taxeneNode,
			List<TaxonomyDocumentEx> taxonomyDocListAllLevel, List<BaseDocumentEx> ancestors) {
		if (taxeneNode == null) return;

		TaxeneNodeEx parentNode = TaxeneNodeEx.findFirstRelationOf("primaryParent", taxeneNode.getRelationships())
				.orElse(null);
		if (parentNode == null || parentNode.getDocument() == null) return;

		accumulateAncestors(parentNode, taxonomyDocListAllLevel, ancestors);
		if (parentNode.getDocument() instanceof TaxonomyDocumentEx) {
			taxonomyDocListAllLevel.add((TaxonomyDocumentEx) parentNode.getDocument());
		}
		if (parentNode.isTaxonomyDocument()) {
			ancestors.add(parentNode.getDocument());
		}
	}


	/**
	 * Same as {@link #getDocumentTaxeneComposite(BaseDocumentEx, String)} but for Curated Documents
	 * See docs for {@link CuratedDocumentTaxeneComposite} for more explanation about why this is special.
	 * @param document
	 * @param projection
	 * @return
	 */
	private CuratedDocumentTaxeneComposite getCuratedDocumentTaxeneComposite(CuratedDocumentEx document,
																			String projection) {
		if (document == null) return null;

		CuratedDocumentTaxeneComposite.Builder documentTaxeneCompositeBuilder = CuratedDocumentTaxeneComposite
				.curatedDocumentBuilder();
		documentTaxeneCompositeBuilder.document(document);

		// @formatter:off
		TaxeneTraverseRequestContext reqCtx = new TaxeneTraverseRequestContext.Builder()
				.setDocId(document.getDocumentId())
				.setActiveOnly(true).setDirection(Direction.OUT)
				.setIncludeDocumentSummaries(true)
				.setTraverseStrategy(TraverseStrategy.BREADTH_FIRST)
				.setRelationships(splitToSet("primaryParent"))
				.setNodeType(NodeType.INTERNAL_NODE)
				.setProjection(projection)
				.build();
		// @formatter:on

		TaxeneNodeEx taxeneNode = taxeneService.traverse(reqCtx);
		documentTaxeneCompositeBuilder.taxeneNode(taxeneNode);

		List<TaxonomyDocumentEx> taxonomyDocAllLevelList = new ArrayList<>();
		List<BaseDocumentEx> ancestors = new ArrayList<>();
		accumulateAncestors(taxeneNode, taxonomyDocAllLevelList, ancestors);
		documentTaxeneCompositeBuilder.taxonomyDocAllLevelList(taxonomyDocAllLevelList).ancestors(ancestors);

		return documentTaxeneCompositeBuilder.build();
	}
}
