package com.about.mantle.spring.jetty;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.metrics.transformer.MetricNameBuilder;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableSet;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.AsyncContextState;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MantleInstrumentedHandler extends HandlerWrapper {

	// These are allowed extensions for each served directory
	private static final Set<String> FONT_EXTENSIONS = ImmutableSet.of("eot", "svg", "ttf", "woff", "woff2");
	private static final Set<String> ICON_EXTENSIONS = ImmutableSet.of("png", "ico");
	private static final Set<String> IMAGE_EXTENSIONS = ImmutableSet.of("jpg", "gif", "png", "svg", "webp");
	private static final Set<String> CSS_EXTENSIONS = ImmutableSet.of("css");
	private static final Set<String> JS_EXTENSIONS = ImmutableSet.of("js");
	// Static can serve all types
	// @formatter:off
	private static final Set<String> STATIC_EXTENSIONS = ImmutableSet.<String>builder()
			.addAll(FONT_EXTENSIONS)
			.addAll(ICON_EXTENSIONS)
			.addAll(IMAGE_EXTENSIONS)
			.addAll(CSS_EXTENSIONS)
			.addAll(JS_EXTENSIONS)
			.build();
	// @formatter:on

	static final String METRIC_PREFIX = "jetty";
	static final String GLOBAL_KEY = "";
	
	private final Map<String, MetricGroup> metricGroups = new HashMap<>();
	private final MetricRegistry metricRegistry;
	private final MetricGroup globalMetricGroup;

	public MantleInstrumentedHandler(MetricRegistry metricRegistry) {

		this.metricRegistry = metricRegistry;
		this.globalMetricGroup = ensureMetricGroup(GLOBAL_KEY);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		MetricGroup metricGroup = getMetricGroup(request);
		boolean notGlobal = metricGroup != globalMetricGroup;
		
		metricGroup.activeDispatches.inc();
		if (notGlobal) globalMetricGroup.activeDispatches.inc();

        final long start;
        final HttpChannelState state = baseRequest.getHttpChannelState();
		if (state.isInitial()) {
            // new request
        	metricGroup.activeRequests.inc();
        	if (notGlobal) globalMetricGroup.activeRequests.inc();
            start = baseRequest.getTimeStamp();
        } else {
            // resumed request
            start = System.currentTimeMillis();
            metricGroup.activeSuspended.dec();
            if (notGlobal) globalMetricGroup.activeSuspended.dec();
            if (state.getState() == HttpChannelState.State.HANDLING) {
				metricGroup.asyncDispatches.mark();
				if (notGlobal) globalMetricGroup.asyncDispatches.mark();
            }
        }

        try {
        	super.handle(target, baseRequest, request, response);
        } finally {
            metricGroup.activeDispatches.dec();
            if (notGlobal) globalMetricGroup.activeDispatches.dec();
            
			long duration = System.currentTimeMillis() - start;
			metricGroup.dispatches.update(duration, TimeUnit.MILLISECONDS);
			if (notGlobal) globalMetricGroup.dispatches.update(duration, TimeUnit.MILLISECONDS);

            if (state.isSuspended()) {
                if (state.isInitial()) {
                    state.addListener(metricGroup.listener);
                    if (notGlobal) state.addListener(globalMetricGroup.listener);
                }
                metricGroup.activeSuspended.inc();
                if (notGlobal) globalMetricGroup.activeSuspended.inc();
            } else if (state.isInitial()) {
            	MetricGroup requestMetricGroup = getMetricGroup(request);
				requestMetricGroup.update(baseRequest);
				if (notGlobal) globalMetricGroup.update(baseRequest);
				
            	metricGroup.activeRequests.dec();
            	if (notGlobal) globalMetricGroup.activeRequests.dec();
            }
            // else onCompletion will handle it.
        }
    
	}

	private MetricGroup getMetricGroup(HttpServletRequest request) {

		String key = getKey(request);

		return ensureMetricGroup(key);
	}

	protected String getKey(HttpServletRequest request) {

		String path = request.getRequestURI();

		int lastDot = path.lastIndexOf('.');
		String extension = lastDot == -1 ? "" : path.substring(lastDot + 1);

		int secondSlash = path.indexOf('/', 1);
		String segment = secondSlash == -1 ? "" : path.substring(1, secondSlash);

		switch (segment) {
			case "static":
				return "pv:no,type:static,ext:" + (STATIC_EXTENSIONS.contains(extension) ? extension : "other");
			case "js":
				return "pv:no,type:js,ext:" + (JS_EXTENSIONS.contains(extension) ? extension : "other");
			case "css":
				return "pv:no,type:css,ext:" + (CSS_EXTENSIONS.contains(extension) ? extension : "other");
			case "font":
				return "pv:no,type:font,ext:" + (FONT_EXTENSIONS.contains(extension) ? extension : "other");
			case "icons":
				return "pv:no,type:icon,ext:" + (ICON_EXTENSIONS.contains(extension) ? extension : "other");
			case "image":
				return "pv:no,type:image,ext:" + (IMAGE_EXTENSIONS.contains(extension) ? extension : "other");
		}

		RequestContext requestContext = RequestContext.get(request);
		if (requestContext != null) {

			String templateName = requestContext.getTemplateName();
			if (isNotBlank(templateName)) {

				boolean pageview = requestContext.isPageview();
				boolean deferred = requestContext.isDeferred();

				return (pageview ? "pv:yes" : "pv:no") + ",deferred:" + (deferred ? "yes" : "no") + ",template:" + templateName;
			}
		}

		// Keeping segments same for consistent wildcarding in graphite queries, ie com.about.globe.jetty.*.*.*....
		return "pv:no,type:other,ext:other";
	}

	private MetricGroup ensureMetricGroup(String key) {

		MetricGroup metricGroup = metricGroups.get(key);
		if (metricGroup != null) return metricGroup;

		synchronized (metricGroups) {
			metricGroup = metricGroups.get(key);
			if (metricGroup != null) return metricGroup;

			metricGroup = new MetricGroup(metricRegistry, key);

			metricGroups.put(key, metricGroup);

			return metricGroup;
		}
	}

	private static class MetricGroup {

		// the requests handled by this handler, excluding active
		private final Timer requests;

		private final Meter[] responses;

		private final Timer getRequests;
		private final Timer postRequests;
		private final Timer headRequests;
		private final Timer putRequests;
		private final Timer deleteRequests;
		private final Timer optionsRequests;
		private final Timer traceRequests;
		private final Timer connectRequests;
		private final Timer moveRequests;
		private final Timer otherRequests;

		// the content length of each response
		private final Histogram contentLengths;

	    // the number of dispatches seen by this handler, excluding active
	    private final Timer dispatches;

	    // the number of active requests
	    private final Counter activeRequests;

	    // the number of active dispatches
	    private final Counter activeDispatches;

	    // the number of requests currently suspended.
	    private final Counter activeSuspended;

	    // the number of requests that have been asynchronously dispatched
	    private final Meter asyncDispatches;

	    // the number of requests that expired while suspended
	    private final Meter asyncTimeouts;

	    private final AsyncListener listener;

		
		public MetricGroup(MetricRegistry metricRegistry, String key) {

			MetricNameBuilder metricNameBuilder = MetricNameBuilder.get();
			metricNameBuilder.setPrefix(METRIC_PREFIX);

			this.requests = metricRegistry.timer(metricNameBuilder.putTag("content","requests").build());

			metricNameBuilder.appendFullTag(key);
			metricNameBuilder.putTag("content","responses");
			this.responses = new Meter[] {
					metricRegistry.meter(metricNameBuilder.putTag("code","1xx").build()), // 1xx
					metricRegistry.meter(metricNameBuilder.putTag("code","2xx").build()),  // 2xx
					metricRegistry.meter(metricNameBuilder.putTag("code","3xx").build()),  // 3xx
					metricRegistry.meter(metricNameBuilder.putTag("code","4xx").build()),  // 4xx
					metricRegistry.meter(metricNameBuilder.putTag("code","5xx").build()),  // 5xx
			};

			metricNameBuilder.clearTags();
			metricNameBuilder.putTag("content","requestTime");
			this.getRequests = metricRegistry.timer(metricNameBuilder.putTag("method","GET").build());
			this.postRequests = metricRegistry.timer(metricNameBuilder.putTag("method","POST").build());
			this.headRequests = metricRegistry.timer(metricNameBuilder.putTag("method","HEAD").build());
			this.putRequests = metricRegistry.timer(metricNameBuilder.putTag("method","PUT").build());
			this.deleteRequests = metricRegistry.timer(metricNameBuilder.putTag("method","DELETE").build());
			this.optionsRequests = metricRegistry.timer(metricNameBuilder.putTag("method","OPTIONS").build());
			this.traceRequests = metricRegistry.timer(metricNameBuilder.putTag("method","TRACE").build());
			this.connectRequests = metricRegistry.timer(metricNameBuilder.putTag("method","CONNECT").build());
			this.moveRequests = metricRegistry.timer(metricNameBuilder.putTag("method","MOVE").build());
			this.otherRequests = metricRegistry.timer(metricNameBuilder.putTag("method","OTHER").build());

			metricNameBuilder.clearTags();

			this.contentLengths = metricRegistry.histogram(metricNameBuilder.putTag("content", "content-length").build());

	        this.dispatches = metricRegistry.timer(metricNameBuilder.putTag("content","dispatches").build());

	        this.activeRequests = metricRegistry.counter(metricNameBuilder.putTag("content","active-requests").build());
	        this.activeDispatches = metricRegistry.counter(metricNameBuilder.putTag("content","active-dispatches").build());
	        this.activeSuspended = metricRegistry.counter(metricNameBuilder.putTag("content","active-suspended").build());

	        this.asyncDispatches = metricRegistry.meter(metricNameBuilder.putTag("content","async-dispatches").build());
	        this.asyncTimeouts = metricRegistry.meter(metricNameBuilder.putTag("content","async-timeouts").build());

			metricNameBuilder.clear();

	        this.listener = new AsyncListener() {
	            @Override
	            public void onTimeout(AsyncEvent event) throws IOException {
	                asyncTimeouts.mark();
	            }

	            @Override
	            public void onStartAsync(AsyncEvent event) throws IOException {
	                event.getAsyncContext().addListener(this);
	            }

	            @Override
	            public void onError(AsyncEvent event) throws IOException {
	            }

	            @Override
	            public void onComplete(AsyncEvent event) throws IOException {
	                final AsyncContextState state = (AsyncContextState) event.getAsyncContext();
	                
	                ServletRequest request = state.getRequest();
	                if (request instanceof Request) update((Request) request);
	                
	                if (state.getHttpChannelState().getState() != HttpChannelState.State.HANDLING) {
	                    activeSuspended.dec();
	                }
	            }
	        };
			
		}

		public void update(Request request) {
			final int response = request.getResponse().getStatus() / 100;
			if (response >= 1 && response <= 5) {
				responses[response - 1].mark();
			}
			final long elapsedTime = System.currentTimeMillis() - request.getTimeStamp();
			requests.update(elapsedTime, TimeUnit.MILLISECONDS);
			requestTimer(request.getMethod()).update(elapsedTime, TimeUnit.MILLISECONDS);
			// Content length is not always set. Content count is more reliable.
			// When content length is set, the value appears to always match content count
			contentLengths.update(request.getResponse().getContentCount());
		}

		private Timer requestTimer(String method) {
			final HttpMethod m = HttpMethod.fromString(method);
			if (m == null) {
				return otherRequests;
			} else {
				switch (m) {
				case GET:
					return getRequests;
				case POST:
					return postRequests;
				case PUT:
					return putRequests;
				case HEAD:
					return headRequests;
				case DELETE:
					return deleteRequests;
				case OPTIONS:
					return optionsRequests;
				case TRACE:
					return traceRequests;
				case CONNECT:
					return connectRequests;
				case MOVE:
					return moveRequests;
				default:
					return otherRequests;
				}
			}
		}
	}
}
