package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.about.hippodrome.models.header.AboutHttpHeaders;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.hippodrome.utilities.serverinfo.ServerInfo;

public class TraceFilter implements Filter {

    private ProjectInfo projectInfo;
    private ServerInfo serverInfo;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig
                .getServletContext());

        this.projectInfo = applicationContext.getBean(ProjectInfo.class);
        this.serverInfo = applicationContext.getBean(ServerInfo.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setHeader(AboutHttpHeaders.HEADER_SERVER_HOSTNAME, serverInfo.getHostname());
            httpServletResponse.setHeader(AboutHttpHeaders.HEADER_APPLICATION_VERSION, projectInfo.getVersion());
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}