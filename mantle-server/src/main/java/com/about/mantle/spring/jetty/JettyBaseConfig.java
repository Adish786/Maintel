package com.about.mantle.spring.jetty;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.management.MBeanServer;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.AcceptRateLimit;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHttpOutputInterceptor;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.servlet.GlobalErrorPageServlet;
import com.about.globe.core.servlet.util.ResponseUtils;
import com.about.hippodrome.config.CommonProperty;
import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.hippodrome.config.servicediscovery.Service;
import com.about.hippodrome.metrics.MetricConfigurer;
import com.about.hippodrome.metrics.metricset.CustomBufferPoolMetricSet;
import com.about.hippodrome.metrics.metricset.CustomGarbageCollectorMetricSet;
import com.about.hippodrome.metrics.metricset.CustomMemoryUsageGaugeSet;
import com.about.hippodrome.metrics.registry.MetricRegistry;
import com.about.hippodrome.metrics.transformer.DefaultTransformer;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.monitoring.jmx.JolokiaMonitoringServer;
import com.google.common.net.HttpHeaders;

import ch.qos.logback.access.jetty.RequestLogImpl;

public class JettyBaseConfig {

	public final static String CAES_PROXY_PREFIX = "/product";
	public final static String ORION_PROXY_PREFIX = "/api/orion";
	public final static String THUMBOR_PROXY_PREFIX = "/thmb";

	private static final Logger logger = LoggerFactory.getLogger(JettyBaseConfig.class);

	private static final String KEY_METRIC_REPORT_INTERVAL = "com.about.metric.report.interval";
	private static final String KEY_INFLUXDB_ENABLED = "com.about.influxdb.enabled";
	private static final String KEY_INFLUXDB_HOST = "com.about.influxdb.host";
	private static final String KEY_INFLUXDB_PORT = "com.about.influxdb.port";
	private static final String KEY_INFLUXDB_DBNAME = "com.about.influxdb.dbname";
	private static final String KEY_INFLUXDB_SCHEME = "com.about.influxdb.scheme";
	private static final String KEY_INFLUXDB_RESERVOIR_ENABLED = "com.about.influxdb.reservoir.enabled";
	private static final String KEY_INFLUXDB_CONNECT_TIMEOUT = "com.about.influxdb.connect.timeout";
	private static final String KEY_INFLUXDB_READ_TIMEOUT = "com.about.influxdb.read.timeout";

	public static final int PORT_OFFSET = Integer.parseInt(System.getProperty("port.offset", "0"));
	public static final int HTTP_PRIMARY_PORT = 8080 + PORT_OFFSET;
	public static final int HTTP_SECONDARY_PORT = 9080 + PORT_OFFSET;
	public static final int HTTPS_PRIMARY_PORT = 8443 + PORT_OFFSET;
	public static final int HTTPS_SECONDARY_PORT = 9443 + PORT_OFFSET;
	public static final int JOLOKIA_PORT = 8778 + PORT_OFFSET;

	public static final int INTERNAL_CONNECTOR_PORT = 7089 + PORT_OFFSET;
	
	private final HippodromePropertyFactory propertyFactory;
	private final MetricRegistry metricRegistry;
	private final SSLContext sslContext;
	private JolokiaMonitoringServer jolokiaMonitoringServer = null;

	public JettyBaseConfig() throws Exception {
		this.propertyFactory = CommonPropertyFactory.INSTANCE.get();
		MetricConfigurer metricConfigurer = MetricConfigurer.configure();
		initMetrics(metricConfigurer);
		metricRegistry = metricConfigurer.build().getMetricRegistry();
		addMetrics(metricRegistry);
		addJMXConnector();
		this.sslContext = getSslContext();
	}

	public MetricRegistry getMetricRegistry() {
		return this.metricRegistry;
	}

	protected Handler configureHandlers(Handler handler) throws Exception {
		addErrorHandler(((ServletContextHandler) handler));

		return instrumentedHandler(metricRegistry, getGzipHandlerForResources(getGzipHandlerForRequests(handler)));
	}

	protected void addErrorHandler(ServletContextHandler context) {

		ServletHolder holder = new ServletHolder(GlobalErrorPageServlet.class);
		context.addServlet(holder, "/global_error_page");

		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
		errorHandler.addErrorPage(ErrorPageErrorHandler.GLOBAL_ERROR_PAGE, "/global_error_page");
		context.setErrorHandler(errorHandler);
		// TODO - uncomment when vertical error pages include placeholder text. Enables 400 handling with
		// vertical branded error pages
//		context.getServer().setErrorHandler(new MantleErrorHandler());
	}

	protected AbstractHandlerContainer instrumentedHandler(MetricRegistry metricRegistry, Handler handler)
			throws Exception {
		MantleInstrumentedHandler instrumentedHandler = new MantleInstrumentedHandler(metricRegistry);
		instrumentedHandler.setHandler(handler);
		return instrumentedHandler;
	}

	/**
	 * For static resources we want to return
	 * GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING in the response.
	 * 
	 * @param handler
	 * @return
	 */
	protected Handler getGzipHandlerForResources(Handler handler) {
		GzipHandler gzipHandler = new GlobeGzipHandler(() -> GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING);
		gzipHandler.setIncludedMimeTypes(getMimeTypesForGzipHandlers());
		gzipHandler.setIncludedPaths("/static/*");
		gzipHandler.setHandler(handler);
		return gzipHandler;
	}

	/**
	 * For html and other requests we want to return VARY_ACCEPT_ENCODING_USER_AGENT
	 * in response's vary header.
	 * 
	 * @param handler
	 * @return
	 */
	protected Handler getGzipHandlerForRequests(Handler handler) {
		GzipHandler gzipHandler = new GlobeGzipHandler(() -> GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING_USER_AGENT);
		gzipHandler.setIncludedMimeTypes(getMimeTypesForGzipHandlers());
		gzipHandler.setIncludedPaths("/*");
		gzipHandler.setExcludedPaths("/static/*", "/thmb/*");
		gzipHandler.setHandler(handler);
		return gzipHandler;
	}

	private String[] getMimeTypesForGzipHandlers() {
		return new String[] { "application/javascript", "application/json", "application/pdf",
				"application/x-javascript", "application/xhtml+xml", "image/gif", "image/jpeg", "image/pjpeg",
				"image/png", "image/svg+xml", "image/x-icon", "text/css", "text/html", "text/javascript", "text/plain",
				"text/xml" };
	}

	protected RequestLog getRequestLog() {
		RequestLogImpl requestLog = new GlobeRequestLogImpl();
		requestLog.setResource("/logback-access.xml");
		requestLog.setQuiet(true);
		return requestLog;
	}

	//https://github.com/qos-ch/logback/pull/269#issuecomment-155881582
	public static class GlobeRequestLogImpl extends RequestLogImpl implements LifeCycle {}
	
	protected MetricRegistry getConfiguredMetricRegistry() {

		return metricRegistry;
	}

	protected void initMetrics(MetricConfigurer metricConfigurer) {
		int interval = propertyFactory.getProperty(KEY_METRIC_REPORT_INTERVAL).asInteger(60).get();

		boolean influxdbEnabled = propertyFactory.getProperty(KEY_INFLUXDB_ENABLED).asBoolean(false).get();
		if (influxdbEnabled) {
			String influxdbName = propertyFactory.getProperty(KEY_INFLUXDB_DBNAME).asString(null).get();
			String influxdbHost = propertyFactory.getProperty(KEY_INFLUXDB_HOST).asString(null).get();
			int influxdbPort = propertyFactory.getProperty(KEY_INFLUXDB_PORT).asInteger(8086).get();
			int connectTimeout = propertyFactory.getProperty(KEY_INFLUXDB_CONNECT_TIMEOUT).asInteger(1000).get();
			int readTimeout = propertyFactory.getProperty(KEY_INFLUXDB_READ_TIMEOUT).asInteger(1000).get();

			String influxdbScheme = propertyFactory.getProperty(KEY_INFLUXDB_SCHEME).asString("http").get();
			boolean enableSlidingWindowReservoir = propertyFactory.getProperty(KEY_INFLUXDB_RESERVOIR_ENABLED)
					.asBoolean(true).get();

			metricConfigurer.httpBasedInfluxdbReporter().enable(influxdbEnabled).interval(interval)
					.enableSlidingWindowReservoir(enableSlidingWindowReservoir).intervalTimeUnit(TimeUnit.SECONDS)
					.withDefaultTags(getDefaultMetricTagsMap()).withTransformer(new DefaultTransformer())
					.build(influxdbScheme, influxdbHost, influxdbPort, influxdbName, null, TimeUnit.SECONDS,
							connectTimeout, readTimeout, "");

		}
	}

	protected Map<String, String> getDefaultMetricTagsMap() {
		Map<String, String> tags = new HashMap<>();
		String application = propertyFactory.getProperty(CommonProperty.APPLICATION.getPropertyName()).asString(null)
				.get();
		String environment = propertyFactory.getProperty(CommonProperty.ENVIRONMENT.getPropertyName()).asString(null)
				.get();
		String datacenter = propertyFactory.getProperty(CommonProperty.DATACENTER.getPropertyName()).asString(null)
				.get();
		String serverName = propertyFactory.getProperty(CommonProperty.SERVERNAME.getPropertyName()).asString(null)
				.get();
		tags.put("app", application);
		tags.put("env", environment);
		tags.put("dc", datacenter);
		tags.put("node", serverName);
		return tags;
	}

	protected void addMetrics(MetricRegistry metricRegistry) {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		metricRegistry.registerAll(new CustomBufferPoolMetricSet(mBeanServer, "nio.bufferPool"));
		metricRegistry.registerAll(new CustomGarbageCollectorMetricSet("jvm.garbageCollector"));
		metricRegistry.registerAll(new CustomMemoryUsageGaugeSet("jvm.memoryUsage"));

	}

	protected void destroy() {
		clearMetrics();
		stopJolokiaServer();
	}

	/**
	 * Stops running jolokia server. Required to avoid "port in use" error when using spring boot devtools
	 * hot reload.
	 */
	private void stopJolokiaServer() {
		if (jolokiaMonitoringServer != null) {
			jolokiaMonitoringServer.stop();
		}
	}

	/**
	 * Removes all registered metrics from reigstry. This avoids errors when metrics are added when using
	 * spring boot devtools hot reload.
	 */
	private void clearMetrics() {
		for (String key : metricRegistry.getMetrics().keySet()) {
			metricRegistry.remove(key);
		}
	}
	
	/**
	 * 
	 * Adding connection accept Rate limit. 
	 * This is useful when app is under DDOS attack.
	 * App will start rejecting connections once it reaches/goes beyond the rate limit.
	 * Will start accepting connections back again when rate is under the rate limit.  
	 * See {@link AcceptRateLimit} for more details
	 */
	protected void addConnectionRateLimit(Server server, ServerConnector connector) {
		Integer connectionRateLimit = propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.connectionRateLimit").asInteger(null).get();
		if(connectionRateLimit != null) {
		    server.addBean(new AcceptRateLimit(connectionRateLimit, 1, TimeUnit.SECONDS, connector));	
		}
	}

	protected void addJMXConnector() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			jolokiaMonitoringServer = new JolokiaMonitoringServer.Builder().withPort(JOLOKIA_PORT).start();
		});
		executor.shutdown();
	}

	protected JettyAppConfig getJettyAppConfig() {
		// @formatter:off
		return new JettyAppConfig(
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.maxThreads").asInteger(200).get(),
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.minThreads").asInteger(8).get(),
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.idleTimeout").asInteger(60000).get(),
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.requestHeaderSize").asInteger(16 * 1024)
						.get(),
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.outputBufferSize").asInteger(32 * 1024)
						.get(),
				propertyFactory.getProperty(getSystemPropertyPrefix() + ".jetty.connectionIdleTimeout").asLong(30000L)
						.get());
		// @formatter:on
	}

	protected String getSystemPropertyPrefix() {
		return "com.about.globe";
	}

	protected void addConnectors(Server server, int httpPort, int httpsPort, JettyAppConfig config) throws Exception {

		// Add HTTP Connector
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		httpConfig.setSendDateHeader(true);
		httpConfig.setSecurePort(httpsPort);
		httpConfig.setSecureScheme("https");
		httpConfig.setRequestHeaderSize(config.getRequestHeaderSize());
		httpConfig.setOutputBufferSize(config.getOutputBufferSize());

		ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		httpConnector.setPort(httpPort);
		httpConnector.setIdleTimeout(config.getConnectionIdleTimeout());
		//add accept rate limit to regular http connector
		addConnectionRateLimit(server, httpConnector);
		server.addConnector(httpConnector);

		configureHttps(server, httpsPort, config, httpConfig);

		// Adding internal connector, serverStatus should be accessed on this connector
		// so that server could still respond to health checks when under DDOS attack on regular http connector.
		if(System.getProperty(CommonProperty.ACCOUNT_NAME.getPropertyName()) != null) {
			addInternalConnector(server);	
		}

		// Set the JVM default sslContext if provided
		if (sslContext != null)
			SSLContext.setDefault(sslContext);

	}

	private void addInternalConnector(Server server) {
		ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration()));
		connector.setPort(INTERNAL_CONNECTOR_PORT);
		server.addConnector(connector);
	}

	protected void setupJettyJmxBean(Server server) {
		// Setup JMX
		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addEventListener(mbContainer);
		server.addBean(mbContainer);

		// Register loggers as MBeans
		mbContainer.beanAdded(null, Log.getLog());
	}

	private void configureHttps(Server server, int httpsPort, JettyAppConfig config, HttpConfiguration httpConfig)
			throws Exception {

		boolean shouldStartHttpsPort = false;

		SslContextFactory sslContextFactory = createJettySslContextFactory();

		if (sslContextFactory != null) {

			shouldStartHttpsPort = true;

			try {
				checkKeyStore(sslContextFactory);
			} catch (Exception e) {
				logger.debug("keystore check failed", e);
				shouldStartHttpsPort = false;
			}
		}

		if (shouldStartHttpsPort) {

			HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
			httpsConfig.addCustomizer(new SecureRequestCustomizer());

			ServerConnector httpsConnector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(httpsConfig));
			httpsConnector.setPort(httpsPort);
			httpsConnector.setIdleTimeout(config.getConnectionIdleTimeout());

			server.addConnector(httpsConnector);

		} else {
			logger.info("Keystore not configured, not starting HTTPS");
		}
	}

	// https://stackoverflow.com/questions/56117749/missing-checkkeystore-in-jetty-9-4-sslcontextfactory/56118046#56118046
	private void checkKeyStore(SslContextFactory ssl) throws IOException {

		if (ssl.getKeyStore() == null) {// too late, already loaded?
			if (StringUtils.isNotBlank(ssl.getKeyStorePath())) // no path, no check
			{
				Path keystorePath = Paths.get(ssl.getKeyStorePath());
				try (InputStream inputStream = Files.newInputStream(keystorePath);
						OutputStream outputStream = new ByteArrayOutputStream()) {
					IO.copy(inputStream, outputStream);
				}
			}
		}
	}

	private static class GlobeGzipHandler extends GzipHandler {

		private Supplier<HttpField> gzipVaryFieldSupplier;

		public GlobeGzipHandler(Supplier<HttpField> gzipVaryFieldSupplier) {
			this.gzipVaryFieldSupplier = gzipVaryFieldSupplier;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			try {
				super.handle(target, baseRequest, request, response);
			} finally {
				// more at: https://dotdash.slack.com/archives/G667ZFFAT/p1558031467021900
				// GzipFilter by default will not set vary header if content length is zero we
				// need to force it so
				// fastly/alb/any other upstream client honors no-cache.
				// Keeping this as is from gzipFilter as a safety net.

				HttpServletResponse httpResponse = (HttpServletResponse) response;

				String varyValue = httpResponse.getHeader(HttpHeader.VARY.asString());
				if (isBlank(varyValue))
					varyValue = "Accept-Encoding, User-Agent";

				httpResponse.setHeader(HttpHeader.VARY.asString(), varyValue);
			}
		}

		@Override
		protected HttpField getVaryField() {
			return gzipVaryFieldSupplier.get();
		}

	}

	/**
	 * For deployed environment it returns {@link SSLContext} as null, which doesn't
	 * start server in https For local, returns for non-null {@link SSLContext}
	 * which starts server in http and https.
	 *
	 * This is here and not in {@link JettyBaseConfig} because, it could be
	 * overridden in downstream clients.
	 * 
	 * @throws Exception
	 */
	@Bean
	protected SSLContext getSslContext() throws Exception {
		SSLContext answer = null;
		if (System.getProperty(CommonProperty.ACCOUNT_NAME.getPropertyName()) != null) {
			answer = null;
		} else {
			// The following variable name is consider a blocker in sonarqube.
			// While we could rename the variable it's not that big of a deal because this
			// credential is only used for local execution. Non-local deployments happen in AWS
			// which takes the path of the if-condition above. Explicitly ignoring this issue.
			String passwd = "changeit"; //NOSONAR
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(this.getClass().getClassLoader().getResourceAsStream("security/keyStore.jks"),
					passwd.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, passwd.toCharArray());
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), null, null);
			answer = ctx;
		}

		return answer;
	}

	public ServletHolder getThumborProxyServlet() {
		// Thumbor proxy; will only be used in QA and Local environments that don't have
		// H2 proxies already set up
		return getProxyServlet("thumbor", getThumborUrl(), THUMBOR_PROXY_PREFIX);
	}

	public ServletHolder getCaesProxyServlet() {
		final boolean caesEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.CAES_ENABLED)
				.asBoolean(false).get();

		if (caesEnabled) {
			// Caes proxy; will only be used in QA and Local environments. If no caes, warn and continue as proxy is not required
			try {
				return getProxyServlet("caes", getCaesUrl() + "/product/", CAES_PROXY_PREFIX);
			} catch (GlobeException e ){
				logger.warn("Unable to start caes proxy. Service may not exist on environment.", e);
			}
		}

		// By default, no caes proxy will be used
		return null;
	}

	/**
	 * Proxy for Orion ad metrics service
	 * @return
	 */
	public ServletHolder getOrionProxyServlet() {
		/* Defaults to true to avoid need for per-vertical configuration to enable. Flag exists to allow
		 * proxy to be disabled if needed without modifying environment.
		 */
		final boolean orionEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.ORION_ENABLED)
				.asBoolean(true).get();

		if (orionEnabled) {
			try {
				String orionUrl = getOrionUrl();
				/* Proxy is not required in QA/local environments and lack of metrics is preferable in production to
				 * the inability to start up verticals, absence of service URL should silently disable proxy.
				 */
				if (orionUrl != null) {
					return getProxyServlet("orion", orionUrl, ORION_PROXY_PREFIX);
				}
			} catch (GlobeException e ){
				logger.warn("Unable to start orion proxy. Service may not exist on environment.", e);
			}
		}
		return null;
	}

	private ServletHolder getProxyServlet(String proxyName, String baseUrl, String prefix) {
		ServletHolder proxyHolder = new ServletHolder(
				baseUrl.startsWith("https") ? new JettyBaseConfig.SecureTransparentProxyServlet()
						: new MantleTransparentProxyServlet());
		URI proxyUri = null;
		try {
			proxyUri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			throw new GlobeException("Couldn't parse " + proxyName + " URI", e);
		}
		proxyHolder.setInitParameter("hostHeader", proxyUri.getHost());
		proxyHolder.setInitParameter("proxyTo", baseUrl);
		proxyHolder.setInitParameter("prefix", prefix);

		// these are here because if a clients headers exceed the buffer size the proxy
		// fails to send the request and we have some long cookies
		final String bufferSize = propertyFactory.getProperty(MantleExternalConfigKeys.PROXY_SERVLET_BUFFER_SIZE)
				.asString("8192").get();
		proxyHolder.setInitParameter("requestBufferSize", bufferSize);
		proxyHolder.setInitParameter("responseBufferSize", bufferSize);

		// default timeout is 30 seconds which is a long time to wait for a response and
		// ties up threads
		final String timeout = propertyFactory.getProperty(MantleExternalConfigKeys.PROXY_SERVLET_TIMEOUT)
				.asString("1500").get();
		proxyHolder.setInitParameter("timeout", timeout);
		proxyHolder.setInitParameter("idleTimeout", timeout);

		return proxyHolder;
	}

	/**
	 * Note that this creates a Jetty {@link SslContextFactory}, _not_ an instance
	 * of Hippodrome's {@link SslContextFactory}.
	 * 
	 * @return
	 * @throws Exception
	 */
	private SslContextFactory createJettySslContextFactory() throws Exception {
		if (sslContext == null)
			return null;

		SslContextFactory jettySslContextFactory = new SslContextFactory();
		jettySslContextFactory.setSslContext(sslContext);

		return jettySslContextFactory;
	}

	public String getThumborUrl() {
		final String thumborBaseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.THUMBOR_BASE_URL)
				.asString(null).get();
		return StringUtils.isNotBlank(thumborBaseUrl) ? thumborBaseUrl : getUrlFromService("thumbor-dotdash");
	}

	public String getCaesUrl() {
		final String caesBaseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.CAES_BASE_URL)
				.asString(null).get();
		return StringUtils.isNotBlank(caesBaseUrl) ? caesBaseUrl : getUrlFromService("caes");
	}

	public String getOrionUrl() {
		final String orionBaseUrl = propertyFactory.getProperty(MantleExternalConfigKeys.ORION_BASE_URL)
				.asString(null).get();
		return StringUtils.isNotBlank(orionBaseUrl) ? orionBaseUrl : getUrlFromService("orion");
	}

	private String getUrlFromService(String serviceName) {
		Optional<URI> serviceUrl = getService(serviceName).getUris().stream().filter(uri -> "https".equals(uri.getScheme()))
				.filter(uri -> StringUtils.isNotBlank(uri.getHost())).filter(uri -> !(uri.getHost().contains("about")))
				.findAny();

		String answer = serviceUrl.isPresent() ? serviceUrl.get().toASCIIString() : null;

		if (StringUtils.isBlank(answer)) {
			throw new GlobeException(serviceName + " service found but couldn't find valid urls");
		}
		return answer;
	}

	private Service getService(String serviceName) {
		Service answer = propertyFactory.getService(serviceName, null);
		if (answer == null) {
			throw new GlobeException("Could not find " + serviceName + " during service discovery");
		}
		return answer;
	}

	/**
	 * Boilerplate implementation of the Jetty Transparent ProxyServlet that can
	 * handle upstream HTTPS requests.
	 * 
	 * @author bcochran
	 */
	private static class SecureTransparentProxyServlet extends MantleTransparentProxyServlet {
		private static final long serialVersionUID = 1L;

		@Override
		protected HttpClient newHttpClient() {
			return new HttpClient(new SslContextFactory(true));
		}

		/**
		 * Overridden because the name created by this logger was difficult to exclude
		 * in Logback (GLBE-5809)
		 */
		@Override
		protected org.eclipse.jetty.util.log.Logger createLogger() {
			return Log.getLogger("com.about.mantle.app.SecureTransparentProxyServlet");
		}
	}
	
	/**
	 * Uses hook provided by ProxyServlet.Transparent to modify headers of remote server response.
	 * We are adding "no-transform" to Cache-Control header. see GLBE-6763
	 */
	private static class MantleTransparentProxyServlet extends ProxyServlet.Transparent {

		/**
		 * Overridden because the name created by this logger was difficult to exclude
		 * in Logback (GLBE-5809)
		 */
		@Override
		protected org.eclipse.jetty.util.log.Logger createLogger() {
			return Log.getLogger("com.about.mantle.app.MantleTransparentProxyServlet");
		}

		private static final long serialVersionUID = 1L;

		@Override
	    protected String filterServerResponseHeader(HttpServletRequest request, Response response, String headerName, String headerValue)	    {
		
			String answer = null;
			if (HttpHeaders.CACHE_CONTROL.equalsIgnoreCase(headerName)) {
				answer = ResponseUtils.addNoTransformCacheControlHeader(headerValue);
			} else {
				answer = super.filterServerResponseHeader(request, response, headerName, headerValue);
			}
			return answer;
		}
	}

}
