package com.about.mantle.definition.action;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextContributor;
import com.about.globe.core.task.TaskMethod;
import com.about.globe.core.task.processor.DependencyRequestManager;
import com.about.globe.core.task.registry.TasksRegistry;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.mantle.handlers.methods.CommonTemplateNames;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import com.about.mantle.model.tasks.ResourceTasks;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MantleRequestHandlerMethodsTest {
	
	HttpServletRequest req;
	HttpServletResponse resp;
	DependencyRequestManager drm;

	TasksRegistry tr;
	PageNotFoundHandler pnfh;
	MantleRenderUtils ru;
	RequestContext rc;
	ResourceTasks rt;
	RequestContextContributor testingContributor;
	MantleRequestHandlerMethods hm;
	CommonTemplateNames commonTemplateNames;

	@Before
	public void init() {
		this.req = mock(HttpServletRequest.class);
		this.resp = mock(HttpServletResponse.class);
		this.drm = mock(DependencyRequestManager.class);
		this.tr = mock(TasksRegistry.class);
		this.pnfh = mock(PageNotFoundHandler.class);
		this.ru = mock(MantleRenderUtils.class);
		this.rc = mock(RequestContext.class);
		this.rt = mock(ResourceTasks.class);
		this.testingContributor = mock(RequestContextContributor.class);
		this.commonTemplateNames = new CommonTemplateNames();
		this.hm = new MantleRequestHandlerMethods(null, null, drm, ru, tr, pnfh, null, null, null, null, null, null, null, null,
				null, null, commonTemplateNames, null, null, null, null, null, null, null);
		//this.at = new MantleActionHandlers(drm, ru, ac, pnfh, null, null, rt);
		
	}

	@Test(expected = GlobeException.class)
	public void serveModel_missingModelId() throws IOException, GlobeException {
		hm.serveModel(rc, req, resp);
	}

	@Test(expected = GlobeException.class)
	public void serveModel_TaskMethodNotFound() throws IOException, GlobeException {

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("modelId", new String[] { "AUTHORS" });

		when(rc.getParameters()).thenReturn(params);

		hm.serveModel(rc, req, resp);

	}
	
	@Test
	public void serveModel_Success() throws IOException, GlobeException {

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("modelId", new String[] { "AUTHORS" });

		TaskMethod tm = mock(TaskMethod.class);
		when(tr.getTaskMethod(anyString(), anyString())).thenReturn(tm);

		String jsonResp = "test";
		when(tm.invoke(any(RequestContext.class), anyMapOf(String.class, Object.class))).thenReturn(jsonResp);

		when(rc.getParameters()).thenReturn(params);

		ServletOutputStream sos = mock(ServletOutputStream.class);

		when(resp.getOutputStream()).thenReturn(sos);

		hm.serveModel(rc, req, resp);

		ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
		String jsonValue = objectMapper.writeValueAsString(jsonResp);
		verify(sos, times(1)).write(jsonValue.getBytes("UTF-8"));
	}
}
