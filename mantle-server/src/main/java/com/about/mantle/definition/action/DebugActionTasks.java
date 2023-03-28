package com.about.mantle.definition.action;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.model.debug.DebugModelsComponent;
import com.about.mantle.model.debug.DebugModelsResponse;
import com.about.mantle.model.debug.DebugModelsValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.context.ApplicationContext;

import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.globe.core.Globe;
import com.about.globe.core.definition.common.Model;
import com.about.globe.core.definition.common.Filterable;
import com.about.globe.core.definition.template.RenderManifest;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.TemplateComponentResolver;
import com.about.globe.core.exception.GlobeActionTaskException;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.task.processor.DependencyNode;
import com.about.globe.core.task.processor.DependencyRequestManager;
import com.about.globe.core.task.processor.ModelResult;
import com.about.mantle.logging.SafeListParamaterFailedLogger;

//TODO: This class includes action tasks (that used to be called by actions.xml) and template task both.
// Split them accordingly.  Perhaps put the action tasks directly in the handler of the controller?
@Tasks
public class DebugActionTasks {

	protected final DependencyRequestManager dependencyRequestManager;
	protected final ApplicationContext applicationContext;
	protected final SafeListParamaterFailedLogger safeListParamaterFailedLogger;
	private final ObjectMapper objectMapper;

	public DebugActionTasks(DependencyRequestManager dependencyRequestManager, ApplicationContext applicationContext) {
		this(dependencyRequestManager, applicationContext, null);
	}

	public DebugActionTasks(DependencyRequestManager dependencyRequestManager, ApplicationContext applicationContext, SafeListParamaterFailedLogger safeListParamaterFailedLogger) {
		this.dependencyRequestManager = dependencyRequestManager;
		this.applicationContext = applicationContext;
		this.safeListParamaterFailedLogger = safeListParamaterFailedLogger;

		objectMapper = new StandardObjectMapperProvider().getContext(null);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public void debugModels(String templateName, HttpServletRequest request,
			HttpServletResponse response) throws IOException, GlobeException {

		ensureUrlParam(request);
		RequestContext requestContext = RequestContext.get(request);
		RenderManifest renderManifest = getRenderManifest(requestContext, templateName);

		response.setCharacterEncoding("UTF-8");

		String instanceId = request.getParameter("id");
		String includeChildrenStr = request.getParameter("includeChildren");
		boolean includeChildren = StringUtils.isEmpty(includeChildrenStr) || Boolean.parseBoolean(includeChildrenStr);
		renderModelJson(renderManifest, requestContext, response, instanceId, includeChildren);
	}

	protected void renderModelJson(RenderManifest renderManifest, RequestContext requestContext,
								   HttpServletResponse response, String instanceId, boolean includeChildren) throws IOException {
		ServletOutputStream outputStream = response.getOutputStream();
		DebugModelsResponse responseModel = new DebugModelsResponse();
		responseModel.setStatus("SUCCESS");

		RenderManifest targetManifest = (instanceId != null) ? getManifestByInstanceId(renderManifest, instanceId) : renderManifest;
		if (targetManifest == null) {
			responseModel.setStatus("ERROR");
			responseModel.setMessage("No component with given ID");
		} else {
			Map<String, List<DebugModelsComponent>> resolvedModels = new HashMap<>();
			DebugModelsComponent debugComponent = gatherModelResults(targetManifest, requestContext, resolvedModels, includeChildren);
			responseModel.setData(debugComponent);
		}

		outputStream.print(objectMapper.writeValueAsString(responseModel));
	}

	public void debugModelTimings(String templateName, HttpServletRequest request,
							HttpServletResponse response) throws IOException, GlobeException {

		// TODO - a lot of duped code in debugEffectiveTemplate

		ensureUrlParam(request);
		RequestContext requestContext = RequestContext.get(request);
		Globe globe = requestContext.getGlobe();
		TemplateComponentResolver templateComponentResolver = globe.getTemplateComponentResolver();
		TemplateComponent templateComponent = templateComponentResolver.resolve(templateName);
		if (templateComponent == null) {
			throw new GlobeActionTaskException(
					"Could not resolve template component [" + templateName + "] for request " + requestContext);
		}

		DependencyNode dependencyNodeRoot = dependencyRequestManager.satisfyDependencies(templateComponent,
				requestContext);

		response.setCharacterEncoding("UTF-8");

		renderModelTimingJson(dependencyNodeRoot, requestContext, response);
	}

	protected void renderModelTimingJson(DependencyNode dependencyNode, RequestContext requestContext,
			HttpServletResponse response) throws IOException {

		ServletOutputStream outputStream = response.getOutputStream();

		List<ModelResult> results = gatherResults(dependencyNode, requestContext, new LinkedList<>());

		outputStream.print("[");

		for (Iterator<ModelResult> iterator = results.iterator(); iterator.hasNext();) {
			ModelResult modelResult = iterator.next();

			outputStream.print("{");
			outputStream.print("\"id\":\"" + modelResult.getModel().getId() + "\",");
			outputStream.print("\"uniqueId\":\"" + modelResult.getModel().getQualifiedId() + "\",");
			outputStream.print("\"startTime\":" + modelResult.getStartTime() + ",");
			outputStream.print("\"endTime\":" + modelResult.getEndTime() + ",");
			outputStream.print("\"thread\":\"" + modelResult.getThreadName() + "\"");
			if (modelResult.getException() != null) {
				outputStream.print(",");
				outputStream.print("\"exception\":\"" + modelResult.getException() + "\"");
			}
			outputStream.print("}");
			if (iterator.hasNext()) outputStream.print(",");
		}

		outputStream.print("]");
	}

	protected RenderManifest getManifestByInstanceId(RenderManifest renderManifest, String componentId) {
		if (componentId.equals(renderManifest.getInstanceId())) {
			return renderManifest;
		}

		for (List<RenderManifest> manifests : renderManifest.getLocations().values()) {
			for (RenderManifest childManifest : manifests) {
				RenderManifest foundManifest = getManifestByInstanceId(childManifest, componentId);
				if (foundManifest != null) return foundManifest;
			}
		}

		return null;
	}

	protected DebugModelsComponent gatherModelResults(RenderManifest renderManifest, RequestContext requestContext,
												 Map<String, List<DebugModelsComponent>> resolvedModels,
												 boolean includeChildren) {

		DebugModelsComponent debugComponent = new DebugModelsComponent();
		debugComponent.setId(renderManifest.getInstanceId());
		debugComponent.setModels(new LinkedList<>());
		debugComponent.setChildren(new LinkedList<>());

		Map<String, Object> manifestModel = renderManifest.getModel();
		for(Model model : renderManifest.getComponent().getDependencies()) {
			String qualifiedId = model.getQualifiedId();
			Object value = manifestModel.get(model.getId());
			String refComponentId = null;

			boolean foundMatch = false;
			if (resolvedModels.containsKey(qualifiedId)) {
				List<DebugModelsComponent> previousComponents = resolvedModels.get(qualifiedId);
				for (DebugModelsComponent previousComponent : previousComponents) {
					Object finalValue = value;
					if (previousComponent.getModels().stream().anyMatch(x -> x.getValue() != null && x.getValue().equals(finalValue))) {
						foundMatch = true;
						value = null;
						refComponentId = previousComponent.getId();
						break;
					}
				}
			}

			// If model value was not found, record value from current component
			if (!foundMatch) {
				resolvedModels.put(qualifiedId, new LinkedList<>());
				resolvedModels.get(qualifiedId).add(debugComponent);
			}

			debugComponent.getModels().add(new DebugModelsValue(model.getId(), qualifiedId, value, refComponentId));
		}

		// To simplify output json, output may be restricted to a single component (no sub components)
		if (includeChildren) {
			for (List<RenderManifest> manifests : renderManifest.getLocations().values()) {
				for (RenderManifest childManifest : manifests) {
					debugComponent.getComponents().add(gatherModelResults(childManifest, requestContext, resolvedModels, includeChildren));
				}
			}
		}

		// Avoid serializing empty arrays for models or child components
		if (debugComponent.getModels().size() == 0) {
			debugComponent.setModels(null);
		}
		if (debugComponent.getComponents().size() == 0) {
			debugComponent.setChildren(null);
		}

		return debugComponent;
	}

	public void debugBlockedParams(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setCharacterEncoding("UTF-8");

		Map<String, List<String>> myMap = safeListParamaterFailedLogger.getMapForPrinting();
		ServletOutputStream outputStream = response.getOutputStream();

		outputStream.print("[");

		boolean firstPath = true;
		for(String path : myMap.keySet()){

			if(!firstPath){
				outputStream.print(",");
			}else{
				firstPath = false;
			}

			outputStream.print("{");
			outputStream.print("\"path\":\"" + path + "\",");
			outputStream.print("\"parametersFailed\": [");

			boolean firstParam = true;

			for(String param : myMap.get(path)){
				if(firstParam){
					outputStream.print("\"" + param+"\"");
					firstParam = false;
				}else{
					outputStream.print(",\"" + param+"\"");
				}
			}
			outputStream.print("]");
			outputStream.print("}");
		}

		outputStream.print("]");

	}

	protected List<ModelResult> gatherResults(DependencyNode node, RequestContext requestContext,
			LinkedList<ModelResult> linkedList) {

		if (node.getModel() != null) {
			linkedList.add(node.getModel().getValue(requestContext));
		}

		for (DependencyNode parentNode : node.getParents()) {
			gatherResults(parentNode, requestContext, linkedList);
		}

		return linkedList;
	}

	public void debugEffectiveTemplate(String templateName, HttpServletRequest request,
			HttpServletResponse response, boolean resolvedDynamicComponents) throws IOException, GlobeException {

		ensureUrlParam(request);
		RequestContext requestContext = RequestContext.get(request);
		RenderManifest renderManifest = getRenderManifest(requestContext, templateName);

		response.setCharacterEncoding("UTF-8");
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());

		if (resolvedDynamicComponents)
			renderManifest.getComponent().toXmlString(writer, Filterable.predicateViewable(requestContext), false, renderManifest);
		else
			renderManifest.getComponent().toXmlString(writer, Filterable.predicateViewable(requestContext), false);

		writer.flush();
	}

	private void ensureUrlParam(HttpServletRequest request) {
		if (StringUtils.isBlank(request.getParameter("url"))) {
			throw new GlobeActionTaskException("`url` is a required parameter for this request.");
		}
	}

	private RenderManifest getRenderManifest(RequestContext requestContext, String templateName) {
		Globe globe = requestContext.getGlobe();
		TemplateComponentResolver templateComponentResolver = globe.getTemplateComponentResolver();
		TemplateComponent templateComponent = templateComponentResolver.resolve(templateName, requestContext);
		if (templateComponent == null) {
			throw new GlobeActionTaskException(
					"Could not resolve template component [" + templateName + "] for request " + requestContext);
		}

		dependencyRequestManager.satisfyDependencies(templateComponent, requestContext);
		RenderManifest manifest = templateComponent.getRenderManifest(requestContext, true);

		return manifest;
	}

	private static final String PROF_LOG_PATH = System.getProperty("user.dir") + "/prof.log";
	private static int profCount = 0;

	/**
	 * enables cpu sampling for the duration given by the request query parameter et
	 * in seconds. only one flamegraph can be generated at a time, no multi request
	 * @return the number of seconds the sampling is enabled for and the id for the graph
	 */
	@Task(name = "collectFlamegraph")
	public int collectFlamegraph(@RequestContextTaskParameter RequestContext ctx) throws IOException, GlobeException, InterruptedException {
		// et= starts a new flamegraph that profiles for the given number of seconds
		String duration = StringUtils.stripToNull(ctx.getParameterSingle("et"));
		if (duration == null) {
			duration = "10";
		}
		int towait = Integer.parseInt(duration);
		Thread bg = new Thread(() -> createProfile(towait), "flame");
		bg.start();
		return towait;
		
	}


	/**
	 * after a sampling period
	 *
	 * @see collectFlameGraph this will take the sampling data and render an interactive svg image which represents the
	 * flamegraph for the sampling period
	 */
	public void renderFlamegraph(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GlobeException, InterruptedException {

		String logFile = PROF_LOG_PATH + "." + String.valueOf(profCount);
		// call the honest profiler jar to create a folded version of the profiling log

		Process process = Runtime.getRuntime().exec(
				"/usr/bin/java -cp /opt/about/flamegraph/honest-profiler.jar com.insightfullogic.honest_profiler.ports.console.FlameGraphDumperApplication "
						+ logFile + " " + logFile + ".folded");
		int result = process.waitFor();

		if (result != 0) {
			File f = new File(logFile);
			f.delete();
			throw new GlobeActionTaskException("honest-profiler step failed");
		}

		// run a perl script that generates a flamegraph from the folded log and outputs an svg to stdout
		Process flame = Runtime.getRuntime()
				.exec("/usr/bin/perl -w /opt/about/flamegraph/flamegraph.pl " + logFile + ".folded");
		IOUtils.copy(flame.getInputStream(), response.getOutputStream());
		flame.waitFor();

		// clean up log file
		File f = new File(logFile);
		f.delete();

	}

	private void sendCommand(String command) throws GlobeActionTaskException {
		// send a command to the honest profiler agent which runs on port 9000, it requires one command per connection
		try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), 9000)) {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(command);
			out.flush();
		} catch (Exception e) {
			throw new GlobeActionTaskException(" failed to send command " + command + " to profiler agent", e);
		}
	}

	private void createProfile(int duration) throws GlobeActionTaskException {
		// set the profiling interval to be 7 milliseconds, prime numbers recommended here apparently
		sendCommand("set interval 7 7");
		// set the profiling log path to be output in the same directory as the jar as prof.log
		// this also causes the log to be recreated, otherwise it will not work
		sendCommand("set logPath " + PROF_LOG_PATH + "." + String.valueOf(++profCount));
		// start the profiler
		sendCommand("start");
		// wait for the amount of time specified by the user for the profiling to occur
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e) {
			Thread.interrupted();
		}
		// stop the profiler
		sendCommand("stop");
		// set the interval back to a large value, just in case, to avoid performance issues
		sendCommand("set interval 700000 700000");
	}
}
