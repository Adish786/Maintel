package com.about.mantle.model.tasks;

import static com.about.globe.core.util.CollectionsUtil.splitToSet;
import static com.about.mantle.model.tasks.DocumentTask.isValidPreviewRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.TaskParameters;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.TaxeneLevelType;
import com.about.mantle.model.extended.NodeAttribute;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TaxeneRelationshipEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ProgrammedSummaryDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.NodeType;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

import jersey.repackaged.com.google.common.collect.Lists;

@Tasks
public class TaxeneRelationTask {
	public static final Logger logger = LoggerFactory.getLogger(TaxeneRelationTask.class);

	private final TaxeneRelationService taxeneService;
	private final DeionSearchServiceTask deionSearchServiceTask;

	private static final List<TemplateTypeEx> EXCLUDED_TEMPLATES_FOR_DESCENDANT_ARTICLES = ImmutableList.of(TemplateTypeEx.REDIRECT, TemplateTypeEx.PROGRAMMEDSUMMARY, TemplateTypeEx.TOPIC, TemplateTypeEx.JWPLAYERVIDEO);

	private final List<Long> taxonomyDocIdsToSortWithDeionSearch;
	private final Long rootDocId;
	
	public TaxeneRelationTask(TaxeneRelationService taxeneService) {
		this(taxeneService, null, null);
	}

	public TaxeneRelationTask(TaxeneRelationService taxeneService, List<Long> taxonomyDocIdsToSortWithDeionSearch, DeionSearchServiceTask deionSearchServiceTask) {
		this(taxeneService, taxonomyDocIdsToSortWithDeionSearch, deionSearchServiceTask, null);
	}

	/*
	 * In order to use the taxeneRelationParentsPrimaryAndSecondary tasks either this
	 * constructor must be used or the rootDocId must be passed into the task method
	 */
	public TaxeneRelationTask(TaxeneRelationService taxeneService, List<Long> taxonomyDocIdsToSortWithDeionSearch, DeionSearchServiceTask deionSearchServiceTask, Long rootDocId) {
		this.taxeneService = taxeneService;
		this.taxonomyDocIdsToSortWithDeionSearch = taxonomyDocIdsToSortWithDeionSearch;
		this.deionSearchServiceTask = deionSearchServiceTask;
		this.rootDocId = rootDocId;
	}

	@Task(name = "taxeneRelationParents")
	@TimedComponent(category = "task")
	public TaxeneNodeEx traverseParents(@TaskParameter(name = "docId") Long docId) {
		return traverse(docId, Direction.OUT, TraverseStrategy.BREADTH_FIRST, null, null, null, null, null, null, null);
	}

	@Task(name = "taxeneRelationChildren")
	@TimedComponent(category = "task")
	public TaxeneNodeEx traverseChildren(@TaskParameter(name = "docId") Long docId) {
		return traverse(docId, Direction.IN, TraverseStrategy.BREADTH_FIRST, null, null, null, null, null, null, null);
	}

	@Deprecated
	public TaxeneNodeEx traverse(Long docId,
								 Direction direction,
								 TraverseStrategy traverseStrategy,
								 String relationships,
								 NodeType nodeType,
								 Integer limit,
								 Boolean includeConfigs,
								 Boolean includeDocumentSummaries,
								 String projection,
								 Integer maxDocPopulation) {
		TaxeneRelationModel model = new TaxeneRelationModel.Builder()
				.setDocId(docId)
				.setDirection(direction)
				.setTraverseStrategy(traverseStrategy)
				.setRelationships(relationships)
				.setNodeType(nodeType)
				.setLimit(limit)
				.setIncludeConfigs(includeConfigs)
				.setIncludeDocumentSummaries(includeDocumentSummaries)
				.setProjection(projection)
				.setMaxDocPopulation(maxDocPopulation)
				.build();
		return traverse(model);
	}

	@Task(name = "taxeneRelation")
	@TimedComponent(category = "task")
	public TaxeneNodeEx traverse(@TaskParameters TaxeneRelationModel model) {

		if (model.getDocId() == null || model.getDirection() == null) {
			throw new GlobeInvalidTaskParameterException("document Id and direction shouldn't be null");
		}
		TraverseStrategy traverseStrategy = model.getTraverseStrategy() != null
				? model.getTraverseStrategy()
				: TraverseStrategy.BREADTH_FIRST;

		//@formatter:off
		TaxeneTraverseRequestContext reqCtx = new TaxeneTraverseRequestContext.Builder()
										.setDocId(model.getDocId())
										.setActiveOnly(true)
										.setDirection(model.getDirection())
										.setIncludeConfigs(model.getIncludeConfigs())
										.setIncludeDocumentSummaries(model.getIncludeDocumentSummaries())
										.setLimit(model.getLimit())
										.setMaxDocPopulation(model.getMaxDocPopulation())
										.setTraverseStrategy(traverseStrategy)
										.setRelationships(splitToSet(model.getRelationships()))
										.setNodeType(model.getNodeType())
										.setProjection(model.getProjection())
										.setIsPreview(model.getIsPreview())
										.build();
		//@formatter:on
		return taxeneService.traverse(reqCtx);
	}

	/**
	 * This method is used to traverse all parents of a given {@link BaseDocumentEx}, it is used for creating a GTM
	 * tracking value for multi-parent taxonomy
	 *
	 * @param doc
	 *            a {@link BaseDocumentEx}
	 * @return a list of {@link TaxeneNodeEx} list, that represents the multi-parent taxonomy paths
	 */
	@Task(name = "taxeneRelationMultiParent")
	@TimedComponent(category = "task")
	public List<List<TaxeneNodeEx>> traverseMultiParent(@TaskParameter(name = "document") BaseDocumentEx doc) {
		List<List<TaxeneNodeEx>> listOfTaxList = new LinkedList<>();
		if (doc == null) return listOfTaxList;

		String projection = "{(shortHeading,templateType,docId)}";
		if (doc.getTemplateType() == TemplateTypeEx.TAXONOMY || doc.getTemplateType() == TemplateTypeEx.TAXONOMYSC) {
			listOfTaxList.add(getTaxonomyPath(doc.getDocumentId(), projection));
		} else {
			TaxeneNodeEx currentNode = traverse(doc.getDocumentId(), Direction.OUT, TraverseStrategy.ONE_LEVEL, null,
					null, null, false, true, projection, null);

			if (currentNode == null || currentNode.getRelationships() == null) return listOfTaxList;

			for (TaxeneRelationshipEx taxRelationship : currentNode.getRelationships().getList()) {
				List<TaxeneNodeEx> taxList = getTaxonomyPath(taxRelationship.getTargetNode().getDocId(), projection);

				/*
				 * Add only primary and secondary parent relationships, and add the primary parent relationship as the
				 * first relationship
				 */
				if (taxRelationship.getName().equals("primaryParent")) {
					listOfTaxList.add(0, taxList);
				} else if (taxRelationship.getName().equals("secondaryParent")) {
					listOfTaxList.add(taxList);
				}
			}
		}

		return listOfTaxList;
	}

	/*
	* @see {@link #getParentsPrimaryAndSecondary(Long, Integer, Boolean, String, Long, Boolean)}
	*/
	@Task(name = "taxeneRelationParentsPrimaryAndSecondary")
	@TimedComponent(category = "task")
	public List<TaxeneNodeEx> getParentsPrimaryAndSecondary(@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "includeAncestors") Boolean includeAncestors) {

		return getParentsPrimaryAndSecondary(docId, limit, includeAncestors, null);
	}


	/*
	 * @see {@link #getParentsPrimaryAndSecondary(Long, Integer, Boolean, String, Long, Boolean)}
	 */
	@Task(name = "taxeneRelationParentsPrimaryAndSecondary")
	@TimedComponent(category = "task")
	public List<TaxeneNodeEx> getParentsPrimaryAndSecondary(@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "includeAncestors") Boolean includeAncestors,
			@TaskParameter(name = "projection") String projection) {

		return getParentsPrimaryAndSecondary(docId, limit, includeAncestors, projection, rootDocId);
	}

	/*
	 * @see {@link #getParentsPrimaryAndSecondary(Long, Integer, Boolean, String, Long, Boolean)}
	 */
	@Task(name = "taxeneRelationParentsPrimaryAndSecondary")
	@TimedComponent(category = "task")
	public List<TaxeneNodeEx> getParentsPrimaryAndSecondary(@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "includeAncestors") Boolean includeAncestors,
			@TaskParameter(name = "projection") String projection,
			@TaskParameter(name = "rootDocId") Long rootDocId
			) {

		return getParentsPrimaryAndSecondary(docId, limit, includeAncestors, projection, rootDocId, null);
	}


	/*
	 * Returns a list of nodes which are the primary and non-primary parents/ancestors for a given docId
	 * @param docId
	 * @param limit
	 * @param includeAncestors
	 * @param projection
	 * @param rootDocId
	 * @param excludeJourneys
	 * @return a list of {@link TaxeneNodeEx} of the primary and non-primary parents of a document
	 */
	@Task(name = "taxeneRelationParentsPrimaryAndSecondary")
	@TimedComponent(category = "task")
	public List<TaxeneNodeEx> getParentsPrimaryAndSecondary(@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "limit") Integer limit,
			@TaskParameter(name = "includeAncestors") Boolean includeAncestors,
			@TaskParameter(name = "projection") String projection,
			@TaskParameter(name = "rootDocId") Long rootDocId,
			@TaskParameter(name = "excludeJourneys") Boolean excludeJourneys) {
		Integer maxPopulatedArticles = limit == null ? null : limit + 1;
		TraverseStrategy travereStrategy = (includeAncestors == null || includeAncestors)
				? TraverseStrategy.BREADTH_FIRST : TraverseStrategy.ONE_LEVEL;

		return getNodesPrimaryAndSecondary(docId, maxPopulatedArticles, Direction.OUT, travereStrategy, limit, false,
				true, projection, includeAncestors, rootDocId, excludeJourneys);
	}

	@Task(name = "breadcrumbByKey")
	public TaxeneNodeEx breadcrumbByKey(@TaskParameter(name = "key") Long key) {
		return getBreadcrumb(key);
	}

	@Task(name = "breadcrumb")
	public TaxeneNodeEx getBreadcrumb(@RequestContextTaskParameter RequestContext requestContext) {
		Long docId = ((VerticalUrlData) requestContext.getUrlData()).getDocId();
		return getBreadcrumb(docId);
	}

	@Task(name = "breadcrumb")
	public TaxeneNodeEx getBreadcrumb(@TaskParameter(name = "docId") Long docId) {
		TaxeneNodeEx taxeneNode = docId != null
				? traverse(new TaxeneRelationModel.Builder()
							.setDocId(docId)
							.setDirection(Direction.OUT)
							.setRelationships("primaryParent")
							.setIncludeDocumentSummaries(true)
							.build())
				: null;
		filterBreadcrumbByTaxonomyDocument(taxeneNode);
		return taxeneNode;
	}

	/**
	 * schemaBreadcrumb
	 *
	 * Used by schema.xml to render the breadcrumb model used in breadcrumbslist.ftl
	 * @param model model containing the following parameters:
	 *  	docId - docId of the document you want to get the breadcrumbs from
	 *  	isPreview - indicates whether the document is being viewed in preview mode or not,
	 *                  determines the cache key used by the taxene traverse call
	 * @return The schema breadcrumb should return a taxeneNode that has up to two more relationships from it.
	 */
	@Task(name = "schemaBreadcrumb")
	public List<TaxeneNodeEx> getSchemaBreadcrumb(@RequestContextTaskParameter RequestContext requestContext,
												  @TaskParameters SchemaBreadcrumbModel model) {

		Long docId = model.getDocId() != null ? model.getDocId() : ((VerticalUrlData) requestContext.getUrlData()).getDocId();
		boolean isPreview = model.getIsPreview() != null ? model.getIsPreview() : isValidPreviewRequest(requestContext.getParameters());

		TaxeneRelationModel taxeneRelationModel = new TaxeneRelationModel.Builder()
				.setDocId(docId)
				.setDirection(Direction.OUT)
				.setRelationships("primaryParent")
				.setIncludeDocumentSummaries(true)
				.setIsPreview(isPreview)
				.build();
		TaxeneNodeEx child = docId != null ? traverse(taxeneRelationModel) : null;

		//Remove non-taxonomy documents from the taxonomy chain from parent onwards
		filterBreadcrumbByTaxonomyDocument(child);

		TaxeneNodeEx parent = child != null ? child.getPrimaryParent() : null;

		List<TaxeneNodeEx> listOfNodes = new ArrayList<>();
		if(parent != null){ //verify not an orphan

			TaxeneNodeEx grandparent = parent.getPrimaryParent();

			if(grandparent !=null){ //check to see if grandparent exists

				if(grandparent.getPrimaryParent() !=null ){
					listOfNodes.add(grandparent);
				}//when there is no great grandparent, that means the grandparent is root,don't add grandparent

				listOfNodes.add(parent);
				listOfNodes.add(child);

			}//When there is no grandparent, the parent is root
			 //collapse on the case where there is a document attached only to the root or the document is an orphan
		}

		return listOfNodes.size() == 0 ? null: listOfNodes;
	}

	@Task(name = "levelByNode")
	public TaxeneLevelType getLevel(@TaskParameter(name = "breadcrumb") TaxeneNodeEx taxonomyNode) {
		// Ignore documents that are not taxonomy documents
		while (taxonomyNode != null && !taxonomyNode.isTaxonomyDocument())
			taxonomyNode = TaxeneNodeEx.findFirstRelationOf("primaryParent", taxonomyNode.getRelationships()).orElse(null);

		if (taxonomyNode == null) return null;

		return TaxeneLevelType.fromLevelId(getLevel(taxonomyNode, 0));
	}

	@Task(name = "levelByKey")
	public TaxeneLevelType getLevel(@TaskParameter(name = "docId") Long docId) {
		return getLevel(breadcrumbByKey(docId));
	}

	private int getLevel(TaxeneNodeEx taxonomyNode, int level) {
		TaxeneNodeEx parent = TaxeneNodeEx.findFirstRelationOf("primaryParent", taxonomyNode.getRelationships()).orElse(null);
		if (parent == null) return level;
		return getLevel(parent, level + 1);
	}

	@Task(name = "ancestor")
	public TaxeneNodeEx getAncestorNode(@TaskParameter(name = "breadcrumb") TaxeneNodeEx taxonomyNode,
			@TaskParameter(name = "taxeneLevel") Integer level) {
		List<TaxeneNodeEx> ancestors = createAncestorList(Lists.newArrayList(), taxonomyNode);
		if (level >= ancestors.size()) return null;
		return ancestors.get(ancestors.size() - level - 1);
	}

	@Task(name = "ancestors")
	public List<TaxeneNodeEx> getAncestorNodes(@TaskParameter(name = "breadcrumb") TaxeneNodeEx taxonomyNode) {
		return Lists.reverse(createAncestorList(Lists.newArrayList(), taxonomyNode));
	}

	/**
	 * Filters out documents from descendantArticles based on some criteria. Meant to
	 * be overridden. Defaults to filtering out the list in EXCLUDED_TEMPLATES_FOR_DESCENDANT_ARTICLES.
	 *
	 * @param node
	 *            the node on which to filter documents
	 *
	 */
	protected boolean filterDescendantArticles(TaxeneNodeEx node) {
		return !EXCLUDED_TEMPLATES_FOR_DESCENDANT_ARTICLES.contains(node.getDocument().getTemplateType())
				// Filtering out noIndex = True documents so hidden recipes aren't displayed
				&& !Boolean.TRUE.equals(node.getDocument().getNoIndex());
	}

	@Task(name = "descendantArticles")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> getDescendantArticles(@RequestContextTaskParameter RequestContext requestContext,
													  @TaskParameters DescendantArticlesModel model) {
		Long docId = model.getDocId() != null ? model.getDocId() : ((VerticalUrlData) requestContext.getUrlData()).getDocId();
		String relationships = model.getRelationships() != null ? model.getRelationships() : "primaryParent";

		// if the current page we are on is included in the pages we would like a different search/sort algorithm on then
		// use the deion search service.
		// Note: this will only apply this change to the "all" tab on taxSC docs, if we would like to include the other tabs
		// we will first have to get their primaryAndSecondaryAncestors (which is config enabled too) and check if those are in the list as well
		if (taxonomyDocIdsToSortWithDeionSearch != null && taxonomyDocIdsToSortWithDeionSearch.contains(docId)) {
			return deionSearchServiceTask.deionSearchForTaxonomyLists(docId, model.getLimit(), relationships);
		}

		TaxeneNodeEx node = traverse(docId, Direction.IN, TraverseStrategy.BREADTH_FIRST, relationships, null,
				model.getLimit(), null, true, null, model.getMaxDocPopulation());

		List<BaseDocumentEx> descendantDocs = node.getDescendantRelationDocuments().stream()
				.filter(docNode -> docNode.getDocument() != null && filterDescendantArticles(docNode))
				.map(docNode -> docNode.getDocument())
				.collect(Collectors.toList());
		return descendantDocs.isEmpty() ? null : descendantDocs;
	}

	@Task(name = "extendedBreadcrumb")
	public TaxeneNodeEx getExtendedBreadcrumb(@TaskParameter(name = "breadcrumb") TaxeneNodeEx node)
			throws JsonProcessingException {
		return getExtendedBreadcrumb(node, false);
	}

	@Task(name = "extendedBreadcrumb")
	public TaxeneNodeEx getExtendedBreadcrumb(@TaskParameter(name = "breadcrumb") TaxeneNodeEx node,
			@TaskParameter(name = "promoteActive") boolean promoteActive) throws JsonProcessingException {
		return getExtendedBreadcrumb(node, 2, promoteActive);
	}

	@Task(name = "extendedBreadcrumb")
	public TaxeneNodeEx getExtendedBreadcrumb(@TaskParameter(name = "breadcrumb") TaxeneNodeEx node,
			@TaskParameter(name = "rootLevel") int rootLevel) {
		return getExtendedBreadcrumb(node, rootLevel, false);
	}

	@Task(name = "extendedBreadcrumb")
	@TimedComponent(category = "task")
	public TaxeneNodeEx getExtendedBreadcrumb(@TaskParameter(name = "breadcrumb") TaxeneNodeEx node,
			@TaskParameter(name = "rootLevel") int rootLevel,
			@TaskParameter(name = "promoteActive") boolean promoteActive) {
		// Skip not taxonomy documents, as we might have parents that are not taxonomy documents
		while (!node.isTaxonomyDocument())
			node = TaxeneNodeEx.findFirstRelationOf("primaryParent", node.getRelationships()).orElse(null);

		return getExtendedBreadcrumb(node, null, TaxeneLevelType.fromLevelId(rootLevel), getLevel(node), null,
				promoteActive);
	}

	/**
	 * Used by the mntl-taxonomysc-child-nodes component to know what taxonomy child nodes exist under the current node.
	 * @param docId
	 * @return
	 */
	@Task(name = "taxonomyChildNodes")
	@TimedComponent(category = "task")
	public List<TaxeneNodeEx> getTaxonomyChildNodes(@TaskParameter(name = "docId") Long docId) {
		// Since some verticals would like to include secondary children in their taxSC children this call must include
		// secondaryRelationships so the `INTERNAL_NODE` will include taxSC children that only include secondaryRelationships
		// getChildren will still filter only primary relationships to the taxSC doc
		// Verticals can still specify the types of relationships they would like to see in their child tabs through
		// the removeSinglePageChildren dependency in the mntl-taxonomysc-child-nodes component
		TaxeneNodeEx node = traverse(docId, Direction.IN, TraverseStrategy.ONE_LEVEL, "primaryParent,secondaryParent", NodeType.INTERNAL_NODE, null, false, true, null, null);
		List<TaxeneNodeEx> answer = node.getRelationships().stream()
				.map(relationship -> relationship.getTargetNode())
				.filter(child -> child.isTaxonomyDocument())
				.collect(Collectors.toList());
		return answer;
	}

	/**
	 * Filters out relationships from taxene node based on some criteria. Meant to
	 * be overridden.
	 *
	 * @param node
	 *            the node on which to filter relationships
	 *
	 */
	protected void filterChildren(TaxeneNodeEx node) {
	}

	private TaxeneNodeEx getExtendedBreadcrumb(TaxeneNodeEx node, TaxeneNodeEx descendants, TaxeneLevelType haltLevel,
			TaxeneLevelType currentLevel, TaxeneNodeEx activeChild, boolean promoteActive) {
		if (node == null) return descendants;
		TaxeneNodeEx populatedNode = TaxeneNodeEx.copy(traverse(node.getDocId(), Direction.IN, TraverseStrategy.ONE_LEVEL, "primaryParent",NodeType.INTERNAL_NODE, null, false, true, null, null));


		filterChildren(populatedNode);
		filterChildrenByTaxonomyDocument(populatedNode);

		if (promoteActive) promoteActiveChild(populatedNode, activeChild);
		setDescendants(populatedNode, descendants);
		return currentLevel == haltLevel ? populatedNode
				: getExtendedBreadcrumb(node.getPrimaryParent(), populatedNode, haltLevel, currentLevel.getParent(),
						node, promoteActive);
	}

	private void setDescendants(TaxeneNodeEx node, TaxeneNodeEx descendants) {
		if (node != null && descendants != null) {
			List<TaxeneRelationshipEx> children = node.getRelationships().getList();
			List<TaxeneRelationshipEx> copy = Lists.newArrayList(children);
			// Find the fully populated ancestor taxonomy document in the
			// current node's children list
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).getTargetNode().getDocId().equals(descendants.getDocId())) {
					TaxeneRelationshipEx relationship = new TaxeneRelationshipEx(copy.get(i).getName(),copy.get(i).getWeight(),descendants);
					copy.set(i, relationship);
					break;
				}
			}
			node.setRelationships(SliceableListEx.of(copy));
		}
	}


	private void promoteActiveChild(TaxeneNodeEx node, TaxeneNodeEx activeChild) {
		if (node != null && activeChild != null) {

			List<TaxeneRelationshipEx> children = node.getRelationships().getList();
			List<TaxeneRelationshipEx> copy = Lists.newArrayList(children);
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).getTargetNode().getDocId().equals(activeChild.getDocId())) {
					TaxeneRelationshipEx promotedChild = copy.remove(i);
					copy.add(0, promotedChild);
					break;
				}
			}

			node.setRelationships(SliceableListEx.of(copy));
		}

	}

	private List<TaxeneNodeEx> createAncestorList(List<TaxeneNodeEx> ancestors, TaxeneNodeEx taxeneNode) {
		if (taxeneNode == null) return ancestors;
		if (taxeneNode.isTaxonomyDocument()) ancestors.add(taxeneNode);
		return createAncestorList(ancestors, TaxeneNodeEx.findFirstRelationOf("primaryParent", taxeneNode.getRelationships()).orElse(null));
	}

	/**
	 * A method to filter the breadcrumb and remove the direct parents that are not taxonomy documents
	 *
	 * @param node
	 *            the {@link TaxeneNodeEx} that need to be filtered
	 */
	protected void filterBreadcrumbByTaxonomyDocument(TaxeneNodeEx node) {
		if (node == null) return;

		TaxeneRelationshipEx tmpNode = TaxeneNodeEx.findFirstRelationshipOf("primaryParent", node.getRelationships()).orElse(null);
		while (tmpNode != null && !tmpNode.getTargetNode().isTaxonomyDocument())
			tmpNode = TaxeneNodeEx.findFirstRelationshipOf("primaryParent", tmpNode.getTargetNode().getRelationships()).orElse(null);

		final TaxeneRelationshipEx updatedRelationship = tmpNode;
		SliceableListEx<TaxeneRelationshipEx> updatedRelationships = SliceableListEx.of(node.getRelationships().stream()
																			.map(r -> {
																				if("primaryParent".equals(r.getName())) {
																					return updatedRelationship;
																				} else {
																					return r;
																				}
																			}).collect(Collectors.toList()));
		node.setRelationships(updatedRelationships);
	}

	/**
	 * A method to filter the breadcrumb's children and remove the documents that are not taxonomy documents (except
	 * journey root documents). Note: Children of the given node should all have a TAXONOMY node type
	 *
	 * @param node
	 *            the {@link TaxeneNodeEx} that need to be filtered
	 */
	@JsonIgnore
	protected void filterChildrenByTaxonomyDocument(TaxeneNodeEx node) {
		if (node == null) return;

		List<TaxeneNodeEx> children = TaxeneNodeEx.fetchAllRelations(node.getRelationships());

		for (TaxeneNodeEx child : children) {

			BaseDocumentEx document = child.getDocument();
			/*
			 * If document is not a taxonomy document, and not a programmed summary document then
			 * take the short heading from the node attribute.
			 */
			if (child.isTaxonomyDocument() || document instanceof ProgrammedSummaryDocumentEx) continue;

			TaxeneNodeEx nodeV2 = getV2Node(child.getDocId());
			if (nodeV2 == null) continue;

			document.setShortHeading(nodeV2.getNodeAttributeValue(NodeAttribute.SHORT_HEADING));
		}
	}

	/**
	 * A method that returns a V2 taxene node which includes the node attributes
	 *
	 * @param docId
	 *            the document id of the node
	 * @return a V2 taxene node
	 */
	private TaxeneNodeEx getV2Node(Long docId) {
		//@formatter:off
		TaxeneTraverseRequestContext traverseReqCtx = new TaxeneTraverseRequestContext.Builder()
					.setDirection(Direction.IN)
					.setDocId(docId)
					.setLimit(0)
					.build();
		//@formatter:on
		return taxeneService.traverse(traverseReqCtx);
	}

	protected List<TaxeneNodeEx> getTaxonomyPath(Long docId, String projection) {
		List<TaxeneNodeEx> taxList = new LinkedList<>();
		if (docId == null) return taxList;

		TaxeneNodeEx tmpNode = traverse(docId, Direction.OUT, TraverseStrategy.BREADTH_FIRST, null, null, null, false,
				true, projection, null);

		if (tmpNode == null) return taxList;

		// Adding current node
		taxList.add(0, tmpNode);

		// Adding ancestors
		includeParents(taxList, tmpNode.getRelationships());

		return taxList;
	}

	private void includeParents(List<TaxeneNodeEx> list, SliceableListEx<TaxeneRelationshipEx> relationships) {
		if (relationships == null || relationships.isEmpty()) return;

		// Getting the first element since taxonomy documents has only one parent
		TaxeneNodeEx targetNode = relationships.getList().get(0).getTargetNode();
		list.add(0, targetNode);

		includeParents(list, targetNode.getRelationships());
	}

	protected List<TaxeneNodeEx> getNodesPrimaryAndSecondary(Long docId, Integer nodeMax, Direction direction,
			TraverseStrategy traverseStrategy, Integer limit, Boolean includeConfigs, Boolean includeDocumentSummaries,
			String projection, Boolean includeAncestors, Long rootDocId, Boolean excludeJourneys) {
		TaxeneNodeEx currentNode = traverse(docId, direction, traverseStrategy, null, null, limit, includeConfigs,
				includeDocumentSummaries, projection, nodeMax);

		if (currentNode == null || currentNode.getRelationships() == null) return new ArrayList<>();

		return getRelationshipNodes(currentNode, includeAncestors, rootDocId, excludeJourneys);
	}

	private List<TaxeneNodeEx> getRelationshipNodes(TaxeneNodeEx node, Boolean includeAncestors, Long rootDocId,
			Boolean excludeJourneys) {

		if (rootDocId == null) {
			throw new GlobeException("Root docId must not be null to use this task.");
		}

		List<TaxeneRelationshipEx> listOfCurrent = node.getRelationships().getList();
		List<TaxeneNodeEx> relations = new LinkedList<>();
		List<TaxeneRelationshipEx> pointerToNext = new LinkedList<>();

		for (TaxeneRelationshipEx taxRelationship : listOfCurrent) {
			TaxeneNodeEx nodeInRelationship = taxRelationship.getTargetNode();

			if (excludeJourneys != null && excludeJourneys) {
				if (taxRelationship.getName().equals("journey")) {
					continue;
				}
			}

			//Only add the node if it is not the homepage
			if (!rootDocId.equals(nodeInRelationship.getDocId())) {
				if (taxRelationship.getName().equals("primaryParent")) {
					relations.add(0, nodeInRelationship);
				} else {
					relations.add(nodeInRelationship);
				}
			}

			if (includeAncestors && nodeInRelationship.getRelationships() != null) {
				pointerToNext.addAll(nodeInRelationship.getRelationships().getList());
			}
		}

		if (!includeAncestors) return relations;

		listOfCurrent = pointerToNext;
		while (listOfCurrent.size() > 0) {
			pointerToNext = new ArrayList<>();

			for (TaxeneRelationshipEx taxRelationship : listOfCurrent) {
				TaxeneNodeEx nodeInRelationship = taxRelationship.getTargetNode();

				if (!rootDocId.equals(nodeInRelationship.getDocId())) {
					relations.add(0, nodeInRelationship);
				}

				if (nodeInRelationship.getRelationships() != null) {
					pointerToNext.addAll(nodeInRelationship.getRelationships().getList());
				}
			}
			listOfCurrent = pointerToNext;
		}

		return relations;
	}

	public static class TaxeneRelationModel {
		private Long docId;
		private Direction direction;
		private TraverseStrategy traverseStrategy;
		private String relationships;
		private NodeType nodeType;
		private Integer limit;
		private Boolean includeConfigs;
		private Boolean includeDocumentSummaries;
		private String projection;
		private Integer maxDocPopulation;
		private Boolean isPreview;

		public TaxeneRelationModel() {}
		public TaxeneRelationModel(Builder builder) {
			docId = builder.docId;
			direction = builder.direction;
			traverseStrategy = builder.traverseStrategy;
			relationships = builder.relationships;
			nodeType = builder.nodeType;
			limit = builder.limit;
			includeConfigs = builder.includeConfigs;
			includeDocumentSummaries = builder.includeDocumentSummaries;
			projection = builder.projection;
			maxDocPopulation = builder.maxDocPopulation;
			isPreview = builder.isPreview;
		}

		public static class Builder {
			private Long docId;
			private Direction direction;
			private TraverseStrategy traverseStrategy;
			private String relationships;
			private NodeType nodeType;
			private Integer limit;
			private Boolean includeConfigs;
			private Boolean includeDocumentSummaries;
			private String projection;
			private Integer maxDocPopulation;
			private Boolean isPreview;

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public Builder setDirection(Direction direction) {
				this.direction = direction;
				return this;
			}

			public Builder setTraverseStrategy(TraverseStrategy traverseStrategy) {
				this.traverseStrategy = traverseStrategy;
				return this;
			}

			public Builder setRelationships(String relationships) {
				this.relationships = relationships;
				return this;
			}

			public Builder setNodeType(NodeType nodeType) {
				this.nodeType = nodeType;
				return this;
			}

			public Builder setLimit(Integer limit) {
				this.limit = limit;
				return this;
			}

			public Builder setIncludeConfigs(Boolean includeConfigs) {
				this.includeConfigs = includeConfigs;
				return this;
			}

			public Builder setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
				this.includeDocumentSummaries = includeDocumentSummaries;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public Builder setMaxDocPopulation(Integer maxDocPopulation) {
				this.maxDocPopulation = maxDocPopulation;
				return this;
			}

			public Builder setIsPreview(Boolean isPreview) {
				this.isPreview = isPreview;
				return this;
			}

			public TaxeneRelationModel build() {
				return new TaxeneRelationModel(this);
			}
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		public TraverseStrategy getTraverseStrategy() {
			return traverseStrategy;
		}

		public void setTraverseStrategy(TraverseStrategy traverseStrategy) {
			this.traverseStrategy = traverseStrategy;
		}

		public String getRelationships() {
			return relationships;
		}

		public void setRelationships(String relationships) {
			this.relationships = relationships;
		}

		public NodeType getNodeType() {
			return nodeType;
		}

		public void setNodeType(NodeType nodeType) {
			this.nodeType = nodeType;
		}

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public Boolean getIncludeConfigs() {
			return includeConfigs;
		}

		public void setIncludeConfigs(Boolean includeConfigs) {
			this.includeConfigs = includeConfigs;
		}

		public Boolean getIncludeDocumentSummaries() {
			return includeDocumentSummaries;
		}

		public void setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
			this.includeDocumentSummaries = includeDocumentSummaries;
		}

		public String getProjection() {
			return projection;
		}

		public void setProjection(String projection) {
			this.projection = projection;
		}

		public Integer getMaxDocPopulation() {
			return maxDocPopulation;
		}

		public void setMaxDocPopulation(Integer maxDocPopulation) {
			this.maxDocPopulation = maxDocPopulation;
		}

		public Boolean getIsPreview() {
			return isPreview;
		}

		public void setIsPreview(Boolean isPreview) {
			this.isPreview = isPreview;
		}
	}

	public static class SchemaBreadcrumbModel {
		private Long docId;
		private Boolean isPreview;

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public Boolean getIsPreview() {
			return isPreview;
		}

		public void setIsPreview(Boolean isPreview) {
			this.isPreview = isPreview;
		}
	}

	public static class DescendantArticlesModel {
		private Integer limit;
		private Integer maxDocPopulation;
		private String relationships;
		private Long docId;

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public Integer getMaxDocPopulation() {
			return maxDocPopulation;
		}

		public void setMaxDocPopulation(Integer maxDocPopulation) {
			this.maxDocPopulation = maxDocPopulation;
		}

		public String getRelationships() {
			return relationships;
		}

		public void setRelationships(String relationships) {
			this.relationships = relationships;
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}
	}
}
