package com.about.mantle.model.utils;

import static org.mockito.Mockito.mock;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.about.globe.core.definition.common.Model;
import com.about.globe.core.definition.common.Property;
import com.about.globe.core.definition.common.TaskModel;
import com.about.globe.core.definition.loader.TemplateComponentLoader;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.xml.XmlTemplateComponent;
import com.about.globe.core.definition.template.xml.XmlTemplates;
import com.about.globe.core.expression.ExpressionEvaluator;
import com.about.globe.core.expression.spring.SpringExpressionEvaluator;
import com.about.globe.core.module.ModuleEntry;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.task.registry.DefaultTasksRegistry;
import com.about.globe.core.task.registry.TasksRegistry;
import com.about.hippodrome.util.projectinfo.ProjectInfo;

public class ComponentLoaderUtils {

	private final TemplateComponentLoader loader;

	public ComponentLoaderUtils(List<Object> tasks) {
		TasksRegistry tasksRegistry = tasks == null ? mock(TasksRegistry.class) : new DefaultTasksRegistry(() -> tasks);
		ExpressionEvaluator expressionEvaluator = new SpringExpressionEvaluator(new SpelExpressionParser(),
				mock(CoreRenderUtils.class), mock(ProjectInfo.class));
		loader = new TemplateComponentLoader(null, tasksRegistry, expressionEvaluator);
	}

	/**
	 * This method returns a map with all template components, given in the templatesXml String
	 * 
	 * @param templatesXml
	 *            a String that represents the templates xml
	 * @return a map with all template components
	 * @throws Exception
	 */
	public Map<String, TemplateComponent> loadTemplates(String templatesXml) throws Exception {
		if (StringUtils.isEmpty(templatesXml)) return null;

		Unmarshaller unmarshaller = JAXBContext.newInstance(XmlTemplates.class).createUnmarshaller();
		XmlTemplates xmlTemplates = (XmlTemplates) unmarshaller.unmarshal(new StringReader(templatesXml));

		Map<String, TemplateComponent> loadedComponents = new HashMap<>();
		for (XmlTemplateComponent xmlTemplate : xmlTemplates.getTemplates()) {
			TemplateComponent component = loader.load(xmlTemplate, new ModuleEntry(null, null, null), loadedComponents,
					null);
			loadedComponents.put(component.getId(), component);
		}
		return loadedComponents;
	}

	/**
	 * This method returns a map of task models, given a map of all template components
	 * 
	 * @param templateComponents
	 *            a map of template components
	 * @return a map of task models
	 */
	public Map<String, TaskModel> loadTaskModels(Map<String, TemplateComponent> templateComponents) {
		if (templateComponents == null) return null;

		Map<String, TaskModel> taskModels = new HashMap<>();
		for (TemplateComponent templateComponent : templateComponents.values()) {
			// Iterate through task models
			for (TemplateComponent template : templateComponent.getComponents()) {
				for (Model model : template.getDependencies()) {
					if (model instanceof TaskModel) {
						taskModels.put(model.getId(), (TaskModel) model);
					}
				}
			}
		}
		return taskModels;
	}

	//@formatter:off
	/**
	 * This method returns a map of task models, given a String representation of the templates, it only looks for models that
	 * are under components for example:
	 * 
	 * <templates>
	 *		<template id='main' source='main_id'>
	 *			<component location='content' id='content_id'>
 	 *				<model name='modeName' id='model1_id'>
	 *					<property name='docId' value='1' type='integer' />
	 *				</model>
	 *			</component>
	 *		</template>
	 *	</templates>
	 * 
	 * @param templateXml
	 *            a String representation of the template components
	 * @return a map of task models
	 * @throws Exception
	 */
	//@formatter:on
	public Map<String, TaskModel> loadTaskModels(String templateXml) throws Exception {
		Map<String, TemplateComponent> templateComponents = loadTemplates(templateXml);

		return templateComponents == null ? null : loadTaskModels(templateComponents);
	}

	/**
	 * This method returns a map of all parameters of a given TaskModel
	 * 
	 * @param model
	 *            a {@link TaskModel}
	 * @return a map of parameters of a TaskModel
	 */
	public Map<String, Object> getParameterValues(TaskModel model) {
		if (model == null || model.getDependencies() == null) return null;

		//@formatter:off
		return model.getDependencies().stream()
									  .filter(dependency -> dependency instanceof Property)
									  .map(dependency -> (Property) dependency)
									  .collect(Collectors.toMap((property) -> property.getId(), (property) -> property.getValue()));
		//@formatter:on
	}
}
