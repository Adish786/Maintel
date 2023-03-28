package com.about.mantle.model.tasks;

import com.about.globe.core.definition.common.Property;
import com.about.globe.core.definition.component.xml.XmlComponents;
import com.about.globe.core.definition.loader.TemplateComponentLoader;
import com.about.globe.core.definition.template.RenderManifest;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.TemplateComponentResolver;
import com.about.globe.core.definition.xml.DefinitionXmlUnmarshaller;
import com.about.globe.core.exception.GlobeDependencyUnsatisfiedException;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.GlobeInvalidConfigurationException;
import com.about.globe.core.exception.GlobeTaskHaltException;
import com.about.globe.core.expression.ExpressionEvaluator;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.module.Module;
import com.about.globe.core.module.ModuleEntry;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.task.processor.DependencyRequestManager;
import com.about.mantle.model.pl.PatternLibrary;
import com.about.mantle.render.MantleRenderUtils;
import com.google.common.base.CharMatcher;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.internal.parser.StringInterpolationSequence;
import com.vaadin.sass.internal.resolver.AbstractResolver;
import com.vaadin.sass.internal.resolver.ScssStylesheetResolver;
import com.vaadin.sass.internal.selector.ClassSelector;
import com.vaadin.sass.internal.selector.Selector;
import com.vaadin.sass.internal.selector.SimpleSelector;
import com.vaadin.sass.internal.selector.SimpleSelectorSequence;
import com.vaadin.sass.internal.tree.BlockNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.RuleNode;
import com.vaadin.sass.internal.tree.VariableNode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.css.sac.InputSource;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

@Tasks
public class PatternLibraryTask {
	public static final Logger logger = LoggerFactory.getLogger(DocumentTask.class);

	private final DependencyRequestManager dependencyRequestManager;
	private final Module module;
	private final Pattern LOCATION_PATTERN = Pattern.compile("<@location .*name=\\\"([^\\\"]+)\\\"");
	private final DefinitionXmlUnmarshaller<XmlComponents> unmarshaller;
	private final ExpressionEvaluator expressionEvaluator;
	private final HashMap<String, List<String>> locationsMap;
	private final MantleRenderUtils renderUtils;

	public PatternLibraryTask(DependencyRequestManager dependencyRequestManager, Module module,
			ExpressionEvaluator expressionEvaluator, MantleRenderUtils renderUtils) {
		this.dependencyRequestManager = dependencyRequestManager;
		this.module = module;
		this.unmarshaller = new DefinitionXmlUnmarshaller<>(XmlComponents.class);
		this.expressionEvaluator = expressionEvaluator;
		this.locationsMap = new HashMap<String, List<String>>();
		this.renderUtils = renderUtils;
	}

	@Task(name = "plBrandGuidelines")
	public String getBrandGuidelines(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "filePath") String filePath) {
				ClassPathResource resource = new ClassPathResource(filePath);
				String result = "";

				if (resource.exists() && resource.isReadable()) {
					result = _getFileContents(resource);
				} else {
					result = _getFileContents(new ClassPathResource("/components/pl/instructions.md"));
				}

				return result;
	}

	private String _getFileContents(ClassPathResource resource) {
		String result = "";

		try {
			result = IOUtils.toString(resource.getInputStream());
		} catch (IOException e) {
			logger.error("Could not load file: " + resource.getPath(), e);
			result = "Error Reading File " + resource.getPath() + ": " + e.getMessage();
		}

		return result;
	}
	
	
	@Task(name = "plComponent")
	public PatternLibrary getPatternLibraryFromComponents(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "components") Collection<TemplateComponent> components)
					throws GlobeDependencyUnsatisfiedException, GlobeTaskHaltException,
					GlobeInvalidConfigurationException, IOException {

		String resourcePath = "/components/pl/view/pl-component.ftl";
		return getPatternLibrary(requestContext, components, resourcePath);
	}

	@Task(name = "plRenderedComponentLibrary")
	public PatternLibrary getPatternLibraryFromRenderedComponent(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "component") TemplateComponent component) throws GlobeDependencyUnsatisfiedException,
					GlobeTaskHaltException, GlobeInvalidConfigurationException, IOException {

		String resourcePath = "/components/pl/view/pl-rendered-component.ftl";
		return getPatternLibrary(requestContext, Collections.singletonList(component), resourcePath);
	}

	@Task(name = "plRenderedComponent")
	public TemplateComponent getPatternLibraryComponentFromRenderedComponent(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "component") TemplateComponent component) throws GlobeDependencyUnsatisfiedException,
					GlobeTaskHaltException, GlobeInvalidConfigurationException, IOException {

		String resourcePath = "/components/pl/view/pl-rendered-component.ftl";
		return getPatternLibraryComponent(requestContext, Collections.singletonList(component), resourcePath);
	}

	@Task(name = "plEditedComponent")
	public TemplateComponent getComponentsFromXML(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "xml") String xml, @TaskParameter(name = "id") String id)
					throws GlobeDependencyUnsatisfiedException, GlobeTaskHaltException,
					GlobeInvalidConfigurationException, IOException {

		if (!renderUtils.isInternalIp(requestContext.getHeaders().getRemoteIp())) return componentError();

		TemplateComponentLoader componentLoader = null;
		XmlComponents xmlComponents = null;
		TemplateComponent component = null;

		try {
			componentLoader = new TemplateComponentLoader(null, requestContext.getGlobe().getTasksRegistry(),
					expressionEvaluator);
			xmlComponents = (XmlComponents) unmarshaller.unmarshalString(StringEscapeUtils.unescapeXml(xml));
			component = componentLoader.load(xmlComponents.getComponents().get(0), new ModuleEntry(null, null, module),
					requestContext.getGlobe().getComponentResolver().getMap(), null);
		} finally {
			if (component == null) {
				return componentError();
			}
		}
		return component;
	}

	private TemplateComponent componentError() {
		TemplateComponent component = TemplateComponent.builder().resourcePath("/components/pl/view/pl-error.ftl")
				.build();
		return component;
	}

	private PatternLibrary getPatternLibrary(RequestContext requestContext, Collection<TemplateComponent> components,
			String componentResourcePath) throws IOException {
		// @formatter:off

		for (TemplateComponent component : components) {
			List<String> locationsList = new ArrayList<String>();

			ModuleEntry entry = module.resolveEntry(component.getResourcePath());

			if (entry != null) {
				String content = IOUtils.toString(entry.getUri().toURL());

				Matcher matcher = LOCATION_PATTERN.matcher(content);
				while(matcher.find()) {
					locationsList.add(matcher.group(1));
				}
				locationsMap.put(component.getId(), locationsList);
			}
		}

		// Get render manifest from root component and return
		TemplateComponent rootComponent = getPatternLibraryComponent(requestContext, components, componentResourcePath);
		RenderManifest renderManifest = rootComponent.getRenderManifest(requestContext, false);
		// @formatter:on

		return new PatternLibrary(renderManifest, locationsMap);
	}

	private TemplateComponent getPatternLibraryComponent(RequestContext requestContext,
			Collection<TemplateComponent> components, String componentResourcePath)
					throws IOException {
		// @formatter:off

		// Create root component to wrap all the component wrappers
		TemplateComponent.Builder rootComponentBuilder = TemplateComponent.builder()
				.location("default")
				.resourcePath("/components/base/view/group.ftl");

		// Wrap each passed component with pattern library component wrapper
		for (TemplateComponent component : components) {

			TemplateComponent plComponent = component.with()
					.location("component")
					.uniqueId(component.getId() + "_1")
					.build();

			TemplateComponent devPanelComponent = TemplateComponent.builder()
					.id("mntl-pl-dev-panel")
					.uniqueId("mntl-pl-dev-panel_1")
					.displayName("Dev Panel")
					.location("devPanel")
					.resourcePath("/components/pl/view/pl-dev-panel.ftl")
					.dependency(Property.builder().id("c").value(plComponent).build())
					.dependency(Property.builder().id("templates").value(getTemplatesWithComponent(requestContext, component.getId())).build())
					.dependency(Property.builder().id("locations").value(locationsMap).build())
					.dependency(Property.builder().id("isInternal").value(renderUtils.isInternalIp(requestContext.getHeaders().getRemoteIp())).build())
					.build();

			TemplateComponent plWrapperComponent = TemplateComponent.builder()
					.location("default")
					.resourcePath(componentResourcePath)
					.component(plComponent)
					.component(devPanelComponent)
					.build();

			rootComponentBuilder.component(plWrapperComponent);
		}

		TemplateComponent rootComponent = rootComponentBuilder.build();
		dependencyRequestManager.satisfyDependencies(rootComponent, requestContext);

		// @formatter:on

		return rootComponent;
	}

	public static String getPatternLibraryUriFromComponent(TemplateComponent component) {

		String answer;

		if (component == null || isBlank(component.getCategoryUri())) {
			answer = null;
		} else {
			answer = "/pattern-library" + component.getCategoryUri() + "/"
					+ makeUriSafe((component.getDisplayName() != null ? component.getDisplayName() : component.getId()));
		}

		return answer;
	}

	@Task(name="plTemplatesWithComponent")
	public List<String> getTemplatesWithComponent(@RequestContextTaskParameter RequestContext requestContext,
												  @TaskParameter(name = "id") String componentId) {
		Set<String> templateIds = requestContext.getGlobe().getTemplateComponentResolver().getIds();

		List<String> templatesWithComponent = templateIds.stream()
				.map(id -> requestContext.getGlobe().getTemplateComponentResolver().resolve(id))
				.filter(template -> hasComponent(template, componentId))
				.map(template -> template.getId())
				.sorted()
				.collect(Collectors.toList());

		return templatesWithComponent;
	}

	/**
	 * Checks if a component with the given ID is present in the search component or any
	 * of its children.
	 * @param component
	 * @param targetId
	 * @return
	 */
	private boolean hasComponent(TemplateComponent component, String targetId) {
		if (component.getReferenceIdChain().stream().anyMatch(id -> targetId.equals(id))) {
			return true;
		}

		return component.getComponents().stream().anyMatch(x -> hasComponent(x, targetId));
	}

	@Task(name = "plCurrentComponent")
	public TemplateComponent getComponentName(@RequestContextTaskParameter RequestContext requestContext) {
		List<TemplateComponent> components = getComponentsByPatternCategory(requestContext);
		return components.size() > 0 ? components.get(0) : null;
	}

	@Task(name = "plComponentsByCategory")
	public List<TemplateComponent> getComponentsByPatternCategory(
			@RequestContextTaskParameter RequestContext requestContext) {

		List<TemplateComponent> components = new ArrayList<>();

		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();
		List<String> requestUriParts = Arrays.asList(split(requestContext.getRequestUri(), '/'));
		requestUriParts.set(requestUriParts.size() - 1, makeUriSafe(requestUriParts.get(requestUriParts.size() - 1)));

		for (String id : componentResolver.getIds()) {
			TemplateComponent component = componentResolver.resolve(id).getInfoComponent();
			String uri = getPatternLibraryUriFromComponent(component);
			if (uri != null && uri.startsWith(StringUtils.join(requestUriParts, "/")) && !components.contains(component)) {
				components.add(component);
			}
			if (uri != null && uri.equals(requestContext.getRequestUri())) {
				components.clear();
				components.add(component);
				break;
			}
		}

		Collections.sort(components, new Comparator<TemplateComponent>() {
			@Override
			public int compare(TemplateComponent o1, TemplateComponent o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		return components;
	}

	@Task(name = "plComponentByCategory")
	public TemplateComponent getComponentByPatternCategory(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id) {

		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();
		TemplateComponent component = componentResolver.resolve(id).getInfoComponent();

		if (component == null) {
			throw new GlobeException("Component resolver could not resolve component with ID `" + id + "`");
		}

		return component;
	}

	public class CategoryNode implements Comparable<CategoryNode> {

		private String id;
		private String displayName;
		private String uri;
		private String labels;
		private Map<String, CategoryNode> children = new TreeMap<>();
		private TemplateComponent component;

		public CategoryNode() {
		}

		public CategoryNode(String id, String displayName, String uri, String labels, TemplateComponent component) {
			this.id = id;
			this.displayName = displayName == null ? this.id : displayName;
			this.uri = uri;
			this.labels = labels;
			this.component = component;
		}

		public String getId() {
			return id;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getUri() {
			return uri;
		}

		public String getLabels() {
			return labels;
		}

		public Collection<CategoryNode> getChildren() {
			return children.values();
		}

		public TemplateComponent getComponent() {
			return component;
		}

		public CategoryNode getChild(String displayName) {
			return children.get(displayName);
		}

		public void addChild(CategoryNode node) {
			children.put(node.getDisplayName(), node);
		}

		@Override
		public int compareTo(CategoryNode o) {
			return displayName.compareTo(o.getDisplayName());
		}
	}

	private static String makeUriSafe(String unsafeString) {
		String safeString = stripToNull(unsafeString);
		if (safeString == null)
			throw new IllegalArgumentException("Malformed pattern library component display name: " + unsafeString);

		safeString = CharMatcher.javaLetterOrDigit().negate().or(CharMatcher.whitespace()).replaceFrom(safeString, '-')
				.replaceAll("-+", "-").toLowerCase();

		return safeString;
	}

	private void updatePatternLibrary(int level, TemplateComponent component, CategoryNode categoryNode) {
		String category = component.getCategory();
		String categoryUri = component.getCategoryUri().substring(1);
		String[] categoryParts = stripAll(split(category, "/"));
		String categoryDisplayName = categoryParts[level];
		String categoryUriSegment = categoryUri;

		int slashIndex = ordinalIndexOf(categoryUri, "/", level + 1);
		if (slashIndex != -1) {
			categoryUriSegment = categoryUri.substring(0, slashIndex);
		}

		CategoryNode childNode = categoryNode.getChild(categoryDisplayName);
		if (childNode == null) {
			childNode = new CategoryNode(makeUriSafe(categoryDisplayName), categoryParts[level],
					"/" + categoryUriSegment, component.getTags(), null);
		}
		categoryNode.addChild(childNode);

		if (categoryParts.length == level + 1) {
			String id = component.getDisplayName() != null ? makeUriSafe(component.getDisplayName())
					: component.getId();
			String displayName = component.getDisplayName();
			String uri = "/" + categoryUri + "/" + id;
			childNode.addChild(new CategoryNode(id, displayName, uri, component.getTags(), component));
		} else {
			updatePatternLibrary(level + 1, component, childNode);
		}
	}

	@Task(name = "plNavigation")
	public Collection<CategoryNode> getPatternLibraryNavigation(
			@RequestContextTaskParameter RequestContext requestContext) {

		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();

		CategoryNode rootNode = new CategoryNode();

		for (String id : componentResolver.getIds()) {
			TemplateComponent component = componentResolver.resolve(id).getInfoComponent();

			if (component != null && isNotBlank(component.getCategory())) {
				updatePatternLibrary(0, component, rootNode);
			}
		}

		return rootNode.getChildren();
	}

	private class ModuleResolver extends AbstractResolver {
		private static final long serialVersionUID = 1L;
		@Override
		public InputSource resolveNormalized(String identifier) {

			ModuleEntry entry = module.getEntry(identifier + ".scss");
			try {
				InputStream is = entry.getUri().toURL().openStream();
				if (is != null) {
					InputSource source = new InputSource();
					source.setByteStream(is);
					source.setURI(identifier);
					return source;
				}
			} catch (IOException e) {
				throw new RuntimeException("Error resolving " + identifier, e);
			}

			return null;
		}

	}

	@Task(name = "plColors")
	public Map<String, String[]> getPatternLibraryColors() throws Exception {
		Map<String, String[]> colors = new HashMap<String, String[]>();

		SCSSDocumentHandlerImpl documentHandler = new SCSSDocumentHandlerImpl();
		ArrayList<ScssStylesheetResolver> resolvers = new ArrayList<>();
		resolvers.add(new ModuleResolver());
		documentHandler.getStyleSheet().setResolvers(resolvers);
		ScssStylesheet stylesheet = ScssStylesheet.get("/css/_colors.scss", null, documentHandler,
				new SCSSErrorHandler());
		List<Node> nodes = stylesheet.getChildren();
		List<Node> appendNodes = new ArrayList<Node>();

		// Use variable nodes to construct sass rules for compilation
		for (Node node : nodes) {
			if (!(node instanceof VariableNode)) continue;
			VariableNode variableNode = (VariableNode) node;

			// Get selector name from sass variable name
			List<SimpleSelector> selectorSequence = new ArrayList<SimpleSelector>();
			selectorSequence.add(new ClassSelector(new StringInterpolationSequence(variableNode.getName())));
			List<Selector> selector = new ArrayList<Selector>();
			selector.add(new Selector(new SimpleSelectorSequence(selectorSequence)));

			// Construct the rule node (set color as the value of the sass variable)
			Collection<Node> children = new ArrayList<Node>();
			children.add(new RuleNode(new StringInterpolationSequence("color"), variableNode.getExpr(), false, null));

			// Save the node so we can append to the sass stylesheet before compilation
			Node appendNode = new BlockNode(selector, children);
			appendNodes.add(appendNode);
		}

		// Append all the constructed rules to the sass stylesheet
		for (Node node : appendNodes) {
			stylesheet.appendChild(node);
		}

		// Compile the sass stylesheet to create css stylesheet
		stylesheet.compile();

		// Use css rules to create a map of color values
		for (Node node : stylesheet.getChildren()) {
			BlockNode blockNode = (BlockNode) node;
			RuleNode ruleNode = (RuleNode) blockNode.getChildren().get(0);
			String variable = blockNode.getSelectors().replace('.', '$');
			String color = ruleNode.getValue().printState();
			String[] variableAndColor = new String[]{variable, color};

			String hsbString;
			float[] hsb;

			// Break down an rgba(r, g, b, a) value
			if (color.contains("rgba")) {
				int rStartIndex = StringUtils.ordinalIndexOf(color, "(", 1) + 1;
				int rEndIndex = StringUtils.ordinalIndexOf(color, ",", 1);
				int gStartIndex = StringUtils.ordinalIndexOf(color, ",", 1) + 2;
				int gEndIndex = StringUtils.ordinalIndexOf(color, ",", 2);
				int bStartIndex = StringUtils.ordinalIndexOf(color, ",", 2) + 2;
				int bEndIndex = StringUtils.ordinalIndexOf(color, ",", 3);

				hsb = Color.RGBtoHSB(
						Integer.valueOf(color.substring(rStartIndex, rEndIndex), 16),
						Integer.valueOf(color.substring(gStartIndex, gEndIndex), 16),
						Integer.valueOf(color.substring(bStartIndex, bEndIndex), 16),
						null);
			// Break down a #rrggbb value
			} else {
				hsb = Color.RGBtoHSB(
						Integer.valueOf(color.substring(1, 3), 16),
						Integer.valueOf(color.substring(3, 5), 16),
						Integer.valueOf(color.substring(5, 7), 16),
						null);
			}

			hsbString = Float.toString(hsb[0]) + Float.toString(hsb[1]) + Float.toString(hsb[2]);

			colors.put(hsbString, variableAndColor);
		}

		return colors;
	}

	@Task(name = "plPagination")
	public Map<String, CategoryNode> getPatternLibraryPagination(
			@RequestContextTaskParameter RequestContext requestContext) {
		List<CategoryNode> nodes = flattenNavigation(getPatternLibraryNavigation(requestContext), null);
		String[] uriParts = split(requestContext.getRequestUri(), '/');
		String currentCategory = makeUriSafe(uriParts[uriParts.length - 1]);
		ListIterator<CategoryNode> iterator = nodes.listIterator();
		while (iterator.hasNext()) {
			boolean hasPrevious = iterator.hasPrevious();
			CategoryNode category = iterator.next();
			if (currentCategory.equals(category.getId())) {
				Map<String, CategoryNode> pagination = new HashMap<String, CategoryNode>();
				if (hasPrevious) pagination.put("prev", nodes.get(iterator.previousIndex() - 1));
				if (iterator.hasNext()) pagination.put("next", nodes.get(iterator.nextIndex()));
				return pagination;
			}
		}

		return null;
	}

	@Task(name = "plLabels")
	public Collection<String> getLabels(@RequestContextTaskParameter RequestContext requestContext) {

		Collection<String> labels = new HashSet<String>();
		TemplateComponentResolver componentResolver = requestContext.getGlobe().getComponentResolver();

		for (String id : componentResolver.getIds()) {
			TemplateComponent component = componentResolver.resolve(id).getInfoComponent();

			if (component != null && isNotBlank(component.getCategory()) && isNotBlank(component.getTags())) {
				List<String> tags = Arrays.asList(split(component.getTags(), ','));
				for (String tag : tags) {
					labels.add(tag);
				}
			}
		}

		return labels;
	}

	private List<CategoryNode> flattenNavigation(Collection<CategoryNode> nodes, List<CategoryNode> list) {
		if (list == null) {
			list = new ArrayList<CategoryNode>();
		}
		for (CategoryNode node : nodes) {
			list.add(node);
			if (!node.getChildren().isEmpty()) {
				flattenNavigation(node.getChildren(), list);
			}
		}
		return list;
	}
}
