package com.about.mantle.model.tasks;

import java.util.List;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TaxeneRelationshipEx;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;

import jersey.repackaged.com.google.common.collect.Lists;

@Tasks
public class JourneyTaxeneRelationTask {
	private final TaxeneRelationTask taxeneRelationTask;

	public JourneyTaxeneRelationTask(TaxeneRelationTask taxeneRelationTask) {
		this.taxeneRelationTask = taxeneRelationTask;
	}

	@Task(name = "journeyBreadcrumb")
	public TaxeneNodeEx journeyBreadcrumb(@RequestContextTaskParameter RequestContext requestContext) {
		return journeyBreadcrumb(requestContext, null);
	}

	@Task(name = "journeyBreadcrumb")
	public TaxeneNodeEx journeyBreadcrumb(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "projection") String projection) {
		Long docId = ((VerticalUrlData) requestContext.getUrlData()).getDocId();
		return taxeneRelationTask.traverse(docId, Direction.OUT, TraverseStrategy.BREADTH_FIRST,
				"journey,primaryParent", null, null, false, true, projection, null);
	}

	@Task(name = "journeyAncestors")
	public List<TaxeneNodeEx> journeyAncestors(@RequestContextTaskParameter RequestContext requestContext) {
		return journeyAncestors(requestContext, null);
	}

	@Task(name = "journeyAncestors")
	public List<TaxeneNodeEx> journeyAncestors(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "projection") String projection) {
		return journeyAncestors(requestContext, projection, null);
	}

	@Task(name = "journeyAncestors")
	public List<TaxeneNodeEx> journeyAncestors(@TaskParameter(name = "journeyBreadcrumb") TaxeneNodeEx taxonomyNode) {
		return Lists.reverse(createAncestorList(Lists.newArrayList(), taxonomyNode));
	}

	@Task(name = "journeyAncestorsFromLevel")
	public List<TaxeneNodeEx> journeyAncestorsFromLevel(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "fromLevel") Integer level) {
		return journeyAncestors(requestContext, null, level);
	}

	@Task(name = "journeyAncestorsFromLevel")
	public List<TaxeneNodeEx> journeyAncestorsFromLevel(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "projection") String projection, @TaskParameter(name = "fromLevel") Integer level) {
		return journeyAncestors(requestContext, projection, level);
	}

	/*
	 * A method for getting all journey ancestors, starting from a given level, for-example: level (0) represents the
	 * root.
	 */
	private List<TaxeneNodeEx> journeyAncestors(RequestContext requestContext, String projection, Integer level) {
		TaxeneNodeEx breadcrumb = journeyBreadcrumb(requestContext, projection);

		if (breadcrumb == null) return null;
		List<TaxeneNodeEx> ancestors = journeyAncestors(breadcrumb);

		if (ancestors == null || level == null || level == 0 || level >= ancestors.size()) return ancestors;
		return ancestors.subList(level, ancestors.size());
	}

	private List<TaxeneNodeEx> createAncestorList(List<TaxeneNodeEx> ancestors, TaxeneNodeEx taxeneNode) {
		if (taxeneNode != null && taxeneNode.getRelationships() != null) {
			ancestors.add(taxeneNode);
			for (TaxeneRelationshipEx relationship : taxeneNode.getRelationships()) {
				ancestors = createAncestorList(ancestors, relationship.getTargetNode());
			}
		}
		return ancestors;
	}
}
