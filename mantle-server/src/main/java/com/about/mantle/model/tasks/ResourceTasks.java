package com.about.mantle.model.tasks;

import com.about.globe.core.definition.component.ResourceHandle;
import com.about.globe.core.definition.resource.ResolvedResource;
import com.about.globe.core.definition.resource.ResourceAggregator;
import com.about.globe.core.definition.template.RenderManifest;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.logging.ContextBasedTracer;
import com.about.globe.core.logging.GlobeContextBasedTracerHolder;
import com.about.globe.core.module.Module;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tasks
public class ResourceTasks {

	public static enum ResourceBundleType {
		Template,
		Site,
		Tiered,
		Template_Lazy,
		Site_Lazy,
		Tiered_Lazy
	}

	private final ResourceAggregator resourceAggregator;
	private final Module module;

	private static final ContextBasedTracer tracer = GlobeContextBasedTracerHolder.getDefault();
	private Logger log = LoggerFactory.getLogger(ResourceTasks.class);

	public ResourceTasks(ResourceAggregator resourceAggregator, Module module) {

		this.resourceAggregator = resourceAggregator;
		this.module = module;
	}

	/**
	 * Pulls all scripts for this request from the manifest.  Filters by group and evaluated flag.
     * Is called by resource-script.ftl
	 * @param requestContext
	 * @param evaluatedOnly
	 * @param group
	 * @param manifest
	 * @return
	 */
	public List<ResolvedResource> scripts(RequestContext requestContext, Boolean evaluatedOnly, String group,
										  List<String> groupOrder, RenderManifest manifest) {

		List<ResolvedResource> answer = pullRenderTimeResourcesFromManifest(
				requestContext,
				evaluatedOnly,
				group,
				groupOrder,
				manifest,
				renderManifest -> renderManifest.getScripts().stream());

		return answer;
	}

	/**
	 * Pulls all css for this request from the manifest.  Filters by group and evaluated flag.
	 * Is called by resource-stylesheet.ftl
	 * @param requestContext
	 * @param evaluatedOnly
	 * @param group
	 * @param manifest
	 * @return
	 */
	public List<ResolvedResource> stylesheets(RequestContext requestContext, Boolean evaluatedOnly, String group,
											 List<String> groupOrder, RenderManifest manifest) {

		/* Stylesheets should not be aggregated for mobile devices. Stylesheets
		 * are inlined on mobile and aggregating them causes each unique aggregated
		 * stylesheet to be added to the FTL cache.
		 */
		boolean allowAggregate = requestContext.getUserAgent().getDeviceCategory() != DeviceCategory.SMARTPHONE;

		List<ResolvedResource> answer = pullRenderTimeResourcesFromManifest(
				requestContext,
				evaluatedOnly,
				group,
				groupOrder,
				manifest,
				renderManifest -> renderManifest.getStylesheets().stream(),
				allowAggregate);

		return answer;
	}
	
	/**
	 * Pulls all SVGs for this request from the manifest.  Filters by group and evaluated flag.
	 * Is called by resource-stylesheet.ftl
	 * @param requestContext
	 * @param evaluatedOnly
	 * @param group
	 * @param manifest
	 * @return
	 */
	public List<ResolvedResource> svgs(RequestContext requestContext, Boolean evaluatedOnly, String group,
											 List<String> groupOrder, RenderManifest manifest) {

		List<ResolvedResource> answer = pullRenderTimeResourcesFromManifest(
				requestContext,
				evaluatedOnly,
				group,
				groupOrder,
				manifest,
				renderManifest -> renderManifest.getSvgs().stream());

		return answer;
	}

	/**
	 * Pulls resources for this request from the manifest.  Filters by group and evaluated flag.
	 * Is called by resource-stylesheet.ftl, resource-script.ftl, and resource-svg.ftl
	 * @param requestContext
	 * @param evaluatedOnly
	 * @param group
	 * @param groupOrder
	 * @param manifest
	 * @param resourceSource - a function that, given a RenderManifest, returns a stream of all of the
	 *                       requested resources for that manifest
	 * @return
	 */
	private List<ResolvedResource> pullRenderTimeResourcesFromManifest(RequestContext requestContext,
																	   Boolean evaluatedOnly,
																	   String group,
																	   List<String> groupOrder,
																	   RenderManifest manifest,
																	   Function<RenderManifest,
																	   Stream<ResolvedResource>> resourceSource) {
		return pullRenderTimeResourcesFromManifest(requestContext, evaluatedOnly, group, groupOrder, manifest, resourceSource, true);
	}

	/**
	 * Pulls resources for this request from the manifest.  Filters by group and evaluated flag.
	 * Is called by resource-stylesheet.ftl, resource-script.ftl, and resource-svg.ftl
	 * @param requestContext
	 * @param evaluatedOnly
	 * @param group
	 * @param groupOrder
	 * @param manifest
	 * @param resourceSource - a function that, given a RenderManifest, returns a stream of all of the
	 *                       requested resources for that manifest
	 * @param allowAggregate - specifies whether resources pulled from manifest are allowed to be
	 *                       aggregated into a single resource or must kept separate
	 * @return
	 */
	private List<ResolvedResource> pullRenderTimeResourcesFromManifest(RequestContext requestContext,
																	   Boolean evaluatedOnly,
																	   String group,
																	   List<String> groupOrder,
																	   RenderManifest manifest,
																	   Function<RenderManifest,
																	   Stream<ResolvedResource>> resourceSource,
																	   boolean allowAggregate) {

		Supplier<Stream<ResolvedResource>> resourceStreamSupplier = () -> RenderManifest.flattenDepthFirst(manifest).flatMap(resourceSource);

		Stream<ResolvedResource> resourceStream = resourceStreamSupplier.get()
				.filter(createResourceFilter(evaluatedOnly, ImmutableSet.of(group), requestContext));

		if (groupOrder != null && !groupOrder.isEmpty()) {
			int indexOfGroup = groupOrder.indexOf(group);
			List<String> groupsToFilter = indexOfGroup == -1 ? groupOrder : groupOrder.subList(0, indexOfGroup);
			Set<String> resourcesToFilter = resourceStreamSupplier.get()
					.filter(createResourceFilter(evaluatedOnly, ImmutableSet.copyOf(groupsToFilter), requestContext))
					.map(resolvedResource -> resolvedResource.getResourceHandle().getPath())
					.collect(Collectors.toSet());
			if (!resourcesToFilter.isEmpty()) {
				resourceStream = resourceStream.filter(resolvedResource -> !resourcesToFilter.contains(resolvedResource.getResourceHandle().getPath()));
			}
		}

		Stream<ResolvedResource> returnStream = resourceStream.distinct();
		if (allowAggregate) {
			// aggregates if applicable
			return returnStream
					.collect(resourceAggregator.createAggregateCollector(requestContext))
					.collect(Collectors.toList());

		}
		return returnStream.collect(Collectors.toList());
	}

	private Predicate<ResolvedResource> createResourceFilter(boolean evaluated,
															 Set<String> groups,
															 RequestContext requestContext) {
	    return resolvedResource -> {

			ResourceHandle resourceHandle = resolvedResource.getResourceHandle();
			if (tracer.isEnabled())
				tracer.trace("Looking for evaluated: {}, groups: {}, and must be renderable", evaluated, groups == null ? "null" : String.join(",", groups));

			boolean evaluatedMatch = evaluated == resourceHandle.isEvaluated();
			boolean groupMatch = groups == null || groups.isEmpty() || groups.contains(resourceHandle.getGroup());
			boolean renderable = resourceHandle.isRenderable(requestContext);

			boolean answer = evaluatedMatch && groupMatch && renderable;

			if (!answer) {
				if (tracer.isEnabled())
					tracer.trace("Resource did not match predicate.  Did 'evaluated' match? {}.  " +
						"Did 'group' match? {}.  Is renderable? {}", evaluatedMatch, groupMatch, renderable);
			} else {
				if (tracer.isEnabled())
					tracer.trace("Resource matched predicate");
			}

			return answer;
		};
	}

	@Task(name = "moduleVersions")
	public Map<String, String> moduleVersions(@TaskParameter(name = "stripLastChar") Boolean stripLastChar) {

		Map<String, String> moduleVersions = new HashMap<>();

		moduleVersions(module, moduleVersions, stripLastChar);

		return moduleVersions;
	}

	private void moduleVersions(Module module, Map<String, String> moduleVersions, boolean stripLastChar) {

		String id = module.getDefinition().getId();
		if (stripLastChar) id = id.substring(0, id.length() - 1);

		String version = module.getProjectInfo().getVersion();

		moduleVersions.put(id, version);

		for (Module parent : module.getParents()) {
			moduleVersions(parent, moduleVersions, stripLastChar);
		}
	}
}
