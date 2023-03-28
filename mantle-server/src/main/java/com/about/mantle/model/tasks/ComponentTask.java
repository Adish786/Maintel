package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.TemplateComponentResolver;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.pl.PatternLibraryComponentCounts;

@Tasks
public class ComponentTask {
	/**
	 * Returns info map for each component registered in globe.
	 * PS - Currently, it gives back only pattern library info.
	 * In future, we could add other info in the map associated with each component.
	 */
	@Task(name = "listComponents")
	public Map<String, Map<String, Object>> listComponents(@RequestContextTaskParameter RequestContext requestContext) {
		Map<String, Map<String, Object>> componentIdToInfo = new HashMap<>();
		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();

		for (String id : componentResolver.getIds()) {
			TemplateComponent component = componentResolver.resolve(id).getInfoComponent();
			componentIdToInfo.put(id, getPatternLibraryInfo(component));
		}
		return componentIdToInfo;
	}

	/**
	 * Returns pattern library information for a component.
	 */
	private Map<String, Object> getPatternLibraryInfo(TemplateComponent component) {
		Map<String, Object> plInfo = new HashMap<>();
		String uri = PatternLibraryTask.getPatternLibraryUriFromComponent(component);
		plInfo.put("pl-url", uri);
		if (uri != null && !uri.isEmpty()) {
			plInfo.put("pl-name", stripToNull(component.getDisplayName()));
			plInfo.put("pl-category", stripToNull(component.getCategory()));
			plInfo.put("pl-tags", stripToNull(component.getTags()));
			plInfo.put("pl-preview-type", stripToNull(component.getPreviewType()));
		}
		return plInfo;
	}

	/**
	 * Returns count of total components and components supported by pattern library and list of
	 * unsupported components. Counts/list excludes mantle (mntl-) components.
	 * @param requestContext
	 * @return
	 */
	@Task(name = "countPatternLibraryEntries")
	public PatternLibraryComponentCounts countPatternLibraryEntries(@RequestContextTaskParameter RequestContext requestContext) {
		int componentsWithInfo = 0;
		List<String> unsupportedComponents = new ArrayList<>();
		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();
		for (String id : componentResolver.getIds().stream().filter(id -> !id.startsWith("mntl-")).collect(Collectors.toList())) {
			TemplateComponent component = componentResolver.resolve(id).getInfoComponent();
			if (component != null) {
				componentsWithInfo++;
			} else {
				unsupportedComponents.add(id);
			}
		}
		Collections.sort(unsupportedComponents);
		PatternLibraryComponentCounts response = new PatternLibraryComponentCounts(componentsWithInfo, componentResolver.getIds().size());
		response.setUnsupportedComponents(unsupportedComponents);
		return response;
	}


}
