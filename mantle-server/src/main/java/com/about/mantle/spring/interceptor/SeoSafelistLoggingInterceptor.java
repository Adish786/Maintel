package com.about.mantle.spring.interceptor;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.mantle.logging.SafeListParamaterFailedLogger;
import com.about.mantle.model.seo.QueryParamSafelist;
import com.google.common.collect.ImmutableSet;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.about.mantle.web.filter.MantleExternalComponentServiceProxyHandler.EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM;

/**
 * Used for logging what would be blocked by new safelistinterceptor changes
 * Temp for GLBE-8768
 */
public class SeoSafelistLoggingInterceptor extends AbstractMantleInterceptor {

    private static final Set<String> RESERVED_QUERYPARAMS = ImmutableSet.of("modelId", "itemIds", "state", "et", "cr",
            "loaded_cr", "loaded_css", "loaded_js", "cis", "force_cis", "pv", "facebookInstant", "logLevel", "prodDfp",
            "forceEndComments", "feedName", "taxDocId", EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM);

    private static final Set<String> SAFELIST_IGNORED_PATHS = ImmutableSet.of("/servemodel/", "/debug/");


    private QueryParamSafelist safelist;
    private CoreRenderUtils renderUtils;
    private PlatformUrlDataFactory urlDataFactory;
    private SafeListParamaterFailedLogger safeListParameterFailedLogger;
    private SeoSafelistInterceptor seoSafelistInterceptor;

    public SeoSafelistLoggingInterceptor(QueryParamSafelist safelist, CoreRenderUtils renderUtils,
                                         PlatformUrlDataFactory urlDataFactory,
                                         SafeListParamaterFailedLogger safeListParameterFailedLogger, SeoSafelistInterceptor seoSafelistInterceptor) {
        this.safelist = safelist;
        this.renderUtils = renderUtils;
        this.urlDataFactory = urlDataFactory;
        this.safeListParameterFailedLogger = safeListParameterFailedLogger;
        this.seoSafelistInterceptor = seoSafelistInterceptor;
    }

    private void checkQueryParamSafelistRedirect(ServletRequest request, ServletResponse response, Object handler) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        RequestContext requestContext = RequestContext.get(httpRequest);
        if (!httpRequest.getMethod().equalsIgnoreCase("GET")) {
            return;
        }

        Map<String, String[]> queryParams = request.getParameterMap();
        if (queryParams.isEmpty()) {
            return;
        }

        if(isExemptPath(httpRequest)){
            return;
        }

        //Used so we can use prehandle from the interceptor safely
        MockHttpServletResponse fakeResponse = new MockHttpServletResponse();

        for (Map.Entry<String, String[]> queryParam : queryParams.entrySet()) {
            Map<String, String []> singleSetQueryParams = new HashMap<>();
            singleSetQueryParams.put(queryParam.getKey(), queryParam.getValue());
            MockHttpServletRequest fakeRequest = new MockHttpServletRequest();
            fakeRequest.setParameterMap(singleSetQueryParams);
            fakeRequest.setRequestUrl(httpRequest.getRequestURL().toString());

            if (!seoSafelistInterceptor.preHandle(fakeRequest, fakeResponse, handler)) {
                safeListParameterFailedLogger.log(httpRequest.getServletPath(), queryParam.getKey());
            }
        }

        return;
    }

    protected boolean isExemptPath(HttpServletRequest httpRequest) {
        String path = httpRequest.getServletPath();


        for(String s: SAFELIST_IGNORED_PATHS){
            if(path.contains(s)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        checkQueryParamSafelistRedirect(request, response, handler);
        return true;
    }

    @Override
    final public String getIncludePathPatterns() {
        return "/**";
    }

    class MockHttpServletResponse implements HttpServletResponse {
        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public boolean containsHeader(String s) {
            return false;
        }

        @Override
        public String encodeURL(String s) {
            return null;
        }

        @Override
        public String encodeRedirectURL(String s) {
            return null;
        }

        @Override
        public String encodeUrl(String s) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String s) {
            return null;
        }

        @Override
        public void sendError(int i, String s) throws IOException {

        }

        @Override
        public void sendError(int i) throws IOException {

        }

        @Override
        public void sendRedirect(String s) throws IOException {

        }

        @Override
        public void setDateHeader(String s, long l) {

        }

        @Override
        public void addDateHeader(String s, long l) {

        }

        @Override
        public void setHeader(String s, String s1) {

        }

        @Override
        public void addHeader(String s, String s1) {

        }

        @Override
        public void setIntHeader(String s, int i) {

        }

        @Override
        public void addIntHeader(String s, int i) {

        }

        @Override
        public void setStatus(int i) {

        }

        @Override
        public void setStatus(int i, String s) {

        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public String getHeader(String s) {
            return null;
        }

        @Override
        public Collection<String> getHeaders(String s) {
            return null;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public void setCharacterEncoding(String s) {

        }

        @Override
        public void setContentLength(int i) {

        }

        @Override
        public void setContentLengthLong(long l) {

        }

        @Override
        public void setContentType(String s) {

        }

        @Override
        public void setBufferSize(int i) {

        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {

        }

        @Override
        public void resetBuffer() {

        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {

        }

        @Override
        public void setLocale(Locale locale) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }
    }

    class MockHttpServletRequest implements HttpServletRequest {

        Map<String, String[]> queryParams;

        String requestUrl;

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String s) {
            return 0;
        }

        @Override
        public String getHeader(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String s) {
            return 0;
        }

        @Override
        public String getMethod() {
            return "GET";
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String s) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(requestUrl);
        }

        public void setRequestUrl (String requestUrl){
            this.requestUrl = requestUrl;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean b) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String s, String s1) throws ServletException {

        }

        @Override
        public void logout() throws ServletException {

        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String s) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
            return null;
        }

        @Override
        public Object getAttribute(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String s) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return queryParams;
        }

        public void setParameterMap(Map<String, String[]> map){
            queryParams = map;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String s, Object o) {

        }

        @Override
        public void removeAttribute(String s) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String s) {
            return null;
        }

        @Override
        public String getRealPath(String s) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }
    }

}
