package com.about.mantle.model.tasks;

import com.about.globe.core.Globe;
import com.about.globe.core.definition.component.ResourceHandle;
import com.about.globe.core.definition.loader.TemplateComponentLoader;
import com.about.globe.core.definition.resource.ResolvedResource;
import com.about.globe.core.definition.resource.Resource;
import com.about.globe.core.definition.resource.Resource.Type;
import com.about.globe.core.definition.resource.ResourceAggregator;
import com.about.globe.core.definition.resource.ResourceResolver;
import com.about.globe.core.definition.resource.aggregate.AggregateKeyGenerator;
import com.about.globe.core.definition.resource.aggregate.DeflaterAggregateKeyGenerator;
import com.about.globe.core.definition.resource.aggregate.OnDemandResourceAggregator;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.TemplateComponentResolver;
import com.about.globe.core.definition.template.xml.XmlTemplateComponent;
import com.about.globe.core.definition.template.xml.XmlTemplates;
import com.about.globe.core.expression.ExpressionEvaluator;
import com.about.globe.core.expression.spring.SpringExpressionEvaluator;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgent;
import com.about.globe.core.module.ModuleEntry;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.task.registry.TasksRegistry;
import com.about.globe.core.testing.GlobeBucket;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.model.tasks.ResourceTasks.ResourceBundleType;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceTasksTest {

	private ResourceTasks resourceTasks;
	private TemplateComponentLoader loader;
	private RequestContext requestContext;
	private UserAgent userAgent;
	private TemplateComponentResolver templateResolver;

	@Before
	public void before() {

		Answer<ResolvedResource> answer = invocation -> {

			ResourceHandle handle = (ResourceHandle) invocation.getArguments()[0];
			Resource resource = new Resource(handle.getPath(), DeviceCategory.PERSONAL_COMPUTER, true, Type.LOCAL);

			return new ResolvedResource(resource, handle, null);
		};
		ResourceResolver cssResourceResolver = mock(ResourceResolver.class);
		when(cssResourceResolver.resolve(any(ResourceHandle.class), any(RequestContext.class))).then(answer);
		ResourceResolver jsResourceResolver = mock(ResourceResolver.class);
		when(jsResourceResolver.resolve(any(ResourceHandle.class), any(RequestContext.class))).then(answer);

		AggregateKeyGenerator keyGenerator = new DeflaterAggregateKeyGenerator();
		ResourceAggregator resourceAggregator = new OnDemandResourceAggregator(keyGenerator, null);

		this.resourceTasks = new ResourceTasks(resourceAggregator, null);

		ExpressionEvaluator expressionEvaluator = new SpringExpressionEvaluator(new SpelExpressionParser(),
				mock(CoreRenderUtils.class), mock(ProjectInfo.class));
		TasksRegistry tasksRegistry = mock(TasksRegistry.class);

		this.loader = new TemplateComponentLoader(null, tasksRegistry, expressionEvaluator);

		requestContext = mock(RequestContext.class);
		
		userAgent = mock(UserAgent.class);
		when(requestContext.getUserAgent()).thenReturn(userAgent);
		when(userAgent.getDeviceCategory()).thenReturn(DeviceCategory.PERSONAL_COMPUTER);
		
		Globe globe = mock(Globe.class);
		templateResolver = mock(TemplateComponentResolver.class);
		when(globe.getTemplateComponentResolver()).thenReturn(templateResolver);
		when(requestContext.getGlobe()).thenReturn(globe);
	}

	private Map<String, TemplateComponent> load(String templatesXml, Map<String, TemplateComponent> loadedComponents)
			throws Exception {
		Unmarshaller unmarshaller = JAXBContext.newInstance(XmlTemplates.class).createUnmarshaller();
		XmlTemplates xmlTemplates = (XmlTemplates) unmarshaller.unmarshal(new StringReader(templatesXml));

		TemplateComponent component = null;
		loadedComponents = defaultIfNull(loadedComponents, new HashMap<String, TemplateComponent>());

		for (XmlTemplateComponent xmlTemplate : xmlTemplates.getTemplates()) {
			component = loader.load(xmlTemplate, new ModuleEntry(null, null, null), loadedComponents, null);
			loadedComponents.put(component.getId(), component);
		}

		return loadedComponents;
	}

	private ResolvedResource[] scripts(RequestContext requestContext, Boolean evaluatedOnly, String group,
			ResourceBundleType bundleType) {

		TemplateComponent template = requestContext.getTemplateComponent();
		List<TemplateComponent> allTemplates = new ArrayList(requestContext.getGlobe().getTemplateComponentResolver().getMap()
				.values());

		List<ResolvedResource> scriptsSet = resourceTasks.scripts(requestContext, evaluatedOnly, group,
				null, null); // added null just to make it compile. See GLBE-5128
		return scriptsSet.toArray(new ResolvedResource[scriptsSet.size()]);
	}

	@Ignore // See GLBE-5128
	@Test
	public void testScripts_noScripts() throws Exception {

		// @formatter:off
		String templatesXml =
			"<templates>" +
			"<template id='layout' source='layout_list'>" +
			"	<component location='left' id='left' />" +
			"	<component location='right' id='right' />" +
			"</template>" +
			"</templates>";
		// @formatter:on

		Map<String, TemplateComponent> componentMap = load(templatesXml, null);
		when(templateResolver.getMap()).thenReturn(componentMap);
		TemplateComponent component = componentMap.get("layout");
		when(requestContext.getTemplateComponent()).thenReturn(component);

		ResolvedResource[] scripts;

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));

		when(requestContext.isResourceConcat()).thenReturn(true);

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
	}

	@Ignore // See GLBE-5128
	@Test
	public void testScripts_groupedScripts() throws Exception {

		// @formatter:off
		String templatesXml =
				"<templates>" +
				"<template id='layout' source='layout_list'>" +
				"	<script evaluated='true' path='/static/js/layout.evaluated.js' />" +
				"	<component location='left' id='left'>" +
				"		<script group='top' path='/static/js/left-top.js' />" +
				"		<script group='bottom' path='/static/js/left-bottom.js' />" +
				"	</component>" +
				"	<component location='right' id='right'>" +
				"		<script group='top' path='/static/js/right-top.js' />" +
				"		<script group='bottom' path='/static/js/right-bottom.js' />" +
				"		<property name='display' value='${null}' onError='collapseSilent' />" +
				"	</component>" +
				"</template>" +
				"</templates>";
		// @formatter:on

		Map<String, TemplateComponent> componentMap = load(templatesXml, null);
		when(templateResolver.getMap()).thenReturn(componentMap);
		TemplateComponent component = componentMap.get("layout");
		when(requestContext.getTemplateComponent()).thenReturn(component);

		ResolvedResource[] scripts;

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));

		when(requestContext.isResourceConcat()).thenReturn(true);

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
	}

	@Ignore // See GLBE-5128
	@Test
	public void testScripts_bundleTypeTemplate() throws Exception {

		// @formatter:off
		String templatesXml =
				"<templates>" +
				"<template id='common'>" +
				"	<script group='top' path='/static/js/common-top.js' />" +
				"	<script group='top' path='/static/js/common-top2.js' />" +
				"	<script group='bottom' path='/static/js/common-bottom.js' />" +
				"	<script evaluated='true' path='/static/js/common.evaluated.js' />" +
				"</template>" +
				"<template id='other' source='other_list' ref='common'>" +
				"	<component location='left' id='left'>" +
				"		<script group='top' path='/static/js/other-left-top.js' />" +
				"		<script group='bottom' path='/static/js/other-left-bottom.js' />" +
				"	</component>" +
				"	<component location='right' id='right'>" +
				"		<script group='top' path='/static/js/other-right-top.js' />" +
				"		<script group='bottom' path='/static/js/other-right-bottom.js' />" +
				"		<property name='display' value='${null}' onError='collapseSilent' />" +
				"	</component>" +
				"</template>" +
				"<template id='layout' source='layout_list' ref='common'>" +
				"	<component location='left' id='left'>" +
				"		<script group='top' path='/static/js/layout-left-top.js' />" +
				"		<script group='bottom' path='/static/js/layout-left-bottom.js' />" +
				"	</component>" +
				"	<component location='right' id='right'>" +
				"		<script group='top' path='/static/js/layout-right-top.js' />" +
				"		<script group='bottom' path='/static/js/layout-right-bottom.js' />" +
				"		<property name='display' value='${null}' onError='collapseSilent' />" +
				"	</component>" +
				"</template>" +
				"</templates>";
		// @formatter:on

		Map<String, TemplateComponent> componentMap = load(templatesXml, null);
		when(templateResolver.getMap()).thenReturn(componentMap);
		TemplateComponent component = componentMap.get("layout");
		when(requestContext.getTemplateComponent()).thenReturn(component);

		ResolvedResource[] scripts;

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(4));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(3));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(6));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(5));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(4));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(3));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(3));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(4));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(3));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(3));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));

		when(requestContext.isResourceConcat()).thenReturn(true);

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Site_Lazy);
		assertThat(scripts.length, is(1));

		scripts = scripts(requestContext, false, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(2));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Tiered_Lazy);
		assertThat(scripts.length, is(1));
	}
	
	private GlobeBucket bucket(String name) {
		return new GlobeBucket(name, "", 0, null, "99");
	}

	@Ignore // See GLBE-5128
	@Test
	public void testScripts_viewsAndTests() throws Exception {
		
		// @formatter:off
		String templatesXml =
				"<templates>" +
				"<template id='layout'>" +
				"	<script group='top' path='/static/js/common-top.js' views='pc' />" +
				"	<script group='top' path='/static/js/common-top2.js' views='mobile' />" +
				"	<component location='left' id='left'>" +
				"		<script group='top' path='/static/js/layout-left-top.js' views='tablet' />" +
				"		<script group='bottom' path='/static/js/layout-left-bottom.js' />" +
				"		<script group='bottom' path='/static/js/layout-left-bottom-variation.js' tests='test' buckets='A' />" +
				"		<script group='bottom' evaluated='true' path='/static/js/layout-left-bottom.evaluated.js' />" +
				"	</component>" +
				"	<component location='right' id='right'>" +
				"		<script group='top' path='/static/js/layout-right-top.js' />" +
				"		<script group='bottom' path='/static/js/layout-right-bottom.js' />" +
				"		<property name='display' value='${null}' onError='collapseSilent' />" +
				"	</component>" +
				"</template>" +
				"</templates>";
		// @formatter:on
		
		Map<String, TemplateComponent> componentMap = load(templatesXml, null);
		when(templateResolver.getMap()).thenReturn(componentMap);
		TemplateComponent component = componentMap.get("layout");
		when(requestContext.getTemplateComponent()).thenReturn(component);
		
		ResolvedResource[] scripts;
		
		when(userAgent.getDeviceCategory()).thenReturn(DeviceCategory.PERSONAL_COMPUTER);
		when(requestContext.getTests()).thenReturn(ImmutableMap.of("test", bucket("A")));
		
		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/common-top.js"));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(2));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.js"));
		assertThat(scripts[1].getResourceHandle().getPath(), is("/static/js/layout-left-bottom-variation.js"));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.evaluated.js"));
		
		when(requestContext.getTests()).thenReturn(ImmutableMap.of("test", bucket("B")));
		
		when(userAgent.getDeviceCategory()).thenReturn(DeviceCategory.TABLET);
		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-top.js"));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.js"));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.evaluated.js"));

		when(userAgent.getDeviceCategory()).thenReturn(DeviceCategory.SMARTPHONE);
		scripts = scripts(requestContext, false, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/common-top2.js"));
		scripts = scripts(requestContext, false, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.js"));
		scripts = scripts(requestContext, true, "top", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(0));
		scripts = scripts(requestContext, true, "bottom", ResourceBundleType.Template_Lazy);
		assertThat(scripts.length, is(1));
		assertThat(scripts[0].getResourceHandle().getPath(), is("/static/js/layout-left-bottom.evaluated.js"));
		
	}
}
